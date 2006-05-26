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

package org.milyn.dom;

import java.io.InputStreamReader;

import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
	
	public void test_countNodesBefore() {
		Document doc = parseDoc("test5.html");
		Element element;

		element = (Element)XmlUtil.getNode(doc, "/x/y/b[2]/c[3]");
		int count = DomUtils.countNodesBefore(element, Node.ELEMENT_NODE);
		assertEquals(8, count);

		element = (Element)XmlUtil.getNode(doc, "/x/y/b[2]/b[2]/g");
		count = DomUtils.countNodesBefore(element, Node.ELEMENT_NODE);
		assertEquals(0, count);

		element = (Element)XmlUtil.getNode(doc, "/x/y/b[2]/b");
		count = DomUtils.countNodesBefore(element);
		assertEquals(3, count);
	}

	
	public void test_countElementsBefore() {
		Document doc = parseDoc("test5.html");
		Element element;

		element = (Element)XmlUtil.getNode(doc, "/x/y/b[2]/c[3]");
		int count = DomUtils.countElementsBefore(element, "c");
		assertEquals(2, count);
	}
	
	public void test_getXPath() {
		Document doc = parseDoc("test5.html");
		
		test_getXPath(doc, "/x/y/b[2]/a");
		test_getXPath(doc, "/x/y/b[2]/a[2]");
		test_getXPath(doc, "/x/y/b[2]/a[3]");

		test_getXPath(doc, "/x/y/b[2]/c");
		test_getXPath(doc, "/x/y/b[2]/c[2]");
		test_getXPath(doc, "/x/y/b[2]/c[3]");
	}
	
	public void test_getAllText() {
		Document doc = parseDoc("test6.html");
		Element element = (Element)XmlUtil.getNode(doc, "/x/y");
		String allText = DomUtils.getAllText(element, true);
		
		assertEquals("This is wh&#97;t we're looking for€", allText);
	}
	
	private void test_getXPath(Document doc, String testPath) {
		Element element = (Element)XmlUtil.getNode(doc, testPath);
		String xpath = DomUtils.getXPath(element);
		assertEquals(testPath, xpath);
	}

	private Document parseDoc(String classpath) {
		try {
			Parser parser = new Parser();
			return parser.parse(new InputStreamReader(getClass().getResourceAsStream(classpath)));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}
}
