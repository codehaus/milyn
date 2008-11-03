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

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.Smooks;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.dom.DOMParser;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.milyn.schema.edi_message_mapping_1_0.EdiMap;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Tests for SmooksEDIReader.
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
			fail("Expected SmooksConfigurationException.");
		} catch(SmooksConfigurationException e) {
			assertTrue(e.getMessage().startsWith("<param> 'mapping-model' not specified on resource configuration"));
		}

		// Mandatory "mapping-model" config param is a valid URI, but doesn't point at anything that exists...
		try {
			test("http://nothing/there.xml");
			fail("Expected IllegalStateException.");
		} catch(IllegalStateException e) {
			assertEquals("Invalid EDI mapping model config specified for org.milyn.smooks.edi.SmooksEDIReader.  Unable to access URI based mapping model [http://nothing/there.xml].  Target Profile(s) [org.milyn.profile.profile#default_profile].", e.getMessage());
		}

		// Mandatory "mapping-model" config param is not a valid URI, nor is it a valid inlined config...
		try {
			test("<z/>");
			fail("Expected SAXException.");
		} catch(SAXException e) {
			assertEquals("Error parsing EDI mapping model [<z/>].  Target Profile(s) [org.milyn.profile.profile#default_profile].", e.getMessage());
		}
	}

	public void test_caching() throws IOException, SAXException {
		byte[] input = StreamUtils.readStream(getClass().getResourceAsStream("edi-input.txt"));
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration config = null;

		// Create and initialise the Smooks config for the parser...
		config = new SmooksResourceConfiguration();
        config.setResource(SmooksEDIReader.class.getName());
		// Set the mapping config on the resource config...
        config.setParameter(SmooksEDIReader.MODEL_CONFIG_KEY, TEST_XML_MAPPING_XML_URI);

		DOMParser parser;

		// Create 1st parser using the config, and run a parse through it...
		parser = new DOMParser(smooks.createExecutionContext(), config);
		parser.parse(new InputStreamReader(new ByteArrayInputStream(input)));

		// Check make sure the parsed and validated model was cached...
		Hashtable mappingTable = SmooksEDIReader.getMappingTable(smooks.getApplicationContext());
		assertNotNull("No mapping table in context!", mappingTable);
		EdiMap mappingModel_request1 = (EdiMap) mappingTable.get(config);
		assertNotNull("No mapping model in mapping table!", mappingModel_request1);

		// Create 2nd parser using the same config, and run a parse through it...
		parser = new DOMParser(smooks.createExecutionContext(), config);
		parser.parse(new InputStreamReader(new ByteArrayInputStream(input)));

		// Make sure the cached model was used on the 2nd parse...
		assertEquals("Not the same model instance => cache not working properly!", mappingModel_request1, mappingTable.get(config));
	}

	private void test(String mapping) throws IOException, SAXException {
		InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream("edi-input.txt")));
		String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("expected.xml")));
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration config = null;

		// Create and initialise the Smooks config for the parser...
        config = new SmooksResourceConfiguration();
        config.setResource(SmooksEDIReader.class.getName());
		// Set the mapping config on the resource config...
		if(mapping != null) {
			config.setParameter(SmooksEDIReader.MODEL_CONFIG_KEY, mapping);
		}

		DOMParser parser = new DOMParser(smooks.createExecutionContext(), config);
		Document doc = parser.parse(new InputStreamReader(input));

		//System.out.println(XmlUtil.serialize(doc.getChildNodes()));
		Diff diff = new Diff(expected, XmlUtil.serialize(doc.getChildNodes()));
		System.out.println(diff.identical());
		assertTrue(diff.identical());
		//assertEquals(removeCRLF(expected), removeCRLF(XmlUtil.serialize(doc.getChildNodes())));
	}

    public void test_v11config() throws IOException, SAXException {
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("expected.xml")));
        Smooks smooks = new Smooks(getClass().getResourceAsStream("v11config.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("edi-input.txt")), result);
        assertEquals(removeCRLF(expected), removeCRLF(result.getResult()));
    }

    public void test_v11_extended_config() throws IOException, SAXException {
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("expected.xml")));
        Smooks smooks = new Smooks(getClass().getResourceAsStream("v11extendedConfig.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StreamSource(getClass().getResourceAsStream("edi-input.txt")), result);
        assertEquals(removeCRLF(expected), removeCRLF(result.getResult()));
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
