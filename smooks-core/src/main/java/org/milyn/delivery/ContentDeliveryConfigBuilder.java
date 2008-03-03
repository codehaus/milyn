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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.*;
import org.milyn.cdr.annotation.VisitIf;
import org.milyn.cdr.annotation.VisitIfNot;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.dom.*;
import org.milyn.delivery.dom.serialize.SerializationUnit;
import org.milyn.delivery.sax.SAXContentDeliveryConfig;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.dtd.DTDStore;
import org.milyn.dtd.DTDStore.DTDObjectContainer;
import org.milyn.event.types.ConfigBuilderEvent;
import org.milyn.profile.ProfileSet;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * Content delivery configuration builder.
 * @author tfennelly
 */
public class ContentDeliveryConfigBuilder {
	
	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(ContentDeliveryConfigBuilder.class);
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
     * An unsorted list of SmooksResourceConfiguration.
     */
    private List<SmooksResourceConfiguration> resourceConfigsList = new ArrayList<SmooksResourceConfiguration>();
    /**
	 * Table (by element) of sorted SmooksResourceConfiguration instances keyed by selector value. Each table entry
	 * contains a List of SmooksResourceConfiguration instances.
	 */
	private LinkedHashMap<String, List<SmooksResourceConfiguration>> resourceConfigTable = new LinkedHashMap<String, List<SmooksResourceConfiguration>>();
    /**
	 * Assembly Visit Befores.
	 */
	private ContentHandlerConfigMapTable<DOMVisitBefore> assemblyVisitBefores = new ContentHandlerConfigMapTable<DOMVisitBefore>();
    /**
	 * Assembly Visit Afters.
	 */
	private ContentHandlerConfigMapTable<DOMVisitAfter> assemblyVisitAfters = new ContentHandlerConfigMapTable<DOMVisitAfter>();
    /**
	 * Processing Visit Befores.
	 */
	private ContentHandlerConfigMapTable<DOMVisitBefore> processingVisitBefores = new ContentHandlerConfigMapTable<DOMVisitBefore>();
    /**
	 * Processing Visit Afters.
	 */
	private ContentHandlerConfigMapTable<DOMVisitAfter> processingVisitAfters = new ContentHandlerConfigMapTable<DOMVisitAfter>();
    /**
	 * Table of SerializationUnit instances keyed by selector. Each table entry
	 * contains a single SerializationUnit instances.
	 */
	private ContentHandlerConfigMapTable<SerializationUnit> serializationUnitTable = new ContentHandlerConfigMapTable<SerializationUnit>();
    /**
     * SAX Visit Befores.
     */
    private ContentHandlerConfigMapTable<SAXVisitBefore> saxVisitBefores = new ContentHandlerConfigMapTable<SAXVisitBefore>();
    /**
     * SAX Visit Afters.
     */
    private ContentHandlerConfigMapTable<SAXVisitAfter> saxVisitAfters = new ContentHandlerConfigMapTable<SAXVisitAfter>();
    /**
     * Config builder events list.
     */
    private List<ConfigBuilderEvent> configBuilderEvents = new ArrayList<ConfigBuilderEvent>();

    /**
	 * DTD for the associated device.
	 */
	private DTDObjectContainer dtd;
    private int elementHandlerCount = 0;
    private int saxElementHandlerCount = 0;

    private int domElementHandlerCount = 0;
    /**
     * Stream filter type config parameter.
     */
    public static final String STREAM_FILTER_TYPE = "stream.filter.type";

    /**
     * Filter type enumeration.
     */
    private static enum StreamFilterType {
        SAX,
        DOM
    }

    /**
	 * Private (hidden) constructor.
     * @param profileSet Profile set.
	 * @param applicationContext Container context.
	 */
	private ContentDeliveryConfigBuilder(ProfileSet profileSet, ApplicationContext applicationContext) {
		this.profileSet = profileSet;
		this.applicationContext = applicationContext;
    }
	
