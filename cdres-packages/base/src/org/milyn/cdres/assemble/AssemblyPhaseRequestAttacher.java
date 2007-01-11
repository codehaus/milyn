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

package org.milyn.cdres.assemble;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.assemble.AbstractAssemblyUnit;
import org.w3c.dom.Element;

/**
 * Attaches the target element to its {@link org.milyn.delivery.ElementList} 
 * <u>during the assembly phase</u>.
 * <p/>
 * These elements will probably colaborate later in the assembly phase.
 * <p/>
 * If the element has an "id" or "name" attribute this will be used as
 * the element key in the list, otherwise the element index will be used.
 * See {@link org.milyn.delivery.trans.TransformationPhaseRequestAttacher}.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;cdres	uatarget="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org/milyn/cdres/assemble/AssemblyPhaseRequestAttacher.class"/&gt;</pre>
 * See {@link org.milyn.cdr.CDRDef}.
 * @author tfennelly
 */
public class AssemblyPhaseRequestAttacher extends AbstractAssemblyUnit {

	/**
	 * Public Constructor.
	 * @param cdrDef Unit definition.
	 */
	public AssemblyPhaseRequestAttacher(CDRDef cdrDef) {
		super(cdrDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element element, ContainerRequest request) {
		request.getElementList(element.getTagName()).addElement(element);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getShortDescription()
	 */
	public String getShortDescription() {
		return "Assembly Phase Request Attacher.";
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getDetailDescription()
	 */
	public String getDetailDescription() {
		return "Attaches the target element to its ElementList during the assembly phase.  ElementList is identified by the element tag name.  See ContainerRequest interface.";
	}
}
