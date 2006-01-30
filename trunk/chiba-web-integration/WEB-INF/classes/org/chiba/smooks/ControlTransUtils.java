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

import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ControlTransUtils {

	public static void wrapControl(Element control, Element chibaData, String id) {
		
		Element xformsLabel = DomUtils.getElement(control, "label", 1, Namespace.XFORMS);
		Document doc = control.getOwnerDocument();
		Element div;
		
		div = doc.createElement("div");
		div.setAttribute("id", id);
		div.setAttribute("class", getControlClassAttrib(control, chibaData));
		if(xformsLabel != null) {
			Element label = doc.createElement("label");
			
			label.setAttribute("id", id + "-label");
			label.setAttribute("for", id + "-value");
			label.setAttribute("class", getControlClassAttrib(xformsLabel, null));
			addLabelContent(label, xformsLabel, chibaData);
			div.appendChild(label);
		}
		control.getParentNode().replaceChild(div, control);
		div.appendChild(control);
	}

	private static String getControlClassAttrib(Element control, Element chibaData) {
		StringBuffer valueBuf = new StringBuffer();
		String authorClasses = control.getAttribute("class");
		String localName = control.getLocalName();
		String appearance = control.getAttributeNS(Namespace.XHTML, "appearance").trim();

		// add author classes
		if(authorClasses.equals("")) {
			authorClasses = control.getAttributeNS(Namespace.XHTML, "class");
			if(authorClasses.equals("")) {
				authorClasses = control.getAttributeNS(Namespace.XFORMS, "class");
			}
		}
		valueBuf.append(authorClasses).append(' ');
		
		// add name classes
		valueBuf.append(localName).append(' ');
		if(appearance != null && !appearance.equals("")) {
			valueBuf.append(appearance).append('-').append(localName).append(' ');
		}

		if(chibaData != null) {
			// add chiba:data "stuff"!
			String enabled = chibaData.getAttributeNS(Namespace.CHIBA, "enabled");
			valueBuf.append((enabled.equals("true")?"enabled":"disabled")).append(' ');
			String readonly = chibaData.getAttributeNS(Namespace.CHIBA, "readonly");
			valueBuf.append((readonly.equals("true")?"readonly":"readwrite")).append(' ');
			String required = chibaData.getAttributeNS(Namespace.CHIBA, "required");
			valueBuf.append((required.equals("true")?"required":"optional")).append(' ');
			String valid = chibaData.getAttributeNS(Namespace.CHIBA, "valid");
			valueBuf.append((valid.equals("true")?"valid":"invalid"));
			// TODO: add the "limited" stuff.
		}
		
		return valueBuf.toString();
	}

	private static void addLabelContent(Element label, Element xformsLabel, Element chibaData) {
		String imgSrc = xformsLabel.getAttributeNS(Namespace.XFORMS, "src").trim();
		Document doc = label.getOwnerDocument();

		DomUtils.addLiteral(label, xformsLabel.getTextContent());
		if(!imgSrc.equals("")) {
			String id = xformsLabel.getAttribute("id");
			Element img = doc.createElement("img");
			
			label.appendChild(img);
			img.setAttribute("src", imgSrc);
			img.setAttribute("id", xformsLabel.getAttribute("id") + "-label");
		} else if(chibaData != null) {
			if("true".equals(chibaData.getAttributeNS(Namespace.CHIBA, "required"))) {
				Element span = doc.createElement("span");
				
				label.appendChild(span);
				span.setAttribute("class", "required-symbol");
				DomUtils.addLiteral(span, "*");
			}
		}
	}
}
