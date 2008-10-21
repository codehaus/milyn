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
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXUtil;
import org.milyn.javabean.BeanRuntimeInfo;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.BeanRuntimeInfo.Classification;
import org.milyn.persistence.dao.DaoRegister;
import org.milyn.persistence.dao.invoker.DaoInvoker;
import org.milyn.persistence.dao.invoker.DaoInvokerFactory;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;


/**
 * @author maurice
 *
 */
public class EntityInstanceLookupPopulator implements DOMElementVisitor, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(EntityInstanceLookupPopulator.class);

    @ConfigParam
    private String beanId;

    @ConfigParam(use = Use.OPTIONAL)
    private String property;

    @ConfigParam(use = Use.OPTIONAL)
    private String setterMethod;

    @ConfigParam(use = Use.OPTIONAL)
    private String valueAttributeName;

    @ConfigParam(name="type", defaultVal = "String")
    private String typeAlias;

    @ConfigParam
    private Class<?> entityClass;

    @ConfigParam
    private String query;

    @ConfigParam(name="default", use = Use.OPTIONAL)
    private String defaultVal;

    @ConfigParam(defaultVal = OnNoResult.NULLIFY_STR, decoder = OnNoResult.DataDecoder.class)
    private OnNoResult onNoResult;

    @ConfigParam(defaultVal = "true")
    private boolean uniqueResult;

    @ConfigParam(use = Use.OPTIONAL)
    private String daoName;

    @AppContext
    private ApplicationContext appContext;

    private BeanRuntimeInfo beanRuntimeInfo;
    private Method propertySetterMethod;
    private boolean isAttribute = true;
    private DataDecoder decoder;
    private boolean checkedForSetterMethod;
    private String mapKeyAttribute;

	private final Object lock = new Object();

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        beanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(beanId, appContext);
        isAttribute = (valueAttributeName != null);

        if (setterMethod == null && property == null && beanRuntimeInfo.getClassification().equals( Classification.NON_COLLECTION )) {
            throw new SmooksConfigurationException("Binding configuration for beanId='" + beanId + "' must contain " +
                    "either a 'property' or 'setterMethod' attribute definition, unless the target bean is a Collection/Array." +
                    "  Bean is type '" + beanRuntimeInfo.getPopulateType().getName() + "'.");
        }

        if(beanRuntimeInfo.getClassification().equals( Classification.MAP_COLLECTION ) && property != null) {
            property = property.trim();
            if(property.length() > 1 && property.charAt(0) == '@') {
                mapKeyAttribute = property.substring(1);
            }
        }


        logger.debug("Bean Instance Populator created for [" + beanId + "].  property=" + property);
    }

    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
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

    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
        if(!isAttribute) {
            element.setCache(new StringWriter());
        }
    }

    public void onChildText(final SAXElement element, final SAXText childText, final ExecutionContext executionContext) throws SmooksException, IOException {
        if(!isAttribute) {
            childText.toWriter((Writer) element.getCache());
        }
    }

    public void onChildElement(final SAXElement element, final SAXElement childElement, final ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
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

    @SuppressWarnings("unchecked")
	private void populateAndSetPropertyValue(final String mapPropertyName, final String dataString, final ExecutionContext executionContext) {
    	final Object bean = BeanUtils.getBean(beanId, executionContext);
        final Object dataObject = decodeDataString(dataString, executionContext);
        final Classification beanType = beanRuntimeInfo.getClassification();

        final Collection<?> entities = lookup(dataObject, executionContext);

        if(entities.size() == 0 && OnNoResult.EXCEPTION == onNoResult ) {
        	throw new NoLookupResultException("The lookup didn't retrieve any result for the query [" + query + "] and value [" + dataString + "]");
        }
        if(entities.size() > 1 && uniqueResult) {
        	throw new NoUniqueLookupResultException("The lookup didn't retrieve a unique result for the query [" + query + "] and value [" + dataString + "]");
        }

        // If we need to create the bean setter method instance...
        if (!checkedForSetterMethod && propertySetterMethod == null) {
            if(setterMethod != null) {
                propertySetterMethod = createPropertySetterMethod(bean, setterMethod, entityClass);
            } else if(property != null) {
                propertySetterMethod = createPropertySetterMethod(bean, BeanUtils.toSetterName(property), entityClass);
            }
            checkedForSetterMethod = true;
        }

        // Set the data on the bean...
        try {
        	for(final Object entity : entities) {
	            if(propertySetterMethod != null) {
	                propertySetterMethod.invoke(bean, entity);
	            } else if(beanType.equals( Classification.MAP_COLLECTION )) {
	                ((Map)bean).put(mapPropertyName, entity);
	            } else if(beanType.equals( Classification.ARRAY_COLLECTION ) || beanType.equals( Classification.COLLECTION_COLLECTION )) {
	                ((List)bean).add(entity);
	            } else if(propertySetterMethod == null) {
	                if(setterMethod != null) {
	                    throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterMethod + "(" + entityClass + ")] not found on type [" + beanRuntimeInfo.getPopulateType().getName() + "].");
	                } else if(property != null) {
	                    throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + BeanUtils.toSetterName(property) + "(" + entityClass + ")] not found on type [" + beanRuntimeInfo.getPopulateType().getName() + "].");
	                }
	            }
        	}
        } catch (final IllegalAccessException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + BeanUtils.toSetterName(property) + "] on bean instance class type [" + bean.getClass() + "].", e);
        } catch (final InvocationTargetException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + BeanUtils.toSetterName(property) + "] on bean instance class type [" + bean.getClass() + "].", e);
        }
    }

    @SuppressWarnings("unchecked")
	private Collection<?> lookup(final Object dataObject, final ExecutionContext executionContext) {
    	final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

    	Object daoObj = null;
    	try {
    		daoObj = emr.getDao(daoName);

    		final DaoInvoker daoInvocation = DaoInvokerFactory.getInstance().create(daoObj, appContext);

	    	return daoInvocation.findByQuery(query, new Object[] { dataObject });

	    } finally {
			if(daoObj != null) {
				emr.returnDao(daoObj);
			}
		}
    }


    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    private Method createPropertySetterMethod(final Object bean, final String setterName, final Class<?> setterParamType) {
        synchronized (lock ) {
        	if (propertySetterMethod == null) {
                propertySetterMethod = BeanUtils.createSetterMethod(setterName, bean, setterParamType);
            }
		}


        return propertySetterMethod;
    }

    private Object decodeDataString(final String dataString, final ExecutionContext executionContext) throws DataDecodeException {
        String dataStr = dataString;
    	if((dataStr == null || "".equals( dataStr )) && defaultVal != null) {
    		dataStr = defaultVal;
        }

        if (decoder == null) {
            decoder = getDecoder(executionContext);
        }

        return decoder.decode(dataStr);
    }

    private DataDecoder getDecoder(final ExecutionContext executionContext) throws DataDecodeException {
        final List<?> decoders = executionContext.getDeliveryConfig().getObjects("decoder:" + typeAlias);

        if (decoders == null || decoders.isEmpty()) {
            decoder = DataDecoder.Factory.create(typeAlias);
        } else if (decoders.get(0) instanceof DataDecoder == false) {
            throw new DataDecodeException("Configured decoder '" + typeAlias + ":" + decoders.get(0).getClass().getName() + "' is not an instance of " + DataDecoder.class.getName());
        } else {
            decoder = (DataDecoder) decoders.get(0);
        }

        return decoder;
    }
}
