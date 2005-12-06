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

package org.milyn.report.msie6;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.dom.DomUtils;
import org.milyn.css.CssAccessor;
import org.milyn.report.AbstractReportingUnit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Report generator for IE6 comments between CSS floats bug.
 * <p/>
 * See http://www.positioniseverything.net/explorer/dup-characters.html. 
 * @author tfennelly
 */
public class CommentsBetweenFloats extends AbstractReportingUnit {

	public CommentsBetweenFloats(CDRDef cdrDef) {
		super(cdrDef);
	}
	
	public void visit(Element element, ContainerRequest request) {
		Node parent = element.getParentNode();

		// If the parent node is not an element, don't report.
		if(parent.getNodeType() != Node.ELEMENT_NODE) {
			return;
		}
		
		// Must be 2 or more comment nodes and whitespace only before the
		// element as siblings - nothing else!
		if(!assert2OrMoreCommentsAndWhitespaceBefore(element)) {
			return;
		}

		// The element being visited, and its parent element, must have
		// float CSS target at both of them.
		CssAccessor cssAccessor = new CssAccessor(request);
		if(cssAccessor.getProperty(element, "float") == null) {
			return;
		}
		if(cssAccessor.getProperty((Element)parent, "float") == null) {
			return;
		}
		
		// Meets all the criteria... Generate issue report
		this.report(element, request);
	}

	private boolean assert2OrMoreCommentsAndWhitespaceBefore(Element element) {
		
		// Are there 2 or more comment siblings before the element?
		int commentCount = DomUtils.countNodesBefore(element, Node.COMMENT_NODE);
		if(commentCount < 2) {
			return false;
		}
		
		// Are there only comment and text node siblings before the element?
		int textCount = DomUtils.countNodesBefore(element, Node.TEXT_NODE);
		int nodeCount = DomUtils.countNodesBefore(element);
		if(nodeCount > commentCount + textCount) {
			return false;
		}

		// Is the text all whitespace?
		String textBefore = DomUtils.getTextBefore(element);
		if(!textBefore.trim().equals("")) {
			return false;
		}
		
		return true;
	}

	public String getShortDescription() {
		return "Report generator for IE6 comments between CSS floats bug";
	}

	public String getDetailDescription() {
		return "Report generator for IE6 comments between CSS floats bug";
	}
}
