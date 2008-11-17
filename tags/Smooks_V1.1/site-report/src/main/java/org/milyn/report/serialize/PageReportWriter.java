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

package org.milyn.report.serialize;

import java.io.IOException;
import java.io.Writer;

import org.milyn.logging.SmooksLogger;
import org.milyn.report.PageReport;

public class PageReportWriter extends AbstractPageWriter {

	public PageReportWriter(Writer writer) {
		super(writer);
		write(getClass().getResourceAsStream("page-report-header.html"));
		write(getClass().getResourceAsStream("navlinks.html"));
	}

	public void writeSummary(PageReport report, String pagePath) {
		int nodeReportCount = report.getNodeReportEntries().size();
		
		writeln("<div class='reportsummary indented'>");
		write("	<b>Num Report Items</b>: ");
		if(nodeReportCount > 0) {
			String nextId = NodeReportWriter.getPath(pagePath, 1);
			write(nodeReportCount + " ");
			PageReportWriter.writeNextReportItemHref(getWriter(), nextId);
		} else {
			write("0");
		}
		write("</div>");
	}
	
	/**
	 * Writer the &lt;a href /&gt; for the next link from one report
	 * entry location to the next.
	 * @param writer Writer to write to.
	 * @param nextId Next id attribute value.
	 */
	public static void writeNextReportItemHref(Writer writer, String nextId) {
		try {
			writer.write("<a class='nextnritemlink' href='#" + nextId + "'><img src='../down.gif' title='Goto next report item...' border='0' /></a>");
		} catch (IOException e) {
			SmooksLogger.getLog().error("Failed to write 'next' report item href.", e);
		}
	}
}
