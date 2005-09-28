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

package org.milyn.cdres.trans;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Element;

/**
 * Renames/replaces an element in the document <u>during the transformation phase</u>.
 * <p/>
 * The element is visited by this Transformation Unit after it's child content
 * has been iterated over.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;cdres	uatarget="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org/milyn/cdres/trans/RenameElementTU.class"&gt;
 * 
 * 	&lt;!-- The name of the replacement element. --&gt;
 * 	&lt;param name="<b>replacementElement</b>"&gt;<i>replacement-element-name</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Copy target elements child content to the replacement 
 * 		element. Default is true. --&gt;
 * 	&lt;param name="<b>keepChildContent</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Copy target elements attributes to the replacement 
 * 		element. Default is true. --&gt;
 * 	&lt;param name="<b>keepAttributes</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/cdres&gt;</pre>
 * See {@link org.milyn.cdr.CDRDef}.
 * 
 * @author tfennelly
 */
public class RenameElementTU extends AbstractTransUnit {

	private String replacementElement;
	private boolean keepChildContent;	
	private boolean keepAttributes;	
	
	public RenameElementTU(CDRDef cdrDef) {
		super(cdrDef);
		replacementElement = cdrDef.getStringParameter("replacementElement");
		if(replacementElement == null) {
			throw new IllegalStateException(RenameElementTU.class + " cdres must define a 'replacementElement' param.");
		}
		keepChildContent = cdrDef.getBoolParameter("keepChildContent", true);
		keepAttributes = cdrDef.getBoolParameter("keepAttributes", true);
	}

	public boolean visitBefore() {
		return false;
	}

	public void visit(Element element, ContainerRequest request) {
		DomUtils.renameElement(element, replacementElement, keepChildContent, keepAttributes);
	}

	public String getShortDescription() {
		return "Rename/replace Element";
	}

	public String getDetailDescription() {
		return "Renames/replaces elements in the document to during the transformation phase.";
	}
}
