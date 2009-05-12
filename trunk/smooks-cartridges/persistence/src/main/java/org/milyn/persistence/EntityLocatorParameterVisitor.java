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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.ordering.Consumer;
import org.milyn.delivery.ordering.Producer;
import org.milyn.delivery.sax.*;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.expression.MVELExpressionEvaluator;
import org.milyn.javabean.BeanRuntimeInfo;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.BeanRuntimeInfo.Classification;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.persistence.parameter.NamedParameterIndex;
import org.milyn.persistence.parameter.Parameter;
import org.milyn.persistence.parameter.ParameterContainer;
import org.milyn.persistence.parameter.ParameterIndex;
import org.milyn.persistence.parameter.ParameterManager;
import org.milyn.persistence.parameter.ParameterProductUtil;
import org.milyn.persistence.parameter.PositionalParameterIndex;
import org.milyn.util.CollectionsUtil;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@VisitBeforeReport(
		condition = "parameters.containsKey('wireBeanId') || parameters.containsKey('valueAttributeName')",
		summary = "<#if resource.parameters.wireBeanId??>Create bean lifecycle observer for the bean under the beanId '${resource.parameters.wireBeanId}'." +
		"<#elseif resource.parameters.valueAttributeName??>Populating <#if resource.parameters.name??>the '${resource.parameters.name}'</#if> parameter " +
		"from the '${resource.parameters.valueAttributeName}' attribute." +
		"</#if>")
@VisitAfterReport(
		condition = "!parameters.containsKey('valueAttributeName')",
		summary = "<#if resource.parameters.wireBeanId??>Removing bean lifecycle observer for the bean under the beanId '${resource.parameters.wireBeanId}'." +
		"<#else>Populating <#if resource.parameters.name??>the '${resource.parameters.name}'</#if> parameter " +
		"from <#if resource.parameters.expression??>an expression<#else>this element.</#if></#if>.")
public class EntityLocatorParameterVisitor implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter, Consumer, Producer  {

	private static Log logger = LogFactory.getLog(EntityLocatorParameterVisitor.class);

	@ConfigParam(name="entityLookupperId")
    private int entityLocatorId;

	@ConfigParam(use = Use.OPTIONAL)
	private String name;

	@ConfigParam
	private Integer index;

    @ConfigParam(defaultVal = ParameterListType.NAMED_STR, decoder = ParameterListType.DataDecoder.class)
    private ParameterListType parameterListType;

    @ConfigParam(name="wireBeanId", use = Use.OPTIONAL)
    private String wireBeanIdName;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private MVELExpressionEvaluator expression;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String valueAttributeName;

    @ConfigParam(name="type", use = Use.OPTIONAL)
    private String typeAlias;

    @ConfigParam(name="default", use = Use.OPTIONAL)
    private String defaultVal;

    @AppContext
    private ApplicationContext appContext;

    private Parameter<?> parameter;

    private BeanRepositoryManager beanRepositoryManager;

    private BeanRuntimeInfo wiredBeanRuntimeInfo;

    private BeanId wireBeanId;

    private boolean isAttribute = true;

    private DataDecoder decoder;

    private boolean beanWiring;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {

    	if(logger.isDebugEnabled()) {
    		logger.debug("Initializing EntityLocatorParameterVisitor with name '"+ name +"'");
    	}

        beanWiring = wireBeanIdName != null;
        isAttribute = (valueAttributeName != null);

        beanRepositoryManager = BeanRepositoryManager.getInstance(appContext);

        if(parameterListType == ParameterListType.NAMED) {
        	NamedParameterIndex parameterIndex = (NamedParameterIndex) ParameterManager.getParameterIndex(entityLocatorId, appContext);
        	parameter = parameterIndex.register(name);
        } else {
        	PositionalParameterIndex parameterIndex = (PositionalParameterIndex) ParameterManager.getParameterIndex(entityLocatorId, appContext);
        	parameter = parameterIndex.register(index);
        }

    }

    /* (non-Javadoc)
     * @see org.milyn.delivery.ordering.Consumer#consumes(java.lang.String)
     */
    public boolean consumes(Object object) {
    	if(wireBeanIdName != null && object.equals(wireBeanIdName)) {
    		return true;
    	} else if(expression != null && expression.getExpression().indexOf(object.toString()) != -1) {
    		return true;
    	}
    	return false;
    }

