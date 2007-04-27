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

package org.milyn.delivery.dom.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockExecutionContext;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.dom.MockContentDeliveryConfig;
import org.milyn.delivery.dom.serialize.Serializer;
import org.milyn.util.CharUtils;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class SerializerTest extends TestCase {

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	}

	public void testSerailize() {
		MockExecutionContext executionContext = new MockExecutionContext();

        // Target a resource at the "document fragment" i.e. the root..

        // Don't write xxx but write its child elements
		SmooksResourceConfiguration configuration = new SmooksResourceConfiguration(SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR, "deviceX", "....");
		((MockContentDeliveryConfig)executionContext.deliveryConfig).serializationUnits.addMapping(SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR, configuration, new AddAttributeSerializer(configuration));

        // Don't write xxx but write its child elements
		configuration = new SmooksResourceConfiguration("xxx", "deviceX", "....");
		((MockContentDeliveryConfig)executionContext.deliveryConfig).serializationUnits.addMapping("xxx", configuration, new RemoveTestSerializationUnit(configuration));

		// write yyyy as a badly-formed empty element
		configuration = new SmooksResourceConfiguration("yyyy", "deviceX", "....");
		configuration.setParameter("wellformed", "false");
        ((MockContentDeliveryConfig)executionContext.deliveryConfig).serializationUnits.addMapping("yyyy", configuration, new EmptyElTestSerializationUnit(configuration));

		/// write zzz as a well-formed empty element
		configuration = new SmooksResourceConfiguration("zzz", "deviceX", "....");
        ((MockContentDeliveryConfig)executionContext.deliveryConfig).serializationUnits.addMapping("zzz", configuration, new EmptyElTestSerializationUnit(configuration));

		try {
			Document doc = XmlUtil.parseStream(getClass().getResourceAsStream("testmarkup.xxml"), false, true);
			Serializer serializer = new Serializer(doc, executionContext);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(output);

			serializer.serailize(writer);
			writer.flush();
			byte[] actualBytes = output.toByteArray();
			System.out.print(new String(actualBytes));
			boolean areEqual = CharUtils.compareCharStreams(getClass().getResourceAsStream("testmarkup.xxml.ser_1"), new ByteArrayInputStream(actualBytes));
			assertTrue("Unexpected Serialization result failure.", areEqual);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
