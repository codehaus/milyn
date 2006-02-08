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

package org.chiba.smooks.transforms;

import java.util.List;

import org.chiba.smooks.Namespace;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Transunit for the xforms:switch element.
 * @author tfennelly
 */
public class TransformSwitchControl extends AbstractTransUnit {

	public TransformSwitchControl(CDRDef cdrDef) {
		super(cdrDef);
	}

	/**
	 * xforms:switch visit method.  Called by the Smooks framework.
	 * <p/>
	 * Called to trigger the element transformation.
	 */
	public void visit(Element swittch, ContainerRequest request) {
		transformCaseElements(swittch);
		wrapSwitch(swittch);

		// Now, get rid of the switch but keep the child content (minus the chiba:data).
		DomUtils.insertBefore(swittch.getChildNodes(), swittch);
		swittch.getParentNode().removeChild(swittch);
	}

	private void wrapSwitch(Element swittch) {
		Document doc = swittch.getOwnerDocument();
		Element div = doc.createElement("div");
		
		div.setAttribute("id", swittch.getAttribute("id"));
		div.setAttribute("class", ControlTransUtils.getControlClassAttrib(swittch, null));
		
		swittch.getParentNode().replaceChild(div, swittch);
		div.appendChild(swittch);
	}

	private void transformCaseElements(Element swittch) {
		List cases = DomUtils.getElements(swittch, "case", Namespace.XFORMS);
		
		for(int i = 0; i < cases.size(); i++) {
			Element caseElement = (Element)cases.get(i);
			String selected = caseElement.getAttributeNS(Namespace.XFORMS, "selected");
			
			if(selected.equals("true")) {
				transformCase(caseElement);
			} else {
				// Just zap it for now if not selected.
				swittch.removeChild(caseElement);
			}
		}
	}
	
	private void transformCase(Element caseElement) {
		Document doc = caseElement.getOwnerDocument();
		Element chibaData = DomUtils.getElement(caseElement, "data", 1, Namespace.CHIBA);
		Element div = doc.createElement("div");
		Element label = DomUtils.getElement(caseElement, "label", 1, Namespace.XFORMS);
		
		div.setAttribute("id", caseElement.getAttribute("id"));
		div.setAttribute("class", "case selected-case");

		if(label != null) {
			caseElement.removeChild(caseElement);
		}
		if(chibaData != null) {
			caseElement.removeChild(chibaData);
		}
		caseElement.getParentNode().insertBefore(div, caseElement);
		DomUtils.copyChildNodes(caseElement, div);
		caseElement.getParentNode().removeChild(caseElement);
	}

	public boolean visitBefore() {
		return false;
	}
}
