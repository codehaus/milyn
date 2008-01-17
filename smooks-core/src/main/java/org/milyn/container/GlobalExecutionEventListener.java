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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Global {@link ExecutionEventListener}.
 * <p/>
 * This event listener listens to and captures all published events.
 * <p/>
 * This listener should be used with great care.  Because it captures and stores
 * all published information, it could quite easily consume large amounts of memory.
 * If access to this information is required in a production environment,
 * consider writing and using a more specialized implementation of the
 * {@link ExecutionEventListener} interface i.e. an implementation that captures the 
 * information in a more memory-friendly way..
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class GlobalExecutionEventListener implements ExecutionEventListener {

    private static Log logger = LogFactory.getLog(GlobalExecutionEventListener.class);
    
    private List<ExecutionEvent> events = new ArrayList<ExecutionEvent>();

    /**
     * Process the {@link ExecutionEvent}.
     * @param event The {@link ExecutionEvent}.
     */
    public void onEvent(ExecutionEvent event) {
        if(event != null) {
            events.add(event);
        } else {
            logger.warn("Invalid call to onEvent method.  null 'event' arg.");
        }
    }

    /**
     * Get the {@link ExecutionEvent} list.
     * @return The {@link ExecutionEvent} list.
     */
    public List<ExecutionEvent> getEvents() {
        return events;
    }
}
