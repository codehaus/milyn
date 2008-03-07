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

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.delivery.StringSource;
import org.milyn.delivery.StringResult;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ElementWritingTest extends TestCase {

    public void test_one_writer_per_element() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("OnWriterPerElementTest.xml"));

        try {
            smooks.filter(new StringSource("<a/>"), null, smooks.createExecutionContext());
            fail("Expected SAXWriterAccessException");
        } catch(SmooksException e) {
            assertEquals("Illegal access to the element writer for element 'a' by SAX visitor 'org.milyn.delivery.sax.SAXVisitorWriter02'.  Writer already acquired by SAX visitor 'org.milyn.delivery.sax.SAXVisitorWriter01'.  See SAXElement javadocs (http://milyn.codehaus.org/Smooks).  Change Smooks visitor resource configuration.", e.getCause().getMessage());
        }
    }

    public void test_default_writing() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("DefaultWritingTest.xml"));
        StringSource stringSource = new StringSource("<a>aa<b>bbb<c />bbb</b>aaa</a>");
        StringResult stringResult = new StringResult();

        smooks.filter(stringSource, stringResult, smooks.createExecutionContext());
        
        assertEquals(stringSource.getSource(), stringResult.getResult());
        assertTrue(SAXVisitBeforeVisitor.visited);
        assertTrue(SAXVisitAfterAndChildrenVisitor.visited);
        assertTrue(SAXVisitAfterAndChildrenVisitor.onChildElement);
        assertTrue(SAXVisitAfterAndChildrenVisitor.onChildText);
        assertTrue(SAXVisitAfterVisitor.visited);
    }
}
