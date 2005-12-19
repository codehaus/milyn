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

package org.milyn.report.msie.v6;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.dom.DomUtils;
import org.milyn.css.CSSAccessor;
import org.milyn.report.AbstractReportingUnit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Report generator for IE6 comments between CSS floats bug.
 * <p/>
 * See http://www.positioniseverything.net/explorer/dup-characters.html. 
 * @author tfennelly
 */
public class MSIE6_0001 extends AbstractReportingUnit {

	public MSIE6_0001(CDRDef cdrDef) {
		super(cdrDef);
	}
	
	/**
	 * Visit method.
	 * <p/>
	 * Called by the smooks framework as it iterates over the source content.
	 */
	public void visit(Element element, ContainerRequest request) {
		Element previousElement = (Element)DomUtils.getPreviousSibling(element, Node.ELEMENT_NODE);
		
		// If there's no previous sibling element, don't report.
		if(previousElement == null) {
			return;
		}
		
		// Must be 2 or more comment nodes and whitespace (only) before the
		// element as siblings - nothing else!
		if(!assert2OrMoreCommentsAndWhitespaceBetween(previousElement, element)) {
			return;
		}

		// The element being visited, and its parent element, must have
		// float CSS target at both of them.
		CSSAccessor cssAccessor = CSSAccessor.getInstance(request);
		if(cssAccessor.getProperty(element, "float") == null) {
			return;
		}
		if(cssAccessor.getProperty((Element)previousElement, "float") == null) {
			return;
		}
		
		// Meets all the criteria... Generate issue report
		this.report(element, request);
	}

	private boolean assert2OrMoreCommentsAndWhitespaceBetween(Element previousElement, Element element) {
		
		// Are there 2 or more comment siblings between the elements?
		int commentCount = DomUtils.countNodesBetween(previousElement, element, Node.COMMENT_NODE);
		if(commentCount < 2) {
			return false;
		}
		
		// Are there only comment and text node siblings between the elements?
		int textCount = DomUtils.countNodesBetween(previousElement, element, Node.TEXT_NODE);
		int nodeCount = DomUtils.countNodesBetween(previousElement, element);
		if(nodeCount > commentCount + textCount) {
			return false;
		}

		// Is the text all whitespace?
		String textBetween = DomUtils.getTextBetween(previousElement, element);
		if(!textBetween.trim().equals("")) {
			return false;
		}
		
		return true;
	}
}
