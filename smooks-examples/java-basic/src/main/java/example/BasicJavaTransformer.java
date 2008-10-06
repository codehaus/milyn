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
package example;

import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.container.ExecutionContext;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Basic transformer that simply renames an element.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BasicJavaTransformer implements DOMElementVisitor {

    private String newElementName;

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        newElementName = resourceConfig.getStringParameter("newName", "xxx");
    }
    
    public void visitBefore(Element element, ExecutionContext executionContext) {
        // Not doing anything on this visit - wait untill after visiting the elements child content...
    }

    public void visitAfter(Element element, ExecutionContext executionContext) {
        // Just rename the target element - keeping child elements - not keeping attributes.
        DomUtils.renameElement(element, newElementName, true, false);
    }
}
