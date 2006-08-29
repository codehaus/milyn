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

package org.milyn.delivery;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksResourceConfigurationSortComparator;
import org.milyn.cdr.SmooksResourceConfigurationStore;
import org.milyn.container.ContainerContext;
import org.milyn.delivery.assemble.AssemblyUnit;
import org.milyn.delivery.process.ProcessingSet;
import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.delivery.serialize.SerializationUnit;
import org.milyn.device.UAContext;
import org.milyn.dtd.DTDStore;
import org.milyn.dtd.DTDStore.DTDObjectContainer;


/**
 * Useragent content delivery configuration.
 * @author tfennelly
 */
public class ContentDeliveryConfigImpl implements ContentDeliveryConfig {
	
	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(ContentDeliveryConfigImpl.class);
	/**
	 * Context key for the table of loaded ContentDeliveryConfig instances.
	 */
	private static final String DELIVERY_CONFIG_TABLE_CTX_KEY = ContentDeliveryConfig.class.getName() + "#configTable";
	/**
	 * Associated device context.
	 */
	private UAContext deviceContext;
	/**
	 * Container context.
	 */
	private ContainerContext containerContext;
	/**
	 * XML selector content spec definition prefix
	 */
	private static final String ELCSPEC_PREFIX = "elcspec:";
	/**
	 * Table of SmooksResourceConfiguration instances keyed by selector value. Each table entry
	 * contains a List of SmooksResourceConfiguration instances.
	 */
	private Hashtable resourceConfigTable = new Hashtable();
	/**
	 * Table of AssemblyUnit instances keyed by selector. Each table entry
	 * contains a single AssemblyUnit instances.
	 */
	private Hashtable assemblyUnitTable = new Hashtable();
	/**
	 * Table of ProcessingSet instances keyed by selector. Each table entry
	 * contains a ProcessingSet instances.
	 */
	private Hashtable processingSetTable = new Hashtable();
	/**
	 * Table of SerializationUnit instances keyed by selector. Each table entry
	 * contains a single SerializationUnit instances.
	 */
	private Hashtable serializationUnitTable = new Hashtable();
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
	 * @param containerContext
	 */
	private ContentDeliveryConfigImpl(UAContext deviceContext, ContainerContext containerContext) {
		this.deviceContext = deviceContext;
		this.containerContext = containerContext;
	}
	
