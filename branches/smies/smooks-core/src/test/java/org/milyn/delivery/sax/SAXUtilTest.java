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

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXUtilTest extends TestCase {

    public void test_getXPath() {
        SAXElement a = new SAXElement("http://x", "a", "a", new Attributes2Impl(), null);
        SAXElement b = new SAXElement("http://x", "b", "b", new Attributes2Impl(), a);
        SAXElement c = new SAXElement("http://x", "c", "c", new Attributes2Impl(), b);
        assertEquals("a/b/c", SAXUtil.getXPath(c));
    }
}
