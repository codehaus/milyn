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
package org.milyn.event;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.util.CharUtils;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExecutionReportGeneratorTest extends TestCase {

    public void test_basic_dom() throws IOException, SAXException {
        Smooks smooks = new Smooks(); // Nothing targeted
        ExecutionContext execContext;
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("expected_dom.txt")));

        // DOM...
        execContext = smooks.createExecutionContext();
        String actual = runBasicTest(smooks, execContext);
        //assertTrue("Report output not as expected.", CharUtils.compareStrings(expected, actual));
    }

    public void test_basic_sax() throws IOException, SAXException {
        Smooks smooks = new Smooks(); // Nothing targeted
        ExecutionContext execContext;
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream("expected_sax.txt")));

        // SAX...
        execContext = smooks.createExecutionContext("null-sax");
        String actual = runBasicTest(smooks, execContext);
        //assertTrue("Report output not as expected.", CharUtils.compareStrings(expected, actual));
    }

    private String runBasicTest(Smooks smooks, ExecutionContext execContext) {
        StringWriter reportWriter = new StringWriter();
        ExecutionReportGenerator reportGenerator = new ExecutionReportGenerator(reportWriter, false, false);

        execContext.setEventListener(reportGenerator);
        smooks.filter(new StreamSource(getClass().getResourceAsStream("test-data-01.xml")), new StreamResult(new StringWriter()), execContext);
        System.out.println(reportWriter);
        return reportWriter.toString();
    }
}
