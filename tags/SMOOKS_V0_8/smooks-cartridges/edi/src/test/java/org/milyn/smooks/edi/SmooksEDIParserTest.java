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

package org.milyn.smooks.edi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.milyn.SmooksStandalone;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.io.StreamUtils;
import org.milyn.schema.ediMessageMapping10.EdimapDocument.Edimap;
import org.milyn.xml.Parser;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * Tests for SmooksEDIParser.
 * @author tfennelly
 */
public class SmooksEDIParserTest extends TestCase {

	private static final String TEST_XML_MAPPING_XML_URI = "classpath:/org/milyn/smooks/edi/edi-to-xml-mapping.xml";

	public void test_inlined() throws IOException, SAXException {
		String mapping = new String(StreamUtils.readStream(getClass().getResourceAsStream("edi-to-xml-mapping.xml")));
		test(mapping);
	}

	public void test_uri_based() throws IOException, SAXException {
		test(TEST_XML_MAPPING_XML_URI);
	}

	public void test_invalid_config() throws IOException, SAXException {
		// Mandatory "mapping-model" config param not specified...
		try {
			test(null);
			fail("Expected IllegalStateException.");
		} catch(IllegalStateException e) {
			assertEquals("Mandatory resource configuration parameter [mapping-model] not specified for [org.milyn.smooks.edi.SmooksEDIParser] parser configuration.  Target Useragent(s) [*].", e.getMessage());
		}

		// Mandatory "mapping-model" config param is a valid URI, but doesn't point at anything that exists...
		try {
			test("http://nothing/there.xml");
			fail("Expected IllegalStateException.");
		} catch(IllegalStateException e) {
			assertEquals("Invalid EDI mapping model config specified for org.milyn.smooks.edi.SmooksEDIParser.  Unable to access URI based mapping model [http://nothing/there.xml].  Target Useragent(s) [*].", e.getMessage());
		}

		// Mandatory "mapping-model" config param is not a valid URI, nor is it a valid inlined config...
		try {
			test("<z/>");
			fail("Expected SAXException.");
		} catch(SAXException e) {
			assertEquals("Error parsing EDI mapping model [<z/>].  Target Useragent(s) [*].", e.getMessage());
		}
	}
	
	public void test_caching() throws IOException, SAXException {
		byte[] input = StreamUtils.readStream(getClass().getResourceAsStream("edi-input.txt"));
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		SmooksResourceConfiguration config = null;

		// Register the useragent used to create the smooks request that will be passed to the parser...
		smooks.registerUseragent("my-useragnet");
		// Create and initialise the Smooks config for the parser...
		config = new SmooksResourceConfiguration("x", SmooksEDIParser.class.getName());
		// Set the mapping config on the resource config... 
		config.setParameter(SmooksEDIParser.MODEL_CONFIG_KEY, TEST_XML_MAPPING_XML_URI);

		Parser parser;

		// Create 1st parser using the config, and run a parse through it...
		parser = new Parser(smooks.createRequest("my-useragnet", null), config);
		parser.parse(new InputStreamReader(new ByteArrayInputStream(input)));
		
		// Check make sure the parsed and validated model was cached...
		Hashtable mappingTable = SmooksEDIParser.getMappingTable(smooks.getContext());
		assertNotNull("No mapping table in context!", mappingTable);
		Edimap mappingModel_request1 = (Edimap) mappingTable.get(config);
		assertNotNull("No mapping model in mapping table!", mappingModel_request1);

		// Create 2nd parser using the same config, and run a parse through it...
		parser = new Parser(smooks.createRequest("my-useragnet", null), config);
		parser.parse(new InputStreamReader(new ByteArrayInputStream(input)));
		
		// Make sure the cached model was used on the 2nd parse...
		assertEquals("Not the same model instance => cache not working properly!", mappingModel_request1, (Edimap) mappingTable.get(config));
	}

	private void test(String mapping) throws IOException, SAXException {
		InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream("edi-input.txt")));
		String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("expected.xml")));
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		SmooksResourceConfiguration config = null;

		// Register the useragent used to create the smooks request that will be passed to the parser...
		smooks.registerUseragent("my-useragnet");
		// Create and initialise the Smooks config for the parser...
		config = new SmooksResourceConfiguration("x", SmooksEDIParser.class.getName());
		// Set the mapping config on the resource config... 
		if(mapping != null) {
			config.setParameter(SmooksEDIParser.MODEL_CONFIG_KEY, mapping);
		}

		Parser parser = new Parser(smooks.createRequest("my-useragnet", null), config);
		Document doc = parser.parse(new InputStreamReader(input));
		
		//System.out.println(XmlUtil.serialize(doc.getChildNodes()));
		assertEquals(removeCRLF(expected), removeCRLF(XmlUtil.serialize(doc.getChildNodes())));
	}
	
	private String removeCRLF(String string) {
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			if(character != '\r' && character != '\n') {
				buffer.append(character);
			}
		}
		
		return buffer.toString();
	}
}
