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
import org.xml.sax.ext.Attributes2Impl;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXElementTest extends TestCase {

    public void test() {
        SAXElement saxElement;

        saxElement = new SAXElement("http://x", "a", "x", new Attributes2Impl());
        assertEquals("http://x", saxElement.getName().getNamespaceURI());
        assertEquals("", saxElement.getName().getPrefix());
        assertEquals("a", saxElement.getName().getLocalPart());

        saxElement = new SAXElement("http://x", "", "x", new Attributes2Impl());
        assertEquals("http://x", saxElement.getName().getNamespaceURI());
        assertEquals("", saxElement.getName().getPrefix());
        assertEquals("x", saxElement.getName().getLocalPart());

        saxElement = new SAXElement("http://x", "a", "x:a", new Attributes2Impl());
        assertEquals("http://x", saxElement.getName().getNamespaceURI());
        assertEquals("x", saxElement.getName().getPrefix());
        assertEquals("a", saxElement.getName().getLocalPart());

        saxElement = new SAXElement("http://x", null, "x", new Attributes2Impl());
        assertEquals("http://x", saxElement.getName().getNamespaceURI());
        assertEquals("", saxElement.getName().getPrefix());
        assertEquals("x", saxElement.getName().getLocalPart());

        saxElement = new SAXElement("http://x", "x", null, new Attributes2Impl());
        assertEquals("http://x", saxElement.getName().getNamespaceURI());
        assertEquals("", saxElement.getName().getPrefix());
        assertEquals("x", saxElement.getName().getLocalPart());

        try {
            new SAXElement("http://x", null, null, new Attributes2Impl());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid SAXELement name paramaters: namespaceURI='http://x', localName='null', qName='null'.", e.getMessage());
        }

        try {
            new SAXElement(null, null, null, new Attributes2Impl());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid SAXELement name paramaters: namespaceURI='null', localName='null', qName='null'.", e.getMessage());
        }
    }
}
