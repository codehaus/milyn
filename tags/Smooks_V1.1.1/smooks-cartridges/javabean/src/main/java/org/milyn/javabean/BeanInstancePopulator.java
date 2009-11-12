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
package org.milyn.javabean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXUtil;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.expression.MVELExpressionEvaluator;
import org.milyn.javabean.BeanRuntimeInfo.Classification;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdRegister;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Bean instance populator visitor class.
 * <p/>
 * Targeted via {@link org.milyn.javabean.BeanPopulator} expansion configuration.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
@VisitBeforeReport(condition = "parameters.containsKey('wireBeanIdName')",
        summary = "Create bean lifecycle observer for bean <b>${resource.parameters.wireBeanId!'undefined'}</b>.",
        detailTemplate = "reporting/BeanInstancePopulatorReport_Before.html")
@VisitAfterReport(condition = "!parameters.containsKey('wireBeanIdName')",
        summary = "Populating <b>${resource.parameters.beanId}</b> with a value from this element.",
        detailTemplate = "reporting/BeanInstancePopulatorReport_After.html")
public class BeanInstancePopulator implements DOMElementVisitor, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(BeanInstancePopulator.class);

    private static String EC_DEFAULT_EXTEND_LIFECYCLE = BeanInstancePopulator.class + "#" + BeanPopulator.GLOBAL_DEFAULT_EXTEND_LIFECYCLE;

    private String id;

    @ConfigParam(name="beanId")
    private String beanIdName;

    @ConfigParam(name="wireBeanId", defaultVal = AnnotationConstants.NULL_STRING)
    private String wireBeanIdName;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private MVELExpressionEvaluator expression;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String property;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String setterMethod;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String valueAttributeName;

    @ConfigParam(name="type", defaultVal = AnnotationConstants.NULL_STRING)
    private String typeAlias;

    @ConfigParam(name="default", defaultVal = AnnotationConstants.NULL_STRING)
    private String defaultVal;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private Boolean extendLifecycle;

    @AppContext
    private ApplicationContext appContext;

    private BeanRepositoryManager beanRepositoryManager;

    private BeanId beanId;

    private BeanId wireBeanId;

    private BeanRuntimeInfo beanRuntimeInfo;
    private BeanRuntimeInfo wiredBeanRuntimeInfo;

    private Method propertySetterMethod;
    private boolean checkedForSetterMethod;
    private boolean isAttribute = true;
    private DataDecoder decoder;
    private String mapKeyAttribute;

    private boolean beanWiring;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	buildId();

    	beanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(beanIdName, appContext);
        beanWiring = wireBeanIdName != null;
        isAttribute = (valueAttributeName != null);

        beanRepositoryManager = BeanRepositoryManager.getInstance(appContext);

        BeanIdRegister beanIdRegister = beanRepositoryManager.getBeanIdRegister();

        beanId = beanIdRegister.getBeanId(beanIdName);

        if (setterMethod == null && property == null ) {
        	if(beanWiring && (beanRuntimeInfo.getClassification() == Classification.NON_COLLECTION || beanRuntimeInfo.getClassification() == Classification.MAP_COLLECTION)) {
        		property = wireBeanIdName;
        	} else if(beanRuntimeInfo.getClassification() == Classification.NON_COLLECTION){
        		throw new SmooksConfigurationException("Binding configuration for beanIdName='" + beanIdName + "' must contain " +
                    "either a 'property' or 'setterMethod' attribute definition, unless the target bean is a Collection/Array." +
                    "  Bean is type '" + beanRuntimeInfo.getPopulateType().getName() + "'.");
        	}
        }

        if(beanRuntimeInfo.getClassification() == Classification.MAP_COLLECTION && property != null) {
            property = property.trim();
            if(property.length() > 1 && property.charAt(0) == '@') {
                mapKeyAttribute = property.substring(1);
            }
        }

        if(logger.isDebugEnabled()) {
        	logger.debug("Bean Instance Populator created for [" + beanIdName + "].  property=" + property);
        }
    }

    private void buildId() {
    	StringBuilder idBuilder = new StringBuilder();
    	idBuilder.append(BeanInstancePopulator.class.getName());
    	idBuilder.append("#");
    	idBuilder.append(beanIdName);

    	if(property != null) {
    		idBuilder.append("#")
    				 .append(property);
    	}
    	if(setterMethod != null) {
    		idBuilder.append("#")
    				 .append(setterMethod)
    				 .append("()");
    	}
    	if(wireBeanIdName != null) {
    		idBuilder.append("#")
    				.append(wireBeanIdName);
    	}

    	id = idBuilder.toString();
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
    	checkBeanExists(executionContext);

    	if(beanWiring) {
        	bindBeanValue(executionContext);
        } else if(isAttribute) {
            // Bind attribute (i.e. selectors with '@' prefix) values on the visitBefore...
            bindDomDataValue(element, executionContext);
        }
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
    	checkBeanExists(executionContext);

    	if(!beanWiring && !isAttribute) {
            bindDomDataValue(element, executionContext);
    	}
    	if(beanWiring) {
    		extendLifecycle(executionContext);
    	}
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    	checkBeanExists(executionContext);

        if(beanWiring) {
        	bindBeanValue(executionContext);
        } else if(!beanWiring && !isAttribute) {
            element.setCache(new TrackedStringWriter());
        } else if(isAttribute) {
            // Bind attribute (i.e. selectors with '@' prefix) values on the visitBefore...
            bindSaxDataValue(element, executionContext);
        }
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!beanWiring && !isAttribute) {
            childText.toWriter((Writer) element.getCache());
        }
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    	checkBeanExists(executionContext);

    	if(!beanWiring && !isAttribute) {
            bindSaxDataValue(element, executionContext);
    	}
    	if(beanWiring) {
    		extendLifecycle(executionContext);
    	}
    }

    private void checkBeanExists(ExecutionContext executionContext) {

    	final BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);
    	if(beanRepository.getBean(beanId) == null) {
    		throw new SmooksConfigurationException("Can't populate object '"  + beanRuntimeInfo.getPopulateType().getName() + "' with bean id '" + beanId + "' because there is no object in the bean context under that bean id.");
    	}
    }

    private void bindDomDataValue(Element element, ExecutionContext executionContext) {
        String dataString;

        if (isAttribute) {
            dataString = DomUtils.getAttributeValue(element, valueAttributeName);
        } else {
            dataString = DomUtils.getAllText(element, false);
        }

        String mapPropertyName;
        if(mapKeyAttribute != null) {
            mapPropertyName = DomUtils.getAttributeValue(element, mapKeyAttribute);
            if(mapPropertyName == null) {
                mapPropertyName = DomUtils.getName(element);
            }
        } else if(property != null) {
            mapPropertyName = property;
        } else {
            mapPropertyName = DomUtils.getName(element);
        }

        if(expression != null) {
            bindExpressionValue(mapPropertyName, executionContext);
        } else {
            populateAndSetPropertyValue(mapPropertyName, dataString, executionContext);
        }
    }

    private void bindSaxDataValue(SAXElement element, ExecutionContext executionContext) {
        String dataString;

        if (isAttribute) {
            dataString = SAXUtil.getAttribute(valueAttributeName, element.getAttributes());
        } else {
            dataString = element.getCache().toString();
        }

        String mapPropertyName;
        if(mapKeyAttribute != null) {
            mapPropertyName = SAXUtil.getAttribute(mapKeyAttribute, element.getAttributes(), null);
            if(mapPropertyName == null) {
                mapPropertyName = element.getName().getLocalPart();
            }
        } else if(property != null) {
            mapPropertyName = property;
        } else {
            mapPropertyName = element.getName().getLocalPart();
        }

        if(expression != null) {
            bindExpressionValue(mapPropertyName, executionContext);
        } else {
            populateAndSetPropertyValue(mapPropertyName, dataString, executionContext);
        }
    }

    private BeanId getWireBeanId() {
    	if(wireBeanId == null) {
    		wireBeanId = beanRepositoryManager.getBeanIdRegister().getBeanId(wireBeanIdName);
    	}

		if(wireBeanId == null) {
            wireBeanId = beanRepositoryManager.getBeanIdRegister().register(wireBeanIdName);
        }

        return wireBeanId;
    }

    private void bindBeanValue(final ExecutionContext executionContext) {
    	final BeanId targetBeanId = getWireBeanId();

    	final BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);

    	Object bean = beanRepository.getBean(targetBeanId);
        if(bean == null) {

            // Register the observer which looks for the creation of the selected bean via its beanIdName. When this observer is triggered then
            // we look if we got something we can set immediatly or that we got an array collection. For an array collection we need the array representation
            // and not the list representation. So we register and observer wo looks for the change from the list to the array
        	beanRepository.addBeanLifecycleObserver(targetBeanId, BeanLifecycle.BEGIN, getId(), false, new BeanRepositoryLifecycleObserver(){

                public void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event) {

                    BeanRuntimeInfo wiredBeanRI = getWiredBeanRuntimeInfo();

                    beanRepository.associateLifecycles(beanId , targetBeanId);

                    if(wiredBeanRI != null && wiredBeanRI.getClassification() == Classification.ARRAY_COLLECTION ) {

                        // Register an observer which looks for the change that the mutable list of the selected bean gets converted to an array. We
                        // can then set this array
                    	beanRepository.addBeanLifecycleObserver( targetBeanId, BeanLifecycle.CHANGE, getId(), true, new BeanRepositoryLifecycleObserver() {
                            public void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event) {

                                populateAndSetPropertyValue(property, event.getBean(), executionContext);

                            }
                        });

                    } else {
                        populateAndSetPropertyValue(property, event.getBean(), executionContext);
                    }
                }

            });
        } else {
            populateAndSetPropertyValue(property, bean, executionContext);
        }
	}

    /**
     * Checks if we need to stop listening for the bean begin lifecycle.
     * If we need to stop listening then it will remove the lifecycle observer.
     *
     * @param executionContext
     */
    private void extendLifecycle(ExecutionContext executionContext) {

    	Boolean doExtendLifecycle = extendLifecycle;
    	if(doExtendLifecycle == null) {
    		doExtendLifecycle = isDefaultExtendLifecycle(executionContext);
        }
    	if(!doExtendLifecycle) {
    		final BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);

    		beanRepository.removeBeanLifecycleObserver(getWireBeanId(), BeanLifecycle.BEGIN, getId());
    	}
    }

    /**
     * This method efficiently returns the global configuration {@link BeanPopulator.GLOBAL_WIRE_AFTER_ELEMENT}.
     *
     * @param executionContext
     * @return the global parameter for wire after element
     */
    private boolean isDefaultExtendLifecycle(ExecutionContext executionContext) {

    	//Look in the execution context to see if we can find the property there
    	Boolean defaultExtendLifecycle = (Boolean) executionContext.getAttribute(EC_DEFAULT_EXTEND_LIFECYCLE);

    	//if we can't find it there then we need to search the global-parameters and add the result
    	//to the execution context
    	if(defaultExtendLifecycle == null) {

    		String wireAfterElementStr = executionContext.getConfigParameter(BeanPopulator.GLOBAL_DEFAULT_EXTEND_LIFECYCLE, "false");

    		defaultExtendLifecycle = Boolean.parseBoolean(wireAfterElementStr.trim());

    		executionContext.setAttribute(EC_DEFAULT_EXTEND_LIFECYCLE, defaultExtendLifecycle);
    	}
    	return defaultExtendLifecycle;
    }

    private void bindExpressionValue(String mapPropertyName, ExecutionContext executionContext) {
        Map<String, Object> beanMap = BeanRepositoryManager.getBeanRepository(executionContext).getBeanMap();
        Object dataObject = expression.getValue(beanMap);

        if(dataObject instanceof String) {
            populateAndSetPropertyValue(mapPropertyName, (String) dataObject, executionContext);
        } else {
            populateAndSetPropertyValue(mapPropertyName, dataObject, executionContext);
        }
    }


    private void populateAndSetPropertyValue(String mapPropertyName, String dataString, ExecutionContext executionContext) {

        Object dataObject = decodeDataString(dataString, executionContext);

        populateAndSetPropertyValue(mapPropertyName, dataObject, executionContext);

    }

    @SuppressWarnings("unchecked")
	private void populateAndSetPropertyValue(String mapPropertyName, Object dataObject, ExecutionContext executionContext) {
    	if ( dataObject == null )
    	{
    		return;
    	}

        Object bean = BeanRepositoryManager.getBeanRepository(executionContext).getBean(beanId);

        Classification beanType = beanRuntimeInfo.getClassification();

        createPropertySetterMethod(bean, dataObject.getClass());

        // Set the data on the bean...
        try {
            if(propertySetterMethod != null) {
            	propertySetterMethod.invoke(bean, dataObject);

            } else if(beanType == Classification.MAP_COLLECTION) {
                ((Map)bean).put(mapPropertyName, dataObject);
            } else if(beanType == Classification.ARRAY_COLLECTION || beanType == Classification.COLLECTION_COLLECTION) {
                ((Collection)bean).add(dataObject);
            } else if(propertySetterMethod == null) {
            	if(setterMethod != null) {
                    throw new SmooksConfigurationException("Bean [" + beanIdName + "] configuration invalid.  Bean setter method [" + setterMethod + "(" + dataObject.getClass().getName() + ")] not found on type [" + beanRuntimeInfo.getPopulateType().getName() + "].  You may need to set a 'decoder' on the binding config.");
                } else if(property != null) {
                    throw new SmooksConfigurationException("Bean [" + beanIdName + "] configuration invalid.  Bean setter method [" + BeanUtils.toSetterName(property) + "(" + dataObject.getClass().getName() + ")] not found on type [" + beanRuntimeInfo.getPopulateType().getName() + "].  You may need to set a 'decoder' on the binding config.");
                }
            }
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + BeanUtils.toSetterName(property) + "] on bean instance class type [" + bean.getClass() + "].", e);
        } catch (InvocationTargetException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + BeanUtils.toSetterName(property) + "] on bean instance class type [" + bean.getClass() + "].", e);
        }
    }

    private void createPropertySetterMethod(Object bean, Class<?> parameter) {

    	if (!checkedForSetterMethod && propertySetterMethod == null) {
            String methodName = null;
        	if(setterMethod != null && !setterMethod.trim().equals("")) {
        		methodName = setterMethod;
            } else if(property != null && !property.trim().equals("")) {
            	methodName = BeanUtils.toSetterName(property);
            }

        	if(methodName != null) {
        		propertySetterMethod = createPropertySetterMethod(bean, methodName, parameter);
        	}

        	checkedForSetterMethod = true;
        }
    }

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    private synchronized Method createPropertySetterMethod(Object bean, String setterName, Class<?> setterParamType) {
        if (propertySetterMethod == null) {
            propertySetterMethod = BeanUtils.createSetterMethod(setterName, bean, setterParamType);
        }

        return propertySetterMethod;
    }

    private Object decodeDataString(String dataString, ExecutionContext executionContext) throws DataDecodeException {
        if((dataString == null || dataString.equals("")) && defaultVal != null) {
        	if(defaultVal.equals("null")) {
        		return null;
        	}
            dataString = defaultVal;
        }

        if (decoder == null) {
            decoder = getDecoder(executionContext);
        }

        return decoder.decode(dataString);
    }


	private DataDecoder getDecoder(ExecutionContext executionContext) throws DataDecodeException {
		@SuppressWarnings("unchecked")
		List decoders = executionContext.getDeliveryConfig().getObjects("decoder:" + typeAlias);

        if (decoders == null || decoders.isEmpty()) {
            decoder = DataDecoder.Factory.create(typeAlias);
        } else if (!(decoders.get(0) instanceof DataDecoder)) {
            throw new DataDecodeException("Configured decoder '" + typeAlias + ":" + decoders.get(0).getClass().getName() + "' is not an instance of " + DataDecoder.class.getName());
        } else {
            decoder = (DataDecoder) decoders.get(0);
        }

        return decoder;
    }


	private BeanRuntimeInfo getWiredBeanRuntimeInfo() {
		if(wiredBeanRuntimeInfo == null) {
            // Don't need to synchronize this.  Worse thing that can happen is we initialize it
            // more than once... no biggie...
            wiredBeanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(wireBeanIdName, appContext);
		}
		return wiredBeanRuntimeInfo;
	}

	private String getId() {
		return id;
	}

    /**
     * This is a specialized StringWriter that tracks the writes to make sure we don't
     * write the same buffer segment multiple times.
     *
     * See JIRA: http://jira.codehaus.org/browse/MILYN-238 
     */
    public static class TrackedStringWriter extends StringWriter {
        private char[] lastWriteBuf;
        private int lastWriteOff;
        private int lastWriteLen;
        private List<WriteRecord> writeTrackingList;

        public void write(char cbuf[], int off, int len) {
            if(cbuf == lastWriteBuf && off == lastWriteOff && len == lastWriteLen) {
                // we've already written this character buffer segment...
                return;
            }
            if(lastWriteBuf != null) {
                // We've written to this writer already and the new incoming buffer
                // is not the same as the last buffer...

                if(writeTrackingList == null) {
                    writeTrackingList = new ArrayList<WriteRecord>();
                } else {
                    if(isAlreadyWritten(cbuf, off, len)) {
                        // we've already written this character buffer segment...
                        return;
                    }
                }
                writeTrackingList.add(new WriteRecord(lastWriteBuf, lastWriteOff, lastWriteLen));
            }

            super.write(cbuf, off, len);
            lastWriteBuf = cbuf;
            lastWriteOff = off;
            lastWriteLen = len;
        }

        private boolean isAlreadyWritten(char cbuf[], int off, int len) {
            int trackListLen = writeTrackingList.size();
            
            for(int i = 0; i < trackListLen; i++) {
                WriteRecord listEntry = writeTrackingList.get(i);
                if(cbuf == listEntry.lastWriteBuf && off == listEntry.lastWriteOff && len == listEntry.lastWriteLen) {
                    // we've already written this character buffer segment...
                    return true;
                }
            }

            return false;
        }

        private class WriteRecord {
            private char[] lastWriteBuf;
            private int lastWriteOff;
            private int lastWriteLen;

            private WriteRecord(char[] lastWriteBuf, int lastWriteOff, int lastWriteLen) {
                this.lastWriteBuf = lastWriteBuf;
                this.lastWriteOff = lastWriteOff;
                this.lastWriteLen = lastWriteLen;
            }
        }
    }
}