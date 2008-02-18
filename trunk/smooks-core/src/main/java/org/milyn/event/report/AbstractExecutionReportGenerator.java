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

import org.milyn.event.BasicExecutionEventListener;
import org.milyn.event.ExecutionEvent;
import org.milyn.event.ResourceBasedEvent;
import org.milyn.event.ElementProcessingEvent;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.FilterLifecycleEvent;
import org.milyn.event.types.DOMFilterLifecycleEvent;
import org.milyn.event.types.ConfigBuilderEvent;
import org.milyn.assertion.AssertArgument;
import org.milyn.delivery.Filter;
import org.milyn.delivery.VisitSequence;
import org.milyn.delivery.dom.serialize.DefaultSerializationUnit;
import org.milyn.SmooksException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.io.IOException;
import java.io.Writer;

/**
 * Abstract execution report generator.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractExecutionReportGenerator extends BasicExecutionEventListener {
    private static Log logger = LogFactory.getLog(FlatExecutionReportGenerator.class);
    private Writer outputWriter;
    private List<ExecutionEvent> preProcessingEvents = new ArrayList<ExecutionEvent>();
    private List<ExecutionEvent> processingEvents = new ArrayList<ExecutionEvent>();
    private Stack<ReportNode> reportNodeStack = new Stack<ReportNode>();
    private List<ReportNode> allNodes = new ArrayList<ReportNode>();
    protected static final DefaultSerializationUnit domSerializer = new DefaultSerializationUnit();
    private boolean escapeXMLChars = true;
    private boolean showDefaultAppliedResources = false;
    private static final String tabsBuffer = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

    public AbstractExecutionReportGenerator(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    public AbstractExecutionReportGenerator(Writer outputWriter, boolean escapeXMLChars, boolean showDefaultAppliedResources) {
        this.outputWriter = outputWriter;
        this.escapeXMLChars = escapeXMLChars;
        this.showDefaultAppliedResources = showDefaultAppliedResources;
    }

    public Writer getOutputWriter() {
        return outputWriter;
    }

    public boolean isEscapeXMLChars() {
        return escapeXMLChars;
    }

    public boolean isShowDefaultAppliedResources() {
        return showDefaultAppliedResources;
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

    public abstract void outputElementStart(ReportNode node) throws IOException;

    public abstract void outputVisitEvents(ReportNode reportNode, VisitSequence visitSequence) throws IOException;

    public abstract void outputConfigBuilderEvents(List<ConfigBuilderEvent> events) throws IOException;

    public abstract void outputElementEnd(ReportNode node) throws IOException;

    public void toOutputWriter(String text) throws IOException {
        if (escapeXMLChars) {
            text = text.replace("<", "&lt;");
            text = text.replace(">", "&gt;");
        }
        outputWriter.write(text);
    }

    public void writeIndentTabs(int numTabs) throws IOException {
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

    public class ReportNode {

        private FlatExecutionReportGenerator.ReportNode parent;
        private List<FlatExecutionReportGenerator.ReportNode> children = new ArrayList<FlatExecutionReportGenerator.ReportNode>();
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

        public ReportNode getParent() {
            return parent;
        }

        public List<ReportNode> getChildren() {
            return children;
        }

        public Object getElement() {
            return element;
        }

        public int getDepth() {
            return depth;
        }

        public List<ExecutionEvent> getElementProcessingEvents() {
            return elementProcessingEvents;
        }
    }
}
