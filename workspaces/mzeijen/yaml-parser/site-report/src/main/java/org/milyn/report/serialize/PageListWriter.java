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

import java.io.Writer;
import java.net.URI;

public class PageListWriter extends AbstractPageWriter {

	public PageListWriter(Writer writer) {
		super(writer);
		write(getClass().getResourceAsStream("page-list-header.html"));
		write(getClass().getResourceAsStream("navlinks.html"));
	}

	public String addPage(URI requestPageURI, URI baseURI, int reportItemCount) {
		String path = requestPageURI.toString().substring(baseURI.toString().length());
		
		// Make sure the output file is a .html file - source may be JSP/ASP etc.
		if(!path.endsWith(".html") || !path.endsWith(".html")) {
			path += ".html";
		}
		path = path.replace('/', '_');
		path = path.replace('\\', '_');
		
		write("<div class='pagelink" + (reportItemCount != 0?" hasreportitems":"") + "'>");
		write("<a href='" + path + "'>");
		write(requestPageURI.toString());
		write("</a> (" + reportItemCount +")");
		write("</div>\r\n");

		return path;
	}
}
