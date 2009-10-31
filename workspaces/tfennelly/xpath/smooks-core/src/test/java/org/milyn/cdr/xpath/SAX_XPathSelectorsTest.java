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
package org.milyn.cdr.xpath;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.FilterSettings;
import org.milyn.cdr.SmooksConfigurationException;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class SAX_XPathSelectorsTest extends TestCase {

    protected void setUp() throws Exception {
        XPathVisitor.saxVisitedBeforeElement = null;
        XPathVisitor.saxVisitedAfterElement = null;
        XPathAfterVisitor.saxVisitedAfterElement = null;
    }

    public void test_01() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-01.xml"));

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("8655", XPathVisitor.saxVisitedBeforeElement.getAttribute("code"));
        assertEquals("8655", XPathVisitor.saxVisitedAfterElement.getAttribute("code"));
    }

    public void test_02() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-02.xml"));

        try {
            smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
            smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Unsupported selector 'item[@code = '8655']/units[text() = 1]' on resource 'Target Profile: [[org.milyn.profile.profile#default_profile]], Selector: [item[@code = '8655']/units[text() = 1]], Selector Namespace URI: [null], Resource: [org.milyn.cdr.xpath.XPathVisitor], Num Params: [0]'.  The 'text()' XPath token is only supported on SAX Visitor implementations that implement the org.milyn.delivery.sax.SAXVisitAfter interface only.  Class 'org.milyn.cdr.xpath.XPathVisitor' implements other SAX Visitor interfaces.", e.getMessage());
        }
    }

    public void test_03() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-03.xml"));

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("1", XPathAfterVisitor.saxVisitedAfterElement.getTextContent());
    }

    public void test_04() throws IOException, SAXException {
        Smooks smooks = new Smooks();

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.addVisitor(new XPathVisitor(), "item[@code = 8655]");
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("8655", XPathVisitor.saxVisitedBeforeElement.getAttribute("code"));
        assertEquals("8655", XPathVisitor.saxVisitedAfterElement.getAttribute("code"));
    }

    public void test_05() throws IOException, SAXException {
        Smooks smooks = new Smooks();

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.addVisitor(new XPathVisitor(), "item[@code = '8655']/units[text() = 1]");
        try {
            smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Unsupported selector 'item[@code = '8655']/units[text() = 1]' on resource 'Target Profile: [[org.milyn.profile.profile#default_profile]], Selector: [item[@code = '8655']/units[text() = 1]], Selector Namespace URI: [null], Resource: [org.milyn.cdr.xpath.XPathVisitor], Num Params: [0]'.  The 'text()' XPath token is only supported on SAX Visitor implementations that implement the org.milyn.delivery.sax.SAXVisitAfter interface only.  Class 'org.milyn.cdr.xpath.XPathVisitor' implements other SAX Visitor interfaces.", e.getMessage());
        }
    }

    public void test_06() throws IOException, SAXException {
        Smooks smooks = new Smooks();

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.addVisitor(new XPathAfterVisitor(), "item[@code = '8655']/units[text() = 1]");
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("1", XPathAfterVisitor.saxVisitedAfterElement.getTextContent());
    }

    public void test_07() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-04.xml"));

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("1", XPathAfterVisitor.saxVisitedAfterElement.getTextContent());
    }

    public void test_08() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-05.xml"));

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals(null, XPathAfterVisitor.saxVisitedAfterElement);
    }

    public void test_09() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-06.xml"));

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("1", XPathAfterVisitor.saxVisitedAfterElement.getTextContent());
    }

    public void test_10() throws IOException, SAXException {
        Smooks smooks = new Smooks();
        Properties namespaces = new Properties();

        namespaces.put("a", "http://a");
        namespaces.put("b", "http://b");
        namespaces.put("c", "http://c");
        namespaces.put("d", "http://d");

        smooks.setFilterSettings(FilterSettings.DEFAULT_SAX);
        smooks.setNamespaces(namespaces);

        smooks.addVisitor(new XPathAfterVisitor(), "/a:ord[@num = 3122 and @state = 'finished']/a:items/c:item[@c:code = '8655']/d:units[text() = 1]");
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order.xml")));
        assertEquals("1", XPathAfterVisitor.saxVisitedAfterElement.getTextContent());
    }

    public void test
}
