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

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * xforms group form wrapping transunit.
 * <p/>
 * Called to trigger the wrapping of the outermost xforms:group element
 * in a html form element.
 * @author tfennelly
 */
public class WrapOuterGroupInForm extends AbstractTransUnit {
	
	private boolean visitBefore = true;
	private static String REQUEST_KEY = WrapOuterGroupInForm.class.toString();

	public WrapOuterGroupInForm(CDRDef cdrDef) {
		super(cdrDef);
		visitBefore = cdrDef.getBoolParameter("visitBefore", true);
	}

	/**
	 * xforms:group visit method.  Called by the Smooks framework.
	 * <p/>
	 * Called to trigger the wrapping of the outermost xforms:group element
	 * in a html form element.  So, this transunit doesn't perform
	 * any transformations on the xforms:group.  That is done by the
	 * {@link TransformGroupControl} transunit - therefore, make sure
	 * this transunit visits the xforms:group first.
	 * <p/>
	 * This transunit needs to be triggered twice - before and after the
	 * groups child content is added i.e. at the start and end of the element.  
	 * This transunit uses these events to wrap the outermost xform:group elements 
	 * in a HTML form element.  By outermost we mean group elements that themselves 
	 * are contained within a group element.
	 * 
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element group, ContainerRequest containerRequest) {
		if(isOuterGroup(group, containerRequest) ) {
			if(visitBefore) {
				wrapInForm(group, containerRequest);
				containerRequest.setAttribute(REQUEST_KEY, group);
			} else {
				containerRequest.removeAttribute(REQUEST_KEY);
			}
		}
	}

	private void wrapInForm(Element group, ContainerRequest containerRequest) {
		Document doc = group.getOwnerDocument();
		Element form = doc.createElement("form");
		Element refreshButton = doc.createElement("input");
		
		form.setAttribute("name", "chibaform");
		form.setAttribute("action", containerRequest.getContextPath() + "PlainHtml");
		form.setAttribute("method", "POST");
		form.setAttribute("enctype", "application/x-www-form-urlencoded");

		refreshButton.setAttribute("type", "submit");
		refreshButton.setAttribute("value", "refresh page");
		refreshButton.setAttribute("class", "refresh-button");
		
		// wrap the group in the form and add the refresh button before and 
		// after the group
		group.getParentNode().replaceChild(form, group);
		form.appendChild(refreshButton);
		form.appendChild(group);
		form.appendChild(refreshButton.cloneNode(true));
		
		// TODO: Could use Smooks server side CSS to control adding of this button.
		// TODO: Where does the action URL come from? 
		// TODO: Write a transformation Unit that's targeted at all xforms elements such 
		// that all "outter" XForms elements are wrapped in a <form>.
	}

	private boolean isOuterGroup(Element group, ContainerRequest request) {
		Object outerGroup = request.getAttribute(REQUEST_KEY);
		
		if(outerGroup == null) {
			return true;
		} else if(outerGroup == group) {
			return true;
		}
		
		return false;
	}

	public boolean visitBefore() {
		return visitBefore;
	}
}
