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

import java.io.IOException;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.AbstractParser;
import org.milyn.io.NullWriter;
import org.milyn.util.Cleanable;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Smooks SAX data stream parser.
 * <p/>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXParser extends AbstractParser {

    private Cleanable cleanableHandler;

    public SAXParser(ExecutionContext execContext) {
        super(execContext);
    }

    protected Writer parse(Source source, Result result, ExecutionContext executionContext) throws SAXException, IOException {
    	DefaultHandler2 handler;
    	Writer writer = getWriter(result, executionContext);
    	
        if (!(writer instanceof NullWriter)) {
        	handler = new SAXHandler(getExecContext(), writer);
        } else {
        	handler = new ReadOnlySAXHandler(getExecContext());
        }
        cleanableHandler = (Cleanable) handler;

        XMLReader saxReader;
        saxReader = createXMLReader(handler);
        saxReader.parse(createInputSource(saxReader, source, executionContext));
        
        return writer;
    }

    public void cleanup() {
    	cleanableHandler.clean();
    }
}
