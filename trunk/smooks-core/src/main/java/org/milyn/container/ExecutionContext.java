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

import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.report.ReportConfiguration;
import org.milyn.profile.ProfileSet;

import java.net.URI;

/**
 * Smooks execution context interface definition.
 *
 * @author tfennelly
 */
public interface ExecutionContext extends BoundAttributeStore {

    /**
     * Sometimes the document being transformed/analysed has a URI associated with it.
     * This can be bound to the execution context under this key.
     */
    public static final URI DOCUMENT_URI = URI.create("org:milyn:smooks:unknowndoc");

    /**
     * Set the document source URI.
     *
     * @param docSource The document URI.
     */
    public void setDocumentSource(URI docSource);

    /**
     * Get the document source URI.
     * <p/>
     * If the document source URI is not set for the context, implementations should
     * return the {@link #DOCUMENT_URI} constant.
     *
     * @return The document URI.
     */
    public URI getDocumentSource();

    /**
     * Get the application context within which this execution context "lives".
     *
     * @return The ApplicationContext instance.
     */
    public abstract ApplicationContext getContext();

    /**
     * Get the set of profiles at which this execution context is targeted.
     * <p/>
     * Basically, the set of profiles for which this execution context is to perform
     * transformation/analysis.
     *
     * @return The target {@link org.milyn.profile.ProfileSet}.
     */
    public abstract ProfileSet getTargetProfiles();

    /**
     * Get the content delivery configuration for the profile set at which this
     * context is targeted.
     *
     * @return ContentDeliveryConfig instance.
     */
	public abstract ContentDeliveryConfig getDeliveryConfig();


    /**
     * Set the content encoding to be used when parsing content on this context.
     * @param contentEncoding Character encoding to be used when parsing content.  Null
     * defaults to "UTF-8".
     * @throws IllegalArgumentException Invalid encoding.
     */
    public abstract void setContentEncoding(String contentEncoding) throws IllegalArgumentException;

    /**
     * Get the content encoding to be used when parsing content on this context.
     * @return Character encoding to be used when parsing content.  Defaults to "UTF-8".
     */
    public abstract String getContentEncoding();

    /**
     * Set the ExecutionEventListener for the {@link ExecutionContext}.
     * <p/>
     * Allows calling code to listen to (and capture data on) specific
     * context execution events e.g. the targeting of resources.
     * <p/>
     * Note, this is not a logging facility and should be used with care.
     * It's overuse should be avoided as it can have a serious negative effect
     * on performance.  By default, no listenrs are applied and so no overhead
     * is incured.
     *
     * @param listener The listener instance.
     * @see org.milyn.event.BasicExecutionEventListener
     */
    public abstract void setEventListener(ExecutionEventListener listener);

    /**
     * Get the ExecutionEventListener for the {@link ExecutionContext}.
     * @return The listener instance.
     * @see #setEventListener(ExecutionEventListener) 
     */
    public abstract ExecutionEventListener getEventListener();

    /**
     * Set the report configuration if an execution report is required.
     * @param reportConfig Execution report configuration.
     */
    public abstract void setReportConfiguration(ReportConfiguration reportConfig);

    /**
     * Get the execution report configuration.
     * @return Execution report configuration, or null if none configured.
     */
    public abstract ReportConfiguration getReportConfiguration();
}