	/**
	 * Get the ContentDeliveryConfig instance for the specified profile set.
	 * @param profileSet The profile set with which this delivery config is associated.
	 * @param applicationContext Application context.
	 * @return The ContentDeliveryConfig instance for the named table.
	 */
	public static ContentDeliveryConfig getConfig(ProfileSet profileSet, ApplicationContext applicationContext) {
		ContentDeliveryConfig config;
		LinkedHashMap<String, ContentDeliveryConfig> configTable;
		
		if(profileSet == null) {
			throw new IllegalArgumentException("null 'profileSet' arg passed in method call.");
		} else if(applicationContext == null) {
			throw new IllegalArgumentException("null 'applicationContext' arg passed in method call.");
		}

		// Get the delivery config config from container context.
        configTable = getDeliveryConfigTable(applicationContext);
        if(configTable == null) {
            synchronized(ContentDeliveryConfigBuilder.class) {
                // Try again, just in case we have 1+ threads firing...
                configTable = getDeliveryConfigTable(applicationContext);
                if(configTable == null) {
                    configTable = new LinkedHashMap<String, ContentDeliveryConfig>();
                    applicationContext.setAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY, configTable);
                }
            }
        }
		// Get the delivery config instance for this UAContext
		config = configTable.get(profileSet.getBaseProfile());
		if(config == null) {
            synchronized(ContentDeliveryConfigBuilder.class) {
                // Try again, just in case we have 1+ threads firing on the same profile...
                config = configTable.get(profileSet.getBaseProfile());
                if(config == null) {
                    ContentDeliveryConfigBuilder configBuilder = new ContentDeliveryConfigBuilder(profileSet, applicationContext);
                    configBuilder.load();
                    config = configBuilder.createConfig();
                    configTable.put(profileSet.getBaseProfile(), config);
                }
            }
        }
		
