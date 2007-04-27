/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software 
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
    
	See the GNU Lesser General Public License for more details:    
	http://www.gnu.org/licenses/lgpl.txt
*/

package org.milyn.delivery.dom;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksResourceConfigurationSortComparator;
import org.milyn.cdr.SmooksResourceConfigurationStore;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.dom.serialize.SerializationUnit;
import org.milyn.delivery.*;
import org.milyn.dtd.DTDStore;
import org.milyn.dtd.DTDStore.DTDObjectContainer;
import org.milyn.profile.ProfileSet;


/**
 * DOM content delivery configuration implementation.
 * @author tfennelly
 */
public class ContentDeliveryConfigImpl implements DOMContentDeliveryConfig {
	
	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(ContentDeliveryConfigImpl.class);
	/**
	 * Context key for the table of loaded ContentDeliveryConfig instances.
	 */
	private static final String DELIVERY_CONFIG_TABLE_CTX_KEY = ContentDeliveryConfig.class.getName() + "#configTable";
	/**
	 * Profile set.
	 */
	private ProfileSet profileSet;
	/**
	 * Container context.
	 */
	private ApplicationContext applicationContext;
	/**
	 * XML selector content spec definition prefix
	 */
	private static final String ELCSPEC_PREFIX = "elcspec:";
	/**
	 * Table of SmooksResourceConfiguration instances keyed by selector value. Each table entry
	 * contains a List of SmooksResourceConfiguration instances.
	 */
	private Hashtable<String, List<SmooksResourceConfiguration>> resourceConfigTable = new Hashtable<String, List<SmooksResourceConfiguration>>();
	/**
	 * Table of AssemblyUnit instances keyed by selector. Each table entry
	 * contains a single {@link DOMElementVisitor} instances.
	 */
	private ContentDeliveryUnitConfigMapTable assemblyUnitTable = new ContentDeliveryUnitConfigMapTable();
	/**
     * Table of Processing Unit instances keyed by selector. Each table entry
     * contains a single {@link DOMElementVisitor} instances.
	 */
	private ContentDeliveryUnitConfigMapTable processingUnitTable = new ContentDeliveryUnitConfigMapTable();
	/**
	 * Table of SerializationUnit instances keyed by selector. Each table entry
	 * contains a single SerializationUnit instances.
	 */
	private ContentDeliveryUnitConfigMapTable serializationUnitTable = new ContentDeliveryUnitConfigMapTable();
	/**
	 * Table of Object instance lists keyed by selector. Each table entry
	 * contains a List of Objects.
	 */
	private Hashtable objectsTable = new Hashtable();
	/**
	 * DTD for the associated device.
	 */
	private DTDObjectContainer dtd;
	
	/**
	 * Private (hidden) constructor.
     * @param profileSet Profile set.
	 * @param applicationContext Container context.
	 */
	private ContentDeliveryConfigImpl(ProfileSet profileSet, ApplicationContext applicationContext) {
		this.profileSet = profileSet;
		this.applicationContext = applicationContext;
    }
	
