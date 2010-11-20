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

import java.io.File;
import java.io.Writer;
import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.Parameter;
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

	public void writeSource(String relURI, Node node) {
		writeln("<div id='nrsummary'>");
		writeln("<p class='nrsourcetitle'>Source Page URI:</p><p id='nrsourcedata'> <i>&lt;base-uri&gt;</i>/" + relURI + "</p>");
		writeln("<p class='nrsourcetitle'>Node XPath:</p><p id='nrsourcedata'>" + DomUtils.getXPath(node) + "</p>");
		writeln("</div>");
	}

	public void write(NodeReport nodeReport) {
		int reportItemCount = nodeReport.getReportCount();
		
		writeln("<div id='nrdetail'>");
		writeln("<div id='nrheader'>");
		writeln("<span class='nridheader'>ID</span><span class='nrdescheader'>Description</span><span class='nrsuggestheader'>Suggestions</span>");
		writeln("</div>");

		for(int i = 0; i < reportItemCount; i++) {
			CDRDef cdrDef = nodeReport.getReportConfig(i);
			String id = cdrDef.getStringParameter("id", "<b>ERROR</b>: 'id' &lt;param&gt; not set.");
			String desc = cdrDef.getStringParameter("description", "<b>ERROR</b>: 'description' &lt;param&gt; not set.");
			List suggestions = cdrDef.getParameters("suggestion");
				
			if(i % 2 == 0) {
				writeln("<div class='nrrow nrrowodd'>");
			} else {
				writeln("<div class='nrrow nrroweven'>");
			}

			writeln("<span class='nrid'>");
			write(id);
			writeln("</span>");
			
			writeln("<span class='nrdesc'>");
			write(desc);
			writeln("</span>");

			writeln("<span class='nrsuggest'>");
			if(suggestions == null || suggestions.size() == 0) {
				writeln("Sorry, no suggestions available on this!");
			} else if(suggestions.size() > 0) {
				writeln("<ol class='suggestionlist'>");
				for(int suggestIndex = 0; suggestIndex < suggestions.size(); suggestIndex++) {
					Parameter param = (Parameter)suggestions.get(suggestIndex);
					
					write("<li>");
					write(param.getValue());
					writeln("</li>");
				}
				writeln("</ol>");
			}
			writeln("</span>");
			writeln("</div>");
		}
		write("</div>");
	}
}
