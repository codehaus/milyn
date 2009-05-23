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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public abstract class AbstractEDIParserTestCase extends TestCase {

    protected void test(String testpack) throws IOException {
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
            parser.setFeature(EDIParser.VALIDATE, true);
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
		} catch (EDIConfigurationException e) {
            assert false : e;
        }
    }

    private String removeCRLF(String string) throws IOException {
        return StreamUtils.trimLines(new StringReader(string)).toString();
	}
}
