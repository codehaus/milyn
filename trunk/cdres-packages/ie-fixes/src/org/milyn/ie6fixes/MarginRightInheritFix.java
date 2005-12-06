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

package org.milyn.ie6fixes;

import java.util.HashSet;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.ParameterAccessor;
import org.milyn.css.CssAccessor;
import org.milyn.magger.CSSProperty;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.w3c.dom.Element;

/**
 * Fix for the IE6 
 * <a href="http://www.quirksmode.org/bugreports/archives/2005/09/Margin_inheritance_bug.html">Margin Inheritance</a> bug.
 * @author tfennelly
 */
public class MarginRightInheritFix extends AbstractTransUnit {

	private static HashSet ieBlockLevelElements;
	
	public MarginRightInheritFix(CDRDef cdrDef) {
		super(cdrDef);
	}

	public void visit(Element element, ContainerRequest request) {
		if(ancestorHasMarginRight(element, request)) {
			if(ieBlockLevelElements == null) {
				initBlockLevelElements(request);
			}
			Element closesBlockLevelEl = getClosestBLE(element);
			if(closesBlockLevelEl != null) {
				String style = closesBlockLevelEl.getAttribute("style");
				if(style != null && style.toLowerCase().indexOf("overflow") == -1) {
					closesBlockLevelEl.setAttribute("style", "overflow: hidden; " + style);
				} else {
					closesBlockLevelEl.setAttribute("style", "overflow: hidden;");
				}
			}
		}
	}

	/**
	 * Initialise the ieBlockLevelElements attribute.
	 * @param request The container request.
	 * @throws IllegalStateException Param not configured for requesting device.
	 */
	private void initBlockLevelElements(ContainerRequest request) throws IllegalStateException {
		synchronized (MarginRightInheritFix.class) {
			ieBlockLevelElements = (HashSet)ParameterAccessor.getParameterObject("blockLevelElements", request.getDeliveryConfig());
			if(ieBlockLevelElements == null) {
				throw new IllegalStateException("Must configure a list of 'blockLevelElements' for " + request.getUseragentContext().getCommonName());
			}
		}
	}

	private Element getClosestBLE(Element element) {
		Element parent = null;
		
		while((parent = (Element)element.getParentNode()) != null) {
			String parentTag = parent.getTagName();
			
			// If the parent is one of the configured block level elements.
			if(ieBlockLevelElements.contains(parentTag)) {
				return parent;
			}
		}
		
		return null;
	}

	private boolean ancestorHasMarginRight(Element element, ContainerRequest request) {
		CssAccessor cssAccessor = new CssAccessor(request);
		Element parent = null;
		
		while((parent = (Element)element.getParentNode()) != null) {
			CSSProperty marginRight = cssAccessor.getProperty(parent, "margin-right");
			if(marginRight != null) {
				return true;
			}
		}
		
		return false;
	}

	public boolean visitBefore() {
		return false;
	}

	public String getShortDescription() {
		return null;
	}

	public String getDetailDescription() {
		return null;
	}
}
