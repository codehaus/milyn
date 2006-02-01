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
import org.w3c.dom.DOMException;
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
		String readonly = chibaData.getAttributeNS(Namespace.CHIBA, "readonly");
		String dataText = chibaData.getTextContent();
		Document document = input.getOwnerDocument();
		Element htmlInput;
		
		ControlTransUtils.wrapControl(input, chibaData, id);
		
		htmlInput = document.createElement("input");
		htmlInput.setAttribute("id", id + "-value");
		htmlInput.setAttribute("name", Prefix.DATA + id);
		htmlInput.setAttribute("type", "text");
		htmlInput.setAttribute("value", dataText);
		
		addTitleAttribute(input, htmlInput);
		if("true".equals(readonly)) {
			htmlInput.setAttribute("disabled", "disabled");
		}
		addClassAttribute(input, htmlInput);
		
		// replace the xforms input with the html input.
		input.getParentNode().replaceChild(htmlInput, input);
	}

	/**
	 * @param input
	 * @param htmlInput
	 * @throws DOMException
	 */
	private void addTitleAttribute(Element input, Element htmlInput) throws DOMException {
		Element titleAttribContainer = DomUtils.getElement(input, "hint", 1, Namespace.XFORMS);
		if(titleAttribContainer == null) {
			titleAttribContainer = DomUtils.getElement(input, "alert", 1, Namespace.XFORMS);
		}
		if(titleAttribContainer != null) {
			String title = titleAttribContainer.getTextContent();
			
			if(title != null && !(title = title.trim()).equals("")) {
				htmlInput.setAttribute("title", title);
			}
		}
	}

	private void addClassAttribute(Element input, Element htmlInput) {
		Element repeat = DomUtils.getParentElement(input, "repeat", Namespace.XFORMS);
		String repeatId;
		
		if(repeat != null) {
			repeatId = repeat.getAttribute("id").trim();
			if(!repeatId.equals("")) {
				int position = DomUtils.countElementsBefore(input, input.getLocalName()) + 1;
				htmlInput.setAttribute("class", repeatId + "-" + position + " value");
				return;
			}
		}
		String classAttrib = input.getAttribute("class").trim();
		if(!classAttrib.equals("")) {
			int position = DomUtils.countElementsBefore(input, input.getLocalName()) + 1;
			htmlInput.setAttribute("class", classAttrib + " value");
			return;
		}
		htmlInput.setAttribute("class", "value");
	}

	public boolean visitBefore() {
		return false;
	}
}
