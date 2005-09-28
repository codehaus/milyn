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

package org.milyn.cdres.css;

import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.value.Value;
import org.milyn.container.MockContainerRequest;
import org.milyn.util.SmooksUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class CssAccessorTest extends TestCase {

	private MockContainerRequest request;
	private StyleSheetStore store;	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		request = new SmooksUtil().getRequest("devicename");
		store = StyleSheetStore.getStore(request);
	}

	public void test_getProperty_1() {
		Document doc = CssTestUtil.parseCPResource("testpage1.html");
		Element style = (Element)XmlUtil.getNode(doc, "/html/head/style");
		Element p = (Element)XmlUtil.getNode(doc, "/html/body/p");
		Element h1 = (Element)XmlUtil.getNode(doc, "/html/body/h1");
		Element h1p = (Element)XmlUtil.getNode(doc, "/html/body/h1/p");
		Element h1h1 = (Element)XmlUtil.getNode(doc, "/html/body/h1/h1");
		StyleSheet styleSheet1;
		StyleSheet styleSheet2;
		
		styleSheet1 = CssTestUtil.parseCSS("style1.css", store.getCssParser());
		store.add(styleSheet1, style);
		
		CssAccessor cssAccessor = new CssAccessor(request);
		
		Value propVal_p = cssAccessor.getPropertyValue(p, "font-size");
		assertNotNull(propVal_p);
		assertEquals("4em", propVal_p.getCssText());
		
		Value propVal_h1p = cssAccessor.getPropertyValue(h1p, "font-size");
		assertNotNull(propVal_h1p);
		assertEquals("2em", propVal_h1p.getCssText());
		
		Value propVal_background_color = cssAccessor.getPropertyValue(h1, "background-color");
		assertNull(propVal_background_color);
		
		// Add the second StyleSheet
		styleSheet2 = CssTestUtil.parseCSS("style2.css", store.getCssParser());
		store.add(styleSheet2, style);
		
		propVal_p = cssAccessor.getPropertyValue(p, "font-size");
		assertNotNull(propVal_p);
		assertEquals("8em", propVal_p.getCssText());
		
		propVal_h1p = cssAccessor.getPropertyValue(h1p, "font-size");
		assertNotNull(propVal_h1p);
		assertEquals("9em", propVal_h1p.getCssText());
		
		Value propVal_h1h1 = cssAccessor.getPropertyValue(h1h1, "font-size");
		assertNotNull(propVal_h1h1);
		assertEquals("1em", propVal_h1h1.getCssText());
		
		propVal_background_color = cssAccessor.getPropertyValue(h1, "background-color");
		assertNotNull(propVal_background_color);
		assertEquals("blue", propVal_background_color.getStringValue());
	}
}
