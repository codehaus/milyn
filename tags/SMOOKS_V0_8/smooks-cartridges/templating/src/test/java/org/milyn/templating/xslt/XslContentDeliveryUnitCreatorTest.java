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

import java.io.InputStream;

import org.milyn.SmooksException;
import org.milyn.SmooksStandalone;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.templating.TemplatingUtils;
import org.milyn.templating.util.CharUtils;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class XslContentDeliveryUnitCreatorTest extends TestCase {

	public void testXslUnitTrans_filebased_replace() {
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
		CharUtils.assertEquals("XSL Comparison Failure - See xsltransunit.expected1.", "/org/milyn/templating/xslt/xsltransunit.expected1", transResult);
	}	

	public void testXslUnitTrans_parambased() {
		testXslUnitTrans_parambased("insertbefore", "xsltransunit.expected2");
		testXslUnitTrans_parambased("insertafter", "xsltransunit.expected3");
		testXslUnitTrans_parambased("addto", "xsltransunit.expected4");
		testXslUnitTrans_parambased("replace", "xsltransunit.expected5");
	}
	
	public void testXslUnitTrans_parambased(String action, String expectedFileName) {
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		SmooksResourceConfiguration res = new SmooksResourceConfiguration("p", "devicename");
		String transResult = null;

		System.setProperty("javax.xml.transform.TransformerFactory", org.apache.xalan.processor.TransformerFactoryImpl.class.getName());
		
		res.setParameter("restype", "xsl");
		res.setParameter("resdata", "<z id=\"{@id}\">Content from template!!</z>");
		res.setParameter("action", action);
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
		CharUtils.assertEquals("XSL Comparison Failure.  action=" + action + ".  See " + expectedFileName, "/org/milyn/templating/xslt/" + expectedFileName, transResult);
	}	
}