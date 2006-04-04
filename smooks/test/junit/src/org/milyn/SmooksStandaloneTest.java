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

import java.io.ByteArrayInputStream;
import java.net.URI;

import org.milyn.cdr.CDRDef;
import org.milyn.container.standalone.TestSmooksStandalone;
import org.milyn.util.DomUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class SmooksStandaloneTest extends TestCase {

	public void testProcess() {
		SmooksStandalone smooksSA = null;
		try {
			smooksSA = new TestSmooksStandalone();
			String response = smooksSA.processAndSerialize("msie6", getClass().getResourceAsStream("html_2.html"));
			System.out.println(response);
			Document doc = DomUtil.parse(response);
			
			assertNull(XmlUtil.getNode(doc, "html/body/xxx[@attrib1]"));
			assertNotNull(XmlUtil.getNode(doc, "html/body/xxx[@attrib2]"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	public void test_Standalone_CodeConfig() {
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		
		// Add 2 useragents and configure them with profiles...
		smooks.registerUseragent("useragent1", new String[] {"profile1", "profile2"});
		smooks.registerUseragent("useragent2", new String[] {"profile2", "profile3"});
	
		// Create CDU configs and target them at the profiles...
		CDRDef cdrDef = new CDRDef("ccc", "profile1 AND not:profile3", RenameElementTrans.class.getName());
		cdrDef.setParameter("new-name", "xxx");
		smooks.registerResource(cdrDef);
		cdrDef = new CDRDef("aaa", "profile2", RenameElementTrans.class.getName());
		cdrDef.setParameter("new-name", "zzz");
		smooks.registerResource(cdrDef);

		// Transform the same message for each useragent...
		String message = "<aaa><bbb>888</bbb><ccc>999</ccc></aaa>";
		String result = smooks.processAndSerialize("useragent1", new ByteArrayInputStream(message.getBytes()));
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><xxx>999</xxx></zzz>", result);
		result = smooks.processAndSerialize("useragent2", new ByteArrayInputStream(message.getBytes()));
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><ccc>999</ccc></zzz>", result);
	}
}
