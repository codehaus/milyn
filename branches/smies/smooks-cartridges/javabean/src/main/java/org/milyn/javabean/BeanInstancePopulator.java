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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXUtil;
import org.milyn.javabean.BeanRuntimeInfo.Classification;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Bean instance populator visitor class.
 * <p/>
 * Targeted via {@link org.milyn.javabean.BeanPopulator} expansion configuration.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public class BeanInstancePopulator implements DOMElementVisitor, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(BeanInstancePopulator.class);

    private String id;

    @ConfigParam
    private String beanId;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String selectedBeanId;

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

    @AppContext
    private ApplicationContext appContext;

    private BeanRuntimeInfo beanRuntimeInfo;
    private BeanRuntimeInfo selectedBeanRuntimeInfo;

    private Method propertySetterMethod;
    private boolean checkedForSetterMethod;
    private boolean isAttribute = true;
    private DataDecoder decoder;
    private String mapKeyAttribute;

    private boolean beanBinding;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	buildId();

    	beanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(beanId, appContext);
        beanBinding = selectedBeanId != null;
        isAttribute = (valueAttributeName != null);


        if (setterMethod == null && property == null ) {
        	if(beanBinding && (beanRuntimeInfo.getClassification() == Classification.NON_COLLECTION || beanRuntimeInfo.getClassification() == Classification.MAP_COLLECTION)) {
        		property = selectedBeanId;
        	} else if(beanRuntimeInfo.getClassification() == Classification.NON_COLLECTION){
        		throw new SmooksConfigurationException("Binding configuration for beanId='" + beanId + "' must contain " +
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

        logger.debug("Bean Instance Populator created for [" + beanId + "].  property=" + property);
    }

    private void buildId() {
    	StringBuilder idBuilder = new StringBuilder();
    	idBuilder.append(BeanInstancePopulator.class.getName());
    	idBuilder.append("#");
    	idBuilder.append(beanId);

    	if(property != null) {
    		idBuilder.append("#")
    				 .append(property);
    	}
    	if(setterMethod != null) {
    		idBuilder.append("#")
    				 .append(setterMethod)
    				 .append("()");
    	}
    	if(selectedBeanId != null) {
    		idBuilder.append("#")
    				.append(selectedBeanId);
    	}

    	id = idBuilder.toString();
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
    	visitBefore(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
    	if(!beanBinding) {

	    	String dataString;

	        if (isAttribute) {
	            dataString = element.getAttribute(valueAttributeName);
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
	        populateAndSetPropertyValue(mapPropertyName, dataString, executionContext);
    	}
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    	visitBefore(executionContext);
    	if(!beanBinding && !isAttribute) {
            element.setCache(new StringWriter());
        }
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!beanBinding && !isAttribute) {
            childText.toWriter((Writer) element.getCache());
        }
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    	if(!beanBinding) {
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

	        populateAndSetPropertyValue(mapPropertyName, dataString, executionContext);
    	}
    }


	private void visitBefore(ExecutionContext executionContext) {
    	if(beanBinding) {
    		Object bean = BeanAccessor.getBean(selectedBeanId, executionContext);
    		if(bean == null) {

    			// Register the observer which looks for the creation of the selected bean via its beanId. When this observer is triggered then
    			// we look if we got something we can set immediatly or that we got an array collection. For an array collection we need the array representation
    			// and not the list representation. So we register and observer wo looks for the change from the list to the array
    			BeanAccessor.registerBeanLifecycleObserver(executionContext, BeanLifecycle.BEGIN, selectedBeanId, getId(), new BeanLifecycleObserver(){

    				public void onBeanLifecycleEvent(
    						BeanLifecycleEvent event) {

    					if(logger.isDebugEnabled()) {
    						logger.debug("Bean lifecycle notification. Observer: '" + getId() + "'. Event: '" + event + "'");

    					}

    					ExecutionContext executionContext = event.getExecutionContext();
    					Object bean = event.getBean();

    					Classification selectedBeanType = getSelectedBeanRuntimeInfo().getClassification();

						BeanAccessor.associateLifecycles(executionContext, beanId, event.getBeanId());

						if(selectedBeanType == Classification.ARRAY_COLLECTION ) {

							// Register an observer which looks for the change that the mutable list of the selected bean gets converted to an array. We
							// can then set this array
							BeanAccessor.registerBeanLifecycleObserver(executionContext, BeanLifecycle.CHANGE, event.getBeanId(), getId(), new BeanLifecycleObserver() {
								public void onBeanLifecycleEvent(
										BeanLifecycleEvent event) {

									if(logger.isDebugEnabled()) {
										logger.debug("Bean lifecycle notification. Observer: '" + getId() + "'. Event: '" + event + "'");

									}

									ExecutionContext executionContext = event.getExecutionContext();

									populateAndSetPropertyValue(property, event.getBean(), executionContext);

									BeanAccessor.unregisterBeanLifecycleObserver(executionContext, BeanLifecycle.CHANGE, event.getBeanId(), getId());

								}
							});

						} else {
							populateAndSetPropertyValue(property, bean, executionContext);
						}

    				}

    			});

    			// Unregister the observers at then end of this beans lifecycle
    			BeanAccessor.registerBeanLifecycleObserver(executionContext, BeanLifecycle.END, beanId, getId(), new BeanLifecycleObserver(){
    				public void onBeanLifecycleEvent(BeanLifecycleEvent event) {

    					BeanAccessor.unregisterBeanLifecycleObserver(event.getExecutionContext(), BeanLifecycle.BEGIN, selectedBeanId, getId());
    					BeanAccessor.unregisterBeanLifecycleObserver(event.getExecutionContext(), BeanLifecycle.END, beanId, getId());

    				}
    			});
    		} else {
    			populateAndSetPropertyValue(property, bean, executionContext);
    		}
    	}
	}


    private void populateAndSetPropertyValue(String mapPropertyName, String dataString, ExecutionContext executionContext) {

        Object dataObject = decodeDataString(dataString, executionContext);

        populateAndSetPropertyValue(mapPropertyName, dataObject, executionContext);

    }

    @SuppressWarnings("unchecked")
	private void populateAndSetPropertyValue(String mapPropertyName, Object dataObject, ExecutionContext executionContext) {
        Object bean = BeanUtils.getBean(beanId, executionContext);

        Classification beanType = beanRuntimeInfo.getClassification();

        createPropertySetterMethod(bean, dataObject.getClass());

        // Set the data on the bean...
        try {
            if(propertySetterMethod != null) {
            	propertySetterMethod.invoke(bean, dataObject);

            } else if(beanType == Classification.MAP_COLLECTION) {
                ((Map)bean).put(mapPropertyName, dataObject);
            } else if(beanType == Classification.ARRAY_COLLECTION || beanType == Classification.COLLECTION_COLLECTION) {
                ((List)bean).add(dataObject);
            } else if(propertySetterMethod == null) {
            	if(setterMethod != null) {
                    throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterMethod + "(" + dataObject.getClass().getName() + ")] not found on type [" + beanRuntimeInfo.getPopulateType().getName() + "].  You may need to set a 'decoder' on the binding config.");
                } else if(property != null) {
                    throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + BeanUtils.toSetterName(property) + "(" + dataObject.getClass().getName() + ")] not found on type [" + beanRuntimeInfo.getPopulateType().getName() + "].  You may need to set a 'decoder' on the binding config.");
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


	private BeanRuntimeInfo getSelectedBeanRuntimeInfo() {
		if(selectedBeanRuntimeInfo == null) {
			selectedBeanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(selectedBeanId, appContext);
		}
		return selectedBeanRuntimeInfo;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.BeanObserver#getBeanId()
	 */
	public String getBeanId() {
		return beanId;
	}

	private String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BeanInstancePopulator == false) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		BeanInstancePopulator other = (BeanInstancePopulator) obj;
		return getId().equals(other.getId());
	}

}
