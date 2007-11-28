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
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.cdr.annotation.Config;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.util.ClassUtil;
import org.w3c.dom.Element;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * Bean instance creator visitor class.
 * <p/>
 * Targeted via {@link org.milyn.javabean.BeanPopulator} expansion configuration.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanInstanceCreator implements DOMElementVisitor, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(BeanInstanceCreator.class);

    @ConfigParam
    private String beanId;

    @ConfigParam(name="beanClass")
    private String beanClassName;

    @ConfigParam(defaultVal = "false")
    private boolean addToList;

    @ConfigParam(use=ConfigParam.Use.OPTIONAL)
    private String setOn; // The name of the bean on which to set this bean    

    @Config
    private SmooksResourceConfiguration config;

    private Class beanClass;
    private boolean isSetOnMethodUnconfigured;
    private String setOnMethod;
    private Method setOnBeanSetterMethod;


    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        beanClass = createBeanRuntime(beanClassName);

        // Get the details of the bean on which instances of beans created by this class are to be set on.
        if (setOn != null) {
            setOnMethod = config.getStringParameter("setOnMethod");
            if(setOnMethod == null) {
                isSetOnMethodUnconfigured = true;

                String setOnProperty = config.getStringParameter("setOnProperty");
                if(setOnProperty == null) {
                    // If 'setOnProperty' is not defined, default to the name of this bean...
                    setOnProperty = beanId;
                    if(addToList && !setOnProperty.endsWith("s")) {
                        setOnProperty += "s";
                    }
                }
                setOnMethod = BeanUtils.toSetterName(setOnProperty);
            }
        }

        logger.debug("BeanInstanceCreator created for [" + beanId + ":" + beanClassName + "].");
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        createAndSetBean(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        if (setOn != null) {
            setOn(executionContext);
        }
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        createAndSetBean(executionContext);
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        if (setOn != null) {
            setOn(executionContext);
        }
    }

    private void createAndSetBean(ExecutionContext executionContext) {
        Object bean;
        bean = createBeanInstance();

        if (setOn != null) {
            // Need to associate the 2 bean lifecycles...
            BeanAccessor.associateLifecycles(executionContext, setOn, beanId, addToList);
        }

        BeanAccessor.addBean(beanId, bean, executionContext, addToList);
        if (logger.isDebugEnabled()) {
            logger.debug("Bean [" + beanId + "] instance created.");
        }
    }

    private void setOn(ExecutionContext execContext) {
        Object bean = BeanUtils.getBean(beanId, execContext);
        Object setOnBean = BeanUtils.getBean(setOn, execContext);

        if (setOnBean != null) {
            // Set the bean instance on another bean. Supports creating an object graph
            if(isSetOnMethodUnconfigured && setOnBean instanceof Collection) {
                ((Collection)setOnBean).add(bean);
            } else {
                try {
                    if (setOnBeanSetterMethod == null) {
                        if (!addToList) {
                            setOnBeanSetterMethod = createBeanSetterMethod(setOnBean, setOnMethod, bean.getClass());
                        } else {
                            setOnBeanSetterMethod = createBeanSetterMethod(setOnBean, setOnMethod, List.class);
                        }
                    }
                    if(logger.isDebugEnabled()) {
                        logger.debug("Setting bean '" + beanId + "' on parent bean '" + setOn + "'.");
                    }
                    if (!addToList) {
                        setOnBeanSetterMethod.invoke(setOnBean, bean);
                    } else {
                        Object beanList = BeanAccessor.getBean(beanId + "List", execContext);
                        setOnBeanSetterMethod.invoke(setOnBean, beanList);
                    }
                } catch (IllegalAccessException e) {
                    throw new SmooksConfigurationException("Error invoking bean setter method [" + setOnMethod + "] on bean instance class type [" + setOnBean.getClass() + "].", e);
                } catch (InvocationTargetException e) {
                    throw new SmooksConfigurationException("Error invoking bean setter method [" + setOnMethod + "] on bean instance class type [" + setOnBean.getClass() + "].", e);
                }
            }
        } else {
            logger.error("Failed to set bean '" + beanId + "' on parent bean '" + setOn + "'.  Failed to find bean '" + setOn + "'.");
        }
    }

    /**
     * Create a new bean instance, generating relevant configuration exceptions.
     *
     * @return A new bean instance.
     */
    private Object createBeanInstance() {
        Object bean;

        try {
            bean = beanClass.newInstance();
        } catch (InstantiationException e) {
            throw new SmooksConfigurationException("Unable to create bean instance [" + beanId + ":" + beanClass.getName() + "].", e);
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Unable to create bean instance [" + beanId + ":" + beanClass.getName() + "].", e);
        }

        return bean;
    }

    /**
     * Create the Javabean runtime class.
     * <p/>
     * Also performs some checks on the bean.
     *
     * @param beanClass The beanClass name.
     * @return The bean runtime class instance.
     */
    private Class createBeanRuntime(String beanClass) {
        Class clazz;

        try {
            clazz = ClassUtil.forName(beanClass, getClass());
        } catch (ClassNotFoundException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " not in classpath.");
        }

        // check for a default constructor.
        try {
            clazz.getConstructor(null);
        } catch (NoSuchMethodException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " doesn't have a public default constructor.");
        }

        return clazz;
    }

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    private synchronized Method createBeanSetterMethod(Object bean, String setterName, Class type) {
        if (setOnBeanSetterMethod == null) {
            setOnBeanSetterMethod = BeanUtils.createBeanSetterMethod(bean, setterName, type);

            if(setOnBeanSetterMethod == null) {
                throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "(" + type.getName() + ")] not found on type [" + bean.getClass().getName() + "].  You may need to set a 'decoder' on the binding config.");
            }
        }

        return setOnBeanSetterMethod;
    }
}
