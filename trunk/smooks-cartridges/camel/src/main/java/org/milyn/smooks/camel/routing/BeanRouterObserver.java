/*
 * Milyn - Copyright (C) 2006 - 2010
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.smooks.camel.routing;

import org.apache.camel.ProducerTemplate;
import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.lifecycle.BeanContextLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanContextLifecycleObserver;
import org.milyn.javabean.lifecycle.BeanLifecycle;

/**
 * BeanRouterObserver is a {@link BeanContextLifecycleObserver} that will route 
 * a specified bean to the configured endpoint. 
 * </p>
 * 
 * @author Daniel Bevenius
 */
public class BeanRouterObserver implements BeanContextLifecycleObserver
{
    private final ProducerTemplate producerTemplate;
    private final String endpointUri;
    private final String beanId;
    
    /**
     * Sole contructor.
     * @param producerTemplate The Camel {@link ProducerTemplate} used to route the bean.
     * @param endpointUri The endpoint uri that the bean should be routed to.
     * @param beanId The beanId which is the beanId in the Smooks {@link BeanContext}.
     */
    public BeanRouterObserver(final ProducerTemplate producerTemplate, final String endpointUri, final String beanId)
    {
        AssertArgument.isNotNull(producerTemplate, "producerTemplate");
        AssertArgument.isNotNull(endpointUri, "endpointUri");
        AssertArgument.isNotNull(beanId, "beanId");
        
        this.endpointUri = endpointUri;
        this.beanId = beanId;
        this.producerTemplate = producerTemplate;
    }
    
    /**
     * Will route to the endpoint if the BeanLifecycle is of type BeanLifecycle.END and
     * the beanId is equals to the beanId that was configured for this instance.
     */
    public void onBeanLifecycleEvent(final BeanContextLifecycleEvent event)
    {
        if (isEndEventAndBeanIdMatches(event))
        {
            producerTemplate.sendBody(endpointUri, event.getBean());
        }
    }
    
    private boolean isEndEventAndBeanIdMatches(final BeanContextLifecycleEvent event)
    {
        return event.getLifecycle() == BeanLifecycle.END && event.getBeanId().getName().equals(beanId);
        
    }
}
