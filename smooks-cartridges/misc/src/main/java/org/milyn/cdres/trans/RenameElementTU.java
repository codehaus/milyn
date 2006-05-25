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
import org.milyn.dom.DomUtils;
import org.w3c.dom.Element;

/**
 * Renames/replaces an element in the document <u>during the processing phase</u>.
 * <p/>
 * The element is visited by this Processing Unit after it's child content
 * has been iterated over.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;smooks-resource	useragent="<i>device/profile</i>" selector="<i>target-element-name</i>" 
 * 	path="org.milyn.cdres.trans.RenameElementTU"&gt;
 * 
 * 	&lt;!-- The name of the replacement element. --&gt;
 * 	&lt;param name="<b>replacementElement</b>"&gt;<i>replacement-element-name</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Copy target elements child content to the replacement 
 * 		element. Default is true. --&gt;
 * 	&lt;param name="<b>keepChildContent</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Copy target elements attributes to the replacement 
 * 		element. Default is true. --&gt;
 * 	&lt;param name="<b>keepAttributes</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/smooks-resource&gt;</pre>
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * 
 * @author tfennelly
 */
public class RenameElementTU extends AbstractProcessingUnit {

	private String replacementElement;
	private boolean keepChildContent;	
	private boolean keepAttributes;	
	
	public RenameElementTU(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
		replacementElement = resourceConfig.getStringParameter("replacementElement");
		if(replacementElement == null) {
			throw new IllegalStateException(RenameElementTU.class + " cdres must define a 'replacementElement' param.");
		}
		keepChildContent = resourceConfig.getBoolParameter("keepChildContent", true);
		keepAttributes = resourceConfig.getBoolParameter("keepAttributes", true);
	}

	public boolean visitBefore() {
		return false;
	}

	public void visit(Element element, ContainerRequest request) {
		DomUtils.renameElement(element, replacementElement, keepChildContent, keepAttributes);
	}
}