	/**
	 * Get the ContentDeliveryConfigImpl instance for the named table.
	 * @param deviceContext The device context for the associated device.
	 * @param containerContext Container context.
	 * @return The ContentDeliveryConfig instance for the named table.
	 */
	public static ContentDeliveryConfig getInstance(UAContext deviceContext, ContainerContext containerContext) {
		ContentDeliveryConfigImpl table = null;
		Hashtable configTable;
		
		if(deviceContext == null) {
			throw new IllegalArgumentException("null 'deviceContext' arg passed in method call.");
		} else if(containerContext == null) {
			throw new IllegalArgumentException("null 'containerContext' arg passed in method call.");
		}

		// Get the delivery config table from container context.
		configTable = (Hashtable)containerContext.getAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY);
		if(configTable == null) {
			configTable = new Hashtable();
			containerContext.setAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY, configTable);
		}
		// Get the delivery config instance for this UAContext
		table = (ContentDeliveryConfigImpl)configTable.get(deviceContext);
		if(table == null) {
			table = new ContentDeliveryConfigImpl(deviceContext, containerContext);
			table.load();
			configTable.put(deviceContext, table);
		}
		
		return table;
	}

	/**
	 * Build the ContentDeliveryConfigImpl for the specified device.
	 * <p/>
	 * Creates the buildTable instance and populates it with the ProcessingUnit matrix
	 * for the specified device.
	 * @param deviceContext The associated device context.
	 */
	private void load() {
		List resourceConfigsList = Arrays.asList(containerContext.getStore().getSmooksResourceConfigurations(deviceContext));

		// Build and sort the resourceConfigTable table - non-transforming elements.
		buildSmooksResourceConfigurationTable(resourceConfigsList);
		sortSmooksResourceConfigurations(resourceConfigTable);
		
		// If there's a DTD for this device, get it and add it to the DTDStore.
		List dtdSmooksResourceConfigurations = (List)resourceConfigTable.get("dtd");
		if(dtdSmooksResourceConfigurations != null && dtdSmooksResourceConfigurations.size() > 0) {
			try {
				SmooksResourceConfiguration dtdSmooksResourceConfiguration = (SmooksResourceConfiguration)dtdSmooksResourceConfigurations.get(0);
				byte[] dtdDataBytes = dtdSmooksResourceConfiguration.getBytes();
                
                if(dtdDataBytes != null) {
    				DTDStore.addDTD(deviceContext, new ByteArrayInputStream(dtdDataBytes));
    				// Initialise the DTD reference for this config table.
    				dtd = DTDStore.getDTDObject(deviceContext);
                } else {
                	logger.error("DTD resource [" + dtdSmooksResourceConfiguration.getPath() + "] not found in classpath.");
                }
			} catch (IOException e) {
                IllegalStateException state = new IllegalStateException("Error reading DTD resource.");
                state.initCause(e);
                throw state;
            }
		}

		// Expand the SmooksResourceConfiguration table and resort
		expandSmooksResourceConfigurationTable();
		sortSmooksResourceConfigurations(resourceConfigTable);
		
		// Extract the ContentDeliveryUnits and build the tables
		extractContentDeliveryUnits();
	}

	/**
	 * Build the basic SmooksResourceConfiguration table from the list.
	 * @param resourceConfigsList List of SmooksResourceConfigurations.
	 */
	private void buildSmooksResourceConfigurationTable(List resourceConfigsList) {
		Iterator iterator = resourceConfigsList.iterator();
		
		while(iterator.hasNext()) {
			SmooksResourceConfiguration config = (SmooksResourceConfiguration)iterator.next();
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
	private void sortSmooksResourceConfigurations(Hashtable table) {
		if(!table.isEmpty()) {
			Iterator tableEntrySet = table.entrySet().iterator();
			
			while(tableEntrySet.hasNext()) {
				Map.Entry entry = (Map.Entry)tableEntrySet.next();
				List markupElSmooksResourceConfigurations = (List)entry.getValue();
				SmooksResourceConfiguration[] resourceConfigs = (SmooksResourceConfiguration[])markupElSmooksResourceConfigurations.toArray(new SmooksResourceConfiguration[markupElSmooksResourceConfigurations.size()]);
				SmooksResourceConfigurationSortComparator sortComparator = new SmooksResourceConfigurationSortComparator(deviceContext);

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
		SmooksResourceConfigurationStrategy cduStrategy = new ContentDeliveryExtractionStrategy(containerContext);
		SmooksResourceConfigurationTableIterator tableIterator = new SmooksResourceConfigurationTableIterator(cduStrategy);
		tableIterator.iterate();
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
	 * Get the ProcessingUnit configure instances, for the named tag, for the useragent 
	 * associated with this table.
	 * @param tag The tag name for which the ProcessingUnits are being requested.
	 * @return ProcessingSet for the specified tag name, or null if none is specified.  
	 */
	public ProcessingSet getProcessingSet(String tag) {
		return (ProcessingSet)processingSetTable.get(tag.toLowerCase());
	}
	
	/**
	 * Get the list of {@link SmooksResourceConfiguration}s for the specified selector definition.
	 * @param selector "selector" attribute value from the .cdrl file in the .cdrar.
	 * @return List of SmooksResourceConfiguration instances, or null.
	 */
	public List getSmooksResourceConfigurations(String selector) {
		return (List)resourceConfigTable.get(selector.toLowerCase());
	}

	private static final Vector EMPTY_LIST = new Vector();

	/**
	 * Get a list {@link Object}s from the supplied {@link SmooksResourceConfiguration} selector value.
	 * <p/>
	 * Uses {@link org.milyn.cdr.CDRStore#getObject(SmooksResourceConfiguration)} to construct the object.
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
					objects.add(containerContext.getStore().getObject(resConfig));
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
	 * The table is keyed by element name and the values are 
	 * {@link AssemblyUnit} instances.
	 * @return The AssemblyUnits table for this delivery context.
	 */
	public Hashtable getAssemblyUnits() {
		return assemblyUnitTable;
	}
	
	/**
	 * Get the SerializationUnit table for this delivery context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link SerializationUnit} instances.
	 * @return The SerializationUnit table for this delivery context.
	 */
	public Hashtable getSerailizationUnits() {
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

        public ContentDeliveryExtractionStrategy(ContainerContext containerContext) {
            store = containerContext.getStore();
        }

        public void applyStrategy(String elementName, SmooksResourceConfiguration resourceConfig) {
			ContentDeliveryUnitCreator creator = null;;

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
                    // Ignore it again - not a CDU - continue on, may be a different type...
                }
            }

            // Get the resource type and "try" creating a ContentDeliveryUnitCreator for that resource
            // type.
            String restype = resourceConfig.getType();
            creator = tryCreateCreator(restype);
			
            // If we have a creator but it's the JavaContentDeliveryUnitCreator we ignore it because
            // we know the class in question is not a ContentDeliveryUnit.  We know this because the 
            // resourceConfig.isContentDeliveryUnit() call at the start of this method failed.
            if(creator != null && !(creator instanceof JavaContentDeliveryUnitCreator)) {
				try {
					addCDU(elementName, resourceConfig, creator);
				} catch (InstantiationException e) {
					logger.error("ContentDeliveryUnit creation failure.", e);
				}
            } else {
				// Just ignore it - something else will use it            	
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
		 * Add a {@link ContentDeliveryUnit} for the specified element and configuration.
		 * @param elementName Element name against which to associate the CDU.
		 * @param resourceConfig Configuration.
		 * @param creator CDU Creator class.
		 * @throws InstantiationException 
		 */
		private boolean addCDU(String elementName, SmooksResourceConfiguration resourceConfig, ContentDeliveryUnitCreator creator) throws InstantiationException {
			ContentDeliveryUnit contentDeliveryUnit;

			// Create the ContentDeliveryUnit.
            contentDeliveryUnit = creator.create(resourceConfig);
			
			if(contentDeliveryUnit instanceof AssemblyUnit) {
				addAssemblyUnit(elementName, contentDeliveryUnit, resourceConfig);				
			} else if(contentDeliveryUnit instanceof ProcessingUnit) {
				addProcessingUnit(elementName, (ProcessingUnit)contentDeliveryUnit, resourceConfig);
			} else if(contentDeliveryUnit instanceof SerializationUnit) {
				addSerializationUnit(elementName, contentDeliveryUnit, resourceConfig);
			} else {
				// It's not a CDU type we know of!  Leave for now - whatever's using it
				// can instantiate it itself.
				return false;
			}
			
			return true;
		}

		/**
		 * Add AssemblyUnit.
		 * @param elementName Element to which the AssemblyUnit is to be applied.
		 * @param assemblyUnit AssemblyUnit to be added.
         * @param resourceConfig Resource configuration.
		 */
		private void addAssemblyUnit(String elementName, ContentDeliveryUnit assemblyUnit, SmooksResourceConfiguration resourceConfig) {
			Vector elAssemblyUnits = (Vector)assemblyUnitTable.get(elementName);
			
			if(elAssemblyUnits == null) {
				elAssemblyUnits = new Vector();
				assemblyUnitTable.put(elementName, elAssemblyUnits);
			}
            ContentDeliveryUnitConfigMap mapInst = 
                new ContentDeliveryUnitConfigMap(assemblyUnit, resourceConfig);
            
			elAssemblyUnits.add(mapInst);
		}

		/**
		 * Add ProcessingUnit.
		 * @param elementName Element to which the ProcessingUnit is to be applied.
		 * @param processingUnit ProcessingUnit to be added.
         * @param resourceConfig Resource configuration.
		 */
		private void addProcessingUnit(String elementName, ProcessingUnit processingUnit, SmooksResourceConfiguration resourceConfig) {
			ProcessingSet processingSet = (ProcessingSet)processingSetTable.get(elementName);
			
			if(processingSet == null) {
				processingSet = new ProcessingSet();
				processingSetTable.put(elementName, processingSet);
			}
            processingSet.addProcessingUnit(processingUnit, resourceConfig);
		}

		/**
		 * Add SerializationUnit.
		 * @param elementName Element to which the SerializationUnit is to be applied.
		 * @param serializationUnit SerializationUnit to be added.
		 * @param resourceConfig Resource configuration.
		 */
		private void addSerializationUnit(String elementName, ContentDeliveryUnit serializationUnit, SmooksResourceConfiguration resourceConfig) {
			List elementSerUnits = (List)serializationUnitTable.get(elementName);
			
			if(elementSerUnits == null) {
				elementSerUnits = new Vector();
				serializationUnitTable.put(elementName, elementSerUnits);
			}
            ContentDeliveryUnitConfigMap mapInst = 
                new ContentDeliveryUnitConfigMap(serializationUnit, resourceConfig);

            elementSerUnits.add(mapInst);
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
