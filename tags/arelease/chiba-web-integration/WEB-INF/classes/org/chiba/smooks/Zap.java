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
import org.w3c.dom.Element;

public class Zap extends AbstractTransUnit {

	public Zap(CDRDef arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void visit(Element arg0, ContainerRequest arg1) {
		System.out.println("Namespace: " + arg0.getNamespaceURI());
		System.out.println("LocalName: " + arg0.getLocalName());
		System.out.println("NodeName: " + arg0.getNodeName());
		System.out.println("Prefix: " + arg0.getPrefix());
		System.out.println("Tagname: " + arg0.getTagName());
	}

	public boolean visitBefore() {
		// TODO Auto-generated method stub
		return false;
	}

}
