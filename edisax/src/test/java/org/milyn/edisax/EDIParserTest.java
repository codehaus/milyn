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

package org.milyn.edisax;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.milyn.io.StreamUtils;
import org.milyn.schema.ediMessageMapping10.EdimapDocument.Edimap;
import org.milyn.schema.ediMessageMapping10.SegmentDocument.Segment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import junit.framework.TestCase;

/**
 * @author tfennelly
 */
public class EDIParserTest extends TestCase {

	public void test_validation() throws IOException, SAXException {
		// Valid doc...
		EDIParser.assertMappingConfigValid(getClass().getResourceAsStream("edi-mapping_01.xml"));
		
		// Invalid doc...
		try {
			EDIParser.assertMappingConfigValid(getClass().getResourceAsStream("edi-mapping_02.xml"));
			fail("Expected SAXException");
		} catch (SAXException e) {
			// OK
		}
	}
	
	public void test_parseMappingModel() throws IOException, SAXException {
		Edimap map = EDIParser.parseMappingModel(getClass().getResourceAsStream("edi-mapping_01.xml"));
		
		// Some basic checks on the model produced by xmlbeans...
		
		// Make sure xml character refs are rewritten on the delimiters
		assertEquals("\n", map.getDelimiters().getSegment());
		assertEquals("*", map.getDelimiters().getField());
		assertEquals("^", map.getDelimiters().getComponent());
		assertEquals("~", map.getDelimiters().getSubComponent());
		
		assertEquals("message-x", map.getSegments().getXmltag());
		Segment[] segments = map.getSegments().getSegmentArray();
		assertEquals(2, segments.length);
		
		assertEquals(1, segments[0].getSegmentArray().length);
		assertEquals(1, segments[0].getFieldArray().length);

		assertEquals(0, segments[1].getSegmentArray().length);
		assertEquals(1, segments[1].getFieldArray().length);
	}
	
	public void test_mappings() throws IOException {
		test("test01");
		test("test02");
		test("test03");
		test("test04");
		test("test05");
		test("test06");
		test("test07");
		test("test08");
		test("test09");
		test("test10");
		test("test11");
		test("test12");
		test("test13");
		test("test14");
	}

	private void test(String testpack) throws IOException {
		InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-input.txt")));
		InputStream mapping = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-to-xml-mapping.xml")));
		String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/expected.xml")));
		MockContentHandler contentHandler = new MockContentHandler();
		
		try {
			EDIParser parser = new EDIParser();
			
			parser.setContentHandler(contentHandler);
			parser.setMappingModel(EDIParser.parseMappingModel(mapping));
			parser.parse(new InputSource(input));
			
			if(!contentHandler.xmlMapping.toString().equals(expected)) {
				System.out.println(contentHandler.xmlMapping.toString());
				assertEquals("Testpack [" + testpack + "] failed.", expected, contentHandler.xmlMapping.toString());
			}
		} catch (SAXException e) {
			String exceptionMessage = e.getClass().getName() + ":" + e.getMessage();
			
			if(!exceptionMessage.equals(expected)) {
				assertEquals("Unexpected exception on testpack [" + testpack + "].  ", expected, exceptionMessage);
			}
		}
	}
	
	public void test_x() throws IOException, SAXException {
		InputStream ediInputStream = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream("test01/edi-input.txt")));
		InputStream edi2SaxMappingConfig = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream("test01/edi-to-xml-mapping.xml")));
		ContentHandler contentHandler = new DefaultHandler();
		
		EDIParser parser = new EDIParser();
		
		parser.setContentHandler(contentHandler);
		parser.setMappingModel(EDIParser.parseMappingModel(edi2SaxMappingConfig));
		parser.parse(new InputSource(ediInputStream));
	}
}
