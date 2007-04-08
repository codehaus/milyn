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

package org.milyn;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.container.standalone.PreconfiguredSmooks;
import org.milyn.util.DomUtil;
import org.milyn.xml.XmlUtil;
import org.milyn.profile.DefaultProfileSet;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class SmooksStandaloneTest extends TestCase {

	public void testProcess() {
		Smooks smooks = null;
		try {
			smooks = new PreconfiguredSmooks();
            StandaloneExecutionContext context = smooks.createExecutionContext("msie6");
            String response = smooks.filterAndSerialize(context, getClass().getResourceAsStream("html_2.html"));
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
		Smooks smooks = new Smooks();

        // Add profile sets...
		smooks.registerProfileSet(DefaultProfileSet.create("message-target1", new String[] {"profile1", "profile2"}));
		smooks.registerProfileSet(DefaultProfileSet.create("message-target2", new String[] {"profile2", "profile3"}));
	
		// Create CDU configs and target them at the profiles...
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("ccc", "profile1 AND not:profile3", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "xxx");
		smooks.registerResource(resourceConfig);
		resourceConfig = new SmooksResourceConfiguration("aaa", "profile2", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "zzz");
		smooks.registerResource(resourceConfig);

		// Transform the same message for each useragent...
		String message = "<aaa><bbb>888</bbb><ccc>999</ccc></aaa>";
        StandaloneExecutionContext context = smooks.createExecutionContext("message-target1");
		String result = smooks.filterAndSerialize(context, new ByteArrayInputStream(message.getBytes()));
		System.out.println(result);
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><xxx>999</xxx></zzz>", result);
        context = smooks.createExecutionContext("message-target2");
        result = smooks.filterAndSerialize(context, new ByteArrayInputStream(message.getBytes()));
		System.out.println(result);
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><ccc>999</ccc></zzz>", result);
	}

	public void test_Standalone_CodeConfig_2() throws SAXException, IOException {
		Smooks smooks = new Smooks();
		
		// Add 2 useragents and configure them with profiles...
		smooks.registerProfileSet(DefaultProfileSet.create("message-target1", new String[] {"profile1", "profile2"}));
		smooks.registerProfileSet(DefaultProfileSet.create("message-target2", new String[] {"profile2", "profile3"}));
	
		// Create CDU configs and target them at the profiles...
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("ccc", "profile1 AND not:profile3", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "xxx");
		smooks.registerResource(resourceConfig);
		resourceConfig = new SmooksResourceConfiguration("aaa", "profile2", RenameElementTrans.class.getName());
		resourceConfig.setParameter("new-name", "zzz");
		smooks.registerResource(resourceConfig);

		// Transform the same message for each useragent...
		String message = "<aaa><bbb>888</bbb><ccc>999</ccc></aaa>";

		StandaloneExecutionContext request = smooks.createExecutionContext("message-target1");
		Document doc = (Document)smooks.filter(request, new ByteArrayInputStream(message.getBytes()));

		CharArrayWriter writer = new CharArrayWriter();
		smooks.serialize(request, doc, writer);
		assertEquals("Unexpected transformation result", "<zzz><bbb>888</bbb><xxx>999</xxx></zzz>", writer.toString());
	}
}
