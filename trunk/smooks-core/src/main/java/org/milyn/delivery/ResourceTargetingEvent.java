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
package org.milyn.delivery;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.event.ExecutionEvent;

import java.util.Arrays;

/**
 * Resource targeting event.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ResourceTargetingEvent<T> implements ExecutionEvent {

    private T target;
    private SmooksResourceConfiguration resourceConfig;
    private Object[] metadata;

    /**
     * Event constructor.
     * @param target The resource target.
     * @param resourceConfig The resource configuration.
     * @param metadata Optional event metadata.
     */
    public ResourceTargetingEvent(T target, SmooksResourceConfiguration resourceConfig, Object... metadata) {
        this.target = target;
        this.resourceConfig = resourceConfig;
        this.metadata = metadata;
    }

    /**
     * Get the event target.
     * @return The event target.
     */
    public T getTarget() {
        return target;
    }

    /**
     * Get the tagreted resource configuration.
     * @return The targeted resource configuration.
     */
    public SmooksResourceConfiguration getResourceConfig() {
        return resourceConfig;
    }

    /**
     * Set event metadata.
     * @param metadata Event metadata.
     */
    public void setMetadata(Object... metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the optional event metadata.
     * @return Event metadata.
     */
    public Object[] getMetadata() {
        return metadata;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Target: ").append(target).append(". ");
        builder.append("Resource: ").append(resourceConfig).append(". ");
        if(metadata != null) {
            builder.append("Event Metadata: ").append(Arrays.asList(metadata)).append(".");
        }

        return builder.toString();
    }
}
