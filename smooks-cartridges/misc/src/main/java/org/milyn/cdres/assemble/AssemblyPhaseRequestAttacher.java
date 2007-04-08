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

package org.milyn.cdres.assemble;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
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
 * See {@link org.milyn.delivery.process.ProcessingPhaseRequestAttacher}.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource	useragent="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org.milyn.cdres.assemble.AssemblyPhaseRequestAttacher"/&gt;</pre>
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * @author tfennelly
 */
public class AssemblyPhaseRequestAttacher extends AbstractAssemblyUnit {

	/**
	 * Public Constructor.
	 * @param resourceConfig Unit definition.
	 */
	public AssemblyPhaseRequestAttacher(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visit(Element element, ExecutionContext request) {
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
		return "Attaches the target element to its ElementList during the assembly phase.  ElementList is identified by the element tag name.  See ExecutionContext interface.";
	}
}
