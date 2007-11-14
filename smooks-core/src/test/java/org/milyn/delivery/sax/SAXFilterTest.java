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
package org.milyn.delivery.sax;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.io.StreamUtils;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXFilterTest extends TestCase {

    public void test_reader_writer() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        StringWriter writer = new StringWriter();

        smooks.filter(new StreamSource(new StringReader(input)), new StreamResult(writer), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(writer.toString())).toString());
    }

    public void test_reader_stream() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        smooks.filter(new StreamSource(new StringReader(input)), new StreamResult(outStream), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(outStream.toString())).toString());
    }

    public void test_stream_stream() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(outStream), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(outStream.toString())).toString());
    }

    public void test_stream_writer() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        StandaloneExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        StringWriter writer = new StringWriter();

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(writer), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(writer.toString())).toString());
    }
}
