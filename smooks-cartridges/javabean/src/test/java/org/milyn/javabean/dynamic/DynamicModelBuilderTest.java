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
	
	public void test_1_schema() throws SAXException, IOException {
		DynamicModelBuilder builder = new DynamicModelBuilder();
		
		JavaResult result = builder.parse(getClass().getResourceAsStream("aaa-message.xml"));
		AAA aaa = (AAA) result.getBean("aaa");		
		assertEquals(1234, aaa.getIntProperty());
		
		aaa = builder.parse(getClass().getResourceAsStream("aaa-message.xml"), AAA.class);
		assertEquals(1234, aaa.getIntProperty());
	}

	public void test_2_schema() throws SAXException, IOException {
		DynamicModelBuilder builder = new DynamicModelBuilder();
		
		JavaResult result = builder.parse(getClass().getResourceAsStream("bbb-message.xml"));
		BBB bbb = (BBB) result.getBean("bbb");		
		assertEquals(1234, bbb.getFloatProperty(), 1.0);
		
		bbb = builder.parse(getClass().getResourceAsStream("bbb-message.xml"), BBB.class);
		assertEquals(1234, bbb.getFloatProperty(), 1.0);
		
		AAA aaa = bbb.getAaa();
		assertEquals(1234, aaa.getIntProperty());
	}
}