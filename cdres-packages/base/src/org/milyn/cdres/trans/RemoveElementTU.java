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
import org.w3c.dom.Element;

/**
 * Removes a DOM element <u>during the transformation phase</u>.
 * <p/>
 * The element is visited by this Transformation Unit after it's child content
 * has been iterated over.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;cdres	uatarget="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org/milyn/cdres/trans/RemoveElementTU.class" /&gt;</pre>
 * See {@link org.milyn.cdr.CDRDef}.
 * @author tfennelly
 */
public class RemoveElementTU extends AbstractTransUnit {

	public RemoveElementTU(CDRDef cdrDef) {
		super(cdrDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.trans.TransUnit#visitBefore()
	 */
	public boolean visitBefore() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element element, ContainerRequest request) {
		element.getParentNode().removeChild(element);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getShortDescription()
	 */
	public String getShortDescription() {
		return "Remove Element";
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getDetailDescription()
	 */
	public String getDetailDescription() {
		return "Removes an element from the document during the transformation phase.";
	}
}
