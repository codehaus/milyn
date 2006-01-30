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

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class TransformInputControl extends AbstractTransUnit {

	public TransformInputControl(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void visit(Element input, ContainerRequest containerRequest) {
		Element chibaData = DomUtils.getElement(input, "data", 1, Namespace.CHIBA);
		
		if(chibaData != null) {
			String type = chibaData.getAttributeNS(Namespace.CHIBA, "type");
			
			if("date".equals(type) || "dateTime".equals(type)) {
				//createDateInput
			} else {
				createStandardInput(input, chibaData, type);
			}
		}		
	}

	private void createStandardInput(Element input, Element chibaData, String type) {
		String id = input.getAttribute("id");
		Element titleAttribContainer = DomUtils.getElement(input, "hint", 1, Namespace.XFORMS);
		String readonly = chibaData.getAttributeNS(Namespace.CHIBA, "readonly");
		String dataText = chibaData.getTextContent();
		StringBuffer transBuf = new StringBuffer();		
		
		ControlTransUtils.wrapControl(input, chibaData, id);
		
		transBuf.append("<input id=\"").append(id).append("-value\" ")
				.append("name=\"").append(Prefix.DATA).append(id).append("\" ")
				.append("type=\"text\" value=\"").append(dataText).append("\" ");
		
		if(titleAttribContainer == null) {
			titleAttribContainer = DomUtils.getElement(input, "alert", 1, Namespace.XFORMS);
		}
		if(titleAttribContainer != null) {
			String title = titleAttribContainer.getTextContent();
			
			if(title != null && !(title = title.trim()).equals("")) {
				transBuf.append("title=\"").append(title).append("\" ");
			}
		}
		
		if("true".equals(readonly)) {
			transBuf.append("disabled=\"disabled\" ");
		}
		
		writeClassAttribute(input, transBuf);

		transBuf.append("/>");
		
		// replace the xforms input with the html form input.
		Document document = input.getOwnerDocument();
		Text transNode = document.createTextNode(transBuf.toString());
		input.getParentNode().replaceChild(transNode, input);
	}

	private void writeClassAttribute(Element input, StringBuffer transBuf) {
		Element repeat = DomUtils.getParentElement(input, "repeat", Namespace.XFORMS);
		String repeatId;
		
		if(repeat != null) {
			repeatId = repeat.getAttribute("id").trim();
			if(!repeatId.equals("")) {
				int position = DomUtils.countElementsBefore(input, input.getLocalName()) + 1;
				transBuf.append("class=\"" + repeatId + "-" + position +" value\" ");
				return;
			}
		}
		String classAttrib = input.getAttribute("class").trim();
		if(!classAttrib.equals("")) {
			int position = DomUtils.countElementsBefore(input, input.getLocalName()) + 1;
			transBuf.append("class=\"" + classAttrib + " value\" ");
			return;
		}
		transBuf.append("class=\"value\" ");
	}

	public boolean visitBefore() {
		return false;
	}
}
