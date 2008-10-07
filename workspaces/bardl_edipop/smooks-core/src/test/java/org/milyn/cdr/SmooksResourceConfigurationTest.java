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

package org.milyn.cdr;

import java.util.List;

import org.milyn.util.DomUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import junit.framework.TestCase;

public class SmooksResourceConfigurationTest extends TestCase {

	public void test_getParameter() {
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("body", "device", "xxx");

		resourceConfig.setParameter("x", "val x");
		assertEquals("Expected x to be 'val x'", "val x", resourceConfig.getParameter("x").getValue());
		resourceConfig.setParameter("y", "val y 1");
		resourceConfig.setParameter("y", "val y 2");
		assertEquals("Expected y to be 'val y 1'", "val y 1", resourceConfig.getParameter("y").getValue());
		
		List yParams = resourceConfig.getParameters("y");
		assertEquals("val y 1", ((Parameter)yParams.get(0)).getValue());
		assertEquals("val y 2", ((Parameter)yParams.get(1)).getValue());
		
		List allParams = resourceConfig.getParameterList();
		assertEquals(2, allParams.size());
		assertEquals("x", ((Parameter)allParams.get(0)).getName());
		assertEquals(yParams, allParams.get(1));
	}

	public void test_getBoolParameter() {
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("body", "device", "xxx");
		resourceConfig.setParameter("x", "true");
		
		assertTrue("Expected x to be true", resourceConfig.getBoolParameter("x", false));
		assertFalse("Expected y to be false", resourceConfig.getBoolParameter("y", false));
	}

	public void test_getStringParameter() {
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("body", "device", "xxx");
		resourceConfig.setParameter("x", "xxxx");
		
		assertEquals("Expected x to be xxxx", "xxxx", resourceConfig.getStringParameter("x", "yyyy"));
		assertEquals("Expected y to be yyyy", "yyyy", resourceConfig.getStringParameter("y", "yyyy"));
	}
    
    public void test_isTargetedAtElement() {
        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("c", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("b c", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("a  b c", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("xx a b c", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("xx b c", "blah");
        SmooksResourceConfiguration rc6 = new SmooksResourceConfiguration("xx", "blah");
        Document doc = DomUtil.parse("<a><b><c/></b></a>");
        Element c = (Element) XmlUtil.getNode(doc, "a/b/c");
        
        assertTrue(rc1.isTargetedAtElementContext(c));
        assertTrue(rc2.isTargetedAtElementContext(c));
        assertTrue(rc3.isTargetedAtElementContext(c));
        assertTrue(!rc4.isTargetedAtElementContext(c));
        assertTrue(!rc5.isTargetedAtElementContext(c));
        assertTrue(!rc6.isTargetedAtElementContext(c));
    }
}
