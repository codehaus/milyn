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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

/**
 * The abstract container tag is extended to create a tag for group other Tinak tags.
 * <p/>
 * The content of this tag is delegated from one of the enclosed tags.
 * @see SelectTag
 * @see ProfilesTag
 * @author tfennelly
 */

public abstract class ContainerTag implements BodyTag {

	/**
	 * Page JSP context
	 */
	private PageContext pageContext = null;
	/**
	 * Tag parent
	 */
	private Tag parent = null;
	/**
	 * Content buffer for content added by child tags.
	 */
	private StringBuffer contentBuf = null;	
	/**
	 * Tag body content.
	 */
	private BodyContent bodyContent = null;    

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
	 * Get the page context.
	 * @return The page context.
	 */
	public PageContext getPageContext() {
		return pageContext;
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
	 * Append content to this instance.
	 * @param content The content to be appended.
	 */
	public void appendContent(String content) {
		if(contentBuf == null) {
			contentBuf = new StringBuffer();
		}
		contentBuf.append(content);
	}
	
	/**
	 * Get the content which was added to this instance.
	 * @return The content contained in this tag.
	 */
	public String getContent() {
		if(contentBuf == null) {
			return "";
		}
		return contentBuf.toString();
	}
	
	/**
	 * Has there been content added to this instance.
	 * @return True if content has been added, otherwise false.
	 */
	public boolean hasContent() {
		return (contentBuf != null && contentBuf.length() > 0);
	}

	/**
	 * Process the doStart event.
	 * @return {@link javax.servlet.jsp.tagext.BodyTagSupport#EVAL_BODY_BUFFERED}
	 */
	public int doStartTag() throws JspException {
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	/**
	 * Set the body content
	 * @param bodyContent The tag body content.
	 */
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

	/**
	 * Process the doInitBody event.
	 */
	public void doInitBody() throws JspException {
	}

	/**
	 * Process the doAfterBody event.
	 * @return {@link Tag#SKIP_BODY}
	 */
	public int doAfterBody() throws JspException {
		// Need to trim the whitespace from around the selection.
		if(bodyContent != null) {
			try {
				bodyContent.clearBuffer();
				if(hasContent()) {
					bodyContent.write(getContent());
					bodyContent.writeOut(bodyContent.getEnclosingWriter());
				}
			} catch (IOException exception) {
				JspException jspException = new JspException("Error clearing and setting container content [" + getClass().getName() + "]");
				jspException.initCause(exception);
				throw jspException; 
			}
		}

		return Tag.SKIP_BODY;
	}

	/**
	 * Process the doEnd event.
	 * @return One of {@link Tag#SKIP_BODY}, {@link Tag#EVAL_BODY_INCLUDE}, {@link Tag#SKIP_PAGE}, {@link Tag#EVAL_PAGE}
	 */
	public int doEndTag() throws JspException {
		if(contentBuf != null) {
			contentBuf.setLength(0);
		}
		return Tag.EVAL_PAGE;
	}

	/**
	 * Process the release event.
	 */
	public void release() {
	}
}
