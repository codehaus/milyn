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

package org.milyn.smooks.scripting;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

public class MyGroovyScript implements ProcessingUnit {

	String newName;
	
	public void setConfiguration(SmooksResourceConfiguration config) {
		newName = config.getStringParameter("new-name", "zzz");
	}

	public void visit(Element fragment, ContainerRequest request) {
		DomUtils.renameElement(fragment, newName, true, true);
	}

	public boolean visitBefore() {
		return false;
	}
}
