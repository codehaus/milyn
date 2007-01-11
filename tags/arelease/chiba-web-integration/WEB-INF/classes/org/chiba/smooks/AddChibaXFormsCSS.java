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
import org.w3c.dom.Element;

public class AddChibaXFormsCSS extends AbstractTransUnit {

	public AddChibaXFormsCSS(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void visit(Element element, ContainerRequest request) {
		// Add the CSS.
		DomUtils.addLiteral(element, 
				"<link type='text/css' rel='stylesheet' href='" 
				+ request.getContextPath() 
				+ "/forms/styles/xforms.css' />\n");

		// TODO: needs to work based on a param of some 
		// sort e.g. from the request or the servlet config!!
	}

	public boolean visitBefore() {
		return false;
	}
}
