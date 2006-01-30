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

package org.chiba.smooks;

import java.io.IOException;
import java.io.Writer;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.serialize.DefaultSerializationUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteGroupAsFieldset extends DefaultSerializationUnit {
	
	private static String REQUEST_KEY = WriteGroupAsFieldset.class.toString();

	public WriteGroupAsFieldset(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void writeElementStart(Element group, Writer writer, ContainerRequest request) throws IOException {
		if(isOuterGroup(group, request)) {
			// TODO: Where does the action URL come from? 
			// TODO: Write a transformation Unit that's targeted at all xforms elements such 
			// that all "outter" XForms elements are wrapped in a <form>.
			writer.write("<form name=\"chibaform\" action=\"" 
					+ request.getContextPath() 
					+ "/PlainHtml\" method=\"POST\" "
					+ "enctype=\"application/x-www-form-urlencoded\">\n");
			writer.write("<input type=\"submit\" value=\"refresh page\" class=\"refresh-button\"/>\n");
		}
		
		writer.write("<fieldset id=\""
			+ group.getAttribute("id") + "\" class=\"group "
			+ group.getAttribute("class") + " " 
			+ group.getAttributeNS(Namespace.XFORMS, "appearance")
			+ "-group\">\n");
		
		translateLabel(group);
	}

	private void translateLabel(Element group) throws IOException {
		Element label = DomUtils.getElement(group, "label", 1);
		
		if(label != null) {
			Document doc = group.getOwnerDocument();
			Element legend = doc.createElement("legend");
			
			legend.setAttribute("id", group.getAttribute("id") + "-label");
			legend.setAttribute("class", "label");
			group.replaceChild(legend, label);
			DomUtils.copyChildNodes(label, legend);
		}
	}

	public void writeElementEnd(Element group, Writer writer, ContainerRequest request) throws IOException {
		writer.write("</fieldset>\n");
		if(isOuterGroup(group, request)) {
			writer.write("</form>"); 
		}
	}

	private boolean isOuterGroup(Element group, ContainerRequest request) {
		Object outerGroup = request.getAttribute(REQUEST_KEY);
		
		if(outerGroup == null) {
			request.setAttribute(REQUEST_KEY, group);
			return true;
		} else if(outerGroup == group) {
			request.removeAttribute(REQUEST_KEY);
			return true;
		}
		
		return false;
	}
}
