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
package org.milyn.edisax.unedifact;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.MockContentHandler;
import org.milyn.edisax.model.EdifactModel;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InterchangeParserTest extends TestCase {
	
	public void test() throws IOException, SAXException, EDIConfigurationException {
		MockContentHandler handler;
		EdifactModel msg1 = EDIParser.parseMappingModel(getClass().getResourceAsStream("MSG1-model.xml"));
		EdifactModel msg2 = EDIParser.parseMappingModel(getClass().getResourceAsStream("MSG2-model.xml"));
		
		InterchangeParser parser = new InterchangeParser();
		parser.addMappingModel(msg1).addMappingModel(msg2);

		// Test message 01 - no UNA segment...
		handler = new MockContentHandler();
		parser.setContentHandler(handler);		
		parser.parse(new InputSource(getClass().getResourceAsStream("unedifact-msg-01.edi")));		
        XMLUnit.setIgnoreWhitespace( true );
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("unedifact-msg-expected.xml")), new StringReader(handler.xmlMapping.toString()));

		// Test message 01 - has a UNA segment...
		handler = new MockContentHandler();
		parser.setContentHandler(handler);		
		parser.parse(new InputSource(getClass().getResourceAsStream("unedifact-msg-02.edi")));		
        XMLUnit.setIgnoreWhitespace( true );
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("unedifact-msg-expected.xml")), new StringReader(handler.xmlMapping.toString()));
	}
}
