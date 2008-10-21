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
package org.milyn.persistence;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.javabean.BeanRuntimeInfo;
import org.milyn.javabean.BeanUtils;
import org.milyn.persistence.dao.DaoRegister;
import org.milyn.persistence.dao.invoker.DaoInvoker;
import org.milyn.persistence.dao.invoker.DaoInvokerFactory;
import org.w3c.dom.Element;


/**
 * @author maurice
 *
 */
public class EntityInstanceCreator implements DOMElementVisitor, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(EntityInstanceCreator.class);

    @ConfigParam
    private String beanId;

    @ConfigParam(defaultVal = "false")
    private boolean addToList;

    @ConfigParam(defaultVal = "false")
    private boolean persist;

    @ConfigParam(defaultVal = "false")
    private boolean flush;

    @ConfigParam(use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(defaultVal = PersistMode.PERSIST_STR, choice = {PersistMode.PERSIST_STR, PersistMode.MERGE_STR}, decoder = PersistMode.DataDecoder.class)
    private PersistMode persistMode;


//    @ConfigParam(use=ConfigParam.Use.OPTIONAL)
//    private String bindingSetOnBean; // The name of the property/method to set the setOn bean on

    @Config
    private SmooksResourceConfiguration config;

    @AppContext
    private ApplicationContext appContext;

    private BeanRuntimeInfo beanRuntimeInfo;

//    private String bindingSetOnBeanMethod;
//    private Method bindingSetOnBeanSetterMethod;

//    private final Object setOnBeanSetterMethodLock = new Object();
//    private final Object bindingSetOnBeanSetterMethodLock = new Object();

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        beanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(beanId, appContext);

        if(beanRuntimeInfo == null) {
            throw new SmooksConfigurationException("Bean '" + beanId + "' must be defined.");
        }

//        if(bindingSetOnBean != null) {
//        	if(setOn == null) {
//        		throw new SmooksConfigurationException("Parent bean '" + setOn + "' must be defined with the setOn parameter.");
//        	}
//
//        	final Classification thisBeanType = beanRuntimeInfo.getClassification();
//
//        	if(thisBeanType.equals( Classification.ARRAY_COLLECTION) || thisBeanType.equals( Classification.COLLECTION_COLLECTION) ) {
//        		throw new SmooksConfigurationException("You can't use the bindingSetOnBean parameter if this bean '" + beanId + "' is an Array or Collection .");
//        	}
//
//        	if(bindingSetOnBean.trim().length() == 0) {
//
//        		bindingSetOnBeanMethod = BeanUtils.toSetterName(setOn);
//
//        	} else {
//        		bindingSetOnBeanMethod = bindingSetOnBean;
//        	}
//        }

        logger.debug("BeanInstanceCreator created for [" + beanId + "].");
    }


    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	visitAfter(executionContext);
    }


    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildText(final SAXElement element, final SAXText childText, final ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(final SAXElement element, final SAXElement childElement, final ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	visitAfter(executionContext);
    }


	private void visitAfter(final ExecutionContext executionContext) {
		if (persist || flush ) {

			Object bean = BeanUtils.getBean(beanId, executionContext);

			bean = daoActions(executionContext, bean);
		}
    }

	/**
	 * @param executionContext
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object daoActions(final ExecutionContext executionContext,
			final Object bean) {

		Object beanResult = bean;

		final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

		Object daoObj = null;
		try {
			daoObj = emr.getDao(daoName);

			if(daoObj == null) {
				throw new IllegalStateException("The DAO registery returned null while getting the DAO [" + daoName + "]");
			}

			final DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(daoObj, appContext);

			if (persist) {
				switch (persistMode) {

				case PERSIST:
					daoInvoker.persist(beanResult);
					break;

				case MERGE:
					beanResult = daoInvoker.merge(beanResult);
					//BeanAccessor.addBean(beanId, beanResult);
					break;

				default:
					throw new IllegalStateException("persistMode '"
							+ persistMode + "' not supported");
				}

			}

			if (flush) {
				daoInvoker.flush();
			}


		} finally {
			if(daoObj != null) {
				emr.returnDao(daoObj);
			}
		}
		return beanResult;
	}


//    @SuppressWarnings("unchecked")
//	private void setBindingSetOnBean(final Object bean, final ExecutionContext execContext) {
//        final Object setOnBean = getSetOnTargetBean(execContext);
//
//        if (setOnBean != null) {
//        	final Classification thisBeanType = beanRuntimeInfo.getClassification();
//
//            // Set the bean instance on another bean. Supports creating an object graph
//            if(thisBeanType.equals( Classification.NON_COLLECTION )) {
//            	setBindingSetOnBeanNonCollection(execContext, bean, setOnBean);
//            } else if(thisBeanType.equals( Classification.MAP_COLLECTION )) {
//            	// It's a map...
//                ((Map)bean).put(bindingSetOnBean, bean);
//            }
//        } else {
//            logger.error("Failed to set bean '" + beanId + "' on parent bean '" + setOn + "'.  Failed to find bean '" + setOn + "'.");
//        }
//    }

//    private void setBindingSetOnBeanNonCollection(final ExecutionContext execContext, final Object bean, final Object setOnBean) {
//        try {
//
//        	final Method method = createBindingSetOnBeanSetterMethod(bean, bindingSetOnBeanMethod, setOnBean.getClass());
//
//            if(logger.isDebugEnabled()) {
//                logger.debug("Setting parent bean '" + setOn + "' on bean '" + beanId + "'.");
//            }
//            method.invoke(bean, setOnBean);
//
//        } catch (final IllegalAccessException e) {
//            throw new SmooksConfigurationException("Error invoking bean setter method [" + bindingSetOnBeanMethod + "] on bean instance class type [" + bean.getClass() + "].", e);
//        } catch (final InvocationTargetException e) {
//            throw new SmooksConfigurationException("Error invoking bean setter method [" + bindingSetOnBeanMethod + "] on bean instance class type [" + bean.getClass() + "].", e);
//        }
//    }

//    /**
//     * Create the bean setter method instance for this visitor.
//     *
//     * @param setterName The setter method name.
//     * @return The bean setter method.
//     */
//    private Method createBindingSetOnBeanSetterMethod(final Object setOnBean, final String setterName, final Class<?> type) {
//    	if(bindingSetOnBeanSetterMethod == null) {
//	    	synchronized (bindingSetOnBeanSetterMethodLock) {
//	    		if (bindingSetOnBeanSetterMethod == null) {
//	    			bindingSetOnBeanSetterMethod = BeanUtils.createSetterMethod(setterName, setOnBean, type);
//
//	                if(bindingSetOnBeanSetterMethod == null) {
//	                    throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "(" + type.getName() + ")] not found on type [" + setOnBean.getClass().getName() + "].");
//	                }
//	            }
//			}
//    	}
//        return bindingSetOnBeanSetterMethod;
//    }
}
