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

package org.milyn.tinak;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * Tag for setting the response header.
 * @author tfennelly
 */
public class SetContentType implements Tag {

	/**
	 * Page JSP context
	 */
	private PageContext pageContext = null;
	/**
	 * Tag parent
	 */
	private Tag parent = null;
	/**
	 * The Content-Type attribute value.
	 */
	private String value = null;

	/**
	 * Set the page context.
	 * <p/>
	 * Called by the container.
	 * @param context The page context.
	 */
	public void setPageContext(PageContext context) {
		pageContext = context;
	}

	/**
	 * Set the tag parent tag.
	 * <p/>
	 * Called by the container.
	 * @param tag The tag parent tag.
	 */
	public void setParent(Tag tag) {
		parent = tag;
	}

	/**
	 * Get the tag parent tag.
	 * @return The tag parent tag.
	 */
	public Tag getParent() {
		return parent;
	}

	/**
	 * Process the doStart event.
	 * @return {@link Tag#SKIP_BODY}
	 */
	public int doStartTag() throws JspException {
		if(value != null && !value.trim().equals("")) {
			pageContext.getResponse().setContentType(value);
		}
		
		return Tag.SKIP_BODY;
	}

	/**
	 * Process the doEnd event.
	 * @return One of {@link Tag#SKIP_BODY}, {@link Tag#EVAL_BODY_INCLUDE}, {@link Tag#SKIP_PAGE}, {@link Tag#EVAL_PAGE}
	 */
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	/**
	 * Process the release event.
	 */
	public void release() {
	}
	
	/**
	 * Set the content type
	 * <p/>
	 * Bean method.
	 * @param contentType The content type attribute value.
	 */
	public void setValue(String contentType) {
		value = contentType;		
	}
}

