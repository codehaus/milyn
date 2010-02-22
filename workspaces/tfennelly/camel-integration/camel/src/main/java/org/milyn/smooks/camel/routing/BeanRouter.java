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

import org.apache.camel.builder.RouteBuilder;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.ordering.Consumer;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;

/**
 * Camel bean routing visitor.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanRouter implements SAXVisitAfter, Consumer {

	private BeanEndpoint fromEndpoint;
	
	@ConfigParam
	private String beanId;
	
	@ConfigParam
	private String toEndpoint;
	
	@AppContext
	private ApplicationContext appContext;
	
	@Initialize
	public void addRoute() {
		RouteBuilder routeBuilder = (RouteBuilder) appContext.getAttribute(RouteBuilder.class);
		
		if(routeBuilder == null) {
			throw new SmooksConfigurationException("RouteBuilder instance not set on Smooks ApplicationContext.  Supply RouteBuilder instance to SmooksProcessor constructor.");
		}
		
		fromEndpoint = new BeanEndpoint(beanId, toEndpoint);
		fromEndpoint.setCamelContext(routeBuilder.getContext());
		routeBuilder.from(fromEndpoint).to(toEndpoint);
	}
	
	/**
	 * Set the beanId of the bean to be routed.
	 * @param beanId the beanId to set
	 * @return This router instance.
	 */
	public BeanRouter setBeanId(String beanId) {
		this.beanId = beanId;
		return this;
	}

	/**
	 * Set the Camel endpoint to which the bean is to be routed.
	 * @param toEndpoint the toEndpoint to set
	 * @return This router instance.
	 */
	public BeanRouter setToEndpoint(String toEndpoint) {
		this.toEndpoint = toEndpoint;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitAfter#visitAfter(org.milyn.delivery.sax.SAXElement, org.milyn.container.ExecutionContext)
	 */
	public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
		Object bean = executionContext.getBeanContext().getBean(beanId);
		
		if(bean != null) {
			try {
				fromEndpoint.sendBean(bean);
			} catch (Exception e) {
				throw new SmooksException("Exception routing beanId '" + beanId +"' to endpoint '" + toEndpoint + "'.", e);
			}
		} else {
			throw new SmooksException("Exception routing beanId '" + beanId +"' to endpoint '" + toEndpoint + "'. Bean not available in BeanContext.  Bean may not have been created yet - check the 'routeOnElement' configuration.");			
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Consumer#consumes(java.lang.Object)
	 */
	public boolean consumes(Object object) {
		return beanId.equals(object);
	}
}
