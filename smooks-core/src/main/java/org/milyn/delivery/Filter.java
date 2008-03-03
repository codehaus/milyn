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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.io.NullReader;
import org.milyn.io.NullWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Content filter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class Filter {

    private static Log logger = LogFactory.getLog(Filter.class);

    /**
     * The Threadlocal storage instance for the ExecutionContext associated with the "current" SmooksDOMFilter thread instance.
     */
    private static final ThreadLocal<ExecutionContext> execThreadLocal = new ThreadLocal<ExecutionContext>();

    public static final String CLOSE_SOURCE = "close.source";
    public static final String CLOSE_RESULT = "close.result";
    public static final String REVERSE_VISIT_ORDER_ON_VISIT_AFTER = "reverse.visit.order.on.visit.after";    
    public static final String TERMINATE_ON_VISITOR_EXCEPTION = "terminate.on.visitor.exception";    

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
        return execThreadLocal.get();
    }

    /**
     * Set the {@link org.milyn.container.ExecutionContext} instance for the current thread.
     *
     * @param executionContext The thread-bound {@link org.milyn.container.ExecutionContext} instance.
     */
    public static void setCurrentExecutionContext(ExecutionContext executionContext) {
        Filter.execThreadLocal.set(executionContext);
    }

    /**
     * Remove the {@link org.milyn.container.ExecutionContext} bound to the current thread.
     */
    public static void removeCurrentExecutionContext() {
        Filter.execThreadLocal.remove();
    }

    protected Reader getReader(Source source, ExecutionContext executionContext) {
        if(source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            if(streamSource.getReader() != null) {
                return streamSource.getReader();
            } else if(streamSource.getInputStream() != null) {
                try {
                    if(executionContext instanceof ExecutionContext) {
                        return new InputStreamReader(streamSource.getInputStream(), executionContext.getContentEncoding());
                    } else {
                        return new InputStreamReader(streamSource.getInputStream(), "UTF-8");
                    }
                } catch(UnsupportedEncodingException e) {
                    throw new SmooksException("Unable to decode input stream.", e);
                }
            } else {
                throw new SmooksException("Invalid " + StreamSource.class.getName() + ".  No InputStream or Reader instance.");
            }
        }

        return new NullReader();
    }

    protected Writer getWriter(Result result, ExecutionContext executionContext) {
        if(!(result instanceof StreamResult)) {
            return new NullWriter();
        }

        StreamResult streamResult = (StreamResult) result;
        if(streamResult.getWriter() != null) {
            return streamResult.getWriter();
        } else if(streamResult.getOutputStream() != null) {
            try {
                if(executionContext instanceof ExecutionContext) {
                    return new OutputStreamWriter(streamResult.getOutputStream(), executionContext.getContentEncoding());
                } else {
                    return new OutputStreamWriter(streamResult.getOutputStream(), "UTF-8");
                }
            } catch(UnsupportedEncodingException e) {
                throw new SmooksException("Unable to encode output stream.", e);
            }
        } else {
            throw new SmooksException("Invalid " + StreamResult.class.getName() + ".  No OutputStream or Writer instance.");
        }
    }

    protected void close(Source source) {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            try {
                if(streamSource.getReader() != null) {
                    streamSource.getReader().close();
                } else if(streamSource.getInputStream() != null) {
                    streamSource.getInputStream().close();
                }
            } catch (Throwable throwable) {
                logger.warn("Failed to close input stream/reader.", throwable);
            }
        }
    }

    protected void close(Result result) {
        if (result instanceof StreamResult) {
            StreamResult streamResult = ((StreamResult) result);

            try {
                if (streamResult.getWriter() != null) {
                    Writer writer = streamResult.getWriter();
                    try {
                        writer.flush();
                    } finally {
                        writer.close();
                    }
                } else if (streamResult.getOutputStream() != null) {
                    OutputStream stream = streamResult.getOutputStream();
                    try {
                        stream.flush();
                    } finally {
                        stream.close();
                    }
                }
            } catch (Throwable throwable) {
                logger.debug("Failed to close output stream/writer.  May already be closed.", throwable);
            }
        }
    }
}