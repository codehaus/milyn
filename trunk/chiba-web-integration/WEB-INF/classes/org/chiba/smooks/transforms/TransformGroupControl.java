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

/**
 * TransUnit for an xforms:group element.
 * <p/>
 * This TransUnit only applies its transforms via it's 
 * {@link #visit(Element, ContainerRequest)} implementation if the xforms:group
 * being visited is not contained within an xforms:repeat. xforms:repeat group
 * element transforms are triggered from the {@link TransformGroupControl} TransUnit.
 * This is why the {@link #transformGroup(Element, String)} method is implemented
 * as a public static methods.
 * @author tfennelly
 */
public class TransformGroupControl extends AbstractTransUnit {

	public TransformGroupControl(CDRDef cdrDef) {
		super(cdrDef);
	}

	/**
	 * xforms:group visit method.  Called by the Smooks framework.
	 * <p/>
	 * Called to trigger the element transformation.
	 */
	public void visit(Element group, ContainerRequest containerRequest) {
		if(!isParentXformsRepeat(group)) {
			// Only transform if not inside an an xforms:repeat.  The repeat 
			// transform will trigger xform:group transforms - see TransformRepeatControl.
			transformGroup(group, "group " + group.getAttribute("class") + " " 
					+ group.getAttributeNS(Namespace.XFORMS, "appearance") + "-group");
		}
	}

	public static void transformGroup(Element group, String classAttribVal) {
		Document doc = group.getOwnerDocument();
		Element fieldset;
		
		transformLabel(group);

		// Transform the group into a fieldset.
		fieldset = doc.createElement("fieldset");
		fieldset.setAttribute("id", group.getAttribute("id"));
		fieldset.setAttribute("class", classAttribVal);

		group.getParentNode().replaceChild(fieldset, group);
		DomUtils.copyChildNodes(group, fieldset);
	}
	
	private static void transformLabel(Element group) {
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
	
	private boolean isParentXformsRepeat(Element group) {
		Element parent = (Element)group.getParentNode();

		if(DomUtils.getName(parent).equals("repeat") &&
				parent.getNamespaceURI().equals(Namespace.XFORMS)) {
			
			return true;
		}
		
		return false;
	}

	public boolean visitBefore() {
		return false;
	}

}
