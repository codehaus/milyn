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

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Content filter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class Filter {

    /**
     * The Threadlocal storage instance for the ExecutionContext associated with the "current" SmooksDOMFilter thread instance.
     */
    private static final ThreadLocal<ExecutionContext> requestThreadLocal = new ThreadLocal<ExecutionContext>();

    /**
     * Filter the content in the supplied {@link javax.xml.transform.Source} instance, outputing the result
     * to the supplied {@link javax.xml.transform.Result} instance.
     *
     * @param source           The content Source.
     * @param result           The content Result.  To serialize the result, supply a {@link javax.xml.transform.stream.StreamResult}.
     *                         To have the result returned as a DOM, supply a {@link javax.xml.transform.dom.DOMResult}.
     * @throws SmooksException Failed to filter.
     */
    public abstract void doFilter(Source source, Result result) throws SmooksException;

    /**
     * Get the {@link org.milyn.container.ExecutionContext} instance bound to the current thread.
     *
     * @return The thread-bound {@link org.milyn.container.ExecutionContext} instance.
     */
    public static ExecutionContext getCurrentExecutionContext() {
        return requestThreadLocal.get();
    }

    /**
     * Set the {@link org.milyn.container.ExecutionContext} instance for the current thread.
     *
     * @param executionContext The thread-bound {@link org.milyn.container.ExecutionContext} instance.
     */
    public static void setCurrentExecutionContext(ExecutionContext executionContext) {
        Filter.requestThreadLocal.set(executionContext);
    }

    /**
     * Remove the {@link org.milyn.container.ExecutionContext} bound to the current thread.
     */
    public static void removeCurrentExecutionContext() {
        Filter.requestThreadLocal.remove();
    }
}
