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

package test.trans;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.delivery.trans.TransUnit;
import org.w3c.dom.Element;

/**
 * 
 * @author tfennelly
 */
public class FTransUnit extends AbstractTransUnit {

	public FTransUnit(CDRDef unitDef) {
		super(unitDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.trans.TransUnit#visitBefore()
	 */
	public boolean visitBefore() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.milyn.trans.TransUnit#visit(org.w3c.dom.Element)
	 */
	public void visit(Element element, ContainerRequest containerRequest) {
	}

	/* (non-Javadoc)
	 * @see org.milyn.trans.TransUnit#getShortDescription()
	 */
	public String getShortDescription() {
		return "Short Description";
	}

	/* (non-Javadoc)
	 * @see org.milyn.trans.TransUnit#getDetailDescription()
	 */
	public String getDetailDescription() {
		return "Long Description..... etc......";
	}
}
