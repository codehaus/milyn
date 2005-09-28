/*
	Milyn - Copyright (C) 2003

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

package org.milyn.dom;

import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class DomUtilsTest extends TestCase {

	public void test_renameElement_nochildren_noattributes() {
		renameElementTest(false, false);
	}

	public void test_renameElement_noattributes() {
		renameElementTest(true, false);
	}

	public void test_renameElement_nochildren() {
		renameElementTest(false, true);
	}


	public void test_renameElement() {
		renameElementTest(true, true);
	}
	
	public void renameElementTest(boolean keepChildContent, boolean keepAttributes) {
		Document doc = parseDoc("test3.html");
		
		Element element = (Element)XmlUtil.getNode(doc, "/x");
		Element replacement = DomUtils.renameElement(element, "z", keepChildContent, keepAttributes);
		assertEquals("z", replacement.getTagName());
		assertEquals(keepChildContent, replacement.hasChildNodes());
		assertEquals(keepAttributes, replacement.hasAttribute("attrib1"));
		assertEquals(keepAttributes, replacement.hasAttribute("attrib2"));
	}

	private Document parseDoc(String classpath) {
		try {
			return XmlUtil.parseStream(getClass().getResourceAsStream(classpath), false);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}
}
