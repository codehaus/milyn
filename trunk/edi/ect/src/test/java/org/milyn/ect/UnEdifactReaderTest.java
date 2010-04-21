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
package org.milyn.ect;

import junit.framework.TestCase;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.zip.ZipInputStream;

/**
 * ConfigReaderTest
 * @author bardl
 */
public class UnEdifactReaderTest extends TestCase {

    public void test_D08A_Messages() throws InstantiationException, IllegalAccessException, IOException, EdiParseException {

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org" + File.separator + "milyn" + File.separator + "ect" + File.separator + "D08A.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ConfigReader configReader = ConfigReader.Impls.UNEDIFACT.newInstance();
        configReader.initialize(zipInputStream, false);

        test("BANSTA", configReader);
        test("CASRES", configReader);
        test("INVOIC", configReader);
        test("PAYMUL", configReader);
        test("TPFREP", configReader);
    }

    public void test_D08A_Segments() throws InstantiationException, IllegalAccessException, IOException, EdiParseException, ParserConfigurationException, SAXException {

        InputStream inputStream = ClassUtil.getResourceAsStream("D08A.zip", this.getClass());
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ConfigReader configReader = ConfigReader.Impls.UNEDIFACT.newInstance();
        configReader.initialize(zipInputStream, false);
        Edimap edimap = configReader.getDefinitionModel();

        StringWriter stringWriter = new StringWriter();
        ConfigWriter configWriter = new ConfigWriter();
        configWriter.generate(stringWriter, edimap);

        String result = stringWriter.toString();
        testSegment("BGM", result);
        testSegment("DTM", result);
        testSegment("NAD", result);
        testSegment("PRI", result);        
    }

    public void testRealLifeInputFiles() throws IOException, InstantiationException, IllegalAccessException, EDIConfigurationException, SAXException {
        InputStream inputStream = ClassUtil.getResourceAsStream("D08A.zip", this.getClass());
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ConfigReader configReader = ConfigReader.Impls.UNEDIFACT.newInstance();
        configReader.initialize(zipInputStream, false);

        //Test INVOIC
        String mappingModel = getEdiMessageAsString(configReader, "INVOIC");
        testPackage("d96a-invoic-1", mappingModel);
    }

    public void testPackage(String packageName, String mappingModel) throws IOException, InstantiationException, IllegalAccessException, SAXException, EDIConfigurationException {
        InputStream testFileInputStream = getClass().getResourceAsStream("testfiles/" + packageName + "/input.edi");

        MockContentHandler contentHandler = new MockContentHandler();
        EDIParser parser = new EDIParser();
        parser.setContentHandler(contentHandler);
        parser.setMappingModel(EDIParser.parseMappingModel(new StringReader(mappingModel)));
        parser.parse(new InputSource(testFileInputStream));

        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("testfiles/" + packageName + "/expected-result.txt"))).trim();

        String result = removeCRLF(contentHandler.xmlMapping.toString().trim());
		expected = removeCRLF(expected);

        if(!result.equals(expected)) {
            System.out.println("Expected: \n[" + expected + "]");
            System.out.println("Actual: \n[" + result + "]");
            assertEquals("Message [" + packageName + "] failed.", expected, result);
        }
    }

    private String getEdiMessageAsString(ConfigReader configReader, String messageType) throws IllegalAccessException, InstantiationException, IOException {
        Edimap edimap = configReader.getMappingModelForMessage(messageType);
        StringWriter sw = new StringWriter();
        ConfigWriter writer = new ConfigWriter();
        writer.generate(sw, edimap);
        return sw.toString();
    }

    private void testSegment(String segmentCode, String definitions) throws IOException {
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("d08a/segment/expected-" + segmentCode.toLowerCase() + ".xml"))).trim();
        String result = removeCRLF(definitions);
        expected = removeCRLF(expected);

        if(!result.contains(expected)) {
            System.out.println("Expected: \n[" + expected + "]");
            System.out.println("Actual: \n[" + result + "]");
        }
        assertTrue("Segment [" + segmentCode + "] is incorrect.", result.contains(expected));
    }
    
    private void test(String messageName, ConfigReader configReader) throws IOException {
    	Edimap edimap = configReader.getMappingModelForMessage(messageName);

        StringWriter stringWriter = new StringWriter();
        ConfigWriter configWriter = new ConfigWriter();
        configWriter.generate(stringWriter, edimap);
		String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("d08a/message/expected-" + messageName.toLowerCase() + ".xml"))).trim();

        String result = removeCRLF(stringWriter.toString());
		expected = removeCRLF(expected);

        if(!result.equals(expected)) {
            System.out.println("Expected: \n[" + expected + "]");
            System.out.println("Actual: \n[" + result + "]");
            assertEquals("Message [" + messageName + "] failed.", expected, result);
        }
    }

    private String removeCRLF(String string) throws IOException {
        return string.replaceAll("\r","").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");
	}

    /************************************************************************
     * Private class MockContentHandler                                     *
     ************************************************************************/    
    private class MockContentHandler extends DefaultHandler {

        protected StringBuffer xmlMapping = new StringBuffer();

        public void startDocument() throws SAXException {
            xmlMapping.setLength(0);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            xmlMapping.append(ch, start, length);
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            xmlMapping.append("<").append(localName).append(">");
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            xmlMapping.append("</").append(localName).append(">");
        }
    }
}
