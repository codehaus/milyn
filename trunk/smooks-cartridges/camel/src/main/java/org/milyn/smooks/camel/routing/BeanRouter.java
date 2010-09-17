/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.smooks.camel.routing;

import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ExecutionLifecycleCleanable;
import org.milyn.delivery.ExecutionLifecycleInitializable;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.Uninitialize;
import org.milyn.delivery.ordering.Consumer;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;

/**
 * Camel bean routing visitor.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public class BeanRouter implements SAXVisitAfter, Consumer, ExecutionLifecycleInitializable, ExecutionLifecycleCleanable {
    
    @ConfigParam
    private String beanId;
    
    @ConfigParam
    private String toEndpoint;
    
    @AppContext
    private ApplicationContext applicationContext;
    
    @Config
    SmooksResourceConfiguration routingConfig;

    private ProducerTemplate producerTemplate;
    private BeanRouterObserver camelRouterObserable;
    private CamelContext camelContext;
    
    public BeanRouter() {
    }
    
    public BeanRouter(final CamelContext camelContext) {
       this.camelContext = camelContext; 
    }

    /**
     * Set the beanId of the bean to be routed.
     * 
     * @param beanId
     *            the beanId to set
     * @return This router instance.
     */
    public BeanRouter setBeanId(final String beanId) {
        this.beanId = beanId;
        return this;
    }

    /**
     * Set the Camel endpoint to which the bean is to be routed.
     * 
     * @param toEndpoint
     *            the toEndpoint to set
     * @return This router instance.
     */
    public BeanRouter setToEndpoint(final String toEndpoint) {
        this.toEndpoint = toEndpoint;
        return this;
    }

    public void visitAfter(final SAXElement element, final ExecutionContext execContext) throws SmooksException, IOException
    {
        final Object bean = getBeanFromExecutionContext(execContext, beanId);
        try {
            producerTemplate.sendBody(toEndpoint, bean);
        }  catch (final Exception e) {
            throw new SmooksException("Exception routing beanId '" + beanId + "' to endpoint '" + toEndpoint + "'.", e);
        }
    }

    private Object getBeanFromExecutionContext(final ExecutionContext execContext, final String beanId) {
        final Object bean = execContext.getBeanContext().getBean(beanId);
        if (bean == null) {
            throw new SmooksException("Exception routing beanId '" + beanId
                    + "'. The bean was not found in the Smooks ExceutionContext.");
        }

        return bean;
    }

    @Initialize
    public void initialize() {
        producerTemplate = getCamelContext().createProducerTemplate();
        if (isBeanRoutingConfigured()) {
	        camelRouterObserable = new BeanRouterObserver(producerTemplate, toEndpoint, beanId);
        }
    }
    
    private CamelContext getCamelContext() {
        if (camelContext == null)
	        return (CamelContext) applicationContext.getAttribute(CamelContext.class);
        else
            return camelContext;
    }
    
    private boolean isBeanRoutingConfigured() {
        return "none".equals(routingConfig.getSelector());
    }

    @Uninitialize
    public void uninitialize() {
        try {
            producerTemplate.stop();
        }  catch (final Exception e) {
            throw new SmooksException(e.getMessage(), e);
        }
    }

    public boolean consumes(final Object object) {
        return beanId.equals(object);
    }

    public void executeExecutionLifecycleInitialize(final ExecutionContext executionContext) {
        if (isBeanRoutingConfigured()) {
            executionContext.getBeanContext().addObserver(camelRouterObserable);
        }
    }
    
    public void executeExecutionLifecycleCleanup(ExecutionContext executionContext) {
        if (isBeanRoutingConfigured()) {
            executionContext.getBeanContext().removeObserver(camelRouterObserable);
        }
    }
    
}
