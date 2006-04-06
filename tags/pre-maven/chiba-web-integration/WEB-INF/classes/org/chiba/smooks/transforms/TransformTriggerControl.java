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

import org.chiba.smooks.Namespace;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Transunit for the xforms:trigger element.
 * <p/>
 * Replaces the trigger with an HTML form input submit button wrapped
 * in a styled span element. 
 * @author tfennelly
 */
public class TransformTriggerControl extends AbstractTransUnit {

	public TransformTriggerControl(CDRDef cdrDef) {
		super(cdrDef);
	}

	/**
	 * xforms:trigger visit method.  Called by the Smooks framework.
	 * <p/>
	 * Called to trigger the element transformation.
	 */
	public void visit(Element trigger, ContainerRequest containerRequest) {
		StringBuffer transBuf = new StringBuffer(100);
		Document document = trigger.getOwnerDocument();
		String id = trigger.getAttribute("id");
		Element label = DomUtils.getElement(trigger, "label", 1, Namespace.XFORMS);
		String value;
		
		if(label != null) {
			value = DomUtils.getAllText(label, false);
		} else {
			value ="no label";
		}
		
		// TODO: convert this transformation to use DOM elements etc Vs String literals.
		// write the span and input element i.e. transform the trigger.
		transBuf.append("<span id=\"").append(id).append("\" class=\"trigger\">"); 
		transBuf.append("<input id=\"").append(id).append("-value\" name=\"t_").append(id); 
		transBuf.append("\" type=\"submit\" class=\"value\" title=\"\" value=\""); 
		transBuf.append(value).append("\" /></span>");
		
		// replace the trigger with the span.
		Text transNode = document.createTextNode(transBuf.toString());
		trigger.getParentNode().replaceChild(transNode, trigger);
	}

	public boolean visitBefore() {
		return false;
	}
}
