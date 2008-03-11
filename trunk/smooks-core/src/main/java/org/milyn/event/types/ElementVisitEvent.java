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
package org.milyn.event.types;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.delivery.Filter;
import org.milyn.delivery.VisitSequence;
import org.milyn.event.ElementProcessingEvent;
import org.milyn.event.ResourceBasedEvent;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;
import org.milyn.util.MVELTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Element Visit Event.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ElementVisitEvent extends ElementProcessingEvent implements ResourceBasedEvent {
    
    private ContentHandlerConfigMap configMapping;
    private VisitSequence sequence;
    private String executionContextState;
    private Throwable error;
    private String reportText;

    public ElementVisitEvent(Object element, ContentHandlerConfigMap configMapping, VisitSequence sequence) {
        super(element);
        this.configMapping = configMapping;
        this.sequence = sequence;
        ExecutionContext executionContext = Filter.getCurrentExecutionContext();
        executionContextState = executionContext.toString();
        initReportText(executionContext);        
    }

    public ElementVisitEvent(Object element, ContentHandlerConfigMap configMapping, VisitSequence sequence, Throwable error) {
        this(element, configMapping, sequence);
        this.error = error;
    }

    public SmooksResourceConfiguration getResourceConfig() {
        return configMapping.getResourceConfig();
    }

    public ContentHandlerConfigMap getConfigMapping() {
        return configMapping;
    }

    public VisitSequence getSequence() {
        return sequence;
    }

    public String getExecutionContextState() {
        return executionContextState;
    }

    public Throwable getError() {
        return error;
    }

    public String getReportText() {
        return reportText;
    }

    private void initReportText(ExecutionContext executionContext) {
        ContentHandler handler = configMapping.getContentHandler();
        if(getSequence() == VisitSequence.BEFORE) {
            VisitBeforeReport reportAnnotation = handler.getClass().getAnnotation(VisitBeforeReport.class);
            if(reportAnnotation != null) {
                applyReportTemplate(reportAnnotation.template(), handler.getClass(), executionContext);
            }
        } else {
            VisitAfterReport reportAnnotation = handler.getClass().getAnnotation(VisitAfterReport.class);
            if(reportAnnotation != null) {
                applyReportTemplate(reportAnnotation.template(), handler.getClass(), executionContext);
            }
        }

        if(reportText == null) {
            // No template ...
            reportText = executionContextState;
        }
    }

    private void applyReportTemplate(String template, Class handlerClass, ExecutionContext executionContext) {
        if(template == AnnotationConstants.NULL_STRING) {
            // No template ...
            return;
        }

        InputStream templateResourceStream = ClassUtil.getResourceAsStream(template, handlerClass);
        if(templateResourceStream != null) {
            try {
                try {
                    template = StreamUtils.readStream(new InputStreamReader(templateResourceStream, "UTF-8"));
                } finally {
                    templateResourceStream.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unexpected exception reading classpath resource '" + template + "'.", e);
            }
        }

        MVELTemplate mvelTemplate = new MVELTemplate(template);
        Map templateParams = new HashMap();

        templateParams.put("resource", configMapping.getResourceConfig());
        templateParams.put("execContext", executionContext);
        templateParams.put("event", this);
         reportText = mvelTemplate.apply(templateParams);
    }
}