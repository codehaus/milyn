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

package org.milyn.container;

import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ElementList;
import org.milyn.delivery.MockContentDeliveryConfig;
import org.milyn.useragent.MockUAContext;
import org.milyn.useragent.UAContext;
import org.milyn.util.IteratorEnumeration;

/**
 * 
 * @author tfennelly
 */
public class MockContainerRequest implements ContainerRequest {

	public String contextPath;
	public URI requestURI;
	public MockUAContext uaContext;
	public ContentDeliveryConfig deliveryConfig = new MockContentDeliveryConfig();
	public MockContainerSession session = new MockContainerSession();
	public MockContainerContext context = new MockContainerContext();
	private Hashtable attributes = new Hashtable();
	public LinkedHashMap parameters = new LinkedHashMap();
	public Hashtable headers = new Hashtable();
	public Hashtable elementListTable = new Hashtable();
	

	public String getContextPath() {
		return contextPath;
	}

	public URI getRequestURI() {
		return requestURI;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		return (new IteratorEnumeration(parameters.keySet().iterator()));
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		return (String[])parameters.get(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getContext()
	 */
	public ContainerContext getContext() {
		if(context == null) {
			throw new IllegalStateException("Call to getContext before context member has been initialised.  Set the 'context' member variable.");
		}
		return context;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getSession()
	 */
	public ContainerSession getSession() {
		if(session == null) {
			throw new IllegalStateException("Call to getSession before session member has been initialised.  Set the 'session' member variable.");
		}
		return session;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getUseragentContext()
	 */
	public UAContext getUseragentContext() {
		if(uaContext == null) {
			throw new IllegalStateException("Call to getUseragentContext before uaContext member has been initialised.  Set the 'uaContext' member variable.");
		}
		return uaContext;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerRequest#getDeliveryConfig()
	 */
	public ContentDeliveryConfig getDeliveryConfig() {
		if(deliveryConfig == null) {
			throw new IllegalStateException("Call to getDeliveryConfig before deliveryConfig member has been initialised.  Set the 'deliveryConfig' member variable.");
		}
		return deliveryConfig;
	}

	/* (non-Javadoc)
	 * @see org.milyn.device.request.HttpRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		return (String)headers.get(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.device.request.HttpRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String name) {
		return (String)parameters.get(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
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
    
    public MockContentDeliveryConfig getMockDeliveryConfig() {
        return (MockContentDeliveryConfig) this.deliveryConfig;
    }
}
