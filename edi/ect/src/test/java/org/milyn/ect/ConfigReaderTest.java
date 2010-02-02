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
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.zip.ZipInputStream;

/**
 * ConfigReaderTest
 * @author bardl
 */
public class ConfigReaderTest extends TestCase {

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

    private void testSegment(String segmentCode, String definitions) throws IOException {
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("d08a/segment/expected-" + segmentCode.toLowerCase() + ".xml"))).trim();
        String result = removeCRLF(definitions);
        expected = removeCRLF(expected);
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

}
