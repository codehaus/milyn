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

package org.milyn;

import java.net.URI;

import org.milyn.container.standalone.TestSmooksStandalone;
import org.milyn.util.DomUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class SmooksStandaloneTest extends TestCase {

	public void testProcess() {
		SmooksStandalone smooksSA = null;
		try {
			smooksSA = new TestSmooksStandalone("msie6");
			String response = smooksSA.processAndSerialize(URI.create("http://www.x.y.com/"), getClass().getResourceAsStream("html_2.html"));
			System.out.println(response);
			Document doc = DomUtil.parse(response);
			
			assertNull(XmlUtil.getNode(doc, "html/body/xxx[@attrib1]"));
			assertNotNull(XmlUtil.getNode(doc, "html/body/xxx[@attrib2]"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}
