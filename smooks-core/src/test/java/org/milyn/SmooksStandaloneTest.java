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
import java.io.CharArrayWriter;
import java.io.IOException;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.standalone.TestSmooksStandalone;
import org.milyn.util.DomUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class SmooksStandaloneTest extends TestCase {

	public void testProcess() {
		SmooksStandalone smooksSA = null;
		try {
			smooksSA = new TestSmooksStandalone();
			String response = smooksSA.filterAndSerialize("msie6", getClass().getResourceAsStream("html_2.html"));
			System.out.println(response);
			Document doc = DomUtil.parse(response);
			
			assertNull(XmlUtil.getNode(doc, "html/body/xxx"));
			assertNotNull(XmlUtil.getNode(doc, "html/body/yyy"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	public void test_Standalone_CodeConfig_1() {
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		
		// Add 2 useragents and configure them with profiles...
		smooks.registerUseragent("message-target1", new String[] {"profile1", "profile2"});
		smooks.registerUseragent("message-target2", new String[] {"profile2", "profile3"});
	
		// Create CDU configs and target them at the profiles...
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("ccc", "profile1 AND not:profile3", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "xxx");
		smooks.registerResource(resourceConfig);
		resourceConfig = new SmooksResourceConfiguration("aaa", "profile2", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "zzz");
		smooks.registerResource(resourceConfig);

		// Transform the same message for each useragent...
		String message = "<aaa><bbb>888</bbb><ccc>999</ccc></aaa>";
		String result = smooks.filterAndSerialize("message-target1", new ByteArrayInputStream(message.getBytes()));
		System.out.println(result);
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><xxx>999</xxx></zzz>", result);
		result = smooks.filterAndSerialize("message-target2", new ByteArrayInputStream(message.getBytes()));
		System.out.println(result);
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><ccc>999</ccc></zzz>", result);
	}

	public void test_Standalone_CodeConfig_2() throws SAXException, IOException {
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		
		// Add 2 useragents and configure them with profiles...
		smooks.registerUseragent("message-target1", new String[] {"profile1", "profile2"});
		smooks.registerUseragent("message-target2", new String[] {"profile2", "profile3"});
	
		// Create CDU configs and target them at the profiles...
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("ccc", "profile1 AND not:profile3", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "xxx");
		smooks.registerResource(resourceConfig);
		resourceConfig = new SmooksResourceConfiguration("aaa", "profile2", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "zzz");
		smooks.registerResource(resourceConfig);

		// Transform the same message for each useragent...
		String message = "<aaa><bbb>888</bbb><ccc>999</ccc></aaa>";
		Document doc = XmlUtil.parseStream(new ByteArrayInputStream(message.getBytes()), false, false);

		doc = (Document)smooks.filter("message-target1", doc);

		CharArrayWriter writer = new CharArrayWriter();
		smooks.serialize("message-target1", doc, writer);
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><xxx>999</xxx></zzz>", writer.toString());
	}
}
