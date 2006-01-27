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
import org.w3c.dom.Element;

public class WriteLabelAsLegend extends DefaultSerializationUnit {
	
	public WriteLabelAsLegend(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void writeElementStart(Element label, Writer writer, ContainerRequest request) throws IOException {
		Element parent = (Element)label.getParentNode();
		
		writer.write("<legend id=\"" + parent.getAttribute("id") + "\" class=\"label\">");
	}

	public void writeElementEnd(Element label, Writer writer, ContainerRequest request) throws IOException {
		writer.write("</legend>\n");
	}
	
}
