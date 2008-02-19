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
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.VisitSequence;
import org.milyn.event.types.*;
import org.milyn.event.ExecutionEvent;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Flat Execution Report generating {@link org.milyn.event.ExecutionEventListener}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FlatReportGenerator extends AbstractExecutionReportGenerator {

    /**
     * Constructor.
     *
     * @param outputWriter See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}
     * @see AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)
     */
    public FlatReportGenerator(Writer outputWriter) {
        super(outputWriter);
    }

    /**
     * Constructor.
     *
     * @param outputWriter                See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}
     * @param escapeXMLChars              See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}
     * @param showDefaultAppliedResources See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}
     * @see AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)
     */
    public FlatReportGenerator(Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        super(outputWriter, escapeXMLChars, showDefaultAppliedResources);
    }

    public void outputStartReport() throws IOException {
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

    public void reportWrapperStart() {
    }

    public void reportWrapperEnd() {
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

    public void outputEndReport() throws IOException {
    }

    /**
     * Generate an execution report for the specified Smooks configuration from the supplied
     * message source.
     *
     * @param smooksConfigPath            Smooks resource path.  See {@link Smooks#Smooks(String)}.
     * @param source                      Smooks filter source.  See {@link Smooks#filter(javax.xml.transform.Source, javax.xml.transform.Result, org.milyn.container.ExecutionContext)}.
     * @param outputWriter                Report output writer.
     * @param escapeXMLChars              See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @param showDefaultAppliedResources See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
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
     * @param escapeXMLChars              See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @param showDefaultAppliedResources See {@link AbstractExecutionReportGenerator#AbstractExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @throws IOException  See {@link Smooks#Smooks(String)}.
     * @throws SAXException See {@link Smooks#Smooks(String)}.
     */
    public static void generateReport(Smooks smooks, Source source, Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        AssertArgument.isNotNull(smooks, "smooks");
        AssertArgument.isNotNull(source, "source");
        AssertArgument.isNotNull(outputWriter, "outputWriter");

        ExecutionContext execContext = smooks.createExecutionContext();
        FlatReportGenerator reportGenerator = new FlatReportGenerator(outputWriter, escapeXMLChars, showDefaultAppliedResources);

        reportGenerator.setFilterEvents(ConfigBuilderEvent.class, ElementVisitEvent.class);
        execContext.setEventListener(reportGenerator);
        smooks.filter(source, new StreamResult(new StringWriter()), execContext);
    }

}