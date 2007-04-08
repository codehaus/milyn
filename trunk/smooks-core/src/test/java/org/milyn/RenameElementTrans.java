/*
	Milyn - Copyright (C) 2006

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

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

public class RenameElementTrans extends AbstractProcessingUnit {

	// cache the new element name.
	private String newElementName;

	public RenameElementTrans(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
		// Capture the new name for the element from the configuration...
		newElementName = resourceConfig.getStringParameter("new-name");
	}

	public void visit(Element element, ExecutionContext executionContext) {
		// Rename the element to the configured new name.
		DomUtils.renameElement(element, newElementName, true, true);
	}

	public boolean visitBefore() {
		// We want this transformation code to visit the target element after all
		// child elements have been iterated over.
		return false;
	}
}