	/**
	 * Get the ContentDeliveryConfigImpl instance for the named table.
	 * @param profileSet The profile set with which this delivery config is associated.
	 * @param applicationContext Application context.
	 * @return The ContentDeliveryConfig instance for the named table.
	 */
	public static ContentDeliveryConfig getInstance(ProfileSet profileSet, ApplicationContext applicationContext) {
		ContentDeliveryConfigImpl config;
		Hashtable<String, ContentDeliveryConfigImpl> configTable;
		
		if(profileSet == null) {
			throw new IllegalArgumentException("null 'profileSet' arg passed in method call.");
		} else if(applicationContext == null) {
			throw new IllegalArgumentException("null 'applicationContext' arg passed in method call.");
		}

		// Get the delivery config config from container context.
		configTable = (Hashtable) applicationContext.getAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY);
		if(configTable == null) {
			configTable = new Hashtable<String, ContentDeliveryConfigImpl>();
			applicationContext.setAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY, configTable);
		}
		// Get the delivery config instance for this UAContext
		config = configTable.get(profileSet.getBaseProfile());
		if(config == null) {
			config = new ContentDeliveryConfigImpl(profileSet, applicationContext);
			config.load();
			configTable.put(profileSet.getBaseProfile(), config);
		}
		
		return config;
	}

	/**
	 * Build the ContentDeliveryConfigImpl for the specified device.
	 * <p/>
	 * Creates the buildTable instance and populates it with the ProcessingUnit matrix
	 * for the specified device.
	 */
	private void load() {
		List resourceConfigsList = Arrays.asList(applicationContext.getStore().getSmooksResourceConfigurations(profileSet));

		// Build and sort the resourceConfigTable table - non-transforming elements.
		buildSmooksResourceConfigurationTable(resourceConfigsList);
		sortSmooksResourceConfigurations(resourceConfigTable);
		
		// If there's a DTD for this device, get it and add it to the DTDStore.
		List dtdSmooksResourceConfigurations = (List)resourceConfigTable.get("dtd");
		if(dtdSmooksResourceConfigurations != null && dtdSmooksResourceConfigurations.size() > 0) {
            SmooksResourceConfiguration dtdSmooksResourceConfiguration = (SmooksResourceConfiguration)dtdSmooksResourceConfigurations.get(0);
            byte[] dtdDataBytes = dtdSmooksResourceConfiguration.getBytes();

            if(dtdDataBytes != null) {
                DTDStore.addDTD(profileSet, new ByteArrayInputStream(dtdDataBytes));
                // Initialise the DTD reference for this config table.
                dtd = DTDStore.getDTDObject(profileSet);
            } else {
                logger.error("DTD resource [" + dtdSmooksResourceConfiguration.getResource() + "] not found in classpath.");
            }
		}

		// Expand the SmooksResourceConfiguration table and resort
		expandSmooksResourceConfigurationTable();
		sortSmooksResourceConfigurations(resourceConfigTable);

		if(logger.isDebugEnabled()) {
			logResourceConfig();
		}
		
		// Extract the ContentDeliveryUnits and build the tables
		extractContentDeliveryUnits();
	}

	/**
	 * Print a debug log of the resource configurations for the associated profile.
	 */
	private void logResourceConfig() {
		logger.debug("==================================================================================================");
		logger.debug("Resource configuration (sorted) for profile [" + profileSet.getBaseProfile() + "].  Sub Profiles: [" + profileSet + "]");
		Iterator configurations = resourceConfigTable.entrySet().iterator();
		int i = 0;
		
		while(configurations.hasNext()) {
			Map.Entry entry = (Entry) configurations.next();
			List resources = (List)entry.getValue();
			
			logger.debug(i + ") " + entry.getKey());
			for (int ii = 0; ii < resources.size(); ii++) {
				logger.debug("\t(" + ii + ") " + resources.get(ii));
			}
		}
		logger.debug("==================================================================================================");
	}

	/**
	 * Build the basic SmooksResourceConfiguration table from the list.
	 * @param resourceConfigsList List of SmooksResourceConfigurations.
	 */
	private void buildSmooksResourceConfigurationTable(List resourceConfigsList) {
		Iterator iterator = resourceConfigsList.iterator();
		
		while(iterator.hasNext()) {
			SmooksResourceConfiguration config = (SmooksResourceConfiguration)iterator.next();
            addResourceConfiguration(config);
		}
	}

    /**
     * Add the supplied resource configuration to this configuration's main
     * resource configuration list.
     * @param config The configuration to be added.
     */
    private void addResourceConfiguration(SmooksResourceConfiguration config) {
        String target = config.getSelector();

        // If it's contextual, it's targeting an XML element...
        if(config.isSelectorContextual()) {
            target = config.getTargetElement();
        }

        Vector selectorUnits = (Vector)resourceConfigTable.get(target);

        if(selectorUnits == null) {
            selectorUnits = new Vector();
            resourceConfigTable.put(target, selectorUnits);
        }

        // Add to the smooks-resource on the resourceConfigTable
        selectorUnits.addElement(config);
    }

    /**
	 * Expand the SmooksResourceConfiguration table.
	 * <p/>
	 * Expand the XmlDef entries to the target elements etc.
	 */
	private void expandSmooksResourceConfigurationTable() {
		class ExpansionSmooksResourceConfigurationStrategy implements SmooksResourceConfigurationStrategy {
			private ExpansionSmooksResourceConfigurationStrategy() {
			}
			public void applyStrategy(String elementName, SmooksResourceConfiguration resourceConfig) {
				// Expand XmlDef entries.
				if(resourceConfig.isXmlDef()) {
					String[] elements = getDTDElements(resourceConfig.getSelector().substring(SmooksResourceConfiguration.XML_DEF_PREFIX.length()));
					for(int i = 0; i < elements.length; i++) {
						addObject(elements[i], resourceConfig, resourceConfigTable);
					}
				}
				
				// Add code to expand other expandable entry types here.
			}
		}
		SmooksResourceConfigurationTableIterator tableIterator = new SmooksResourceConfigurationTableIterator(new ExpansionSmooksResourceConfigurationStrategy());
		tableIterator.iterate();
	}

	/**
	 * Iterate over the table smooks-resource instances and sort the SmooksResourceConfigurations
	 * on each element.  Ordered by specificity.
	 */
	private void sortSmooksResourceConfigurations(Hashtable<String, List<SmooksResourceConfiguration>> table) {
		if(!table.isEmpty()) {
			Iterator tableEntrySet = table.entrySet().iterator();
			
			while(tableEntrySet.hasNext()) {
				Map.Entry entry = (Map.Entry)tableEntrySet.next();
				List markupElSmooksResourceConfigurations = (List)entry.getValue();
				SmooksResourceConfiguration[] resourceConfigs = (SmooksResourceConfiguration[])markupElSmooksResourceConfigurations.toArray(new SmooksResourceConfiguration[markupElSmooksResourceConfigurations.size()]);
				SmooksResourceConfigurationSortComparator sortComparator = new SmooksResourceConfigurationSortComparator(profileSet);

				Arrays.sort(resourceConfigs, sortComparator);
				entry.setValue(new Vector(Arrays.asList(resourceConfigs)));
			}
		}
	}

	/**
	 * Extract the ContentDeliveryUnits from the SmooksResourceConfiguration table and add them to  
	 * their respective tables.
	 */
	private void extractContentDeliveryUnits() {
		ContentDeliveryExtractionStrategy cduStrategy = new ContentDeliveryExtractionStrategy(applicationContext);
		SmooksResourceConfigurationTableIterator tableIterator = new SmooksResourceConfigurationTableIterator(cduStrategy);

        tableIterator.iterate();
        // Process any expansions that may have been added...
        cduStrategy.processExpansionConfigurations();
    }
	
	/**
	 * Get the DTD elements for specific device context.
	 * @param string DTD spec string e.g. "elcspec:empty"
	 * @return List of element names.
	 */
	private String[] getDTDElements(String string) {
		String tmpString = string.toLowerCase();
		
		if(tmpString.startsWith(ELCSPEC_PREFIX)) {
			tmpString = tmpString.substring(ELCSPEC_PREFIX.length());
			if(tmpString.equals("empty")) {
				return dtd.getEmptyElements();
			} else if(tmpString.equals("not-empty")) {
				return dtd.getNonEmptyElements();
			} else if(tmpString.equals("any")) {
				return dtd.getAnyElements();
			} else if(tmpString.equals("not-any")) {
				return dtd.getNonAnyElements();
			} else if(tmpString.equals("mixed")) {
				return dtd.getMixedElements();
			} else if(tmpString.equals("not-mixed")) {
				return dtd.getNonMixedElements();
			} else if(tmpString.equals("pcdata")) {
				return dtd.getPCDataElements();
			} else if(tmpString.equals("not-pcdata")) {
				return dtd.getNonPCDataElements();
			}
		}
		
		throw new IllegalStateException("Unsupported DTD spec definition [" + string + "]");
	}

	/**
	 * Add the Object for the specified element to the supplied table.
	 * @param element The element to which the Object is to be added.
	 * @param object The Object to be added.
	 * @param table The table to be added to.
	 */
	private void addObject(String element, Object object, Hashtable table) {
		List markupElCDRs = (List)table.get(element);
		
		if(markupElCDRs == null) {
			markupElCDRs = new Vector();
			table.put(element, markupElCDRs);
		}
		
		if(!markupElCDRs.contains(object)) {
			markupElCDRs.add(object);
		}
	}

    /**
     * Get the Processing Unit table for this delivery context.
     * <p/>
     * The table is keyed by element name and the values are
     * {@link org.milyn.delivery.ContentDeliveryUnitConfigMap} instances where the contained
     * {@link org.milyn.delivery.ContentDeliveryUnit} is an {@link DOMElementVisitor}.
     * @return The Processing Unit table for this delivery context.
     */
    public ContentDeliveryUnitConfigMapTable getProcessingUnits() {
        return processingUnitTable;
	}
	
	/**
	 * Get the list of {@link SmooksResourceConfiguration}s for the specified selector definition.
	 * @param selector The configuration "selector" attribute value from the .cdrl file in the .cdrar.
	 * @return List of SmooksResourceConfiguration instances, or null.
	 */
	public List getSmooksResourceConfigurations(String selector) {
		return (List)resourceConfigTable.get(selector.toLowerCase());
	}
	
	/**
	 * Get the {@link SmooksResourceConfiguration} map for the target execution context.
	 * <p/>
	 * This Map will be {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator preordered} 
	 * for the target execution context.
	 * 
	 * @return {@link SmooksResourceConfiguration} map for the target execution context, keyed by the configuration 
	 * {@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() selector}, with each value being a
	 * {@link List} of preordered {@link SmooksResourceConfiguration} instances.
	 */
	public Map getSmooksResourceConfigurations() {
		return resourceConfigTable;
	}

	private static final Vector EMPTY_LIST = new Vector();

	/**
	 * Get a list {@link Object}s from the supplied {@link SmooksResourceConfiguration} selector value.
	 * <p/>
	 * Uses {@link org.milyn.cdr.SmooksResourceConfigurationStore#getObject(org.milyn.cdr.SmooksResourceConfiguration)} to construct the object.
	 * @param selector selector attribute value from the .cdrl file in the .cdrar.
	 * @return List of Object instances.  An empty list is returned where no 
	 * selectors exist.
	 */
	public List getObjects(String selector) {
		Vector objects;
		
		selector = selector.toLowerCase();
		objects = (Vector)objectsTable.get(selector);
		if(objects == null) {
			List unitDefs = (List)resourceConfigTable.get(selector);

			if(unitDefs != null && unitDefs.size() > 0) {
				objects = new Vector(unitDefs.size());
				for(int i = 0; i < unitDefs.size(); i++) {
					SmooksResourceConfiguration resConfig = (SmooksResourceConfiguration)unitDefs.get(i);
					objects.add(applicationContext.getStore().getObject(resConfig));
				}
			} else {
				objects = EMPTY_LIST;
			}
			
			objectsTable.put(selector, objects);
		}
		
		return objects;
	}
	
	/**
	 * Get the AssemblyUnits table for this delivery context.
	 * <p/>
	 * @return The AssemblyUnits table for this delivery context.
	 */
	public ContentDeliveryUnitConfigMapTable getAssemblyUnits() {
		return assemblyUnitTable;
	}
	
	/**
	 * Get the SerializationUnit table for this delivery context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link SerializationUnit} instances.
	 * @return The SerializationUnit table for this delivery context.
	 */
	public ContentDeliveryUnitConfigMapTable getSerailizationUnits() {
		return serializationUnitTable;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getDTD()
	 */
	public DTDObjectContainer getDTD() {
		return dtd;
	}
	
	/**
	 * ContentDeliveryUnit extraction strategy.
	 * @author tfennelly
	 */
	private final class ContentDeliveryExtractionStrategy implements SmooksResourceConfigurationStrategy {
		
        private SmooksResourceConfigurationStore store;
        private List<SmooksResourceConfiguration> expansionConfigs = new ArrayList<SmooksResourceConfiguration>();

        public ContentDeliveryExtractionStrategy(ApplicationContext applicationContext) {
            store = applicationContext.getStore();
        }

        public void applyStrategy(String elementName, SmooksResourceConfiguration resourceConfig) {
			ContentDeliveryUnitCreator creator;

			// Try it as a Java class before trying anything else.  This is to
			// accomodate specification of the class in the standard 
			// Java form e.g. java.lang.String Vs java/lang/String.class
            if(resourceConfig.isJavaContentDeliveryUnit()) {
    			try {                
    				creator = store.getContentDeliveryUnitCreator("class");
    				if(addCDU(elementName, resourceConfig, creator)) {
    					// Job done - it's a CDU and we've added it!
    					return;
    				}
    			} catch (UnsupportedContentDeliveryUnitTypeException e) {
    				throw new IllegalStateException("No ContentDeliveryUnitCreator configured (IoC) for type 'class' (Java).");
    			} catch (InstantiationException e) {
                    // Ignore it again - not a proper Java CDU - continue on, may be a different type...
                }
            }

            // Get the resource type and "try" creating a ContentDeliveryUnitCreator for that resource
            // type.
            String restype = resourceConfig.getResourceType();
            creator = tryCreateCreator(restype);
			
            // If we have a creator but it's the JavaContentDeliveryUnitCreator we ignore it because
            // we know the class in question does not implement ContentDeliveryUnit.  We know because
            // we tried this above.
            if(creator != null && !(creator instanceof JavaContentDeliveryUnitCreator)) {
				try {
					addCDU(elementName, resourceConfig, creator);
				} catch (InstantiationException e) {
					logger.warn("ContentDeliveryUnit creation failure.", e);
				}
            } else {
				// Just ignore it - something else will use it (hopefully)            	
            }
		}
        
        /**
         * Try create the CDU creator for the specified resource type.
         * <p/>
         * Return null if unsuccessful i.e. no exceptions.
         * @param restype The resource type.
         * @return The appropriate CDU creator instance, or null if there is none.
         */
        private ContentDeliveryUnitCreator tryCreateCreator(String restype) {
			ContentDeliveryUnitCreator creator;

			try {
				if(restype == null || restype.trim().equals("")) {
					logger.warn("Request to attempt ContentDeliveryUnitCreator creation based on a null/empty resource type.");
					return null;
				}
				creator = store.getContentDeliveryUnitCreator(restype);
			} catch (UnsupportedContentDeliveryUnitTypeException e) {
				return null;
			}
			
			return creator;
        }

        /**
		 * Add a {@link org.milyn.delivery.ContentDeliveryUnit} for the specified element and configuration.
		 * @param elementName Element name against which to associate the CDU.
		 * @param resourceConfig Configuration.
		 * @param creator CDU Creator class.
		 * @throws InstantiationException Failed to instantia
         * @return True if the CDU was added, otherwise false. 
		 */
		private boolean addCDU(String elementName, SmooksResourceConfiguration resourceConfig, ContentDeliveryUnitCreator creator) throws InstantiationException {
			ContentDeliveryUnit contentDeliveryUnit;

			// Create the ContentDeliveryUnit.
			try {
				contentDeliveryUnit = creator.create(resourceConfig);
			} catch(Throwable thrown) {
				if(logger.isDebugEnabled()) {
					logger.warn("ContentDeliveryUnitCreator [" + creator.getClass().getName()  + "] unable to create resource processing instance for resource [" + resourceConfig + "]. " + thrown.getMessage());
				} else {
					logger.warn("ContentDeliveryUnitCreator [" + creator.getClass().getName()  + "] unable to create resource processing instance for resource [" + resourceConfig + "].", thrown);
				}
				return false;
			}
			
			if(contentDeliveryUnit instanceof DOMElementVisitor) {
                Phase phaseAnnotation = contentDeliveryUnit.getClass().getAnnotation(Phase.class);

                if(phaseAnnotation != null && phaseAnnotation.value() == VisitPhase.ASSEMBLY) {
                    // It's an assembly unit...
                    assemblyUnitTable.addMapping(elementName, resourceConfig, contentDeliveryUnit);
                } else {
                    // It's a processing unit...
                    processingUnitTable.addMapping(elementName, resourceConfig, contentDeliveryUnit);
                }
            } else if(contentDeliveryUnit instanceof SerializationUnit) {
                serializationUnitTable.addMapping(elementName, resourceConfig, contentDeliveryUnit);
			} else {
				// It's not a CDU type we know of!  Leave for now - whatever's using it
				// can instantiate it itself.
				return false;
			}

            // Content delivery units are allowed to dynamically add new configurations...
            if(contentDeliveryUnit instanceof ExpandableContentDeliveryUnit) {
                List<SmooksResourceConfiguration> additionalConfigs = ((ExpandableContentDeliveryUnit)contentDeliveryUnit).getExpansionConfigurations();
                if(additionalConfigs != null && !additionalConfigs.isEmpty()) {
                    expansionConfigs.addAll(additionalConfigs);
                }
            }

            return true;
		}

        /**
         * Process any expansion configurations on this ContentDeliveryExtractionStrategy instance.
         */
        public void processExpansionConfigurations() {
            for(SmooksResourceConfiguration config : expansionConfigs) {
                String targetElement = config.getTargetElement();

                // Try adding it as a ContentDeliveryUnit instance...
                applyStrategy(targetElement, config);
                // Add the configuration itself to the main list...
                addResourceConfiguration(config);
            }
        }
    }

	/**
	 * Iterate over the SmooksResourceConfiguration table applying the constructor 
	 * supplied SmooksResourceConfigurationStrategy.
	 * @author tfennelly
	 */
	private class SmooksResourceConfigurationTableIterator {
		
		/**
		 * Iteration strategy.
		 */
		private SmooksResourceConfigurationStrategy strategy;
		
		/**
		 * Private constructor.
		 * @param strategy Strategy algorithm implementation.
		 */
		private SmooksResourceConfigurationTableIterator(SmooksResourceConfigurationStrategy strategy) {
			this.strategy = strategy;
		}
		
		/**
		 * Iterate over the table applying the strategy.
		 */
		private void iterate() {
			if(!resourceConfigTable.isEmpty()) {
				Hashtable tableClone = (Hashtable)resourceConfigTable.clone();
				Iterator iterator = tableClone.entrySet().iterator();				
				
				while(iterator.hasNext()) {
					Map.Entry entry = (Map.Entry)iterator.next();
					String elementName = (String)entry.getKey();
					List resourceConfigList = (List)entry.getValue();

					for(int i = 0; i < resourceConfigList.size(); i++) {
						SmooksResourceConfiguration resourceConfig = (SmooksResourceConfiguration)resourceConfigList.get(i);
						
						strategy.applyStrategy(elementName, resourceConfig);
					}
				}
			}			
		}
	}
	
	/**
	 * Unitdef iteration strategy interface.
	 * @author tfennelly
	 */
	private interface SmooksResourceConfigurationStrategy {
		/**
		 * Apply the strategy algorithm.
		 * @param elementName The element name the SmooksResourceConfiguration
		 * @param unitDef
		 */
		public void applyStrategy(String elementName, SmooksResourceConfiguration unitDef);
	}
}
