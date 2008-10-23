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

import junit.framework.TestCase;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.util.DomUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Arrays;
import java.util.List;

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
    
    public void test_isTargetedAtElement_DOM() {
        Document doc = DomUtil.parse("<a><b><c><d><e/></d></c></b></a>");
        Element e = (Element) XmlUtil.getNode(doc, "a/b/c/d/e");

        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("e", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("d/e", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("a/b/c/d/e", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("xx/a/b/c/d/e", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("xx/b/c/d/e", "blah");
        SmooksResourceConfiguration rc6 = new SmooksResourceConfiguration("xx", "blah");
        SmooksResourceConfiguration rc7 = new SmooksResourceConfiguration("/c/d/e", "blah");

        assertTrue(rc1.isTargetedAtElementContext(e));
        assertTrue(rc2.isTargetedAtElementContext(e));
        assertTrue(rc3.isTargetedAtElementContext(e));
        assertTrue(!rc4.isTargetedAtElementContext(e));
        assertTrue(!rc5.isTargetedAtElementContext(e));
        assertTrue(!rc6.isTargetedAtElementContext(e));
        assertTrue(!rc7.isTargetedAtElementContext(e));
    }

    public void test_isTargetedAtElement_DOM_wildcards() {
        Document doc = DomUtil.parse("<a><b><c><d><e/></d></c></b></a>");
        Element e = (Element) XmlUtil.getNode(doc, "a/b/c/d/e");

        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("e", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("d/e", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("a/b/*/d/e", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("xx/a/b/*/d/e", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("xx/b/*/d/e", "blah");
        SmooksResourceConfiguration rc6 = new SmooksResourceConfiguration("a/*/c/*/e", "blah");
        SmooksResourceConfiguration rc7 = new SmooksResourceConfiguration("a/b/**/e", "blah");
        SmooksResourceConfiguration rc8 = new SmooksResourceConfiguration("a/**/**/e", "blah");
        SmooksResourceConfiguration rc9 = new SmooksResourceConfiguration("a/b/*/**/e", "blah");
        SmooksResourceConfiguration rc10 = new SmooksResourceConfiguration("a/**/c/**/e", "blah");
        SmooksResourceConfiguration rc11 = new SmooksResourceConfiguration("**/c/**/e", "blah");
        SmooksResourceConfiguration rc12 = new SmooksResourceConfiguration("**/**/e", "blah");
        SmooksResourceConfiguration rc13 = new SmooksResourceConfiguration("**/e", "blah");
        SmooksResourceConfiguration rc14 = new SmooksResourceConfiguration("a/**/e", "blah");
        SmooksResourceConfiguration rc15 = new SmooksResourceConfiguration("a/b/**", "blah");
        SmooksResourceConfiguration rc16 = new SmooksResourceConfiguration("a/b/**/*", "blah");
        SmooksResourceConfiguration rc17 = new SmooksResourceConfiguration("h/**", "blah");
        SmooksResourceConfiguration rc18 = new SmooksResourceConfiguration("h/**/e", "blah");
        SmooksResourceConfiguration rc19 = new SmooksResourceConfiguration("a/h/**/e", "blah");
        SmooksResourceConfiguration rc20 = new SmooksResourceConfiguration("/a/**/e", "blah");
        SmooksResourceConfiguration rc21 = new SmooksResourceConfiguration("/**/e", "blah");
        SmooksResourceConfiguration rc22 = new SmooksResourceConfiguration("*/e", "blah");
        SmooksResourceConfiguration rc23 = new SmooksResourceConfiguration("/*/e", "blah");

        assertTrue(rc1.isTargetedAtElementContext(e));
        assertTrue(rc2.isTargetedAtElementContext(e));
        assertTrue(rc3.isTargetedAtElementContext(e));

        assertTrue(!rc4.isTargetedAtElementContext(e));
        assertTrue(!rc5.isTargetedAtElementContext(e));

        assertTrue(rc6.isTargetedAtElementContext(e));
        assertTrue(rc7.isTargetedAtElementContext(e));
        assertTrue(rc8.isTargetedAtElementContext(e));
        assertTrue(rc9.isTargetedAtElementContext(e));
        assertTrue(rc10.isTargetedAtElementContext(e));
        assertTrue(rc11.isTargetedAtElementContext(e));
        assertTrue(rc12.isTargetedAtElementContext(e));
        assertTrue(rc13.isTargetedAtElementContext(e));
        assertTrue(rc14.isTargetedAtElementContext(e));
        assertTrue(rc15.isTargetedAtElementContext(e));
        assertTrue(rc16.isTargetedAtElementContext(e));
        
        assertTrue(!rc17.isTargetedAtElementContext(e));
        assertTrue(!rc18.isTargetedAtElementContext(e));
        assertTrue(!rc19.isTargetedAtElementContext(e));

        assertTrue(rc20.isTargetedAtElementContext(e));
        assertTrue(rc21.isTargetedAtElementContext(e));
        assertTrue(rc22.isTargetedAtElementContext(e));

        assertTrue(!rc23.isTargetedAtElementContext(e));
    }

    public void test_isTargetedAtElement_DOM_rooted() {
        Document doc = DomUtil.parse("<a><b><c><a><d><e/></d></a></c></b></a>");
        Element e = (Element) XmlUtil.getNode(doc, "a/b/c/a/d/e");

        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("/a/b/c/a/d/e", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("/a/d/e", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("/**/d/e", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("/a/b/**/d/e", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("/a/b/*/d/e", "blah");

        try {
            new SmooksResourceConfiguration("xx/$document/a/b/c/a/d/e", "blah");
            fail("Expected SmooksConfigurationException.");
        } catch (SmooksConfigurationException ex) {
            assertEquals("Invalid selector 'xx/$document/a/b/c/a/d/e'.  '$document' token can only exist at the start of the selector.", ex.getMessage());
        }

        assertTrue(rc1.isTargetedAtElementContext(e));
        assertTrue(!rc2.isTargetedAtElementContext(e));
        assertTrue(rc3.isTargetedAtElementContext(e));
        assertTrue(rc4.isTargetedAtElementContext(e));
        assertTrue(!rc5.isTargetedAtElementContext(e));
    }

    public void test_isTargetedAtElement_SAX() {
        SAXElement e = buildE();

        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("e", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("d/e", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("a/b/c/d/e", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("xx/a/b/c/d/e", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("xx/b/c/d/e", "blah");
        SmooksResourceConfiguration rc6 = new SmooksResourceConfiguration("xx", "blah");

        assertTrue(rc1.isTargetedAtElementContext(e));
        assertTrue(rc2.isTargetedAtElementContext(e));
        assertTrue(rc3.isTargetedAtElementContext(e));
        assertTrue(!rc4.isTargetedAtElementContext(e));
        assertTrue(!rc5.isTargetedAtElementContext(e));
        assertTrue(!rc6.isTargetedAtElementContext(e));
    }

    public void test_isTargetedAtElement_SAX_wildcards() {
        SAXElement e = buildE();

        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("e", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("d/e", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("a/b/*/d/e", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("xx/a/b/*/d/e", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("xx/b/*/d/e", "blah");
        SmooksResourceConfiguration rc6 = new SmooksResourceConfiguration("a/*/c/*/e", "blah");
        SmooksResourceConfiguration rc7 = new SmooksResourceConfiguration("a/b/**/e", "blah");
        SmooksResourceConfiguration rc8 = new SmooksResourceConfiguration("a/**/**/e", "blah");
        SmooksResourceConfiguration rc9 = new SmooksResourceConfiguration("a/b/*/**/e", "blah");
        SmooksResourceConfiguration rc10 = new SmooksResourceConfiguration("a/**/c/**/e", "blah");
        SmooksResourceConfiguration rc11 = new SmooksResourceConfiguration("**/c/**/e", "blah");
        SmooksResourceConfiguration rc12 = new SmooksResourceConfiguration("**/**/e", "blah");
        SmooksResourceConfiguration rc13 = new SmooksResourceConfiguration("**/e", "blah");
        SmooksResourceConfiguration rc14 = new SmooksResourceConfiguration("a/**/e", "blah");
        SmooksResourceConfiguration rc15 = new SmooksResourceConfiguration("a/b/**", "blah");
        SmooksResourceConfiguration rc16 = new SmooksResourceConfiguration("a/b/**/*", "blah");
        SmooksResourceConfiguration rc17 = new SmooksResourceConfiguration("h/**", "blah");
        SmooksResourceConfiguration rc18 = new SmooksResourceConfiguration("h/**/e", "blah");
        SmooksResourceConfiguration rc19 = new SmooksResourceConfiguration("a/h/**/e", "blah");
        SmooksResourceConfiguration rc20 = new SmooksResourceConfiguration("/a/**/e", "blah");
        SmooksResourceConfiguration rc21 = new SmooksResourceConfiguration("/**/e", "blah");
        SmooksResourceConfiguration rc22 = new SmooksResourceConfiguration("*/e", "blah");
        SmooksResourceConfiguration rc23 = new SmooksResourceConfiguration("/*/e", "blah");

        assertTrue(rc1.isTargetedAtElementContext(e));
        assertTrue(rc2.isTargetedAtElementContext(e));
        assertTrue(rc3.isTargetedAtElementContext(e));

        assertTrue(!rc4.isTargetedAtElementContext(e));
        assertTrue(!rc5.isTargetedAtElementContext(e));

        assertTrue(rc6.isTargetedAtElementContext(e));
        assertTrue(rc7.isTargetedAtElementContext(e));
        assertTrue(rc8.isTargetedAtElementContext(e));
        assertTrue(rc9.isTargetedAtElementContext(e));
        assertTrue(rc10.isTargetedAtElementContext(e));
        assertTrue(rc11.isTargetedAtElementContext(e));
        assertTrue(rc12.isTargetedAtElementContext(e));
        assertTrue(rc13.isTargetedAtElementContext(e));
        assertTrue(rc14.isTargetedAtElementContext(e));
        assertTrue(rc15.isTargetedAtElementContext(e));
        assertTrue(rc16.isTargetedAtElementContext(e));

        assertTrue(!rc17.isTargetedAtElementContext(e));
        assertTrue(!rc18.isTargetedAtElementContext(e));
        assertTrue(!rc19.isTargetedAtElementContext(e));

        assertTrue(rc20.isTargetedAtElementContext(e));
        assertTrue(rc21.isTargetedAtElementContext(e));
        assertTrue(rc22.isTargetedAtElementContext(e));

        assertTrue(!rc23.isTargetedAtElementContext(e));
    }

    public void test_attributeSelector() {        
        // Test that the attribute part of the selector doesn't get lowercased...
        SmooksResourceConfiguration resource = new SmooksResourceConfiguration("a/b/@myAttribute");
        assertEquals("[a, b, @myAttribute]", Arrays.asList(resource.getContextualSelector()).toString());
    }

    public void test_isTargetedAtElement_SAX_rooted() {
        SAXElement e = buildE_rooted();

        SmooksResourceConfiguration rc1 = new SmooksResourceConfiguration("/a/b/c/a/d/e", "blah");
        SmooksResourceConfiguration rc2 = new SmooksResourceConfiguration("/a/d/e", "blah");
        SmooksResourceConfiguration rc3 = new SmooksResourceConfiguration("/**/d/e", "blah");
        SmooksResourceConfiguration rc4 = new SmooksResourceConfiguration("/a/b/**/d/e", "blah");
        SmooksResourceConfiguration rc5 = new SmooksResourceConfiguration("/a/b/*/d/e", "blah");

        assertTrue(rc1.isTargetedAtElementContext(e));
        assertTrue(!rc2.isTargetedAtElementContext(e));
        assertTrue(rc3.isTargetedAtElementContext(e));
        assertTrue(rc4.isTargetedAtElementContext(e));
        assertTrue(!rc5.isTargetedAtElementContext(e));
    }

    private SAXElement buildE() {
        SAXElement element;

        element = new SAXElement(null, "a", null, new AttributesImpl(), null);
        element = new SAXElement(null, "b", null, new AttributesImpl(), element);
        element = new SAXElement(null, "c", null, new AttributesImpl(), element);
        element = new SAXElement(null, "d", null, new AttributesImpl(), element);
        element = new SAXElement(null, "e", null, new AttributesImpl(), element);

        return element;
    }

    private SAXElement buildE_rooted() {
        SAXElement element;

        element = new SAXElement(null, "a", null, new AttributesImpl(), null);
        element = new SAXElement(null, "b", null, new AttributesImpl(), element);
        element = new SAXElement(null, "c", null, new AttributesImpl(), element);
        element = new SAXElement(null, "a", null, new AttributesImpl(), element);
        element = new SAXElement(null, "d", null, new AttributesImpl(), element);
        element = new SAXElement(null, "e", null, new AttributesImpl(), element);

        return element;
    }
}
