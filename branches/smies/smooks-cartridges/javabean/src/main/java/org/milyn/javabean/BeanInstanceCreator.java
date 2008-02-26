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
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.util.ClassUtil;
import org.w3c.dom.Element;
import org.milyn.javabean.BeanRuntimeInfo.Classification;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    @AppContext
    private ApplicationContext appContext;

    private BeanRuntimeInfo beanRuntimeInfo;

    // Properties associated with the bean on which the created bean
    // is to be set.  Is it a List, Map etc...
    private BeanRuntimeInfo setOnBeanRuntimeInfo;
    private boolean isSetOnMethodConfigured = false;
    private String setOnMethod;
    private Method setOnBeanSetterMethod;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        beanRuntimeInfo = resolveBeanRuntime(beanClassName);
        BeanRuntimeInfo.recordBeanRuntimeInfo(beanId, beanRuntimeInfo, appContext);

        // Get the details of the bean on which instances of beans created by this class are to be set on.
        if (setOn != null) {
            setOnBeanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(setOn, appContext);
            if(setOnBeanRuntimeInfo == null) {
                throw new SmooksConfigurationException("Parent bean '" + setOn + "' must be defined before bean '" + beanId + "'.");
            }

            setOnMethod = config.getStringParameter("setOnMethod");
            if(setOnMethod == null) {
                String setOnProperty = config.getStringParameter("setOnProperty");
                if(setOnProperty == null) {
                    // If 'setOnProperty' is not defined, default to the name of this bean...
                    setOnProperty = beanId;
                    if(addToList && !setOnProperty.endsWith("s")) {
                        setOnProperty += "s";
                    }
                }
                setOnMethod = BeanUtils.toSetterName(setOnProperty);
            } else {
                isSetOnMethodConfigured = true;
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
        Object setOnBean = getSetOnTargetBean(execContext);

        if (setOnBean != null) {
            Classification thisBeanType = beanRuntimeInfo.getClassification();
            Classification setOnBeanType = setOnBeanRuntimeInfo.getClassification();

            // If this bean is an array, we need to convert the List used to create it into
            // an array of that type, and set that array as the bean on the setOnBean...
            if(thisBeanType == Classification.ARRAY_COLLECTION) {
                bean = BeanUtils.convertListToArray((List)bean, beanRuntimeInfo.getArrayType());
            }

            // Set the bean instance on another bean. Supports creating an object graph
            if(setOnBeanType == Classification.NON_COLLECTION) {
                setOnNonCollection(execContext, bean, setOnBean);
            } else if(!isSetOnMethodConfigured && (setOnBeanType == Classification.COLLECTION_COLLECTION ||
                                                setOnBeanType == Classification.ARRAY_COLLECTION)) {
                    // It's a Collection or array.  Arrays are always populated through a List...
                    ((Collection)setOnBean).add(bean);
            } else if(!isSetOnMethodConfigured && setOnBeanType == Classification.MAP_COLLECTION) {
                // It's a map...
                ((Map)setOnBean).put(beanId, bean);
            }
        } else {
            logger.error("Failed to set bean '" + beanId + "' on parent bean '" + setOn + "'.  Failed to find bean '" + setOn + "'.");
        }
    }

    private void setOnNonCollection(ExecutionContext execContext, Object bean, Object setOnBean) {
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

    private Object getSetOnTargetBean(ExecutionContext execContext) {
        Object setOnBean = BeanUtils.getBean(setOn, execContext);

        return setOnBean;
    }

    /**
     * Create a new bean instance, generating relevant configuration exceptions.
     *
     * @return A new bean instance.
     */
    private Object createBeanInstance() {
        Object bean;

        try {
            bean = beanRuntimeInfo.getPopulateType().newInstance();
        } catch (InstantiationException e) {
            throw new SmooksConfigurationException("Unable to create bean instance [" + beanId + ":" + beanRuntimeInfo.getPopulateType().getName() + "].", e);
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Unable to create bean instance [" + beanId + ":" + beanRuntimeInfo.getPopulateType().getName() + "].", e);
        }

        return bean;
    }

    /**
     * Resolve the Javabean runtime class.
     * <p/>
     * Also performs some checks on the bean.
     *
     * @param beanClass The beanClass name.
     * @return The bean runtime class instance.
     */
    private BeanRuntimeInfo resolveBeanRuntime(String beanClass) {
        BeanRuntimeInfo beanInfo = new BeanRuntimeInfo();
        Class clazz;

        // If it's an array, we use a List and extract an array from it on the
        // visitAfter event....
        if(beanClass.endsWith("[]")) {
            beanInfo.setClassification(BeanRuntimeInfo.Classification.ARRAY_COLLECTION);
            String arrayTypeName = beanClass.substring(0, beanClass.length() - 2);
            try {
                beanInfo.setArrayType(ClassUtil.forName(arrayTypeName, getClass()));
            } catch (ClassNotFoundException e) {
                throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + arrayTypeName + " not on classpath.");
            }
            beanInfo.setPopulateType(ArrayList.class);

            return beanInfo;
        }

        try {
            clazz = ClassUtil.forName(beanClass, getClass());
        } catch (ClassNotFoundException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " not on classpath.");
        }

        beanInfo.setPopulateType(clazz);

        // We maintain a targetType enum because it helps us avoid performing
        // instanceof checks, which are cheap when the instance being checked is
        // an instanceof, but is expensive if it's not....
        if(Map.class.isAssignableFrom(clazz)) {
            beanInfo.setClassification(BeanRuntimeInfo.Classification.MAP_COLLECTION);
        } else if(Collection.class.isAssignableFrom(clazz)) {
            beanInfo.setClassification(BeanRuntimeInfo.Classification.COLLECTION_COLLECTION);
        } else {
            beanInfo.setClassification(BeanRuntimeInfo.Classification.NON_COLLECTION);
        }

        // check for a default constructor.
        try {
            clazz.getConstructor(null);
        } catch (NoSuchMethodException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " doesn't have a public default constructor.");
        }

        return beanInfo;
    }

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    private synchronized Method createBeanSetterMethod(Object bean, String setterName, Class type) {
        if (setOnBeanSetterMethod == null) {
            setOnBeanSetterMethod = BeanUtils.createSetterMethod(setterName, bean, type);

            if(setOnBeanSetterMethod == null) {
                throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "(" + type.getName() + ")] not found on type [" + bean.getClass().getName() + "].  You may need to set a 'decoder' on the binding config.");
            }
        }

        return setOnBeanSetterMethod;
    }
}