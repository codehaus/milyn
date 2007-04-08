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

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryConfigImpl;
import org.milyn.delivery.ElementList;
import org.milyn.useragent.UAContext;
import org.milyn.useragent.UnknownUseragentException;
import org.milyn.servlet.ServletUAContext;
import org.milyn.profile.ProfileSet;

/**
 * Smooks ExecutionContext implementation for the HttpServlet container.
 * @author tfennelly
 */
public class HttpServletExecutionContext implements ExecutionContext {

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
	 * Associated ApplicationContext for the servlet environment.
	 */
	private ApplicationContext applicationContext;
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
	public HttpServletExecutionContext(HttpServletRequest servletRequest, ServletConfig servletConfig, ServletApplicationContext containerContext) throws UnknownUseragentException {
		if(servletRequest == null) {
			throw new IllegalArgumentException("null 'servletRequest' arg in constructor call.");
		}
		if(containerContext == null) {
			throw new IllegalArgumentException("null 'applicationContext' arg in constructor call.");
		}		
		this.servletRequest = servletRequest;
		this.applicationContext = containerContext;
		uaContext = ServletUAContext.getInstance(servletRequest, servletConfig);
		deliveryConfig = ContentDeliveryConfigImpl.getInstance(uaContext.getProfileSet(), containerContext);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ExecutionContext#getDocumentSource()
	 */
	public URI getDocumentSource() {
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
	 * @see org.milyn.container.ExecutionContext#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		return servletRequest.getParameterNames();
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ExecutionContext#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		return servletRequest.getParameterValues(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ExecutionContext#getApplicationContext()
	 */
	public ApplicationContext getContext() {
		return applicationContext;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ExecutionContext#getTargetProfiles()
	 */
	public ProfileSet getTargetProfiles() {
		return uaContext.getProfileSet();
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ExecutionContext#getDeliveryConfig()
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
	 * @see org.milyn.container.ExecutionContext#getElementList(java.lang.String)
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
	 * @see org.milyn.container.ExecutionContext#clearElementLists()
	 */
	public void clearElementLists() {
		elementListTable.clear();
	}
	
	/**
	 * Get the HttpServletRequest instance associated with this ExecutionContext
	 * implementation.
	 * @return HttpServletRequest instance.
	 */
	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}
}
