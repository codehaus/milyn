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

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.VisitSequence;
import org.milyn.event.types.*;
import org.milyn.event.ExecutionEvent;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Flat Execution Report generating {@link org.milyn.event.ExecutionEventListener}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FlatReportGenerator extends AbstractReportGenerator {

    public FlatReportGenerator(Writer outputWriter) {
        this(new ReportConfiguration(outputWriter));
    }

    public FlatReportGenerator(ReportConfiguration reportConfiguration) {
        super(reportConfiguration);
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
}