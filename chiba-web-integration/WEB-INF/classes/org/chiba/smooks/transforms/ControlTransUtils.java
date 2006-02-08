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
import org.milyn.dom.DomUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class for common operations performed on XForms controls.
 * @author tfennelly
 */
public class ControlTransUtils {

	/**
	 * Get the control's "class" attribute value.
	 * <p/>
	 * Aggregates the controls "author", "name" and "mip" (chiba) class attribute
	 * components.
	 * @param control The XForms control element in question.
	 * @param chibaData The chiba:data element contained within the control element.
	 * @return Attribute value String.
	 */
	public static String getControlClassAttrib(Element control, Element chibaData) {
		StringBuffer valueBuf = new StringBuffer();
		String localName = control.getLocalName();
		String appearance = control.getAttributeNS(Namespace.XFORMS, "appearance").trim();

		// add author classes
		valueBuf.append(getAuthorClasses(control)).append(' ');
		
		// add name classes
		valueBuf.append(getNameClasses(localName, appearance)).append(' ');

		// Get the chiba data classes
		valueBuf.append(getMipClasses(chibaData));
		
		return valueBuf.toString();
	}

	/**
	 * Get the "mip" (whatever that is!) "class" attribute components.
	 * @param chibaData The chiba:data element contained within a control element.
	 * @return Mip "class" attribute value String.
	 */
	public static String getMipClasses(Element chibaData) throws DOMException {
		if(chibaData != null) {
			// add chiba:data "stuff"!
			String enabled = chibaData.getAttributeNS(Namespace.CHIBA, "enabled");
			String readonly = chibaData.getAttributeNS(Namespace.CHIBA, "readonly");
			String required = chibaData.getAttributeNS(Namespace.CHIBA, "required");
			String valid = chibaData.getAttributeNS(Namespace.CHIBA, "valid");

			return (enabled.equals("true")?"enabled ":"disabled ") +
					(readonly.equals("true")?"readonly ":"readwrite ") +
					(required.equals("true")?"required ":"optional ") +
					(valid.equals("true")?"valid":"invalid");

			// TODO: add the "limited" stuff.
		} else {
			return "";
		}
	}

	/**
	 * Get the class attribute components supplied by a page author on the supplied 
	 * XFroms control.
	 * @param control The XForms control element in question.
	 * @return Author class attribute components.
	 */
	public static String getAuthorClasses(Element control) throws DOMException {
		String authorClasses;
		authorClasses = control.getAttribute("class");
		if(authorClasses.equals("")) {
			authorClasses = control.getAttributeNS(Namespace.XHTML, "class");
			if(authorClasses.equals("")) {
				authorClasses = control.getAttributeNS(Namespace.XFORMS, "class");
			}
		}
		return authorClasses;
	}

	/**
	 * Get the class attribute value components for a control based on the controls
	 * local name and xforms:appearance attribute value.
	 * @param localName Control element localname.
	 * @param appearance Control element's xforms:appearance.
	 * @return "Class" attribute name components for the supplied control. 
	 */
	public static String getNameClasses(String localName, String appearance) {
		if(appearance != null && !appearance.equals("")) {
			return localName + " " + appearance + "-" + localName;
		} else {
			return localName + " ";
		}
	}

	/**
	 * Wrap the supplied control in a div, adding a label if an xforms:label is
	 * contained within the control.
	 * @param control The control to be wrapped.
	 * @param chibaData The chiba:data element contained within the control element.
	 */
	public static void wrapControl(Element control, Element chibaData) {		
		String id = control.getAttribute("id");
		Element xformsLabel = DomUtils.getElement(control, "label", 1, Namespace.XFORMS);
		Document doc = control.getOwnerDocument();
		Element div;
		
		div = doc.createElement("div");
		div.setAttribute("id", id);
		div.setAttribute("class", getControlClassAttrib(control, chibaData));
		if(xformsLabel != null) {
			Element label = getControlLabel(xformsLabel, chibaData);

			label.setAttribute("id", id + "-label");
			label.setAttribute("for", id + "-value");
			div.appendChild(label);
		}
		control.getParentNode().replaceChild(div, control);
		div.appendChild(control);
	}

	/**
	 * Get a label element for a control based on the controls xforms:label and
	 * chiba:data.
	 * @param xformsLabel Control xforms:label.
	 * @param chibaData Control chiba:data.
	 * @return Control label element.
	 */
	private static Element getControlLabel(Element xformsLabel, Element chibaData) {
		String imgSrc = xformsLabel.getAttributeNS(Namespace.XFORMS, "src").trim();
		Document doc = xformsLabel.getOwnerDocument();
		Element label = doc.createElement("label");
		
		label.setAttribute("class", getControlClassAttrib(xformsLabel, null));

		DomUtils.addLiteral(label, DomUtils.getAllText(xformsLabel, false));
		if(!imgSrc.equals("")) {
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
		
		return label;
	}
}
