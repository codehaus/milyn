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
import java.io.InputStream;

import org.milyn.SmooksException;
import org.milyn.SmooksStandalone;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.templating.CharUtils;
import org.milyn.templating.TemplatingUtils;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class XslContentDeliveryUnitCreatorTest extends TestCase {

    /**
     * Removed because of problems caused in Maven.
     */
	public void testXslUnitTrans() {
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		SmooksResourceConfiguration res = new SmooksResourceConfiguration("p", "devicename", "org/milyn/templating/xslt/xsltransunit.xsl");
		String transResult = null;

		System.setProperty("javax.xml.transform.TransformerFactory", org.apache.xalan.processor.TransformerFactoryImpl.class.getName());
		smooks.registerUseragent("devicename");
		smooks.registerResource(res);
		TemplatingUtils.registerCDUCreators(smooks.getContext());
		
		try {
			InputStream stream = getClass().getResourceAsStream("htmlpage.html");
			transResult = smooks.filterAndSerialize("devicename", stream);
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		boolean equalsExpected = CharUtils.compareCharStreams(getClass().getResourceAsStream("xsltransunit.expected"), new ByteArrayInputStream(transResult.getBytes()));
		if(!equalsExpected) {
			System.out.println("XSL Comparison Failure - See xsltransunit.expected.");
			System.out.println("============== Actual ==================");
			System.out.println(transResult);
			System.out.println("====================================================================================");
		}
		assertTrue("Expected XSL Transformation result failure.", equalsExpected);
	}	
}
