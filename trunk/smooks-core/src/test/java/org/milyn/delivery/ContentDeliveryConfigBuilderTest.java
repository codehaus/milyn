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
package org.milyn.delivery;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.io.StreamUtils;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.delivery.dom.DOMContentDeliveryConfig;
import org.milyn.delivery.sax.SAXContentDeliveryConfig;
import org.milyn.delivery.sax.SAXVisitor01;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ContentDeliveryConfigBuilderTest extends TestCase {

    public void test_sax() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-sax.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();

        assertTrue(execContext.getDeliveryConfig() instanceof SAXContentDeliveryConfig);
        SAXContentDeliveryConfig config = (SAXContentDeliveryConfig) execContext.getDeliveryConfig();

        // Should be 5: 4 configured + 1 auto-installed ContextObjectSerializationUnit 
        assertEquals(5, config.getSaxVisitors().getCount());
        assertTrue(config.getSaxVisitors().getMappings("b").get(0).getContentHandler() instanceof SAXVisitor01);
    }

    public void test_dom() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-dom.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();

        assertTrue(execContext.getDeliveryConfig() instanceof DOMContentDeliveryConfig);
        DOMContentDeliveryConfig config = (DOMContentDeliveryConfig) execContext.getDeliveryConfig();

        assertEquals(2, config.getProcessingUnits().getCount());
        assertEquals(1, config.getAssemblyUnits().getCount());
        assertEquals(2, config.getSerailizationUnits().getCount());
    }

    public void test_dom_sax_1() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-dom-sax-1.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();

        // Should default to DOM
        assertTrue(execContext.getDeliveryConfig() instanceof DOMContentDeliveryConfig);
    }

    public void test_dom_sax_2() throws IOException, SAXException {
        Smooks smooks;
        StandaloneExecutionContext execContext;

        smooks = new Smooks(getClass().getResourceAsStream("smooks-config-dom-sax-2.1.xml"));
        execContext = smooks.createExecutionContext();
        assertTrue(execContext.getDeliveryConfig() instanceof SAXContentDeliveryConfig);

        smooks = new Smooks(getClass().getResourceAsStream("smooks-config-dom-sax-2.2.xml"));
        execContext = smooks.createExecutionContext();
        assertTrue(execContext.getDeliveryConfig() instanceof DOMContentDeliveryConfig);

        smooks = new Smooks(getClass().getResourceAsStream("smooks-config-dom-sax-2.3.xml"));
        try {
            smooks.createExecutionContext();
            fail("Expected SmooksException");
        } catch(SmooksException e) {
            assertEquals("Invalid 'stream.filter.type' configuration parameter value of 'xxxx'.  Must be 'SAX' or 'DOM'.", e.getMessage());
        }
    }

    public void test_invalid() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-invalid.xml"));

        try {
            smooks.createExecutionContext();
            fail("Expected SmooksException");
        } catch(SmooksException e) {
            String expected = StreamUtils.trimLines(getClass().getResourceAsStream("smooks-config-invalid-error.txt")).toString();
            String actual = StreamUtils.trimLines(new StringReader(e.getMessage())).toString();

            assertEquals(expected, actual);
        }
    }
}
