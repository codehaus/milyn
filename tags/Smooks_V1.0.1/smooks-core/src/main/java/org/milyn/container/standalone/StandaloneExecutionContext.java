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
import java.util.Hashtable;

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.event.ExecutionEventListener;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryConfigBuilder;
import org.milyn.profile.ProfileSet;
import org.milyn.profile.UnknownProfileMemberException;

/**
 * Standalone Container Request implementation.
 * @author tfennelly
 */
public class StandaloneExecutionContext implements ExecutionContext {

    private ProfileSet targetProfileSet;
    private Hashtable attributes = new Hashtable();
    private ContentDeliveryConfig deliveryConfig;
    private URI docSource;
	private String contentEncoding;
    private ApplicationContext context;
    private ExecutionEventListener executionListener;
    private Throwable terminationError;

    /**
	 * Public Constructor.
	 * <p/>
     * The execution context is constructed within the context of a target profile and
     * application context.
	 * @param targetProfile The target base profile for the execution context.
	 * These parameters are not appended to the supplied requestURI.  This arg must be supplied, even if it's empty.
     * @param context The application context.
     * @throws UnknownProfileMemberException Unknown target profile.
	 */
	public StandaloneExecutionContext(String targetProfile, ApplicationContext context) throws UnknownProfileMemberException {
		this(targetProfile, context, "UTF-8");
	}
    
	/**
	 * Public Constructor.
	 * <p/>
     * The execution context is constructed within the context of a target profile and
     * application context.
	 * @param targetProfile The target profile (base profile) for this context.
	 * These parameters are not appended to the supplied requestURI.  This arg must be supplied, even if it's empty.
     * @param context The application context.
	 * @param contentEncoding Character encoding to be used when parsing content.  Null 
	 * defaults to "UTF-8".
     * @throws UnknownProfileMemberException Unknown target profile.
	 */
	public StandaloneExecutionContext(String targetProfile, ApplicationContext context, String contentEncoding) throws UnknownProfileMemberException {
        if(targetProfile == null) {
            throw new IllegalArgumentException("null 'targetProfile' arg in constructor call.");
        }
        if(context == null) {
            throw new IllegalArgumentException("null 'context' arg in constructor call.");
        }
		this.context = context;
		setContentEncoding(contentEncoding);
        targetProfileSet = context.getProfileStore().getProfileSet(targetProfile);        
        deliveryConfig = ContentDeliveryConfigBuilder.getConfig(targetProfileSet, context);
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

	public ApplicationContext getContext() {
		return context;
	}

	public ProfileSet getTargetProfiles() {
		return targetProfileSet;
	}

	public ContentDeliveryConfig getDeliveryConfig() {
		return deliveryConfig;
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

    public void setEventListener(ExecutionEventListener listener) {
        this.executionListener = listener;
    }

    public ExecutionEventListener getEventListener() {
        return executionListener;
    }

    public void setTerminationError(Throwable terminationError) {
        this.terminationError = terminationError;
    }

    public Throwable getTerminationError() {
        return terminationError;
    }

    /* (non-Javadoc)
      * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.Object, java.lang.Object)
      */
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.Object)
	 */
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.Object)
	 */
	public void removeAttribute(Object key) {
		attributes.remove(key);
	}

    public String toString() {
        return attributes.toString();
    }
    
    public Hashtable getAttributes()
    {
    	return attributes;
    }
}