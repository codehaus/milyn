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

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.w3c.dom.Element;

/**
 * Set a DOM element attribute <u>during the processing phase</u>.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource	useragent="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org.milyn.cdres.trans.SetAttributeTU"&gt;
 * 
 * 	&lt;!-- The name of the element attribute to be set. --&gt;
 * 	&lt;param name="<b>attributeName</b>"&gt;<i>attribute-name</i>&lt;/param&gt;
 * 
 * 	&lt;!-- The value of the element attribute to be set. --&gt;
 * 	&lt;param name="<b>attributeValue</b>"&gt;<i>attribute-value</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Overwrite existing attributes of the same name. Default false. --&gt;
 * 	&lt;param name="<b>overwrite</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Visit the target element before iterating over the elements
 * 		child content. Default false. --&gt;
 * 	&lt;param name="<b>visitBefore</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/smooks-resource&gt;</pre>
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * @author tfennelly
 */
public class SetAttributeTU extends AbstractProcessingUnit {

	private String attributeName;
	private String attributeValue;
	private boolean visitBefore;
	private boolean overwrite;
	
	public SetAttributeTU(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
		attributeName = resourceConfig.getStringParameter("attributeName");
		if(attributeName == null) {
			throw new IllegalStateException(SetAttributeTU.class + " cdres must define a 'attributeName' param.");
		}
		attributeValue = resourceConfig.getStringParameter("attributeValue");
		if(attributeValue == null) {
			throw new IllegalStateException(SetAttributeTU.class + " cdres must define a 'attributeValue' param.");
		}
		overwrite = resourceConfig.getBoolParameter("overwrite", false);
		visitBefore = resourceConfig.getBoolParameter("visitBefore", false);
	}

	public void visit(Element element, ContainerRequest request) {
		// If the element already has the new attribute and we're
		// not overwriting, leave all as is and return.
		if(!overwrite && element.hasAttribute(attributeName)) {
			return;
		}
		element.setAttribute(attributeName, attributeValue);
	}

	public boolean visitBefore() {
		return visitBefore;
	}
}
