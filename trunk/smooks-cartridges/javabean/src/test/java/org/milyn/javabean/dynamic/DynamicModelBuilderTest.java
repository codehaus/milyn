/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.javabean.dynamic;

import java.io.IOException;

import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DynamicModelBuilderTest extends TestCase {

	public static final String NS_DESCRIPTOR = "META-INF/services/org/smooks/javabean/dynamic/ns-descriptors.properties";
	
	public void test_1_schema() throws SAXException, IOException {
		DynamicModelBuilder builder = new DynamicModelBuilder(NS_DESCRIPTOR);
		
		Model<JavaResult> model = builder.parse(getClass().getResourceAsStream("aaa-message.xml"));
		AAA aaa = model.getModelRoot().getBean(AAA.class);
		assertEquals(1234, aaa.getIntProperty());
		assertEquals("http://www.acme.com/xsd/aaa.xsd", model.getBeanMetadata(aaa).getNamespace());
		
		aaa = builder.parse(getClass().getResourceAsStream("aaa-message.xml"), AAA.class);
		assertEquals(1234, aaa.getIntProperty());
	}

	public void test_2_schema_with_validation() throws SAXException, IOException {
		test_2_schema(new DynamicModelBuilder(NS_DESCRIPTOR).validate(true));
	}

	public void test_2_schema_without_validation() throws SAXException, IOException {
		test_2_schema(new DynamicModelBuilder(NS_DESCRIPTOR).validate(false));
	}

	private void test_2_schema(DynamicModelBuilder builder) throws SAXException, IOException {
		Model<JavaResult> model = builder.parse(getClass().getResourceAsStream("bbb-message.xml"));
		BBB bbb = model.getModelRoot().getBean(BBB.class);		
		assertEquals(1234, bbb.getFloatProperty(), 1.0);
		
		assertEquals("http://www.acme.com/xsd/bbb.xsd", model.getBeanMetadata(bbb).getNamespace());
		AAA aaa = bbb.getAaa();
		assertEquals("http://www.acme.com/xsd/aaa.xsd", model.getBeanMetadata(aaa).getNamespace());
		
		bbb = builder.parse(getClass().getResourceAsStream("bbb-message.xml"), BBB.class);
		assertEquals(1234, bbb.getFloatProperty(), 1.0);
		
		aaa = bbb.getAaa();
		assertEquals(1234, aaa.getIntProperty());
	}

	public void test_error_on_post_parse_config_change() throws SAXException, IOException {
		DynamicModelBuilder builder = new DynamicModelBuilder(NS_DESCRIPTOR);
		
		// Should be ok to modify the builder config before we do the first parse...
		builder.validate(false);
		builder.validate(true);
		
		builder.parse(getClass().getResourceAsStream("bbb-message.xml"));
		
		// An attempt to modify the builder config should now result in an error because we have called parse...
		try {
			builder.validate(false);
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals("Invalid operation.  The 'parse' method has been invoked at least once.", e.getMessage());
		}
	}
}