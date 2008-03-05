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
package org.milyn.delivery.dom;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

import org.xml.sax.SAXException;
import org.milyn.Smooks;
import org.milyn.io.StreamUtils;
import org.milyn.event.report.FlatReportGenerator;
import org.milyn.container.ExecutionContext;

import javax.xml.transform.stream.StreamSource;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DOMReportTest extends TestCase {

    public void test_report() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config2.xml"));
        ExecutionContext executionContext = smooks.createExecutionContext();
        StringWriter reportWriter = new StringWriter();

        executionContext.setEventListener(new FlatReportGenerator(reportWriter));
        smooks.filter(new StreamSource(new StringReader("<a><b/></a>")), null, executionContext);

        //System.out.println(reportWriter);
        assertTrue(StreamUtils.compareCharStreams(
                getClass().getResourceAsStream("report-expected.txt"),
                new ByteArrayInputStream(reportWriter.toString().getBytes())));
    }
}
