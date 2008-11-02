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

import org.milyn.edisax.model.EDIConfigDigester;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.model.internal.SegmentGroup;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

/**
 * @author tfennelly
 */
public class EDIParserTest extends AbstractEDIParserTestCase {

	public void test_validation() throws IOException, SAXException {
		// Valid doc...
        try {
            EDIConfigDigester.digestConfig(getClass().getResourceAsStream("edi-mapping_01.xml"));
        } catch (EDIConfigurationException e) {
            fail("Digesting edi-mapping_01.xml should not fail.");
        }

        // Invalid doc...
		try {
			EDIConfigDigester.digestConfig(getClass().getResourceAsStream("edi-mapping_02.xml"));
			fail("Expected SAXException");
		} catch (SAXException e) {
			// OK
        } catch (EDIConfigurationException e) { 
            fail("Expected SAXException");
        }
    }
	
	public void test_parseMappingModel() throws IOException, SAXException, EDIConfigurationException {
		EdifactModel map = EDIParser.parseMappingModel(getClass().getResourceAsStream("edi-mapping_01.xml"));
		
		// Some basic checks on the model produced by xmlbeans...
		
		// Make sure xml character refs are rewritten on the delimiters
		assertEquals("\n", map.getDelimiters().getSegment());
		assertEquals("*", map.getDelimiters().getField());
		assertEquals("^", map.getDelimiters().getComponent());
		assertEquals("~", map.getDelimiters().getSubComponent());
		
		assertEquals("message-x", map.getEdimap().getSegments().getXmltag());
		List<SegmentGroup> segments = map.getEdimap().getSegments().getSegments();
		assertEquals(2, segments.size());

        Segment segment = (Segment) segments.get(0);
        assertEquals(1, segment.getSegments().size());
		assertEquals(1, segment.getFields().size());

        segment = (Segment) segments.get(1);
        assertEquals(0, segment.getSegments().size());
		assertEquals(1, segment.getFields().size());
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

}
