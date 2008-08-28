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

import junit.framework.TestCase;
import org.milyn.io.StreamUtils;
import org.milyn.schema.edi_message_mapping_1_0.Edimap;
import org.milyn.schema.edi_message_mapping_1_0.Segment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.List;

/**
 * @author tfennelly
 */
public class EDIParserTest extends TestCase {

	public void test_validation() throws IOException, SAXException {
		// Valid doc...
		EDIParser.assertMappingConfigValid(new InputStreamReader(getClass().getResourceAsStream("edi-mapping_01.xml")));
		
		// Invalid doc...
		try {
			EDIParser.assertMappingConfigValid(new InputStreamReader(getClass().getResourceAsStream("edi-mapping_02.xml")));
			fail("Expected SAXException");
		} catch (SAXException e) {
			// OK
		}
	}
	
	public void test_parseMappingModel() throws IOException, SAXException {
		EdifactModel map = EDIParser.parseMappingModel(getClass().getResourceAsStream("edi-mapping_01.xml"));
		
		// Some basic checks on the model produced by xmlbeans...
		
		// Make sure xml character refs are rewritten on the delimiters
		assertEquals("\n", map.getDelimiters().getSegment());
		assertEquals("*", map.getDelimiters().getField());
		assertEquals("^", map.getDelimiters().getComponent());
		assertEquals("~", map.getDelimiters().getSubComponent());
		
		assertEquals("message-x", map.getSequence().getSegments().getXmltag());
		List<Segment> segments = map.getSequence().getSegments().getSegment();
		assertEquals(2, segments.size());
		
		assertEquals(1, segments.get(0).getSegment().size());
		assertEquals(1, segments.get(0).getField().size());

		assertEquals(0, segments.get(1).getSegment().size());
		assertEquals(1, segments.get(1).getField().size());
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
		test("test15");
        test("test16");
        test("test17");
        test("test18");
	}

    public void test_MILYN_108() throws IOException {
        test("test-MILYN-108-01"); // Tests Segment Truncation
        test("test-MILYN-108-02"); // Tests Segment Truncation
        test("test-MILYN-108-03"); // Tests Segment Truncation
        test("test-MILYN-108-04"); // Tests Segment Truncation

        test("test-MILYN-108-05"); // Tests Component Truncation
        test("test-MILYN-108-06"); // Tests Component Truncation
        test("test-MILYN-108-07"); // Tests Component Truncation

        test("test-MILYN-108-08"); // Tests Field Truncation
        test("test-MILYN-108-09"); // Tests Field Truncation

        test("test-MILYN-108-10"); // Tests Field and Component Truncation
        test("test-MILYN-108-11"); // Tests Field and Component Truncation
    }

    private void test(String testpack) throws IOException {
		InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-input.txt")));
		InputStream mapping = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-to-xml-mapping.xml")));
		String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/expected.xml"))).trim();
		MockContentHandler contentHandler = new MockContentHandler();
		
		expected = removeCRLF(expected);
		try {
			EDIParser parser = new EDIParser();
			String mappingResult = null; 
			
			parser.setContentHandler(contentHandler);
			parser.setMappingModel(EDIParser.parseMappingModel(mapping));
			parser.parse(new InputSource(input));
			
			mappingResult = contentHandler.xmlMapping.toString().trim();
			mappingResult = removeCRLF(mappingResult);
			if(!mappingResult.equals(expected)) {
				System.out.println("Expected: \n[" + expected + "]");
				System.out.println("Actual: \n[" + mappingResult + "]");
				assertEquals("Testpack [" + testpack + "] failed.", expected, mappingResult);
			}
		} catch (SAXException e) {
			String exceptionMessage = e.getClass().getName() + ":" + e.getMessage();
			
			exceptionMessage = removeCRLF(exceptionMessage);
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
	
	private String removeCRLF(String string) throws IOException {
        return StreamUtils.trimLines(new StringReader(string)).toString();
	}
}
