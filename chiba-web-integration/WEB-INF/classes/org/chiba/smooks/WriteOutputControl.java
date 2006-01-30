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
import org.w3c.dom.Element;

public class WriteOutputControl extends DefaultSerializationUnit {
	
	public WriteOutputControl(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void writeElementStart(Element output, Writer writer, ContainerRequest request) throws IOException {
		String id = output.getAttribute("id");
		String appearance = output.getAttributeNS(Namespace.XFORMS, "appearance");
		String classAttrib = output.getAttribute("class");
		Element chibaData = DomUtils.getElement(output, "data", 1, Namespace.CHIBA);
		String dataText;
		
		if(chibaData == null) {
			return;
		}
		
		dataText = chibaData.getTextContent();
		if("image".equals(appearance)) {
			writer.write("<img id=\"" + id + "-value\"");
			if(classAttrib != null && !classAttrib.equals("")) {
				writer.write(" class=\"" + classAttrib + "\"");
			}
			writer.write(" src=\"" + dataText + "\"/>");
			// TODO: Add alt attribute
		} else if("anchor".equals(appearance)) {
			writer.write("<a id=\"" + id + "-value\"");
			if(classAttrib != null && !classAttrib.equals("")) {
				writer.write(" class=\"" + dataText + "\"");
			}
			writer.write(" href=\"" + dataText + "\">");
		} else {
			writer.write("<span id=\"" + id + "\">");
			writer.write("<span id=\"" + id + "-value\">");
			writer.write(dataText);
			writer.write("</span></span>");
		}
		
		// Now, remove the chiba data elemenet
		chibaData.getParentNode().removeChild(chibaData);
	}

	public void writeElementEnd(Element output, Writer writer, ContainerRequest request) throws IOException {
	}
}
