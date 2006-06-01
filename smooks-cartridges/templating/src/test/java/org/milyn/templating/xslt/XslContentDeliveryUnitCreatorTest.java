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

package org.milyn.templating.xslt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.milyn.SmooksException;
import org.milyn.container.MockContainerRequest;
import org.milyn.delivery.SmooksXML;
import org.milyn.io.StreamUtils;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class XslContentDeliveryUnitCreatorTest extends TestCase {

    public void test_empty() {
        
    }
    
    /*
     * Removed because of problems caused in Maven.
     * 
	public void xtestXslUnitTrans_from_classpath() {
		SmooksUtil smooksUtil;
		
		smooksUtil = new SmooksUtil();
		smooksUtil.addSmooksResourceConfiguration("p", "devicename", "org/milyn/delivery/xsltransunit.xsl", null);
		
		transAndCompare(smooksUtil.getRequest("devicename"));
	}	

	public void xtestXslUnitTrans_from_cdrar() {
		SmooksUtil smooksUtil;
		byte[] xslBytes = null;
		
		try {
			xslBytes = StreamUtils.readStream(getClass().getResourceAsStream("xsltransunit.xsl"));
		} catch (IOException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		smooksUtil = new SmooksUtil();
		smooksUtil.addSmooksResourceConfiguration("p", "devicename", "xxxxx.xsl", xslBytes);
		
		transAndCompare(smooksUtil.getRequest("devicename"));
	}

	private void transAndCompare(MockContainerRequest request) {
		SmooksXML smooks;
		Node deliveryNode = null;

		smooks = new SmooksXML(request);
		try {
			InputStream stream = getClass().getResourceAsStream("htmlpage.html");
			deliveryNode = smooks.filter(new InputStreamReader(stream));
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		assertNotNull("Null transform 'Document' return.", deliveryNode);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(output);
			smooks.serialize(deliveryNode, writer);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		byte[] transResult = output.toByteArray();
		boolean equalsExpected = CharUtils.compareCharStreams(getClass().getResourceAsStream("xsltransunit.expected"), new ByteArrayInputStream(transResult));
		if(!equalsExpected) {
			System.out.println("XSL Comparison Failure - See xsltransunit.expected.");
			System.out.println("============== Actual ==================");
			System.out.println(new String(transResult));
			System.out.println("====================================================================================");
			try {
				FileOutputStream actual = new FileOutputStream("/xxx.txt");
				actual.write(transResult);
				actual.flush();
				actual.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertTrue("Expected XSL Transformation result failure.", equalsExpected);
	}	
    */
}
