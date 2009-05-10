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
package org.milyn.delivery.dom;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class VisitorEventsTest extends TestCase {

    protected void setUp() throws Exception {
        reset();
    }

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config3.xml"));

        smooks.filter(smooks.createExecutionContext(), new StreamSource(new StringReader("<x/>")), null);
        assertFalse(VisitBeforeDOMVisitor.visited);
        assertFalse(VisitAfterDOMVisitor.visited);
        reset();
        smooks.filter(smooks.createExecutionContext(), new StreamSource(new StringReader("<a/>")), null);
        assertTrue(VisitBeforeDOMVisitor.visited);
        assertFalse(VisitAfterDOMVisitor.visited);
        reset();
        smooks.filter(smooks.createExecutionContext(), new StreamSource(new StringReader("<a><b/></a>")), null);
        assertTrue(VisitBeforeDOMVisitor.visited);
        assertTrue(VisitAfterDOMVisitor.visited);
        assertEquals("Hi There!", VisitAfterDOMVisitor.staticInjectedParam);
    }

    private void reset() {
        VisitBeforeDOMVisitor.visited = false;
        VisitAfterDOMVisitor.visited = false;
    }
}
