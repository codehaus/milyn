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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.delivery.VisitSequence;
import org.milyn.delivery.dom.serialize.DefaultSerializationUnit;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.WriterUtil;
import org.milyn.event.types.*;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Execution Report generating {@link org.milyn.event.ExecutionEventListener}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExecutionReportGenerator extends BasicExecutionEventListener {

    private static Log logger = LogFactory.getLog(ExecutionReportGenerator.class);

    private Writer outputWriter;
    private List<ExecutionEvent> preProcessingEvents = new ArrayList<ExecutionEvent>();
    private List<ExecutionEvent> processingEvents = new ArrayList<ExecutionEvent>();
    private Stack<ReportNode> reportNodeStack = new Stack<ReportNode>();
    private List<ReportNode> allNodes = new ArrayList<ReportNode>();
    private DefaultSerializationUnit domSerializer = new DefaultSerializationUnit();
    private boolean escapeXMLChars = true;
    private boolean showDefaultAppliedResources = false;

    /**
     * Constructor.
     * <p/>
     * Special XML characrers are escaped.  Default applied resources ({@link org.milyn.delivery.sax.DefaultSAXElementVisitor}, {@link DefaultSerializationUnit})
     * are not output in the resource.
     *
     * @param outputWriter Report output writer.
     * @see #ExecutionReportGenerator(java.io.Writer, boolean, boolean)
     */
    public ExecutionReportGenerator(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    /**
     * Constructor.
     *
     * @param outputWriter                Report output writer.
     * @param escapeXMLChars              True if special XML characters should encoded (entity encoded) in the report output e.g. rewrite '<' characters to '&lt;'.
     * @param showDefaultAppliedResources True if default applied resources ({@link org.milyn.delivery.sax.DefaultSAXElementVisitor}, {@link DefaultSerializationUnit})
     *                                    are to be output in the resource, otherwise false.
     */
    public ExecutionReportGenerator(Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        this.outputWriter = outputWriter;
        this.escapeXMLChars = escapeXMLChars;
        this.showDefaultAppliedResources = showDefaultAppliedResources;
    }

    /**
     * Process the {@link org.milyn.event.ExecutionEvent}.
     *
     * @param event The {@link org.milyn.event.ExecutionEvent}.
     */
    public void onEvent(ExecutionEvent event) {
        AssertArgument.isNotNull(event, "event");

        if(ignoreEvent(event)) {
            // Don't capture this event...
            return;
        }
        
        if (event instanceof FilterLifecycleEvent) {
            processLifecycleEvent((FilterLifecycleEvent) event);
        } else if (event instanceof ElementPresentEvent) {
            ReportNode node = new ReportNode((ElementPresentEvent) event);
            allNodes.add(node);
            processNewElementEvent(node);
        } else {
            if (!showDefaultAppliedResources) {
                if (event instanceof ResourceBasedEvent) {
                    if (((ResourceBasedEvent) event).getResourceConfig().isDefaultResource()) {
                        // Ignore this event...
                        return;
                    }
                }
            }

            if (reportNodeStack.isEmpty()) {
                // We haven't started to process the message/phase yet....
                preProcessingEvents.add(event);
            } else if (event instanceof ElementProcessingEvent) {
                // We have started processing the message/phase, so attach the event to the ReportNode
                // associated with the event element...
                ReportNode reportNode = getReportNode(((ElementProcessingEvent) event).getElement());

                if (reportNode != null) {
                    reportNode.elementProcessingEvents.add(event);
                }
            } else {
                processingEvents.add(event);
            }
        }
    }

    protected boolean ignoreEvent(ExecutionEvent event) {
        if(event instanceof FilterLifecycleEvent) {
            return false;
        } else if(event instanceof ElementPresentEvent) {
            return false;
        }

        return super.ignoreEvent(event);
    }

    private void processLifecycleEvent(FilterLifecycleEvent event) {
        try {
            if (event instanceof DOMFilterLifecycleEvent) {
                DOMFilterLifecycleEvent domEvent = (DOMFilterLifecycleEvent) event;
                if (domEvent.getDOMEventType() == DOMFilterLifecycleEvent.DOMEventType.PROCESSING_STARTED) {
                    // Assembly phase is done... output assembly report just at the start of the
                    // processing phase...
                    outputReport();
                } else if (domEvent.getDOMEventType() == DOMFilterLifecycleEvent.DOMEventType.SERIALIZATION_STARTED) {
                    // Processing phase is done (if it was)... output processing report just at the start of the
                    // serialization phase...
                    outputReport();
                }
            } else if (event.getEventType() == FilterLifecycleEvent.EventType.STARTED) {
                // Output the configuration builder events before we continue
                // with the START...
                outputConfigBuilderEvents(Filter.getCurrentExecutionContext().getDeliveryConfig().getConfigBuilderEvents());
            } else if (event.getEventType() == FilterLifecycleEvent.EventType.FINISHED) {
                // We're done now, output the last of it...
                outputReport();
            }
            outputWriter.write(event.toString() + "\n");
        } catch (IOException e) {
            throw new SmooksException("Failed to write report.", e);
        }
    }

    private void processNewElementEvent(ReportNode node) {
        if (reportNodeStack.isEmpty()) {
            reportNodeStack.push(node);
        } else {
            ReportNode head = reportNodeStack.peek();

            while (head != null && node.depth <= head.depth) {
                // element associated with the current head node on the stack is closed. Drop back
                // a level in the report model before adding the new node...
                reportNodeStack.pop();
                if (!reportNodeStack.isEmpty()) {
                    head = reportNodeStack.peek();
                } else {
                    head = null;
                }
            }

            node.parent = head;
            if (node.parent != null) {
                node.parent.children.add(node);
            }
            reportNodeStack.push(node);
        }
    }

    private void outputReport() throws IOException {
        if (!allNodes.isEmpty()) {
            outputNode(reportNodeStack.elementAt(0));
        }

        // And clear everything...
        preProcessingEvents.clear();
        processingEvents.clear();
        reportNodeStack.clear();
        allNodes.clear();
    }

    private void outputNode(ReportNode reportNode) throws IOException {
        List<ReportNode> children;

        outputElementStart(reportNode);
        outputVisitEvents(reportNode, VisitSequence.BEFORE);

        children = reportNode.children;
        for (ReportNode child : children) {
            outputNode(child);
        }

        outputElementEnd(reportNode);
        outputVisitEvents(reportNode, VisitSequence.AFTER);
    }

    private void outputElementStart(ReportNode node) throws IOException {
        Object elementObj = node.element;
        StringWriter startWriter = new StringWriter();

        writeIndentTabs(node.depth);
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

    private void outputVisitEvents(ReportNode reportNode, VisitSequence visitSequence) throws IOException {
        List<ExecutionEvent> events = reportNode.elementProcessingEvents;
        int numTabs = reportNode.depth + 1;

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

    private void outputConfigBuilderEvents(List<ConfigBuilderEvent> events) throws IOException {
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

    private void outputElementEnd(ReportNode node) throws IOException {
        Object elementObj = node.element;
        StringWriter startWriter = new StringWriter();

        writeIndentTabs(node.depth);
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

    private void toOutputWriter(String text) throws IOException {
        if (escapeXMLChars) {
            text = text.replace("<", "&lt;");
            text = text.replace(">", "&gt;");
        }
        outputWriter.write(text);
    }

    private void writeIndentTabs(int numTabs) throws IOException {
        outputWriter.write(tabsBuffer, 0, numTabs);
    }

    private ReportNode getReportNode(Object element) {
        for (ReportNode node : allNodes) {
            if (node.element == element) {
                return node;
            }
        }

        return null;
    }

    /**
     * Generate an execution report for the specified Smooks configuration from the supplied
     * message source.
     *
     * @param smooksConfigPath            Smooks resource path.  See {@link Smooks#Smooks(String)}.
     * @param source                      Smooks filter source.  See {@link Smooks#filter(javax.xml.transform.Source, javax.xml.transform.Result, org.milyn.container.ExecutionContext)}.
     * @param outputWriter                Report output writer.
     * @param escapeXMLChars              See {@link #ExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @param showDefaultAppliedResources See {@link #ExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
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
     * @param escapeXMLChars              See {@link #ExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @param showDefaultAppliedResources See {@link #ExecutionReportGenerator(java.io.Writer, boolean, boolean)}.
     * @throws IOException  See {@link Smooks#Smooks(String)}.
     * @throws SAXException See {@link Smooks#Smooks(String)}.
     */
    public static void generateReport(Smooks smooks, Source source, Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        ExecutionContext execContext = smooks.createExecutionContext();
        ExecutionReportGenerator reportGenerator = new ExecutionReportGenerator(outputWriter, escapeXMLChars, showDefaultAppliedResources);

        reportGenerator.setFilterEvents(ConfigBuilderEvent.class, ElementVisitEvent.class);
        execContext.setEventListener(reportGenerator);
        smooks.filter(source, new StreamResult(new StringWriter()), execContext);
    }

    private class ReportNode {

        private ReportNode parent;
        private List<ReportNode> children = new ArrayList<ReportNode>();
        private Object element;
        private int depth;
        private List<ExecutionEvent> elementProcessingEvents = new ArrayList<ExecutionEvent>();

        public ReportNode(ElementPresentEvent eventPresentEvent) {
            this.element = eventPresentEvent.getElement();
            this.depth = eventPresentEvent.getDepth();
        }

        public String toString() {
            return (element + " (depth " + depth + ")");
        }
    }

    private static final String tabsBuffer = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
}