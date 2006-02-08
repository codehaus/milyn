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
import org.chiba.smooks.Prefix;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TransUnit for the xforms:repeat element.
 * <p/>
 * Currently only supports application of "full" appearance repeat transformations
 * i.e. doesn't yet support "minimal" or "compact".
 * @author tfennelly
 */
public class TransformRepeatControl extends AbstractTransUnit {

	public TransformRepeatControl(CDRDef cdrDef) {
		super(cdrDef);
	}

	/**
	 * xforms:repeat visit method.  Called by the Smooks framework.
	 * <p/>
	 * Called to trigger the element transformation.
	 */
	public void visit(Element repeat, ContainerRequest containerRequest) {
		String appearance = repeat.getAttributeNS(Namespace.XFORMS, "appearance");
		Element chibaData = DomUtils.getElement(repeat, "data", 1, Namespace.CHIBA);
		
		if(appearance.equals("minimal")) {
			// TODO: Minimal repeat implementation
		} else if(appearance.equals("compact")) {
			// TODO: Compact repeat implementation
		} else if(appearance.equals("full")) {
			applyFullRepeat(repeat, chibaData);
		}
		
		// Now, get rid of the repeat but keep the child content (minus the chiba:data).
		repeat.removeChild(chibaData);
		DomUtils.insertBefore(repeat.getChildNodes(), repeat);
		repeat.getParentNode().removeChild(repeat);
	}

	private void applyFullRepeat(Element repeat, Element chibaData) {
		String id = repeat.getAttribute("id");
		List groups = DomUtils.getElements(repeat, "group", Namespace.XFORMS);
		int index = 0;
		
		try {
			index = Integer.parseInt(chibaData.getAttributeNS("index", Namespace.CHIBA));
			index--;
		} catch(NumberFormatException nfe) {
			index = 0;
		}
		
		ControlTransUtils.wrapControl(repeat, chibaData);
		for(int i = 0; i < groups.size(); i++) {
			String classAttribVal = "repeat-item";
			Element group = (Element)groups.get(i);
			Element groupChibaData = DomUtils.getElement(group, "data", 1, Namespace.CHIBA);
			
			if(i == index) {
				classAttribVal += " repeat-index";
			}
			classAttribVal += ControlTransUtils.getControlClassAttrib(group, groupChibaData);
			
			insertRadioBefore(group, i + 1, id, (i == index));
			TransformGroupControl.transformGroup(group, classAttribVal);
		}
	}

	private void insertRadioBefore(Element group, int position, String repeatId, boolean selected) {
		Document doc = group.getOwnerDocument();
		Element div = doc.createElement("div");
		Element radioInput = doc.createElement("input");
		
		group.getParentNode().insertBefore(div, group);
		div.appendChild(radioInput);
		radioInput.setAttribute("type", "radio");
		radioInput.setAttribute("name", Prefix.SELECTOR + repeatId); // TODO: XSLT uses "outermost-Id"???
		radioInput.setAttribute("class", "repeat-selector");
		radioInput.setAttribute("value", repeatId + ":" + position);
		if(selected) {
			// TODO: needs to encorporate "string($outermost-id)=string($repeat-id)" check
			radioInput.setAttribute("checked", "checked");
		}
		
		/*
        <div>
            <input type="radio" name="{$selector-prefix}{$outermost-id}" value="{$repeat-id}:{position()}" class="repeat-selector">
                <xsl:if test="string($outermost-id)=string($repeat-id) and string($repeat-index)=string(position())">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
            </input>
            <xsl:call-template name="group-body">
                <xsl:with-param name="group-id" select="$repeat-item-id"/>
                <xsl:with-param name="group-classes" select="$repeat-item-classes"/>
            </xsl:call-template>
        </div>
		 */	
	}

	public boolean visitBefore() {
		return false;
	}
}
