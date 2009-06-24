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
package org.milyn.smooks.split;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class FragmentSplitterTest extends TestCase {

    public void test_children_only() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        StreamSource source = new StreamSource(getClass().getResourceAsStream("input-message.xml"));
        JavaResult result = new JavaResult();

        smooks.filter(source, result);
        assertEquals("<cc1:Address xmlns:cc1=\"http://test.com\">\n" +
                "            <cc1:AddressType>Home</cc1:AddressType>\n" +
                "            <cc1:AddressLine1>String</cc1:AddressLine1>\n" +
                "            <cc1:City>String</cc1:City>\n" +
                "            <cc1:PostalCode>String</cc1:PostalCode>\n" +
                "            <cc1:County>String</cc1:County>\n" +
                "        </cc1:Address>",
                result.getBean("soapBody"));
    }

    public void test_all() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-02.xml"));
        StreamSource source = new StreamSource(getClass().getResourceAsStream("input-message.xml"));
        JavaResult result = new JavaResult();

        smooks.filter(source, result);
        assertEquals("<soapenv:Body>\n" +
                "        <cc1:Address xmlns:cc1=\"http://test.com\">\n" +
                "            <cc1:AddressType>Home</cc1:AddressType>\n" +
                "            <cc1:AddressLine1>String</cc1:AddressLine1>\n" +
                "            <cc1:City>String</cc1:City>\n" +
                "            <cc1:PostalCode>String</cc1:PostalCode>\n" +
                "            <cc1:County>String</cc1:County>\n" +
                "        </cc1:Address>\n" +
                "    </soapenv:Body>",
                result.getBean("soapBody"));
    }
}
