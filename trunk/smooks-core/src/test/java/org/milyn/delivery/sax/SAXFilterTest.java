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
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXFilterTest extends TestCase {

    public void test_reader_writer() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        StringWriter writer = new StringWriter();

        smooks.filter(new StreamSource(new StringReader(input)), new StreamResult(writer), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(writer.toString())).toString());
    }

    public void test() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtil.parseStream(getClass().getResourceAsStream("test-02.xml"));
        Element root = document.getDocumentElement();
        Attr a = root.getAttributeNodeNS("http://y", "a");
        Attr b = root.getAttributeNode("b");

        String name = a.getName();
        String localName = a.getLocalName();
        String namespace = a.getNamespaceURI();
        String value = a.getValue();

        System.out.println(document);
    }

    public void test_reader_stream() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        smooks.filter(new StreamSource(new StringReader(input)), new StreamResult(outStream), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(outStream.toString())).toString());
    }

    public void test_stream_stream() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(outStream), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(outStream.toString())).toString());
    }

    public void test_stream_writer() throws SAXException, IOException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));
        StringWriter writer = new StringWriter();

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(writer), execContext);
        assertEquals(StreamUtils.trimLines(new StringReader(input)).toString(), StreamUtils.trimLines(new StringReader(writer.toString())).toString());
    }

    protected void setUp() throws Exception {
        SAXVisitor01.element = null;
        SAXVisitor01.children.clear();
        SAXVisitor01.childText.clear();
        SAXVisitor02.element = null;
        SAXVisitor02.children.clear();
        SAXVisitor02.childText.clear();
        SAXVisitor03.element = null;
        SAXVisitor03.children.clear();
        SAXVisitor03.childText.clear();

        SAXVisitBeforeVisitor.visited = false;
        SAXVisitBeforeAndChildrenVisitor.reset();
        SAXVisitAfterVisitor.visited = false;
        SAXVisitAfterAndChildrenVisitor.reset();
    }

    public void test_selection() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-02.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), null, execContext);
        assertEquals("h", SAXVisitor01.element.getName().getLocalPart());
        assertEquals("y", SAXVisitor01.element.getName().getPrefix());
        assertEquals("http://y", SAXVisitor01.element.getName().getNamespaceURI());
        assertEquals(0, SAXVisitor01.children.size());
        assertEquals(0, SAXVisitor01.childText.size());

        assertEquals("i", SAXVisitor02.element.getName().getLocalPart());
        assertEquals("", SAXVisitor02.element.getName().getPrefix());
        assertEquals("http://x", SAXVisitor02.element.getName().getNamespaceURI());
        assertEquals(0, SAXVisitor02.children.size());
        assertEquals(1, SAXVisitor02.childText.size());

        assertEquals("h", SAXVisitor03.element.getName().getLocalPart());
        assertEquals("", SAXVisitor03.element.getName().getPrefix());
        assertEquals("http://x", SAXVisitor03.element.getName().getNamespaceURI());
        assertEquals(1, SAXVisitor03.children.size());
        assertEquals(7, SAXVisitor03.childText.size());
    }

    public void test_contextual() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-03.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), null, execContext);
        assertEquals("h", SAXVisitor01.element.getName().getLocalPart());
        assertEquals("y", SAXVisitor01.element.getName().getPrefix());
        assertEquals("http://y", SAXVisitor01.element.getName().getNamespaceURI());
        assertEquals(0, SAXVisitor01.children.size());
        assertEquals(0, SAXVisitor01.childText.size());

        assertNull(SAXVisitor02.element);

        assertEquals("i", SAXVisitor03.element.getName().getLocalPart());
        assertEquals("", SAXVisitor03.element.getName().getPrefix());
        assertEquals("http://x", SAXVisitor03.element.getName().getNamespaceURI());
        assertEquals(0, SAXVisitor03.children.size());
        assertEquals(1, SAXVisitor03.childText.size());
    }

    public void test_$document() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-04.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        String input = new String(StreamUtils.readStream(getClass().getResourceAsStream("test-01.xml")));

        smooks.filter(new StreamSource(new ByteArrayInputStream(input.getBytes())), null, execContext);
        assertEquals("xx", SAXVisitor01.element.getName().getLocalPart());
        assertEquals("xx", SAXVisitor02.element.getName().getLocalPart());
        assertNull(SAXVisitor03.element);
    }

    public void test_visitBeforeOnly() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-05.xml"));

        smooks.filter(new StreamSource(new StringReader("<a/>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitBeforeVisitor.visited);
        assertEquals("Hi There!", SAXVisitBeforeVisitor.staticInjectedParam);
    }

    public void test_visitBeforeAndChildren() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-05.xml"));

        smooks.filter(new StreamSource(new StringReader("<b/>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitBeforeAndChildrenVisitor.visited);
        assertFalse(SAXVisitBeforeAndChildrenVisitor.onChildText);
        assertFalse(SAXVisitBeforeAndChildrenVisitor.onChildElement);
        SAXVisitBeforeAndChildrenVisitor.reset();

        smooks.filter(new StreamSource(new StringReader("<b>text</b>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitBeforeAndChildrenVisitor.visited);
        assertTrue(SAXVisitBeforeAndChildrenVisitor.onChildText);
        assertFalse(SAXVisitBeforeAndChildrenVisitor.onChildElement);
        SAXVisitBeforeAndChildrenVisitor.reset();

        smooks.filter(new StreamSource(new StringReader("<b><x/></b>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitBeforeAndChildrenVisitor.visited);
        assertFalse(SAXVisitBeforeAndChildrenVisitor.onChildText);
        assertTrue(SAXVisitBeforeAndChildrenVisitor.onChildElement);
        SAXVisitBeforeAndChildrenVisitor.reset();

        smooks.filter(new StreamSource(new StringReader("<b>text<x/></b>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitBeforeAndChildrenVisitor.visited);
        assertTrue(SAXVisitBeforeAndChildrenVisitor.onChildText);
        assertTrue(SAXVisitBeforeAndChildrenVisitor.onChildElement);
        SAXVisitBeforeAndChildrenVisitor.reset();
    }

    public void test_visitAfterOnly() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-05.xml"));

        smooks.filter(new StreamSource(new StringReader("<c/>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitAfterVisitor.visited);
    }

    public void test_visitAfterAndChildren() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-05.xml"));

        smooks.filter(new StreamSource(new StringReader("<d/>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitAfterAndChildrenVisitor.visited);
        assertFalse(SAXVisitAfterAndChildrenVisitor.onChildText);
        assertFalse(SAXVisitAfterAndChildrenVisitor.onChildElement);
        SAXVisitAfterAndChildrenVisitor.reset();

        smooks.filter(new StreamSource(new StringReader("<d>text</d>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitAfterAndChildrenVisitor.visited);
        assertTrue(SAXVisitAfterAndChildrenVisitor.onChildText);
        assertFalse(SAXVisitAfterAndChildrenVisitor.onChildElement);
        SAXVisitAfterAndChildrenVisitor.reset();

        smooks.filter(new StreamSource(new StringReader("<d><x/></d>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitAfterAndChildrenVisitor.visited);
        assertFalse(SAXVisitAfterAndChildrenVisitor.onChildText);
        assertTrue(SAXVisitAfterAndChildrenVisitor.onChildElement);
        SAXVisitAfterAndChildrenVisitor.reset();

        smooks.filter(new StreamSource(new StringReader("<d>text<x/></d>")), null, smooks.createExecutionContext());
        assertTrue(SAXVisitAfterAndChildrenVisitor.visited);
        assertTrue(SAXVisitAfterAndChildrenVisitor.onChildText);
        assertTrue(SAXVisitAfterAndChildrenVisitor.onChildElement);
        SAXVisitAfterAndChildrenVisitor.reset();
    }

    public void test_report() throws IOException, SAXException {
        System.out.println("********* FIX TEST!!");
        /*
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-04.xml"));
        ExecutionContext executionContext = smooks.createExecutionContext();
        StringWriter reportWriter = new StringWriter();

        executionContext.setEventListener(new FlatReportGenerator(reportWriter));
        smooks.filter(new StreamSource(new StringReader("<c/>")), null, executionContext);

        assertTrue(StreamUtils.compareCharStreams(
                getClass().getResourceAsStream("report-expected.txt"),
                new ByteArrayInputStream(reportWriter.toString().getBytes())));
        */
    }
}
