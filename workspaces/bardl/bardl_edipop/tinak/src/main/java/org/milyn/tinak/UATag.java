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

import org.milyn.servlet.ServletUAContext;
import org.milyn.useragent.UnknownUseragentException;
import org.milyn.useragent.UAContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * The ua tag is a means of selecting or deselecting content based on the
 * accessing useragents common name or membership of a profile.
 * @author Tom Fennelly
 */

public class UATag implements BodyTag {

    /**
     * Page JSP context
     */
    private PageContext pageContext = null;
    /**
     * Tag parent
     */
    private Tag parent = null;
    /**
     * Useragent context.
     */
    private UAContext uaContext = null;
    /**
     * Match attribute value.  This may contain a list of useragent common
     * names and/or useragent profile names, pipe ('|') separated.
     */
    private String match = null;
    /**
     * Directive attribute value.  Can have values of "add" ("+") or "remove" ("-").
     * The default is always "add".
     */
    private String directive = null;
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
        try {
            uaContext = ServletUAContext.getInstance((HttpServletRequest)pageContext.getRequest(), pageContext.getServletConfig());
        } catch(UnknownUseragentException unknownDevice) {
            uaContext = null;
        } catch(Throwable thrown) {
            thrown.printStackTrace();
        }
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
     * @return One of {@link Tag#SKIP_BODY}, {@link Tag#EVAL_BODY_INCLUDE} or {@link BodyTag#EVAL_BODY_BUFFERED}
     */
    public int doStartTag() throws JspException {
        boolean add = true;
        boolean parentSelect = false;

        if(match == null && directive != null) {
            throw new JspException("ua: 'dir' attribute specified but no 'match' attribute specified.");
        }

		// Select tag checks...
        if(parent instanceof SelectTag) {
			if(directive != null) {
				// Shouldn't have a dir attribute
				throw new JspException("'dir' attribute specified on ua tag inside a select.");
			} else if(((SelectTag)parent).hasContent()) {
				// Don't evaluate this ua match if it's within an enclosing select which has
				// already encountered a ua whose match evaluated true.
	            return Tag.SKIP_BODY;
        	}
			parentSelect = true;
        }

        if(match != null) {
            if(directive != null) {
                directive = directive.trim().toLowerCase();
                if(directive.equals("-") || directive.equals("remove")) {
                    add = false;
                }
            }

            try {
                if(uaContext != null && match != null) {
                    String matchCN = uaContext.getCommonName();
                    StringTokenizer tokenizer = new StringTokenizer(match, "|,;");

                    while(tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken().trim().toLowerCase();

                        if(token.equals(matchCN) || (uaContext.getProfileSet().isMember(token))) {
                            if(add) {
								return evalBody(parentSelect);
                            } else {
                                return Tag.SKIP_BODY;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                JspException throwExcep = new JspException("Unable to process UATag.");
                throwExcep.initCause(ex);
                throw throwExcep;
            }

            if(add) {
                return Tag.SKIP_BODY;
            }
        }

		return evalBody(parentSelect);
    }
    
	/**
	 * Get the EVAL_BODY value based on the presence of (or lack of) a parent select
	 * tag.
	 * @param parentSelect True if the parent tag is a select, otherwise false.
	 * @return The EVAL_BODY value.  One of {@link Tag#EVAL_BODY_INCLUDE} or {@link BodyTag#EVAL_BODY_BUFFERED} 
	 */
    private int evalBody(boolean parentSelect) { 
		if(parentSelect) {                            	
			return BodyTag.EVAL_BODY_BUFFERED;
		}
		return Tag.EVAL_BODY_INCLUDE;
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
				String content = bodyContent.getString();

				bodyContent.clearBuffer();
				if(parent instanceof SelectTag) {
					((SelectTag)parent).appendContent(content);
				}
			} catch (IOException exception) {
				JspException jspException = new JspException("Error clearing content of " + getClass().getName());
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
        return Tag.EVAL_PAGE;
    }

    /**
     * Bean method for setting the match attribute ("match") value from the page.
     * <p/>
     * A match value of 'first' is used to select the first nested <coe>ua</code> tag
     * which evaluates a match positively.
     * @param match The match value.
     */
    public void setMatch(String match) {
        if(match != null) {
            this.match = match.toLowerCase();
        }
    }

    /**
     * Bean method for setting the directive attribute ("dir") value from the page.
     * @param directive The "dir" attribute value.
     */
    public void setDir(String directive) {
        if(directive != null) {
            this.directive = directive.trim().toLowerCase();
        }
    }

    /**
     * Process the release event.
     */
    public void release() {
    }
}