		return config;
	}

    private ContentDeliveryConfig createConfig() {
        StreamFilterType filterType = getStreamFilterType();

        configBuilderEvents.add(new ConfigBuilderEvent("SAX/DOM support characteristics of the Resource Configuration map:\n" + getResourceFilterCharacteristics()));
        configBuilderEvents.add(new ConfigBuilderEvent("Using Stream Filter Type: " + filterType));

        if(filterType == StreamFilterType.DOM) {
            DOMContentDeliveryConfig domConfig = new DOMContentDeliveryConfig();

            logger.debug("Using the DOM Stream Filter.");
            domConfig.setAssemblyVisitBefores(assemblyVisitBefores);
            domConfig.setAssemblyVisitAfters(assemblyVisitAfters);
            domConfig.setProcessingVisitBefores(processingVisitBefores);
            domConfig.setProcessingVisitAfters(processingVisitAfters);
            domConfig.setSerailizationVisitors(serializationUnitTable);
            domConfig.setApplicationContext(applicationContext);
            domConfig.setSmooksResourceConfigurations(resourceConfigTable);
            domConfig.setDtd(dtd);
            domConfig.getConfigBuilderEvents().addAll(configBuilderEvents);

            return domConfig;
        } else {
            SAXContentDeliveryConfig saxConfig = new SAXContentDeliveryConfig();

            logger.debug("Using the SAX Stream Filter.");
            saxConfig.setVisitBefores(saxVisitBefores);
            saxConfig.setVisitAfters(saxVisitAfters);
            saxConfig.setApplicationContext(applicationContext);
            saxConfig.setSmooksResourceConfigurations(resourceConfigTable);
            saxConfig.setDtd(dtd);
            saxConfig.getConfigBuilderEvents().addAll(configBuilderEvents);
            
            saxConfig.optimizeConfig();

            return saxConfig;
        }
    }

    private StreamFilterType getStreamFilterType() {
        StreamFilterType filterType;

        if(logger.isDebugEnabled()) {
            logger.debug("SAX/DOM support characteristics of the Resource Configuration map:\n" + getResourceFilterCharacteristics());
        }

        if(saxElementHandlerCount == elementHandlerCount && domElementHandlerCount == elementHandlerCount) {
            // All element handlers support SAX and DOM... must select one then...
            Parameter filterTypeParam = ParameterAccessor.getParameter(STREAM_FILTER_TYPE, resourceConfigTable);

            if(filterTypeParam == null) {
                filterType = StreamFilterType.DOM;
                logger.info("All configured XML Element Content Handler resource configurations can be " +
                        "applied using the SAX or DOM Stream Filter.  Defaulting to DOM Filter.  Set '" + ParameterAccessor.GLOBAL_PARAMETERS + ":"
                        + STREAM_FILTER_TYPE + "'.  Turn on debug logging for more info.");
                logger.debug("You can explicitly select the Filter type as follows:\n" +
                        "\t\t<resource-config selector=\"" + ParameterAccessor.GLOBAL_PARAMETERS + "\">\n" +
                        "\t\t\t<param name=\"" + STREAM_FILTER_TYPE + "\">SAX/DOM</param>\n" +
                        "\t\t</resource-config>");
            } else if(filterTypeParam.getValue().equalsIgnoreCase(StreamFilterType.DOM.name())) {
                filterType = StreamFilterType.DOM;
            } else if(filterTypeParam.getValue().equalsIgnoreCase(StreamFilterType.SAX.name())) {
                filterType = StreamFilterType.SAX;
            } else {
                throw new SmooksException("Invalid '" + STREAM_FILTER_TYPE + "' configuration parameter value of '" + filterTypeParam + "'.  Must be 'SAX' or 'DOM'.");
            }
        } else if(domElementHandlerCount == elementHandlerCount) {
            filterType = StreamFilterType.DOM;
        } else if(saxElementHandlerCount == elementHandlerCount) {
            filterType = StreamFilterType.SAX;
        } else {
            throw new SmooksException("Ambiguous Resource Configuration set.  All Element Content Handlers must support processing on the SAX and/or DOM Filter:\n" + getResourceFilterCharacteristics());
        }
        
        return filterType;
    }

    /**
     * Logging support function.
     * @return Verbose characteristics string.
     */
    private String getResourceFilterCharacteristics() {
        StringBuffer stringBuf = new StringBuffer();
        List<ContentHandler> printedHandlers = new ArrayList<ContentHandler>();

        stringBuf.append("\t\tDOM   SAX    Resource  ('x' equals supported)\n");
        stringBuf.append("\t\t---------------------------------------------------------------------\n");

        printHandlerCharacteristics(assemblyVisitBefores, stringBuf, printedHandlers);
        printHandlerCharacteristics(assemblyVisitAfters, stringBuf, printedHandlers);
        printHandlerCharacteristics(processingVisitBefores, stringBuf, printedHandlers);
        printHandlerCharacteristics(processingVisitAfters, stringBuf, printedHandlers);
        printHandlerCharacteristics(serializationUnitTable, stringBuf, printedHandlers);
        printHandlerCharacteristics(saxVisitBefores, stringBuf, printedHandlers);
        printHandlerCharacteristics(saxVisitAfters, stringBuf, printedHandlers);

        stringBuf.append("\n\n");

        return stringBuf.toString();
    }

    private <U extends ContentHandler> void printHandlerCharacteristics(ContentHandlerConfigMapTable<U> table, StringBuffer stringBuf, List<ContentHandler> printedHandlers) {
        Collection<List<ContentHandlerConfigMap<U>>> map = table.getTable().values();

        for (List<ContentHandlerConfigMap<U>> mapList : map) {
            for (ContentHandlerConfigMap<U> configMap : mapList) {
                ContentHandler handler = configMap.getContentHandler();
                boolean domSupported = isDOMContentHandler(handler);
                boolean saxSupported = isSAXContentHandler(handler);

                if(printedHandlers.contains(handler)) {
                    continue;
                } else {
                    printedHandlers.add(handler);
                }

                stringBuf.append("\t\t " + (domSupported?"x":" ") +
                        "     " + (saxSupported?"x":" ") +
                        "     " + configMap.getResourceConfig() + "\n");
            }
        }
    }

    private static LinkedHashMap<String, ContentDeliveryConfig> getDeliveryConfigTable(ApplicationContext applicationContext) {
        return (LinkedHashMap) applicationContext.getAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY);
    }

    /**
	 * Build the ContentDeliveryConfigBuilder for the specified device.
	 * <p/>
	 * Creates the buildTable instance and populates it with the ProcessingUnit matrix
	 * for the specified device.
	 */
	private void load() {
        resourceConfigsList.clear();
        resourceConfigsList.addAll(Arrays.asList(applicationContext.getStore().getSmooksResourceConfigurations(profileSet)));

		// Build and sort the resourceConfigTable table - non-transforming elements.
		buildSmooksResourceConfigurationTable(resourceConfigsList);
		sortSmooksResourceConfigurations(resourceConfigTable);
		
		// If there's a DTD for this device, get it and add it to the DTDStore.
		List dtdSmooksResourceConfigurations = resourceConfigTable.get("dtd");
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

        // Extract the ContentDeliveryUnits and build the tables
        extractContentHandlers();

        if(logger.isDebugEnabled()) {
            logResourceConfig();
        }
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

        addResourceConfiguration(target, config);
    }

    /**
     * Add the config for the specified element.
     * @param element The element to which the config is to be added.
     * @param resourceConfiguration The Object to be added.
     */
    private void addResourceConfiguration(String element, SmooksResourceConfiguration resourceConfiguration) {
        // Add it to the unsorted list...
        if(!resourceConfigsList.contains(resourceConfiguration)) {
            resourceConfigsList.add(resourceConfiguration);
        }

        // Add it to the sorted resourceConfigTable...
        List elementConfigList = resourceConfigTable.get(element);
        if(elementConfigList == null) {
            elementConfigList = new Vector();
            resourceConfigTable.put(element, elementConfigList);
        }
        if(!elementConfigList.contains(resourceConfiguration)) {
            elementConfigList.add(resourceConfiguration);
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
						addResourceConfiguration(elements[i], resourceConfig);
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
	private void sortSmooksResourceConfigurations(Map<String, List<SmooksResourceConfiguration>> table) {
        Parameter sortParam = ParameterAccessor.getParameter("sort.resources", table);
        if(sortParam != null && sortParam.getValue().trim().equalsIgnoreCase("true")) {
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
    }

    /**
	 * Extract the ContentHandler instances from the SmooksResourceConfiguration table and add them to
	 * their respective tables.
	 */
	private void extractContentHandlers() {
		ContentHandlerExtractionStrategy cduStrategy = new ContentHandlerExtractionStrategy(applicationContext);
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

    private void logExecutionEvent(SmooksResourceConfiguration resourceConfig, String message) {
        configBuilderEvents.add(new ConfigBuilderEvent(resourceConfig, message));
    }

    private boolean isDOMContentHandler(ContentHandler contentHandler) {
        return (contentHandler instanceof DOMVisitBefore || contentHandler instanceof DOMVisitAfter || contentHandler instanceof SerializationUnit);
    }

    private boolean isSAXContentHandler(ContentHandler contentHandler) {
        // Intentionally not checking for SAXVisitChildren.  Must be incorporated into a visit before or after...
        return (contentHandler instanceof SAXVisitBefore || contentHandler instanceof SAXVisitAfter);
    }

    /**
	 * ContentHandler extraction strategy.
	 * @author tfennelly
	 */
	private final class ContentHandlerExtractionStrategy implements SmooksResourceConfigurationStrategy {
		
        private SmooksResourceConfigurationStore store;

        public ContentHandlerExtractionStrategy(ApplicationContext applicationContext) {
            store = applicationContext.getStore();
        }

        public void applyStrategy(String elementName, SmooksResourceConfiguration resourceConfig) {
            applyCDUStrategy(elementName, resourceConfig);
        }

        public boolean applyCDUStrategy(String elementName, SmooksResourceConfiguration resourceConfig) {
			ContentHandlerFactory creator;

			// Try it as a Java class before trying anything else.  This is to
			// accomodate specification of the class in the standard 
			// Java form e.g. java.lang.String Vs java/lang/String.class
            if(resourceConfig.isJavaContentHandler()) {
    			try {                
    				creator = store.getContentHandlerFactory("class");
    				if(addCDU(elementName, resourceConfig, creator)) {
    					// Job done - it's a CDU and we've added it!
    					return true;
    				}
    			} catch (UnsupportedContentHandlerTypeException e) {
    				throw new IllegalStateException("No ContentHandlerFactory configured (IoC) for type 'class' (Java).");
    			} catch (InstantiationException e) {
                    // Ignore it again - not a proper Java CDU - continue on, may be a different type...
                }
            }

            // Get the resource type and "try" creating a ContentHandlerFactory for that resource
            // type.
            String restype = resourceConfig.getResourceType();
            creator = tryCreateCreator(restype);
			
            // If we have a creator but it's the JavaContentHandlerFactory we ignore it because
            // we know the class in question does not implement ContentHandler.  We know because
            // we tried this above.
            if(creator != null) {
                if(!(creator instanceof JavaContentHandlerFactory)) {
                    try {
                        return addCDU(elementName, resourceConfig, creator);
                    } catch (InstantiationException e) {
                        logger.warn("ContentHandler creation failure.", e);
                    }
                }
            } else {
				// Just ignore it - something else will use it (hopefully)
                if(restype != null) {
                    logExecutionEvent(resourceConfig, "Unable to create ContentHandler class instance for resource.  " +
                            "This is probably because there's no " + ContentHandlerFactory.class.getSimpleName()  + " implementation for resource " +
                            "type '" + restype + "' available on the classpath.");
                }
            }

            return false;
        }
        
        /**
         * Try create the CDU creator for the specified resource type.
         * <p/>
         * Return null if unsuccessful i.e. no exceptions.
         * @param restype The resource type.
         * @return The appropriate CDU creator instance, or null if there is none.
         */
        private ContentHandlerFactory tryCreateCreator(String restype) {
			ContentHandlerFactory creator;

			try {
				if(restype == null || restype.trim().equals("")) {
					logger.debug("Request to attempt ContentHandlerFactory creation based on a null/empty resource type.");
					return null;
				}
				creator = store.getContentHandlerFactory(restype);
			} catch (UnsupportedContentHandlerTypeException e) {
				return null;
			}
			
			return creator;
        }

        /**
		 * Add a {@link ContentHandler} for the specified element and configuration.
		 * @param elementName Element name against which to associate the CDU.
		 * @param resourceConfig Configuration.
		 * @param handlerFactory CDU Creator class.
		 * @throws InstantiationException Failed to instantia
         * @return True if the CDU was added, otherwise false. 
		 */
		private boolean addCDU(String elementName, SmooksResourceConfiguration resourceConfig, ContentHandlerFactory handlerFactory) throws InstantiationException {
			ContentHandler contentHandler;

			// Create the ContentHandler.
			try {
				contentHandler = handlerFactory.create(resourceConfig);
            } catch(SmooksConfigurationException e) {
                throw e;
            } catch(Throwable thrown) {
                String message = "ContentHandlerFactory [" + handlerFactory.getClass().getName()  + "] unable to create resource processing instance for resource [" + resourceConfig + "]. ";

                if(logger.isDebugEnabled()) {
                    logger.warn(message, thrown);
                } else {
                    logger.warn(message + thrown.getMessage());
				}
                configBuilderEvents.add(new ConfigBuilderEvent(resourceConfig, message, thrown));
                
                return false;
			}

            if(isSAXContentHandler(contentHandler) || isDOMContentHandler(contentHandler)) {
                elementHandlerCount++;

                if(isSAXContentHandler(contentHandler)) {
                    saxElementHandlerCount++;
                    if(contentHandler instanceof SAXVisitBefore && visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                        saxVisitBefores.addMapping(elementName, resourceConfig, (SAXVisitBefore) contentHandler);
                    }
                    if(contentHandler instanceof SAXVisitAfter && visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                        saxVisitAfters.addMapping(elementName, resourceConfig, (SAXVisitAfter) contentHandler);
                    }
                    logExecutionEvent(resourceConfig, "Added as a SAX resource.");
                }

                if(isDOMContentHandler(contentHandler)) {
                    domElementHandlerCount++;

                    if(contentHandler instanceof SerializationUnit) {
                        serializationUnitTable.addMapping(elementName, resourceConfig, (SerializationUnit) contentHandler);
                        logExecutionEvent(resourceConfig, "Added as a DOM " + SerializationUnit.class.getSimpleName() + " resource.");
                    } else {
                        Phase phaseAnnotation = contentHandler.getClass().getAnnotation(Phase.class);
                        String visitPhase = resourceConfig.getStringParameter("VisitPhase", VisitPhase.PROCESSING.toString());

                        if(phaseAnnotation != null && phaseAnnotation.value() == VisitPhase.ASSEMBLY) {
                            // It's an assembly unit...
                            if(contentHandler instanceof DOMVisitBefore && visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                                assemblyVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) contentHandler);
                            }
                            if(contentHandler instanceof DOMVisitAfter && visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                                assemblyVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) contentHandler);
                            }
                            logExecutionEvent(resourceConfig, "Added as a DOM Assembly Phase resource.");
                        } else if (visitPhase.equalsIgnoreCase(VisitPhase.ASSEMBLY.toString())) {
                            // It's an assembly unit...
                            if(contentHandler instanceof DOMVisitBefore && visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                                assemblyVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) contentHandler);
                            }
                            if(contentHandler instanceof DOMVisitAfter && visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                                assemblyVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) contentHandler);
                            }
                            logExecutionEvent(resourceConfig, "Added as a DOM Assembly Phase resource.");
                        } else {
                            // It's a processing unit...
                            if(contentHandler instanceof DOMVisitBefore && visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                                processingVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) contentHandler);
                            }
                            if(contentHandler instanceof DOMVisitAfter && visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                                processingVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) contentHandler);
                            }
                            logExecutionEvent(resourceConfig, "Added as a DOM Processing Phase resource.");
                        }
                    }
                }
            } else if(!(contentHandler instanceof ConfigurationExpander)) {
                // It's not a ContentHandler type we care about!  Leave for now - whatever's using it
                // can instantiate it itself.
                return false;
            }

            // Content delivery units are allowed to dynamically add new configurations...
            if(contentHandler instanceof ConfigurationExpander) {
                List<SmooksResourceConfiguration> additionalConfigs = ((ConfigurationExpander)contentHandler).expandConfigurations();
                if(additionalConfigs != null && !additionalConfigs.isEmpty()) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("Adding expansion resource configurations created by: " + resourceConfig);
                        for (SmooksResourceConfiguration additionalConfig : additionalConfigs) {
                            logger.debug("\tAdding expansion resource configuration: " + additionalConfig);
                        }
                    }
                    processExpansionConfigurations(additionalConfigs);
                }
            }

            return true;
		}

        /**
         * Process the supplied expansion configurations.
         * @param additionalConfigs Expansion configs.
         */
        private void processExpansionConfigurations(List<SmooksResourceConfiguration> additionalConfigs) {
            for(SmooksResourceConfiguration config : additionalConfigs) {
                String targetElement = config.getTargetElement();

                // Try adding it as a ContentHandler instance...
                if(!applyCDUStrategy(targetElement, config)) {
                    // Else just add it to the main list...
                    addResourceConfiguration(config);
                }
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
            for (int i = 0; i < resourceConfigsList.size(); i++) {
                SmooksResourceConfiguration smooksResourceConfiguration = resourceConfigsList.get(i);
                strategy.applyStrategy(smooksResourceConfiguration.getTargetElement(), smooksResourceConfiguration);
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

    protected static boolean visitBeforeAnnotationsOK(SmooksResourceConfiguration resourceConfig, ContentHandler contentHandler) {
        Class<? extends ContentHandler> handlerClass = contentHandler.getClass();

        try {
            if(contentHandler instanceof SAXVisitBefore) {
                Method interfaceMethod = SAXVisitBefore.class.getMethods()[0];
                Method handlerVisitBeforeMethod = handlerClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

                if(!visitAnnotationsOK(resourceConfig, handlerClass, handlerVisitBeforeMethod)) {
                    return false;
                }
            }
            if(contentHandler instanceof DOMVisitBefore) {
                Method interfaceMethod = DOMVisitBefore.class.getMethods()[0];
                Method handlerVisitBeforeMethod = handlerClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

                if(!visitAnnotationsOK(resourceConfig, handlerClass, handlerVisitBeforeMethod)) {
                    return false;
                }
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unexpected runtime exception. If the class implements the interface, then it should implement the interface methods, unless it's abstract.", e);
        }

        return true;
    }

    protected static boolean visitAfterAnnotationsOK(SmooksResourceConfiguration resourceConfig, ContentHandler contentHandler) {
        Class<? extends ContentHandler> handlerClass = contentHandler.getClass();

        try {
            if(contentHandler instanceof SAXVisitAfter) {
                Method interfaceMethod = SAXVisitAfter.class.getMethods()[0];
                Method handlerVisitBeforeMethod = handlerClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

                if(!visitAnnotationsOK(resourceConfig, handlerClass, handlerVisitBeforeMethod)) {
                    return false;
                }
            }
            if(contentHandler instanceof DOMVisitAfter) {
                Method interfaceMethod = DOMVisitAfter.class.getMethods()[0];
                Method handlerVisitBeforeMethod = handlerClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

                if(!visitAnnotationsOK(resourceConfig, handlerClass, handlerVisitBeforeMethod)) {
                    return false;
                }
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unexpected runtime exception. If the class implements the interface, then it should implement the interface methods, unless it's abstract.", e);
        }

        return true;
    }

    private static boolean visitAnnotationsOK(SmooksResourceConfiguration resourceConfig, Class<? extends ContentHandler> handlerClass, Method handlerVisitMethod) {
        VisitIf visitIf = handlerVisitMethod.getAnnotation(VisitIf.class);
        VisitIfNot visitIfNot = handlerVisitMethod.getAnnotation(VisitIfNot.class);

        if(visitIf != null) {
            String paramVal = resourceConfig.getStringParameter(visitIf.param(), visitIf.defaultVal());
            if(!paramVal.equals(visitIf.value())) {
                logger.debug("Not calling '" + handlerVisitMethod.toString() + "' on handler '" + handlerClass + "'. <param> '" + visitIf.param() + "' value equals '" + paramVal + "' and not '" + visitIf.value() + "'.");
                return false;
            }
        }
        if(visitIfNot != null) {
            String paramVal = resourceConfig.getStringParameter(visitIfNot.param(), visitIfNot.defaultVal());
            if(paramVal.equals(visitIfNot.value())) {
                logger.debug("Not calling '" + handlerVisitMethod.toString() + "' on handler '" + handlerClass + "'. <param> '" + visitIfNot.param() + "' value equals '" + paramVal + "'.");
                return false;
            }
        }

        return true;
    }
}
