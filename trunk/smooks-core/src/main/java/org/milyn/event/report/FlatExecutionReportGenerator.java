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
package org.milyn.event.report;

import org.milyn.Smooks;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.VisitSequence;
import org.milyn.delivery.dom.serialize.DefaultSerializationUnit;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.WriterUtil;
import org.milyn.event.types.*;
import org.milyn.event.ExecutionEvent;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Execution Report generating {@link org.milyn.event.ExecutionEventListener}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FlatExecutionReportGenerator extends AbstractExecutionReportGenerator {

    /**
     * Constructor.
     * <p/>
     * Special XML characrers are escaped.  Default applied resources ({@link org.milyn.delivery.sax.DefaultSAXElementVisitor}, {@link DefaultSerializationUnit})
     * are not output in the resource.
     *
     * @param outputWriter Report output writer.
     * @see #FlatExecutionReportGenerator(java.io.Writer, boolean, boolean)
     */
    public FlatExecutionReportGenerator(Writer outputWriter) {
        super(outputWriter);
    }

    /**
     * Constructor.
     *
     * @param outputWriter                Report output writer.
     * @param escapeXMLChars              True if special XML characters should encoded (entity encoded) in the report output e.g. rewrite '<' characters to '&lt;'.
     * @param showDefaultAppliedResources True if default applied resources ({@link org.milyn.delivery.sax.DefaultSAXElementVisitor}, {@link DefaultSerializationUnit})
     *                                    are to be output in the resource, otherwise false.
     */
    public FlatExecutionReportGenerator(Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        super(outputWriter, escapeXMLChars, showDefaultAppliedResources);
    }

    public void outputElementStart(ReportNode node) throws IOException {
        Object elementObj = node.getElement();
        StringWriter startWriter = new StringWriter();

        writeIndentTabs(node.getDepth());
        if (elementObj instanceof Element) {
            Element element = (Element) elementObj;
            domSerializer.writeElementStart(element, startWriter);
            toOutputWriter(startWriter.toString() + "\n");
        } else if (elementObj instanceof SAXElement) {
            SAXElement element = (SAXElement) elementObj;
            WriterUtil.writeStartElement(element, startWriter);
            toOutputWriter(startWriter.toString() + "\n");
        }
    }

    public void outputVisitEvents(ReportNode reportNode, VisitSequence visitSequence) throws IOException {
        List<ExecutionEvent> events = reportNode.getElementProcessingEvents();
        int numTabs = reportNode.getDepth() + 1;

        for (ExecutionEvent event : events) {
            if (event instanceof ResourceTargetingEvent) {
                ResourceTargetingEvent targetEvent = (ResourceTargetingEvent) event;
                if(targetEvent.getSequence() == null || targetEvent.getSequence() == visitSequence) {
                    SmooksResourceConfiguration config = targetEvent.getResourceConfig();
                    writeIndentTabs(numTabs);
                    toOutputWriter("Target Resource Event: [" + config + "]\n");
                }
            } else if (event instanceof ElementVisitEvent) {
                ElementVisitEvent visitEvent = (ElementVisitEvent) event;
                if(visitEvent.getSequence() == visitSequence) {
                    SmooksResourceConfiguration config = ((ElementVisitEvent) event).getResourceConfig();
                    writeIndentTabs(numTabs);
                    toOutputWriter("Visit Event (" + visitSequence + "): [" + config + "]\n");
                }
            }
        }
    }

    public void outputConfigBuilderEvents(List<ConfigBuilderEvent> events) throws IOException {
        for (ConfigBuilderEvent event : events) {
            if(event.getResourceConfig() != null) {
                toOutputWriter("Config Resource: [" + event.getResourceConfig() + "]\n");
            }
            if(event.getMessage() != null) {
                toOutputWriter("Message: [" + event.getMessage() + "]\n");
            }
            if(event.getThrown() != null) {
                toOutputWriter("Thrown: [" + event.getThrown().getMessage() + "]\n");
            }
            toOutputWriter("-----------------------------------------------------------------------------------\n");
        }
    }

    public void outputElementEnd(ReportNode node) throws IOException {
        Object elementObj = node.getElement();
        StringWriter startWriter = new StringWriter();

        writeIndentTabs(node.getDepth());
        if (elementObj instanceof Element) {
            Element element = (Element) elementObj;
            domSerializer.writeElementEnd(element, startWriter);
            toOutputWriter(startWriter.toString() + "\n");
        } else if (elementObj instanceof SAXElement) {
            SAXElement element = (SAXElement) elementObj;
            WriterUtil.writeEndElement(element, startWriter);
            toOutputWriter(startWriter.toString() + "\n");
        }
    }

    /**
     * Generate an execution report for the specified Smooks configuration from the supplied
     * message source.
     *
     * @param smooksConfigPath            Smooks resource path.  See {@link Smooks#Smooks(String)}.
     * @param source                      Smooks filter source.  See {@link Smooks#filter(javax.xml.transform.Source, javax.xml.transform.Result, org.milyn.container.ExecutionContext)}.
     * @param outputWriter                Report output writer.
     * @param escapeXMLChars              See {@link #FlatExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @param showDefaultAppliedResources See {@link # FlatExecutionReportGenerator (java.io.Writer, boolean, boolean)}.
     * @throws IOException  See {@link Smooks#Smooks(String)}.
     * @throws SAXException See {@link Smooks#Smooks(String)}.
     */
    public static void generateReport(String smooksConfigPath, Source source, Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) throws IOException, SAXException {
        Smooks smooks = new Smooks(smooksConfigPath);
        generateReport(smooks, source, outputWriter, escapeXMLChars, showDefaultAppliedResources);
    }

    /**
     * Generate an execution report using the specified Smooks instance and the supplied
     * message source.
     *
     * @param smooks                      Smooks instance.
     * @param source                      Smooks filter source.  See {@link Smooks#filter(javax.xml.transform.Source, javax.xml.transform.Result, org.milyn.container.ExecutionContext)}.
     * @param outputWriter                Report output writer.
     * @param escapeXMLChars              See {@link #FlatExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @param showDefaultAppliedResources See {@link # FlatExecutionReportGenerator (java.io.Writer, boolean, boolean)}.
     * @throws IOException  See {@link Smooks#Smooks(String)}.
     * @throws SAXException See {@link Smooks#Smooks(String)}.
     */
    public static void generateReport(Smooks smooks, Source source, Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        ExecutionContext execContext = smooks.createExecutionContext();
        FlatExecutionReportGenerator reportGenerator = new FlatExecutionReportGenerator(outputWriter, escapeXMLChars, showDefaultAppliedResources);

        reportGenerator.setFilterEvents(ConfigBuilderEvent.class, ElementVisitEvent.class);
        execContext.setEventListener(reportGenerator);
        smooks.filter(source, new StreamResult(new StringWriter()), execContext);
    }

}