    /* (non-Javadoc)
     * @see org.milyn.delivery.ordering.Producer#getProducts()
     */
	@SuppressWarnings("unchecked")
	public Set<? extends Object> getProducts() {
    	return CollectionsUtil.toSet(parameter);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
    	if(beanWiring) {
        	bindBeanValue(executionContext);
        } else if(isAttribute) {
            bindDomDataValue(element, executionContext);
        }
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
    	if(!beanWiring && !isAttribute) {
            bindDomDataValue(element, executionContext);
    	}
    	if(beanWiring) {
    		removeBeanLifecycleObserver(executionContext);
    	}
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {

        if (!isAttribute) {
            // It's not an attribute binding i.e. it's the element's text.
            // Turn on Text Accumulation...
            element.accumulateText();
        }

        if(beanWiring) {
        	bindBeanValue(executionContext);
        } else if(isAttribute) {
            // Bind attribute (i.e. selectors with '@' prefix) values on the visitBefore...
            bindSaxDataValue(element, executionContext);
        }
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    	if(!beanWiring && !isAttribute) {
            bindSaxDataValue(element, executionContext);
    	}
    	if(beanWiring) {
    		removeBeanLifecycleObserver(executionContext);
    	}
    }


    private void bindDomDataValue(Element element, ExecutionContext executionContext) {
        String dataString;

        if (isAttribute) {
            dataString = DomUtils.getAttributeValue(element, valueAttributeName);
        } else {
            dataString = DomUtils.getAllText(element, false);
        }

        if(expression != null) {
            bindExpressionValue(executionContext);
        } else {
            populateAndSetPropertyValue(dataString, executionContext);
        }
    }

    private void bindSaxDataValue(SAXElement element, ExecutionContext executionContext) {
        String dataString;

        if (isAttribute) {
            dataString = SAXUtil.getAttribute(valueAttributeName, element.getAttributes());
        } else {
            dataString = element.getTextAsString();
        }


        if(expression != null) {
            bindExpressionValue(executionContext);
        } else {
            populateAndSetPropertyValue(dataString, executionContext);
        }
    }

    private BeanId getWireBeanId() {
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
            // we look if we got something we can set immediately or that we got an array collection. For an array collection we need the array representation
            // and not the list representation. So we register and observer who looks for the change from the list to the array
        	beanRepository.addBeanLifecycleObserver(targetBeanId, BeanLifecycle.BEGIN, getId(), false, new BeanRepositoryLifecycleObserver(){

                public void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event) {

                    BeanRuntimeInfo wiredBeanRI = getWiredBeanRuntimeInfo();

                    if(wiredBeanRI != null && wiredBeanRI.getClassification() == Classification.ARRAY_COLLECTION ) {

                        // Register an observer which looks for the change that the mutable list of the selected bean gets converted to an array. We
                        // can then set this array
                    	beanRepository.addBeanLifecycleObserver( targetBeanId, BeanLifecycle.CHANGE, getId(), true, new BeanRepositoryLifecycleObserver() {
                            public void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event) {

                                populateAndSetPropertyValue(event.getBean(), executionContext);

                            }
                        });

                    } else {
                        populateAndSetPropertyValue(event.getBean(), executionContext);
                    }
                }

            });
        } else {
            populateAndSetPropertyValue(bean, executionContext);
        }
	}


    private void removeBeanLifecycleObserver(ExecutionContext executionContext) {
    	BeanRepositoryManager.getBeanRepository(executionContext)
    		.removeBeanLifecycleObserver(getWireBeanId(), BeanLifecycle.BEGIN, getId());
    }


    private void bindExpressionValue(ExecutionContext executionContext) {
        Map<String, Object> beanMap = BeanRepositoryManager.getBeanRepository(executionContext).getBeanMap();
        Object dataObject = expression.getValue(beanMap);

        if(dataObject instanceof String) {
            populateAndSetPropertyValue((String) dataObject, executionContext);
        } else {
            populateAndSetPropertyValue(dataObject, executionContext);
        }
    }


    private void populateAndSetPropertyValue(String dataString, ExecutionContext executionContext) {

        Object dataObject = decodeDataString(dataString, executionContext);

        populateAndSetPropertyValue(dataObject, executionContext);
    }

	private void populateAndSetPropertyValue(Object dataObject, ExecutionContext executionContext) {
    	if ( dataObject == null )
    	{
    		return;
    	}

    	ParameterContainer<Parameter<?>> container = ParameterManager.getParameterContainer(entityLocatorId, executionContext);
    	container.put(parameter, dataObject);
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
            // more than once...
            wiredBeanRuntimeInfo = BeanRuntimeInfo.getBeanRuntimeInfo(wireBeanIdName, appContext);
		}
		return wiredBeanRuntimeInfo;
	}

	public String getId() {
		return EntityLocatorParameterVisitor.class.getName() + "#" + entityLocatorId + "#" + name;
	}

}