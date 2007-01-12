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

package org.milyn.smooks.scripting;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.ElementVisitor;
import org.milyn.io.StreamUtils;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * @author tfennelly
 */
public class GroovyContentDeliveryUnitCreatorTest extends TestCase {

	public void test_badscript() {
		GroovyContentDeliveryUnitCreator creator = new GroovyContentDeliveryUnitCreator();
		SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", "classpath:/org/milyn/smooks/scripting/MyGroovyScript_bad.groovy");
		
		try {
			creator.create(config);
			fail("Expected InstantiationException");
		} catch (InstantiationException e) {
			assertEquals("Invalid Groovy script classpath:/org/milyn/smooks/scripting/MyGroovyScript_bad.groovy.  Must implement one of the following Smooks interfaces:\n\t\t1. org.milyn.delivery.assemble.AssemblyUnit, or\n\t\torg.milyn.delivery.process.ProcessingUnit, or\n\t\torg.milyn.delivery.serialize.SerializationUnit.", e.getMessage());
		}
	}

	public void test_goodscript_by_URI() throws InstantiationException, IllegalArgumentException, IOException, SAXException {
		test_goodscript_by_URI("classpath:/org/milyn/smooks/scripting/MyGroovyScript.groovy");
		test_goodscript_by_URI("/org/milyn/smooks/scripting/MyGroovyScript.groovy");
	}

	public void test_goodscript_by_Inlining() throws InstantiationException, IllegalArgumentException, IOException, SAXException {
		String script = new String(StreamUtils.readStream(getClass().getResourceAsStream("MyGroovyScript.groovy")));
		SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", null);
		
		config.setParameter("resdata", script);
		test_goodscript(config);
	}
	
	private void test_goodscript_by_URI(String path) throws InstantiationException, IllegalArgumentException, IOException, SAXException {
		test_goodscript(new SmooksResourceConfiguration("x", path));
	}
	
	private void test_goodscript(SmooksResourceConfiguration config) throws InstantiationException, IllegalArgumentException, IOException, SAXException {
		GroovyContentDeliveryUnitCreator creator = new GroovyContentDeliveryUnitCreator();
		
		config.setParameter("new-name", "yyy");
		ElementVisitor resource = (ElementVisitor) creator.create(config);
		
		Document doc = XmlUtil.parseStream(new ByteArrayInputStream("<xxx/>".getBytes()), false, false);
		assertEquals("xxx", doc.getDocumentElement().getTagName());
		resource.visit(doc.getDocumentElement(), null);
		assertEquals("yyy", doc.getDocumentElement().getTagName());
	}
}
