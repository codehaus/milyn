package org.milyn.cdr;

import java.util.Properties;

import junit.framework.TestCase;

public class PropertyListParameterDecoderTest extends TestCase {

	public void test_decodeValue() {
		SmooksResourceConfiguration config = new SmooksResourceConfiguration("x", "x");
		PropertyListParameterDecoder propDecoder = new PropertyListParameterDecoder(config);
		
		Properties properties = (Properties) propDecoder.decodeValue("x=111\ny=222");
		assertEquals(2, properties.size());
		assertEquals("111", properties.getProperty("x"));
		assertEquals("222", properties.getProperty("y"));
	}

}
