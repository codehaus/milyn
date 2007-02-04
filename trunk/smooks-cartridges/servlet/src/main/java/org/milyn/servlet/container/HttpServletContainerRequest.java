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

package org.milyn.servlet.container;

import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.milyn.container.ContainerContext;
import org.milyn.container.ContainerRequest;
import org.milyn.container.ContainerSession;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryConfigImpl;
import org.milyn.delivery.ElementList;
import org.milyn.useragent.UAContext;
import org.milyn.useragent.UnknownUseragentException;
import org.milyn.servlet.ServletUAContext;

/**
 * Smooks ContainerRequest implementation for the HttpServlet container.
 * @author tfennelly
 */
public class HttpServletContainerRequest implements ContainerRequest {

	/**
	 * HttpServletRequest instance.
	 */
	private HttpServletRequest servletRequest;
	/**
	 * Requesting device UAContext.
	 */
	private UAContext uaContext;
	/**
	 * Requesting device ContentDeliveryConfigImpl.
	 */
	private ContentDeliveryConfig deliveryConfig;
	/**
	 * Associated ContainerSession for the servlet environment.
	 */
	private ContainerSession session;
	/**
	 * Associated ContainerContext for the servlet environment.
	 */
	private ContainerContext containerContext;
	/**
	 * ElementList table for request.
	 */
	private Hashtable elementListTable = new Hashtable();
	/**
	 * Request URI.
	 */
	private URI requestURI;

	/**
	 * Public Constructor.
	 * @param servletRequest HttpServletRequest instance.
	 * @param servletConfig ServletConfig instance.
	 * @throws UnknownUseragentException Unable to match device.
	 */
	public HttpServletContainerRequest(HttpServletRequest servletRequest, ServletConfig servletConfig, ServletContainerContext containerContext) throws UnknownUseragentException {
		if(servletRequest == null) {
			throw new IllegalArgumentException("null 'servletRequest' arg in constructor call.");
		}
		if(containerContext == null) {
			throw new IllegalArgumentException("null 'containerContext' arg in constructor call.");
		}		
		this.servletRequest = servletRequest;
		this.containerContext = containerContext;
		uaContext = ServletUAContext.getInstance(servletRequest, servletConfig);
		session = new HttpServletContainerSession(servletRequest.getSession());
		deliveryConfig = ContentDeliveryConfigImpl.getInstance(uaContext, containerContext);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getContextPath()
	 */
	public String getContextPath() {
		return servletRequest.getContextPath();
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getRequestURI()
	 */
	public URI getRequestURI() {
		if(requestURI == null) {
			String queryString = servletRequest.getQueryString();
			if(queryString != null) {
				requestURI = URI.create(servletRequest.getRequestURL().toString() + "?" + queryString);
			} else {
				requestURI = URI.create(servletRequest.getRequestURL().toString());
			}
		}
		return requestURI;
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.useragent.request.HttpRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		return servletRequest.getHeader(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.useragent.request.HttpRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String name) {
		return servletRequest.getParameter(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		return servletRequest.getParameterNames();
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		return servletRequest.getParameterValues(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getContext()
	 */
	public ContainerContext getContext() {
		return containerContext;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getSession()
	 */
	public ContainerSession getSession() {
		return session;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getUseragentContext()
	 */
	public UAContext getUseragentContext() {
		return uaContext;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getDeliveryConfig()
	 */
	public ContentDeliveryConfig getDeliveryConfig() {
		return deliveryConfig;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
		servletRequest.setAttribute(name, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return servletRequest.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		servletRequest.removeAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getElementList(java.lang.String)
	 */
	public ElementList getElementList(String name) {
		String nameLower = name.toLowerCase();
		ElementList list = (ElementList)elementListTable.get(nameLower);
		
		if(list == null) {
			list = new ElementList();
			elementListTable.put(nameLower, list);
		}
		
		return list;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#clearElementLists()
	 */
	public void clearElementLists() {
		elementListTable.clear();
	}
	
	/**
	 * Get the HttpServletRequest instance associated with this ContainerRequest 
	 * implementation.
	 * @return HttpServletRequest instance.
	 */
	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}
}
