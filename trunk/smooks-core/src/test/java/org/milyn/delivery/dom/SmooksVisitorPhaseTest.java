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
import org.milyn.Smooks;
import org.milyn.io.StreamUtils;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.container.ExecutionContext;
import org.milyn.event.BasicExecutionEventListener;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.CharArrayWriter;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Tests to make sure the phase annotations work properly.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksVisitorPhaseTest extends TestCase {

    public void test_phase_selection() throws IOException, SAXException {
        Smooks smooks = new Smooks();
        ExecutionContext execContext;
        DOMContentDeliveryConfig config;

        smooks.addConfigurations("config1.xml", getClass().getResourceAsStream("config1.xml"));
        execContext = smooks.createExecutionContext();
        config = (DOMContentDeliveryConfig) execContext.getDeliveryConfig();

        // Check the assembly units...
        List<ContentHandlerConfigMap<DOMElementVisitor>> assemblyUnits = config.getAssemblyUnits().getMappings("a");
        assertEquals(2, assemblyUnits.size());
        assertTrue(assemblyUnits.get(0).getContentHandler() instanceof AssemblyVisitor1);
        assertTrue(assemblyUnits.get(1).getContentHandler() instanceof ConfigurableVisitor);

        List<ContentHandlerConfigMap<DOMElementVisitor>> processingUnits = config.getProcessingUnits().getMappings("a");
        assertEquals(2, processingUnits.size());
        assertTrue(processingUnits.get(0).getContentHandler() instanceof ProcessorVisitor1);
        assertTrue(processingUnits.get(1).getContentHandler() instanceof ConfigurableVisitor);
    }

    public void test_filtering() throws IOException, SAXException {
        Smooks smooks = new Smooks();
        BasicExecutionEventListener eventListener = new BasicExecutionEventListener();

        smooks.addConfigurations("config2.xml", getClass().getResourceAsStream("config2.xml"));
        // Create an exec context - no profiles....
        ExecutionContext executionContext = smooks.createExecutionContext();
        CharArrayWriter outputWriter = new CharArrayWriter();

        // Filter the input message to the outputWriter, using the execution context...
        executionContext.setEventListener(eventListener);
        smooks.filter(new StreamSource(getClass().getResourceAsStream("testxml1.xml")), new StreamResult(outputWriter), executionContext);

        System.out.println(outputWriter.toString());
        byte[] expected = StreamUtils.readStream(getClass().getResourceAsStream("testxml1-expected.xml"));
        assertTrue(StreamUtils.compareCharStreams(new ByteArrayInputStream(expected), new ByteArrayInputStream(outputWriter.toString().getBytes())));
        assertEquals(13, eventListener.getEvents().size());
    }
}
