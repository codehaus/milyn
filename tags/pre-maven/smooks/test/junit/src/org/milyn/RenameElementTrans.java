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

package org.milyn;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Element;

public class RenameElementTrans extends AbstractTransUnit {

	// cache the new element name.
	private String newElementName;

	public RenameElementTrans(CDRDef cdrDef) {
		super(cdrDef);
		// Capture the new name for the element from the configuration...
		newElementName = cdrDef.getStringParameter("new-name");
	}

	public void visit(Element element, ContainerRequest containerRequest) {
		// Rename the element to the configured new name.
		DomUtils.renameElement(element, newElementName, true, true);
	}

	public boolean visitBefore() {
		// We want this transformation code to visit the target element after all
		// child elements have been iterated over.
		return false;
	}
}
