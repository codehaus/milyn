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
package org.milyn.smooks.edi.unedifact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.stream.StreamSource;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.FilterSettings;
import org.milyn.Smooks;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIUtils;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNEdifactReaderTest extends TestCase {

	public void test_DOM() throws IOException, SAXException {
		test(FilterSettings.DEFAULT_DOM);
	}

	public void test_SAX() throws IOException, SAXException {
		test(FilterSettings.DEFAULT_SAX);
	}
	
	public void test(FilterSettings filterSettings) throws IOException, SAXException {
		Smooks smooks = new Smooks("/org/milyn/smooks/edi/unedifact/smooks-config-xml.xml");
		StringResult result = new StringResult();
		
		smooks.setFilterSettings(filterSettings);
		smooks.filterSource(new StreamSource(getClass().getResourceAsStream("unedifact-msg-01.edi")), result);

		XMLUnit.setIgnoreWhitespace( true );
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("unedifact-msg-expected-01.xml")), new StringReader(result.toString()));		
	}


	public void test_zipped() throws IOException, SAXException, EDIConfigurationException {
		createZip();
		
		Smooks smooks = new Smooks("/org/milyn/smooks/edi/unedifact/smooks-config-zip.xml");
		StringResult result = new StringResult();
		
		smooks.filterSource(new StreamSource(getClass().getResourceAsStream("unedifact-msg-01.edi")), result);

		XMLUnit.setIgnoreWhitespace( true );
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("unedifact-msg-expected-01.xml")), new StringReader(result.toString()));		
	}

	private void createZip() throws IOException {
		File zipFile = new File("target/mapping-models.zip");
		
		zipFile.delete();
		
		ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));		
		try {
			addZipEntry("test/models/MSG1-model.xml", "MSG1-model.xml", zipStream);
			addZipEntry("test/models/MSG2-model.xml", "MSG2-model.xml", zipStream);
			addZipEntry("test/models/MSG3-model.xml", "MSG3-model.xml", zipStream);
			addZipEntry(EDIUtils.EDI_MAPPING_MODEL_ZIP_LIST_FILE, "mapping-models.lst", zipStream);
		} finally {
			zipStream.close();
		}
	}

	private void addZipEntry(String name, String resource, ZipOutputStream zipStream) throws IOException {
		ZipEntry zipEntry = new ZipEntry(name);
		byte[] resourceBytes = StreamUtils.readStream(getClass().getResourceAsStream(resource));
		
		zipStream.putNextEntry(zipEntry);
		zipStream.write(resourceBytes);
	}
}
