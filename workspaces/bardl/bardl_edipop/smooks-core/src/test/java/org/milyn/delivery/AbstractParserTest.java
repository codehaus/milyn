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

import junit.framework.TestCase;
import org.milyn.container.ExecutionContext;
import org.milyn.Smooks;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class AbstractParserTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-AbstractParserTest.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();

        TestParser parser = new TestParser(execContext);
        TestXMLReader reader = (TestXMLReader) parser.createXMLReader(new DefaultHandler2());

        assertEquals(7, reader.features.size());
        assertTrue(reader.features.get("feature-on-1"));
        assertTrue(reader.features.get("feature-on-2"));
        assertTrue(!reader.features.get("feature-off-1"));
        assertTrue(!reader.features.get("feature-off-2"));

        assertNotNull(reader.dtdHandler);
        assertNotNull(reader.entityResolver);
        assertNotNull(reader.errorHandler);
    }

    private class TestParser extends AbstractParser {
        public TestParser(ExecutionContext execContext) {
            super(execContext);
        }
    }

}
