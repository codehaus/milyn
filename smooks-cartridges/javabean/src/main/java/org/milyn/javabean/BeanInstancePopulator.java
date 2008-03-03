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
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXUtil;
import org.milyn.javabean.BeanRuntimeInfo.Classification;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Bean instance populator visitor class.
 * <p/>
 * Targeted via {@link org.milyn.javabean.BeanPopulator} expansion configuration.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanInstancePopulator implements DOMElementVisitor, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(BeanInstancePopulator.class);

    @ConfigParam
    private String beanId;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String property;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String setterMethod;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String valueAttributeName;

    @ConfigParam(name="type", defaultVal = "String")
    private String typeAlias;

    @ConfigParam(name="default", defaultVal = AnnotationConstants.NULL_STRING)
    private String defaultVal;

    @AppContext
    private ApplicationContext appContext;

    private BeanRuntimeInfo beanRuntimeInfo;
    private Method propertySetterMethod;
    private boolean isAttribute = true;
    private DataDecoder decoder;
    private boolean checkedForSetterMethod;
    private String mapKeyAttribute;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        beanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(beanId, appContext);
        isAttribute = (valueAttributeName != null);

        if (setterMethod == null && property == null && beanRuntimeInfo.getClassification() == Classification.NON_COLLECTION) {
            throw new SmooksConfigurationException("Binding configuration for beanId='" + beanId + "' must contain " +
                    "either a 'property' or 'setterMethod' attribute definition, unless the target bean is a Collection/Array." +
                    "  Bean is type '" + beanRuntimeInfo.getPopulateType().getName() + "'.");
        }

        if(beanRuntimeInfo.getClassification() == Classification.MAP_COLLECTION && property != null) {
            property = property.trim();
            if(property.length() > 1 && property.charAt(0) == '@') {
                mapKeyAttribute = property.substring(1);
            }
        }

        logger.debug("Bean Instance Populator created for [" + beanId + "].  property=" + property);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
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

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!isAttribute) {
            element.setCache(new StringWriter());
        }
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!isAttribute) {
            childText.toWriter((Writer) element.getCache());
        }
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
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

    private void populateAndSetPropertyValue(String mapPropertyName, String dataString, ExecutionContext executionContext) {
        Object bean = BeanUtils.getBean(beanId, executionContext);
        Object dataObject = decodeDataString(dataString, executionContext);
        Classification beanType = beanRuntimeInfo.getClassification();

        // If we need to create the bean setter method instance...
        if (!checkedForSetterMethod && propertySetterMethod == null) {
            if(setterMethod != null && !setterMethod.trim().equals("")) {
                propertySetterMethod = createPropertySetterMethod(bean, setterMethod, dataObject.getClass());
            } else if(property != null && !property.trim().equals("")) {
                propertySetterMethod = createPropertySetterMethod(bean, BeanUtils.toSetterName(property), dataObject.getClass());
            }
            checkedForSetterMethod = true;
        }

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

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    private synchronized Method createPropertySetterMethod(Object bean, String setterName, Class setterParamType) {
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
}
