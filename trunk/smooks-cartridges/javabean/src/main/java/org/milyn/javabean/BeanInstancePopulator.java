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
import org.milyn.xml.DomUtils;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.Config;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXUtil;
import org.w3c.dom.Element;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

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

    @ConfigParam
    private String property;

    @ConfigParam(defaultVal = ConfigParam.NULL)
    private String valueAttributeName;

    @ConfigParam(name="type", defaultVal = "String")
    private String typeAlias;

    @ConfigParam(name="default", defaultVal = ConfigParam.NULL)
    private String defaultVal;

    @Config
    private SmooksResourceConfiguration config;

    private Method beanSetterMethod;
    private boolean isAttribute = true;
    private DataDecoder decoder;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        isAttribute = (valueAttributeName != null);

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

        populateAndSetBean(dataString, executionContext);
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!isAttribute) {
            element.setCache(childText.getText());
        }
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        String dataString;

        if (isAttribute) {
            dataString = SAXUtil.getAttribute(valueAttributeName, element.getAttributes());
        } else {
            dataString = (String) element.getCache();
        }

        populateAndSetBean(dataString, executionContext);
    }

    private void populateAndSetBean(String dataString, ExecutionContext executionContext) {
        Object bean = BeanUtils.getBean(beanId, executionContext);
        Object dataObject = decodeDataString(dataString, executionContext);

        // If we need to create the bean setter method instance...
        if (beanSetterMethod == null) {
            beanSetterMethod = createBeanSetterMethod(bean, BeanUtils.toSetterName(property), dataObject.getClass());
        }

        // Set the data on the bean...
        try {
            beanSetterMethod.invoke(bean, dataObject);
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
    private synchronized Method createBeanSetterMethod(Object bean, String setterName, Class type) {
        if (beanSetterMethod == null) {
            beanSetterMethod = BeanUtils.createBeanSetterMethod(bean, setterName, type);

            if(beanSetterMethod == null) {
                throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "(" + type.getName() + ")] not found on type [" + bean.getClass().getName() + "].  You may need to set a 'decoder' on the binding config.");
            }
        }

        return beanSetterMethod;
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
