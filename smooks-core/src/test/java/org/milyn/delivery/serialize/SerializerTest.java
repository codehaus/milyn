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

package org.milyn.delivery.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockContainerRequest;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.MockContentDeliveryConfig;
import org.milyn.delivery.serialize.Serializer;
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
		MockContainerRequest containerRequest = new MockContainerRequest();
		
		// Don't write xxx but write its child elements
		SmooksResourceConfiguration unitDef = new SmooksResourceConfiguration("xxx", "deviceX", "....");
		Vector serUnits = new Vector();
		serUnits.add(new ContentDeliveryUnitConfigMap(new TestSerializationUnit_Remove(unitDef), unitDef));
		((MockContentDeliveryConfig)containerRequest.deliveryConfig).serializationUnits.put("xxx", serUnits);

		// write yyyy as a badly-formed empty element
		unitDef = new SmooksResourceConfiguration("yyyy", "deviceX", "....");
		unitDef.setParameter("wellformed", "false");
		serUnits = new Vector();
		serUnits.add(new ContentDeliveryUnitConfigMap(new TestSerializationUnit_EmptyEl(unitDef), unitDef));
		((MockContentDeliveryConfig)containerRequest.deliveryConfig).serializationUnits.put("yyyy", serUnits);

		/// write zzz as a well-formed empty element
		unitDef = new SmooksResourceConfiguration("zzz", "deviceX", "....");
		serUnits = new Vector();
		serUnits.add(new ContentDeliveryUnitConfigMap(new TestSerializationUnit_EmptyEl(unitDef), unitDef));
		((MockContentDeliveryConfig)containerRequest.deliveryConfig).serializationUnits.put("zzz", serUnits);
		
		try {
			Document doc = XmlUtil.parseStream(getClass().getResourceAsStream("testmarkup.xxml"), false, true);
			Serializer serializer = new Serializer(doc, containerRequest);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(output);

			serializer.serailize(writer);
			writer.flush();
			byte[] actualBytes = output.toByteArray();
			System.out.print(new String(actualBytes));
			boolean areEqual = CharUtils.compareCharStreams(getClass().getResourceAsStream("testmarkup.xxml.ser_1"), new ByteArrayInputStream(actualBytes));
			assertTrue("Expected Serialization result failure.", areEqual);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
