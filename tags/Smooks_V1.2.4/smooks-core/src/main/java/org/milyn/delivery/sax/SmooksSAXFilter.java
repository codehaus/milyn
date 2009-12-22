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
package org.milyn.delivery.sax;

import org.milyn.SmooksException;
import org.milyn.payload.JavaSource;
import org.milyn.payload.FilterResult;
import org.milyn.payload.FilterSource;
import org.milyn.cdr.ParameterAccessor;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.Writer;

/**
 * Smooks SAX Filter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksSAXFilter extends Filter {

    private ExecutionContext executionContext;
    private SAXParser parser;
    private boolean closeSource;
    private boolean closeResult;

    public SmooksSAXFilter(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        closeSource = ParameterAccessor.getBoolParameter(Filter.CLOSE_SOURCE, true, executionContext.getDeliveryConfig());
        closeResult = ParameterAccessor.getBoolParameter(Filter.CLOSE_RESULT, true, executionContext.getDeliveryConfig());
        parser = new SAXParser(executionContext);
    }

    public void doFilter() throws SmooksException {
        Source source = FilterSource.getSource(executionContext);
        Result result = FilterResult.getResult(executionContext, StreamResult.class);

        doFilter(source, result);
    }

    protected void doFilter(Source source, Result result) {
        if (!(source instanceof StreamSource) && !(source instanceof JavaSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported by the SAX Filter. Only supports StreamSource and JavaSource at present.");
        }
        if(!(result instanceof FilterResult)) {
            if (!(result instanceof StreamResult) && result != null) {
                throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported by the SAX Filter. Only supports StreamResult at present.");
            }
        }

        try {
            Writer writer = parser.parse(source, result, executionContext);
            writer.flush();
        } catch (Exception e) {
            throw new SmooksException("Failed to filter source.", e);
        } finally {
            if(closeSource) {
                close(source);
            }
            if(closeResult) {
                close(result);
            }
        }
    }

    public void cleanup() {
        parser.cleanup();
    }
}
