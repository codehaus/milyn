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

package org.milyn.dtd;

import java.util.Arrays;
import java.util.List;

import org.milyn.device.MockUAContext;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class DTDStoreTest extends TestCase {

	/**
	 * @param arg0
	 */
	public DTDStoreTest(String arg0) {
		super(arg0);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	}
	
	public void testGetEmptyAnyMixedEtcElements() {
		MockUAContext uaContext = new MockUAContext("device3");
		
		uaContext.addProfiles(new String[] {"prof1", "prof2", "prof3"});
		DTDStore.addDTD(uaContext, getClass().getResourceAsStream("xhtml1-transitional.dtd"));
		DTDStore.DTDObjectContainer dtdContainer = DTDStore.getDTDObject(uaContext);
		
		String[] elements = dtdContainer.getEmptyElements();	
		assertTrue(Arrays.equals(new String[] {"basefont", "area", "link", "isindex", "col", "base", "meta", "img", "br", "hr", "param", "input"}, elements));
		elements = dtdContainer.getAnyElements();	
		assertTrue(Arrays.equals(new String[] {}, elements));
		elements = dtdContainer.getMixedElements();	
		assertTrue(Arrays.equals(new String[] {"button", "textarea", "em", "small", "noframes", "bdo", "form", "label", "dt", "span", "title", "strong", "script", "div", "blockquote", "kbd", "body", "ins", "dd", "fieldset", "big", "code", "option", "u", "s", "q", "p", "i", "pre", "caption", "b", "a", "style", "applet", "tt", "th", "center", "td", "samp", "font", "dfn", "noscript", "object", "sup", "h6", "h5", "h4", "h3", "h2", "h1", "iframe", "strike", "sub", "acronym", "del", "li", "cite", "var", "legend", "abbr", "address"}, elements));
		elements = dtdContainer.getPCDataElements();	
		assertTrue(Arrays.equals(new String[] {}, elements));
	}

	public void testChildElements() {
		MockUAContext uaContext = new MockUAContext("device3");
		
		uaContext.addProfiles(new String[] {"prof1", "prof2", "prof3"});
		DTDStore.addDTD(uaContext, getClass().getResourceAsStream("xhtml1-transitional.dtd"));
		DTDStore.DTDObjectContainer dtdContainer = DTDStore.getDTDObject(uaContext);
		List l1 = dtdContainer.getChildElements("html");
		List l2 = dtdContainer.getChildElements("html");
		
		assertTrue(l1 == l2);
		assertEquals(2, dtdContainer.getChildElements("html").size());
		assertEquals(64, dtdContainer.getChildElements("body").size());
		assertEquals(0, dtdContainer.getChildElements("img").size());
		assertEquals(40, dtdContainer.getChildElements("b").size());
		assertEquals(null, dtdContainer.getChildElements("xxxx"));
	}

	public void testElementAttributes() {
		MockUAContext uaContext = new MockUAContext("device3");
		
		uaContext.addProfiles(new String[] {"prof1", "prof2", "prof3"});
		DTDStore.addDTD(uaContext, getClass().getResourceAsStream("xhtml1-transitional.dtd"));
		DTDStore.DTDObjectContainer dtdContainer = DTDStore.getDTDObject(uaContext);
		List l1 = null;
		List l2 = null;
		try {
			l1 = dtdContainer.getElementAttributes("html");
			l2 = dtdContainer.getElementAttributes("html");
		} catch (ElementNotDefined e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertTrue(l1 == l2);
		assertEquals(5, l1.size());
		assertTrue(l1.contains("dir"));
		assertTrue(l1.contains("xml:lang"));
		assertTrue(l1.contains("lang"));
		assertTrue(l1.contains("xmlns"));
		assertTrue(l1.contains("id"));

		try {
			dtdContainer.getElementAttributes("xxxxx");
			fail("Expected ElementNotDefined");
		} catch (ElementNotDefined e) {
			// OK
		}
	}
	
	private void print(String name) {
		MockUAContext uaContext = new MockUAContext("device3");
		
		uaContext.addProfiles(new String[] {"prof1", "prof2", "prof3"});
		DTDStore.addDTD(uaContext, getClass().getResourceAsStream(name));
		DTDStore.DTDObjectContainer dtdContainer = DTDStore.getDTDObject(uaContext);
		
		System.out.println("-------- " + name + " ---------");
		System.out.println(Arrays.asList(dtdContainer.getEmptyElements()));
	}
	
	public void testPrint() {
		print("xhtml1-transitional.dtd");
		print("html32.xml.dtd");
		print("wml_1_1.dtd");
		print("wml12.dtd");
		print("wml13.dtd");
	}
}
