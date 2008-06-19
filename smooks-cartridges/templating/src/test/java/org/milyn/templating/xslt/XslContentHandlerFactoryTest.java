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

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.SmooksUtil;
import org.milyn.javabean.BeanAccessor;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.templating.util.CharUtils;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * 
 * @author tfennelly
 */
public class XslContentHandlerFactoryTest extends TestCase {

	public void testXslUnitTrans_filebased_replace() {
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration res = new SmooksResourceConfiguration("p", "org/milyn/templating/xslt/xsltransunit.xsl");
		String transResult = null;

        System.setProperty("javax.xml.transform.TransformerFactory", org.apache.xalan.processor.TransformerFactoryImpl.class.getName());
		SmooksUtil.registerResource(res, smooks);
		
		try {
			InputStream stream = getClass().getResourceAsStream("htmlpage.html");
            ExecutionContext context = smooks.createExecutionContext();
			transResult = SmooksUtil.filterAndSerialize(context, stream, smooks);
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
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration res = new SmooksResourceConfiguration("p", "<z id=\"{@id}\">Content from template!!</z>");
		String transResult = null;

		System.setProperty("javax.xml.transform.TransformerFactory", org.apache.xalan.processor.TransformerFactoryImpl.class.getName());
		
		res.setResourceType("xsl");
        res.setParameter(XslContentHandlerFactory.IS_XSLT_TEMPLATELET, "true");
		res.setParameter("action", action);
		SmooksUtil.registerResource(res, smooks);
		
		try {
			InputStream stream = getClass().getResourceAsStream("htmlpage.html");
            ExecutionContext context = smooks.createExecutionContext();
			transResult = SmooksUtil.filterAndSerialize(context, stream, smooks);
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		CharUtils.assertEquals("XSL Comparison Failure.  action=" + action + ".  See " + expectedFileName, "/org/milyn/templating/xslt/" + expectedFileName, transResult);
	}	

    public void test_xsl_bind() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("test-configs-bind.cdrl"));
        StringReader input;
        ExecutionContext context;

        input = new StringReader("<a><b><c/></b></a>");
        context = smooks.createExecutionContext();
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<bind/>", BeanAccessor.getBean(context, "mybeanTemplate"));

        input = new StringReader("<c/>");
        context = smooks.createExecutionContext();
        smooks.filter(new StreamSource(input), null, context);
        assertEquals("<bind/>", BeanAccessor.getBean(context, "mybeanTemplate"));
    }

    public void test_badxsl() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("bad-xsl-config.xml"));

        try {
            smooks.filter(new StreamSource(new StringReader("<doc/>")), null, smooks.createExecutionContext());
            fail("Expected SmooksConfigurationException.");
        } catch(SmooksConfigurationException e) {
            assertEquals("Error loading Templating resource: Target Profile: [[org.milyn.profile.profile#default_profile]], Selector: [$document], Selector Namespace URI: [null], Resource: [/org/milyn/templating/xslt/bad-stylesheet.xsl], Num Params: [0]", e.getMessage());
        }
    }
}
