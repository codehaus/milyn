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

import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.profile.ProfileSet;

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
}
