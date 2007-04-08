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

package org.milyn.cdres.trans;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.w3c.dom.Element;

/**
 * Removes a DOM element attribute <u>during the processing phase</u>.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource	useragent="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org.milyn.cdres.trans.RemoveAttributeTU"&gt;
 * 
 * 	&lt;!-- The name of the element attribute to be removed. --&gt;
 * 	&lt;param name="<b>attributeName</b>"&gt;<i>attribute-name</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Visit the target element before iterating over the elements
 * 		child content. Default false. --&gt;
 * 	&lt;param name="<b>visitBefore</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/smooks-resource&gt;</pre>
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * @author tfennelly
 */
public class RemoveAttributeTU extends AbstractProcessingUnit {

	private String attributeName;
	private boolean visitBefore;
	
	public RemoveAttributeTU(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
		attributeName = resourceConfig.getStringParameter("attributeName");
		if(attributeName == null) {
			throw new IllegalStateException(RemoveAttributeTU.class + " resourceConfig must define a 'attributeName' param.");
		}
		visitBefore = resourceConfig.getBoolParameter("visitBefore", false);
	}

	public void visit(Element element, ExecutionContext request) {
		element.removeAttribute(attributeName);
	}

	public boolean visitBefore() {
		return visitBefore;
	}
}
