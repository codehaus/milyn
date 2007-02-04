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

package org.milyn.container.standalone;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.milyn.container.ContainerContext;
import org.milyn.container.ContainerRequest;
import org.milyn.container.ContainerSession;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ElementList;
import org.milyn.useragent.UAContext;
import org.milyn.util.IteratorEnumeration;

/**
 * Standalone Container Request implementation.
 * @author tfennelly
 */
public class StandaloneContainerRequest implements ContainerRequest {

	private Hashtable attributes = new Hashtable();
	private StandaloneContainerSession session;
	private Hashtable elementListTable = new Hashtable();
	private URI requestURI;
	private LinkedHashMap parameters;
	private String contentEncoding;
    
    /**
     * Public Constructor.
     * <p/>
     * The request is constructed witin the scope of a container session.  The
     * session is associated with a specific useragent name.
     * @param session The container session within which this request is to be processed.
     */
    public StandaloneContainerRequest(StandaloneContainerSession session) {
        this(null, null, session);
    }
    
	/**
	 * Public Constructor.
	 * <p/>
	 * The request is constructed witin the scope of a container session.  The
	 * session is associated with a specific useragent name.
	 * @param requestURI The requestURI to be processed.
	 * @param parameters The request parameters.  The parameter values should be String arrays i.e. {@link String String[]}.
	 * These parameters are not appended to the supplied requestURI.  This arg must be supplied, even if it's empty.
	 * @param session The container session within which this request is to be processed.
	 */
	public StandaloneContainerRequest(URI requestURI, LinkedHashMap parameters, StandaloneContainerSession session) {
		this(requestURI, parameters, session, "UTF-8");
	}
    
	/**
	 * Public Constructor.
	 * <p/>
	 * The request is constructed witin the scope of a container session.  The
	 * session is associated with a specific useragent name.
	 * @param requestURI The requestURI to be processed.
	 * @param parameters The request parameters.  The parameter values should be String arrays i.e. {@link String String[]}.
	 * These parameters are not appended to the supplied requestURI.  This arg must be supplied, even if it's empty.
	 * @param session The container session within which this request is to be processed.
	 * @param contentEncoding Character encoding to be used when parsing content.  Null 
	 * defaults to "UTF-8".
	 */
	public StandaloneContainerRequest(URI requestURI, LinkedHashMap parameters, StandaloneContainerSession session, String contentEncoding) {
		if(session == null) {
			throw new IllegalArgumentException("null 'session' arg in constructor call.");
		}
		this.requestURI = requestURI;
        if(parameters != null) {
            this.parameters = parameters;
        } else {
            this.parameters = new LinkedHashMap();
        }
		this.session = session;
		setContentEncoding(contentEncoding);
	}

	public String getContextPath() {
		return "/";
	}
	
	public URI getRequestURI() {
		if(requestURI == null) {
			return URI.create("http://milyn.codehaus.org/request/uri/not/set");
		}
		return requestURI;
	}

	public Enumeration getParameterNames() {
		return new IteratorEnumeration(parameters.keySet().iterator());
	}

	public String[] getParameterValues(String name) {
		Object value = parameters.get(name);
		
		if(value instanceof String[]) {
			return (String[])value;
		}
		throw new IllegalStateException("Request [" + requestURI + "] parameter [" + name + "] must be of type java.lang.String[] i.e. a String array.");
	}

	public ContainerContext getContext() {
		return session.getContext();
	}

	public ContainerSession getSession() {
		return session;
	}

	public UAContext getUseragentContext() {
		return session.getUseragentContext();
	}

	public ContentDeliveryConfig getDeliveryConfig() {
		return session.getDeliveryConfig();
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

	/* (non-Javadoc)
	 * @see org.milyn.device.request.HttpRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	/**
	 * Set the content encoding to be used when parsing content on this standalone request instance. 
	 * @param contentEncoding Character encoding to be used when parsing content.  Null 
	 * defaults to "UTF-8".
	 * @throws IllegalArgumentException Invalid encoding.
	 */
	public void setContentEncoding(String contentEncoding) throws IllegalArgumentException {
		contentEncoding = (contentEncoding == null)?"UTF-8":contentEncoding;
		try {
			// Make sure the encoding is supported....
			"".getBytes(contentEncoding);
		} catch (UnsupportedEncodingException e) {
			IllegalArgumentException argE = new IllegalArgumentException("Invalid 'contentEncoding' arg [" + contentEncoding + "].  This encoding is not supported.");
			argE.initCause(e);
			throw argE;
		}
		this.contentEncoding = contentEncoding;
	}

	/**
	 * Get the content encoding to be used when parsing content on this standalone request instance. 
	 * @return Character encoding to be used when parsing content.  Defaults to "UTF-8".
	 */
	public String getContentEncoding() {
		return (contentEncoding == null)?"UTF-8":contentEncoding;
	}

	public String getParameter(String name) {
		return null;
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
}
