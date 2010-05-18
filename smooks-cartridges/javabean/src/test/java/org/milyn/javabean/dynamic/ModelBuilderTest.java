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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ModelBuilderTest extends TestCase {

	public static final String NS_DESCRIPTOR = "META-INF/services/org/smooks/javabean/dynamic/ns-descriptors.properties";
	
	public void test_1_schema() throws SAXException, IOException {
		ModelBuilder builder = new ModelBuilder(NS_DESCRIPTOR);
		
		Model<AAA> model = builder.readModel(getClass().getResourceAsStream("aaa-message.xml"), AAA.class);
		AAA aaa = model.getModelRoot();
		assertEquals(1234.98765, aaa.getDoubleProperty());
		assertEquals("http://www.acme.com/xsd/aaa.xsd", model.getBeanMetadata(aaa).getNamespace());
		
		aaa = builder.readObject(getClass().getResourceAsStream("aaa-message.xml"), AAA.class);
		assertEquals(1234.98765, aaa.getDoubleProperty());
	}

	public void test_2_schema_with_validation() throws SAXException, IOException {
		test_2_schema(new ModelBuilder(NS_DESCRIPTOR).validate(true));
	}

	public void test_2_schema_without_validation() throws SAXException, IOException {
		test_2_schema(new ModelBuilder(NS_DESCRIPTOR).validate(false));
	}

	private void test_2_schema(ModelBuilder builder) throws SAXException, IOException {
		Model<BBB> model = builder.readModel(getClass().getResourceAsStream("bbb-message.xml"), BBB.class);
		BBB bbb = model.getModelRoot();		
		assertEquals(1234, bbb.getFloatProperty(), 1.0);
		
		assertEquals("http://www.acme.com/xsd/bbb.xsd", model.getBeanMetadata(bbb).getNamespace());
		List<AAA> aaas = bbb.getAaas();
        assertEquals(3, aaas.size());
		assertEquals("http://www.acme.com/xsd/aaa.xsd", model.getBeanMetadata(aaas.get(0)).getNamespace());

		bbb = builder.readObject(getClass().getResourceAsStream("bbb-message.xml"), BBB.class);
		assertEquals(1234, bbb.getFloatProperty(), 1.0);

		aaas = bbb.getAaas();
		assertEquals(1234.98765, aaas.get(0).getDoubleProperty());

        StringWriter writer = new StringWriter();
        model.writeModel(writer);
//        System.out.println(writer);
        XMLUnit.setIgnoreWhitespace( true );
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("bbb-message.xml")), new StringReader(writer.toString()));       
	}

	public void test_error_on_post_parse_config_change() throws SAXException, IOException {
		ModelBuilder builder = new ModelBuilder(NS_DESCRIPTOR);
		
		// Should be ok to modify the builder config before we do the first parse...
		builder.validate(false);
		builder.validate(true);
		
		builder.readModel(getClass().getResourceAsStream("bbb-message.xml"), JavaResult.class);
		
		// An attempt to modify the builder config should now result in an error because we have called parse...
		try {
			builder.validate(false);
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals("Invalid operation.  The 'parse' method has been invoked at least once.", e.getMessage());
		}
	}

    public void test_build_model() throws IOException, SAXException {
        ModelBuilder builder = new ModelBuilder(NS_DESCRIPTOR);
        BBB bbb = new BBB();
        List<AAA> aaas = new ArrayList<AAA>();
        Model<BBB> model = new Model<BBB>(bbb, builder);

        model.registerBean(bbb);

        bbb.setFloatProperty(1234.87f);
        bbb.setAaas(aaas);

        aaas.add(new AAA());
        aaas.get(0).setDoubleProperty(1234.98765d);
        aaas.get(0).setIntProperty(123);
        model.registerBean(aaas.get(0));
        aaas.add(new AAA());
        aaas.get(1).setDoubleProperty(2234.98765d);
        aaas.get(1).setIntProperty(223);
        model.registerBean(aaas.get(1));
        aaas.add(new AAA());
        aaas.get(2).setDoubleProperty(3234.98765d);
        aaas.get(2).setIntProperty(323);
        model.registerBean(aaas.get(2));

        StringWriter writer = new StringWriter();
        model.writeModel(writer);
//        System.out.println(writer);
        XMLUnit.setIgnoreWhitespace( true );
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("bbb-message.xml")), new StringReader(writer.toString()));
    }
}