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
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.assemble.AbstractAssemblyUnit;
import org.w3c.dom.Element;

/**
 * Removes a DOM element <u>during the assembly phase</u>.
 * <p/>
 * The element is visited by this Assembly Unit before it's child content
 * has been iterated over.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource	useragent="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org.milyn.cdres.assemble.RemoveElementAU" /&gt;</pre>
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * @author tfennelly
 */
public class RemoveElementAU extends AbstractAssemblyUnit {

	/**
	 * Public Constructor.
	 * @param resourceConfig Resource Configuration.
	 */
	public RemoveElementAU(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
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
		return "Removes and element from the document during the assembly phase.";
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryUnit#getDetailDescription()
	 */
	public String getDetailDescription() {
		return "Removes and element from the document during the assembly phase.";
	}
}
