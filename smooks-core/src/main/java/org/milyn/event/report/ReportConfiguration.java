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

import org.milyn.event.ExecutionEvent;
import org.milyn.event.types.ConfigBuilderEvent;
import org.milyn.event.types.ElementVisitEvent;

import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Report generation configuration.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ReportConfiguration {

    private ReportType type;
    private Writer outputWriter;
    private boolean escapeXMLChars = false;
    private boolean showDefaultAppliedResources = false;
    private Class<? extends ExecutionEvent>[] filterEvents;

    public ReportConfiguration(ReportType type, Writer outputWriter) {
        this.type = type;
        this.outputWriter = outputWriter;
        setFilterEvents(ConfigBuilderEvent.class, ElementVisitEvent.class);
    }

    public ReportType getType() {
        return type;
    }

    public Writer getOutputWriter() {
        return outputWriter;
    }

    public boolean escapeXMLChars() {
        return escapeXMLChars;
    }

    public void setEscapeXMLChars(boolean escapeXMLChars) {
        this.escapeXMLChars = escapeXMLChars;
    }

    public boolean showDefaultAppliedResources() {
        return showDefaultAppliedResources;
    }

    public void setShowDefaultAppliedResources(boolean showDefaultAppliedResources) {
        this.showDefaultAppliedResources = showDefaultAppliedResources;
    }


    /**
     * Set a list of {@link org.milyn.event.ExecutionEvent event} types on which to filter.
     * <p/>
     * The listener will only capture {@link org.milyn.event.ExecutionEvent event} types
     * provided in this list.  If not set, all events will be captured.
     *
     * @param filterEvents Filter events.
     */
    public void setFilterEvents(Class<? extends ExecutionEvent>... filterEvents) {
        this.filterEvents = filterEvents;
    }

    public Class<? extends ExecutionEvent>[] getFilterEvents() {
        return filterEvents;
    }
}
