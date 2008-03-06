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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.javabean.BeanRuntimeInfo.Classification;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;
import org.milyn.util.ClassUtil;
import org.w3c.dom.Element;

/**
 * Bean instance creator visitor class.
 * <p/>
 * Targeted via {@link org.milyn.javabean.BeanPopulator} expansion configuration.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanInstanceCreator implements DOMVisitBefore, SAXVisitBefore {

    private static Log logger = LogFactory.getLog(BeanInstanceCreator.class);

    private static boolean WARNED_SETON_DEPRECATED = false;

    private String id;

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
    	buildId();

    	beanRuntimeInfo = resolveBeanRuntime(beanClassName);
        BeanRuntimeInfo.recordBeanRuntimeInfo(beanId, beanRuntimeInfo, appContext);

        // Get the details of the bean on which instances of beans created by this class are to be set on.
        if (setOn != null) {

        	// We only warn once that the setOn is deprecated. We do it once because we
        	// don't want overflood the log with the same warning.
        	if(!WARNED_SETON_DEPRECATED && logger.isWarnEnabled()) {
        		logger.warn("The setOn parameter is deprecated. It is " +
        				"possible that it will be removed in the next major release. " +
        				"It is better to use the bean binding method " +
        				"(bean binding = binding with a ${beanId} selector notation). This warning is only given once.");

        		WARNED_SETON_DEPRECATED = true;
        	}

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

        if(logger.isDebugEnabled()) {
        	logger.debug("BeanInstanceCreator created for [" + beanId + ":" + beanClassName + "].");
        }
    }

    private void buildId() {
    	StringBuilder idBuilder = new StringBuilder();
    	idBuilder.append(BeanInstanceCreator.class.getName());
    	idBuilder.append("#");
    	idBuilder.append(beanId);


    	id = idBuilder.toString();
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        createAndSetBean(executionContext);
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        createAndSetBean(executionContext);
    }


    private Object convert(Object bean, ExecutionContext executionContext) {

        bean = BeanUtils.convertListToArray((List<?>)bean, beanRuntimeInfo.getArrayType());

        BeanAccessor.changeBean(executionContext, beanId, bean);

    	return bean;
    }

    @SuppressWarnings("deprecation")
	private void createAndSetBean(ExecutionContext executionContext) {
        Object bean;
        bean = createBeanInstance();

        if (setOn != null) {
            // Need to associate the 2 bean lifecycles...
            BeanAccessor.associateLifecycles(executionContext, setOn, beanId, addToList);
        }

        BeanAccessor.addBean(beanId, bean, executionContext, addToList);

        if(setOn != null || beanRuntimeInfo.getClassification() == Classification.ARRAY_COLLECTION) {
        	BeanAccessor.addBeanLifecycleObserver(
        			executionContext, beanId, BeanLifecycle.END, getId(), true, new BeanLifecycleObserver() {

        		/* (non-Javadoc)
        		 * @see org.milyn.javabean.lifecycle.BeanLifecycleObserver#notifyBeanLifecycleEvent(org.milyn.javabean.lifecycle.BeanLifecycleEvent)
        		 */
        		public void onBeanLifecycleEvent(BeanLifecycleEvent event) {

    				ExecutionContext executionContext = event.getExecutionContext();

    				Classification thisBeanType = beanRuntimeInfo.getClassification();

    		    	boolean isArray = (thisBeanType == Classification.ARRAY_COLLECTION);
    		    	boolean isSetOn = (setOn != null);

    		    	if(isArray || isSetOn) {
    			    	Object bean  = BeanUtils.getBean(beanId, executionContext);

    			    	if(isArray) {
    			    		// This bean is an array, we need to convert the List used to create it into
    			            // an array of that type, and add that array to the BeanAccessor, overwriting the list

    			    		bean = convert(bean, executionContext);
    			    	}
    			    	if (isSetOn) {

    			            setOn(bean, executionContext);
    			        }
    		    	}

        		}

        	});
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Bean [" + beanId + "] instance created.");
        }


    }

    @SuppressWarnings("unchecked")
	private void setOn(Object bean, ExecutionContext executionContext) {
        Object setOnBean = getSetOnTargetBean(executionContext);

        if (setOnBean != null) {

            Classification setOnBeanType = setOnBeanRuntimeInfo.getClassification();



            // Set the bean instance on another bean. Supports creating an object graph
            if(setOnBeanType == Classification.NON_COLLECTION) {
                setOnNonCollection(executionContext, bean, setOnBean);
            } else if(!isSetOnMethodConfigured && (setOnBeanType == Classification.COLLECTION_COLLECTION ||
                                                setOnBeanType == Classification.ARRAY_COLLECTION)) {
                // It's a Collection or array.  Arrays are always populated through a List...
            	((Collection)setOnBean).add(bean);
            } else if(!isSetOnMethodConfigured && setOnBeanType == Classification.MAP_COLLECTION) {
                // It's a map...
                ((Map)setOnBean).put(beanId, bean);
            }
        } else if(logger.isErrorEnabled()) {
            logger.error("Failed to set bean '" + beanId + "' on parent bean '" + setOn + "'.  Failed to find bean '" + setOn + "'.");
        }
    }

    private void setOnNonCollection(ExecutionContext execContext, Object bean, Object setOnBean) {
        try {
            if (setOnBeanSetterMethod == null) {
                if (!addToList) {
                    createBeanSetterMethod(setOnBean, setOnMethod, bean.getClass());
                } else {
                    createBeanSetterMethod(setOnBean, setOnMethod, List.class);
                }
            }
            if(logger.isDebugEnabled()) {
                logger.debug("Setting bean '" + beanId + "' on parent bean '" + setOn + "'.");
            }
            if (!addToList) {
                setOnBeanSetterMethod.invoke(setOnBean, bean);
            } else {
                Object beanList = BeanAccessor.getBean(execContext, beanId + "List");
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
        Class<?> clazz;

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
            clazz.getConstructor();
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
    private synchronized Method createBeanSetterMethod(Object bean, String setterName, Class<?> type) {
        if (setOnBeanSetterMethod == null) {
            setOnBeanSetterMethod = BeanUtils.createSetterMethod(setterName, bean, type);

            if(setOnBeanSetterMethod == null) {
                throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "(" + type.getName() + ")] not found on type [" + bean.getClass().getName() + "].  You may need to set a 'decoder' on the binding config.");
            }
        }

        return setOnBeanSetterMethod;
    }

    private String getId() {
		return id;
	}

    @Override
    public String toString() {
    	return getId();
    }

}
