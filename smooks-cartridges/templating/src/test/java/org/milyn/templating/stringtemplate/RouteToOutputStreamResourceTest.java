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
package org.milyn.templating.stringtemplate;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.delivery.StringSource;
import org.milyn.templating.MockOutStreamResource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class RouteToOutputStreamResourceTest extends TestCase {

    protected void setUp() throws Exception {
        MockOutStreamResource.outputStream = new ByteArrayOutputStream();
    }

    public void test_dom_1() throws IOException, SAXException {
        test_1(Filter.StreamFilterType.DOM);
    }

    public void test_dom_2() throws IOException, SAXException {
        test_2(Filter.StreamFilterType.DOM);
    }

    public void test_sax_1() throws IOException, SAXException {
        test_1(Filter.StreamFilterType.SAX);
    }

    public void test_sax_2() throws IOException, SAXException {
        test_2(Filter.StreamFilterType.SAX);
    }

    public void test_1(Filter.StreamFilterType filterType) throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("route-to-stream-01.cdrl"));

        Filter.setFilterType(smooks, filterType);
        smooks.filter(new StringSource("<a><c x='cx' /><d><e x='ex' /></d></a>"), null);
        assertEquals("<mybean>ex</mybean><mybean>cx</mybean>", new String(MockOutStreamResource.outputStream.toByteArray()));
    }

    public void test_2(Filter.StreamFilterType filterType) throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("route-to-stream-02.cdrl"));

        Filter.setFilterType(smooks, filterType);
        smooks.filter(new StringSource("<a><c x='cx' /><d><e x='ex' /></d></a>"), null);
        assertEquals("<mybean>cx</mybean><mybean>ex</mybean>", new String(MockOutStreamResource.outputStream.toByteArray()));
    }
}