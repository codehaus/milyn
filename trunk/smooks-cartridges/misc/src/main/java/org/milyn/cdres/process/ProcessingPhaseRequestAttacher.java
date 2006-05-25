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

package org.milyn.cdres.process;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.w3c.dom.Element;

/**
 * Attaches the target element to its {@link org.milyn.delivery.ElementList} 
 * <u>during the processing phase</u>.
 * <p/>
 * These elements will probably colaborate later in the processing phase.
 * <p/>
 * If the element has an "id" or "name" attribute this will be used as
 * the element key in the list, otherwise the element index will be used.
 * See {@link org.milyn.cdres.assemble.AssemblyPhaseRequestAttacher}.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource	useragent="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org.milyn.cdres.process.ProcessingPhaseRequestAttacher"&gt;
 * 	&lt;!-- (Optional) Visit the target element before iterating over the elements
 * 		child content. Default true. --&gt;
 * 	&lt;param name="<b>visitBefore</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/smooks-resource&gt;</pre>
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * @author tfennelly
 */
public class ProcessingPhaseRequestAttacher extends AbstractProcessingUnit {

	private boolean visitBefore = true;
	
	/**
	 * Public Constructor.
	 * @param resourceConfig Unit definition.
	 */
	public ProcessingPhaseRequestAttacher(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
		visitBefore = resourceConfig.getBoolParameter("visitBefore", true);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.process.TransUnit#visitBefore()
	 */
	public boolean visitBefore() {
		return visitBefore;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element element, ContainerRequest request) {
		request.getElementList(element.getTagName()).addElement(element);
	}
}
