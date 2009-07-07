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
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Smooks SAX data stream parser.
 * <p/>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXParser extends AbstractParser {

    private SAXHandler saxHandler;

    public SAXParser(ExecutionContext execContext) {
        super(execContext);
    }

    protected Writer parse(Source source, Result result, ExecutionContext executionContext) throws SAXException, IOException {

        Writer writer = getWriter(result, executionContext);
        XMLReader saxReader;

        saxHandler = new SAXHandler(getExecContext(), writer);
        saxReader = createXMLReader(saxHandler);
        saxReader.parse(createInputSource(saxReader, source, executionContext));
        return writer;
    }

    public void cleanup() {
        saxHandler.cleanup();
    }
}
