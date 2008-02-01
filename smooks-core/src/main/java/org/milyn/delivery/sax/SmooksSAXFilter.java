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
import org.milyn.cdr.ParameterAccessor;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.delivery.FilterResult;
import org.milyn.delivery.java.JavaSource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.Reader;
import java.io.Writer;

/**
 * Smooks SAX Filter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksSAXFilter extends Filter {
    
    private ExecutionContext executionContext;
    private boolean closeInput;
    private boolean closeOutput;

    public SmooksSAXFilter(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        closeInput = ParameterAccessor.getBoolParameter(Filter.CLOSE_INPUT, true, executionContext.getDeliveryConfig());
        closeOutput = ParameterAccessor.getBoolParameter(Filter.CLOSE_OUTPUT, true, executionContext.getDeliveryConfig());
    }

    public void doFilter(Source source, Result result) throws SmooksException {
        if (!(source instanceof StreamSource) && !(source instanceof JavaSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported by the SAX Filter. Only supports StreamSource and JavaSource at present.");
        }
        if(!(result instanceof FilterResult)) {
            if (!(result instanceof StreamResult) && result != null) {
                throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported by the SAX Filter. Only supports StreamResult at present.");
            }
        }

        try {
            Reader reader = getReader(source, executionContext);
            Writer writer = getWriter(result, executionContext);
            SAXParser parser = new SAXParser(executionContext);

            parser.parse(reader, writer);
            try {
                writer.flush();
            } finally {
                if(closeOutput) {
                    writer.close();
                }
            }
        } catch (Exception e) {
            throw new SmooksException("Failed to filter source.", e);
        } finally {
            if(closeInput) {
                close(source);
            }
            if(closeOutput) {
                close(result);
            }
        }
    }
}
