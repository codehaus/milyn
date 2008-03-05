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
import org.milyn.event.ExecutionEvent;
import org.milyn.event.types.ConfigBuilderEvent;
import org.milyn.event.types.ElementVisitEvent;
import org.milyn.event.types.ResourceTargetingEvent;
import org.milyn.io.StreamUtils;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * HTML Execution Report generating {@link org.milyn.event.ExecutionEventListener}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class HtmlReportGenerator extends AbstractReportGenerator {

    private File subfilesDir;
    private int subfileCounter = 0;
    private int visitEventIndex = 1;
    private File styleFile;
    private File jscriptFile;

    public HtmlReportGenerator(Writer outputWriter) {
        this(new ReportConfiguration(outputWriter));
    }

    protected HtmlReportGenerator(ReportConfiguration reportConfiguration) {
        super(reportConfiguration);
        createSubfilesDir();
        styleFile = new File(subfilesDir, "style.css");
        jscriptFile = new File(subfilesDir, "jscript.js");
    }

    public void outputStartReport() throws IOException {
        // Output out the CSS and Javascript files
        StreamUtils.writeFile(styleFile, StreamUtils.readStream(getClass().getResourceAsStream("html/style.css")));
        StreamUtils.writeFile(jscriptFile, StreamUtils.readStream(getClass().getResourceAsStream("html/jscript.js")));

        // Output the report header...
        toOutputWriter(getTemplate("report-header.html"));
    }

    public void outputConfigBuilderEvents(List<ConfigBuilderEvent> events) throws IOException {
        File configEventsFile = getNextOutputFile();
        Writer configEventsWriter = new FileWriter(configEventsFile);

        try {
            // Output the link to the
            toOutputWriter("<b><u><a href='" + configEventsFile.toURL() + "' onclick=\"return popup(this)\">Configuration Builder Events</a></u></b>");
            configEventsWriter.write(getTemplate("eventfile-header.html"));
            configEventsWriter.write("Before filtering a message Source, Smooks needs to assemble the set of \"resources\" that " +
                    "apply to that message.  The following is a list of events generated while assembling these resources.<p/>");
            for (ConfigBuilderEvent event : events) {
                configEventsWriter.write("<div><table class='event'>\n");

                if (event.getResourceConfig() != null) {
                    configEventsWriter.write("<tr>\n");
                    configEventsWriter.write("<td class=\"field\">Resource</td>\n");
                    configEventsWriter.write("<td class=\"value\"><a href=\"\" onmouseover=\"showresource('" + escapeXML(escapeXML(event.getResourceConfig().toXML())) + "');\" onmouseout=\"hidetrail();\">" + event.getResourceConfig().getResource() + "</a></td>\n");
                    configEventsWriter.write("</tr>\n");
                }
                if (event.getMessage() != null) {
                    configEventsWriter.write("<tr>\n");
                    configEventsWriter.write("<td class=\"field\">Message</td>\n");
                    String message = event.getMessage();
                    if(message.length() > 200) {
                        configEventsWriter.write("<td class=\"value\"><a href=\"\" onmouseover=\"showresource('" + escapeXML(escapeXML(message)) + "');\" onmouseout=\"hidetrail();\">View Message</a></td>\n");
                    } else {
                        configEventsWriter.write("<td class=\"value\">" + escapeXML(message) + "</td>\n");
                    }
                    configEventsWriter.write("</tr>\n");
                }
                if (event.getThrown() != null) {
                    StringWriter stackTraceWriter = new StringWriter();
                    event.getThrown().printStackTrace(new PrintWriter(stackTraceWriter));

                    configEventsWriter.write("<tr>\n");
                    configEventsWriter.write("<td class=\"field\">Error</td>\n");
                    configEventsWriter.write("<td class=\"value\"><a href=\"\" onmouseover=\"showresource('" + escapeXML(escapeXML(stackTraceWriter.toString())) + "');\" onmouseout=\"hidetrail();\">View</a></td>\n");
                    configEventsWriter.write("</tr>\n");
                }

                configEventsWriter.write("</table></div>\n");
            }
            configEventsWriter.write(getTemplate("eventfile-footer.html"));
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                configEventsWriter.flush();
            } finally {
                configEventsWriter.close();
            }
        }
    }

    private Writer baseWriter;
    public void reportWrapperStart() throws IOException {
        toOutputWriter(getTemplate("report-wrapper-start.html"));
        baseWriter = getReportConfiguration().getOutputWriter();
        getReportConfiguration().setOutputWriter(new StringWriter());
    }

    public void reportWrapperEnd() throws IOException {
        StringWriter reportBodyWriter = (StringWriter) getOutputWriter();
        String reportBody = reportBodyWriter.toString();

        reportBody = reportBody.replace("\n", "<br/>");
        baseWriter.write(reportBody);

        getReportConfiguration().setOutputWriter(baseWriter);
        toOutputWriter(getTemplate("report-wrapper-end.html"));
    }

    public void outputVisitEvents(ReportNode reportNode, VisitSequence visitSequence) throws IOException {
        File nodeOutputFile = getNextOutputFile();
        Writer nodeOutputWriter = new FileWriter(nodeOutputFile);

        try {
            List<ExecutionEvent> events = reportNode.getElementProcessingEvents();
            int numTabs = reportNode.getDepth() + 1;

            if(!events.isEmpty()) {
                // Output the link to the
                toOutputWriter("<a href='" + nodeOutputFile.toURL() + "' onclick=\"return popup(this)\">" + visitEventIndex + "</a>");
                visitEventIndex++;
                nodeOutputWriter.write(getTemplate("eventfile-header.html"));
                nodeOutputWriter.write("Visit '" + visitSequence + "' events on element '" + reportNode.getElement() + "':<p/>");
                for (ExecutionEvent event : events) {
                    nodeOutputWriter.write("<div><table class='event'>\n");

                    if (event instanceof ResourceTargetingEvent) {
                        ResourceTargetingEvent targetEvent = (ResourceTargetingEvent) event;
                        if (targetEvent.getSequence() == null || targetEvent.getSequence() == visitSequence) {
                            SmooksResourceConfiguration config = targetEvent.getResourceConfig();

                            nodeOutputWriter.write("<tr>\n");
                            nodeOutputWriter.write("<td class=\"field\">Targeted Resource Event</td>\n");
                            nodeOutputWriter.write("<td class=\"value\"><a href=\"\" onmouseover=\"showresource('" + escapeXML(escapeXML(config.toXML())) + "');\" onmouseout=\"hidetrail();\">" + config.getResource() + "</a></td>\n");
                            nodeOutputWriter.write("</tr>\n");
                        }
                    } else if (event instanceof ElementVisitEvent) {
                        ElementVisitEvent visitEvent = (ElementVisitEvent) event;
                        if (visitEvent.getSequence() == visitSequence) {
                            SmooksResourceConfiguration config = ((ElementVisitEvent) event).getResourceConfig();

                            nodeOutputWriter.write("<tr>\n");
                            nodeOutputWriter.write("<td class=\"field\">Resource</td>\n");
                            nodeOutputWriter.write("<td class=\"value\"><a href=\"\" onmouseover=\"showresource('" + escapeXML(escapeXML(config.toXML())) + "');\" onmouseout=\"hidetrail();\">" + config.getResource() + "</a></td>\n");
                            nodeOutputWriter.write("</tr>\n");

                            nodeOutputWriter.write("<tr>\n");
                            nodeOutputWriter.write("<td class=\"field\">ExecutionContext State (Before Visit)</td>\n");
                            nodeOutputWriter.write("<td class=\"value\">" + escapeXML(visitEvent.getExecutionContextState()) + "</td>\n");
                            nodeOutputWriter.write("</tr>\n");
                        }
                    }

                    nodeOutputWriter.write("</table></div>\n");
                }
                nodeOutputWriter.write(getTemplate("eventfile-footer.html"));
            }
        } finally {
            try {
                nodeOutputWriter.flush();
            } finally {
                nodeOutputWriter.close();
            }
        }
    }

    public void outputEndReport() throws IOException {
        // Output the report footer...
        toOutputWriter(getTemplate("report-footer.html"));
    }

    private File getNextOutputFile() {
        subfileCounter++;
        return new File(subfilesDir, Integer.toString(subfileCounter) + ".html");
    }

    private void createSubfilesDir() {
        subfilesDir = new File(System.getProperty("java.io.tmpdir") + "/" + System.currentTimeMillis());
        subfilesDir.mkdirs();
    }

    private String getTemplate(String name) throws IOException {
        String template = StreamUtils.readStream(new InputStreamReader(getClass().getResourceAsStream("html/" + name)));
        template = template.replace("$$CSS$$", styleFile.toURL().toString());
        return template.replace("$$JSCRIPT$$", jscriptFile.toURL().toString());
    }

}