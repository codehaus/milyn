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

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ElementList;
import org.milyn.delivery.ContentDeliveryConfigImpl;
import org.milyn.util.IteratorEnumeration;
import org.milyn.profile.ProfileSet;
import org.milyn.profile.UnknownProfileMemberException;

/**
 * Standalone Container Request implementation.
 * @author tfennelly
 */
public class StandaloneExecutionContext implements ExecutionContext {

    private ProfileSet targetProfileSet;
    private Hashtable attributes = new Hashtable();
	private Hashtable elementListTable = new Hashtable();
    private ContentDeliveryConfig deliveryConfig;
    private URI docSource;
    private LinkedHashMap parameters;
	private String contentEncoding;
    private StandaloneApplicationContext context;

    /**
     * Public Constructor.
     * <p/>
     * The request is constructed witin the scope of a container session.  The
     * session is associated with a specific useragent name.
     * @param context The application context.
     * @throws UnknownProfileMemberException Unknown target profile.
     */
    public StandaloneExecutionContext(String targetProfile, StandaloneApplicationContext context) throws UnknownProfileMemberException {
        this(targetProfile, null, context);
    }
    
	/**
	 * Public Constructor.
	 * <p/>
	 * The request is constructed witin the scope of a container session.  The
	 * session is associated with a specific useragent name.
	 * @param targetProfile The target base profile for the execution context.
	 * @param parameters The request parameters.  The parameter values should be String arrays i.e. {@link String String[]}.
	 * These parameters are not appended to the supplied requestURI.  This arg must be supplied, even if it's empty.
     * @param context The application context.
     * @throws UnknownProfileMemberException Unknown target profile.
	 */
	public StandaloneExecutionContext(String targetProfile, LinkedHashMap parameters, StandaloneApplicationContext context) throws UnknownProfileMemberException {
		this(targetProfile, parameters, context, "UTF-8");
	}
    
	/**
	 * Public Constructor.
	 * <p/>
	 * The request is constructed witin the scope of a container session.  The
	 * session is associated with a specific useragent name.
	 * @param targetProfile The target profile (base profile) for this context.
	 * @param parameters The request parameters.  The parameter values should be String arrays i.e. {@link String String[]}.
	 * These parameters are not appended to the supplied requestURI.  This arg must be supplied, even if it's empty.
     * @param context The application context.
	 * @param contentEncoding Character encoding to be used when parsing content.  Null 
	 * defaults to "UTF-8".
     * @throws UnknownProfileMemberException Unknown target profile.
	 */
	public StandaloneExecutionContext(String targetProfile, LinkedHashMap parameters, StandaloneApplicationContext context, String contentEncoding) throws UnknownProfileMemberException {
        if(targetProfile == null) {
            throw new IllegalArgumentException("null 'targetProfile' arg in constructor call.");
        }
        if(context == null) {
            throw new IllegalArgumentException("null 'context' arg in constructor call.");
        }
        if(parameters != null) {
            this.parameters = parameters;
        } else {
            this.parameters = new LinkedHashMap();
        }
		this.context = context;
		setContentEncoding(contentEncoding);
        targetProfileSet = context.getProfileStore().getProfileSet(targetProfile);        
        deliveryConfig = ContentDeliveryConfigImpl.getInstance(targetProfileSet, context);
    }

    public void setDocumentSource(URI docSource) {
        this.docSource = docSource;
    }

    public URI getDocumentSource() {
		if(docSource == null) {
			return ExecutionContext.DOCUMENT_URI;
		}
		return docSource;
	}

	public Enumeration getParameterNames() {
		return new IteratorEnumeration(parameters.keySet().iterator());
	}

	public String[] getParameterValues(String name) {
		Object value = parameters.get(name);
		
		if(value instanceof String[]) {
			return (String[])value;
		}
		throw new IllegalStateException("Request [" + docSource + "] parameter [" + name + "] must be of type java.lang.String[] i.e. a String array.");
	}

	public ApplicationContext getContext() {
		return context;
	}

	public ProfileSet getTargetProfiles() {
		return targetProfileSet;
	}

	public ContentDeliveryConfig getDeliveryConfig() {
		return deliveryConfig;
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
