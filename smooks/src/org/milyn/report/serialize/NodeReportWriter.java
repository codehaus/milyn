/*
	Milyn - Copyright (C) 2003

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

import java.io.File;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.CDRDef.Parameter;
import org.milyn.dom.DomUtils;
import org.milyn.report.PageReport.NodeReport;
import org.w3c.dom.Node;

public class NodeReportWriter extends AbstractPageWriter {

	protected NodeReportWriter(Writer writer) {
		super(writer);
		write(getClass().getResourceAsStream("node-report-header.html"));
	}

	/**
	 * Get the path for the Node Report for the specific node report (by number). 
	 * @param pagePath The associated page.  The page on which the node exists.
	 * @param nodeReportNumber The node report number.
	 * @return The reconstructed Node Report page.
	 */
	public static String getPath(String pagePath, int nodeReportNumber) {
		File file = new File(pagePath);
		StringBuffer reconstructedPath = new StringBuffer();
		String parentFile = file.getParent();
		
		if(parentFile != null) {
			reconstructedPath.append(parentFile);
			if(!parentFile.endsWith(File.separator)) {
				reconstructedPath.append(File.separator);
			}
		}
		reconstructedPath.append("node").append(nodeReportNumber);
		reconstructedPath.append("_");
		reconstructedPath.append(file.getName());
		
		return reconstructedPath.toString();
	}

	public void writeSource(URI requestURI, Node node) {
		writeln("<table>");
		writeln("<tr><td class='reportsrc'>Source Page URI:</td><td>" + requestURI + "</td><tr>");
		writeln("<tr><td class='reportsrc'>Node XPath:</td><td>" + DomUtils.getXPath(node) + "</td><tr>");
		writeln("</table>");
	}

	public void write(NodeReport nodeReport) {
		int reportItemCount = nodeReport.getReportCount();
		
		writeln("<table class='nodereport'>");
		writeln("<tr><th>Description</th><th>Suggestions</th></tr>");
		for(int i = 0; i < reportItemCount; i++) {
			CDRDef cdrDef = nodeReport.getReportConfig(i);
			String desc = cdrDef.getStringParameter("description", "<b>ERROR</b>: 'description' &lt;param&gt; not set.");
			List suggestions = cdrDef.getParameters("suggestion");
				
			if(i % 2 == 0) {
				writeln("<tr class='nodereporteven'>");
			} else {
				writeln("<tr class='nodereportodd'>");
			}
			write("<td class='nodereportdesc'>");
			write(desc);
			writeln("</td>");

			writeln("<td class='nodereportsuggest'>");
			if(suggestions == null || suggestions.size() == 0) {
				writeln("&nbsp");
			} else if(suggestions.size() > 0) {
				writeln("<ol>");
				for(int suggestIndex = 0; suggestIndex < suggestions.size(); suggestIndex++) {
					Parameter param = (Parameter)suggestions.get(suggestIndex);
					
					write("<li>");
					write(param.getValue());
					writeln("</li>");
				}
				writeln("</ol>");
			}
			writeln("</td>");
			
			writeln("</tr>");
		}
		write("</table>");
	}
}
