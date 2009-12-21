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
package org.milyn.routing.basic;

import junit.framework.TestCase;

import org.milyn.FilterSettings;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class FragmentSerializerNSConfigTest extends TestCase {

    public void test_children_only_SAX() throws IOException, SAXException {
    	test_children_only(FilterSettings.DEFAULT_SAX);
    }
    public void test_children_only_DOM() throws IOException, SAXException {
    	test_children_only(FilterSettings.DEFAULT_DOM);
    }
    private void test_children_only(FilterSettings filterSettings) throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01-ext.xml"));
        StreamSource source = new StreamSource(getClass().getResourceAsStream("input-message-01.xml"));
        JavaResult result = new JavaResult();

        smooks.setFilterSettings(filterSettings);
        smooks.filterSource(source, result);
        assertEquals("<cc1:Address xmlns:cc1=\"http://test.com\">\n" +
                "            <cc1:AddressType>Home</cc1:AddressType>\n" +
                "            <cc1:AddressLine1>String</cc1:AddressLine1>\n" +
                "            <cc1:City>String</cc1:City>\n" +
                "            <cc1:PostalCode>String</cc1:PostalCode>\n" +
                "            <cc1:County>String</cc1:County>\n" +
                "        </cc1:Address>",
                result.getBean("soapBody").toString().trim());
    }

    public void test_all_SAX() throws IOException, SAXException {
    	test_all(FilterSettings.DEFAULT_SAX);
    }
    public void test_all_DOM() throws IOException, SAXException {
    	test_all(FilterSettings.DEFAULT_DOM);
    }
    private void test_all(FilterSettings filterSettings) throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-02-ext.xml"));
        StreamSource source = new StreamSource(getClass().getResourceAsStream("input-message-01.xml"));
        JavaResult result = new JavaResult();

        smooks.setFilterSettings(filterSettings);
        smooks.filterSource(source, result);
        assertEquals("<?xml version=\"1.0\"?>\n<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "        <cc1:Address xmlns:cc1=\"http://test.com\">\n" +
                "            <cc1:AddressType>Home</cc1:AddressType>\n" +
                "            <cc1:AddressLine1>String</cc1:AddressLine1>\n" +
                "            <cc1:City>String</cc1:City>\n" +
                "            <cc1:PostalCode>String</cc1:PostalCode>\n" +
                "            <cc1:County>String</cc1:County>\n" +
                "        </cc1:Address>\n" +
                "    </soapenv:Body>",
                result.getBean("soapBody").toString().trim());
    }
}
