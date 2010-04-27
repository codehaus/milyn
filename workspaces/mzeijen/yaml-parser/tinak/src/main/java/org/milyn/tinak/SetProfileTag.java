/*
	Milyn - Copyright (C) 2006 - 2010

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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.ServletContext;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The set-profile tag adds a profile to the context.
 * @author Tom Fennelly
 */

public class SetProfileTag implements Tag {

    /**
     * Page JSP context
     */
    private PageContext pageContext = null;
    /**
     * Tag parent
     */
    private Tag parent = null;
    /**
     * The profile name.
     */
    private String name = null;
    /**
     * The useragent common name list.  The list of useragent common names which
     * are a member of this profile.
     */
    private String cnList = null;

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
     * @return One of {@link Tag#SKIP_BODY}, {@link Tag#EVAL_BODY_INCLUDE}, {@link Tag#SKIP_PAGE}, {@link Tag#EVAL_PAGE}
     */
    public int doStartTag() throws JspException {
        try {
            if(name != null && cnList != null) {
                StringTokenizer tokenizer = new StringTokenizer(cnList, "|,;");
                Hashtable profiles = getProfiles();

                while(tokenizer.hasMoreTokens()) {
                    String cn = tokenizer.nextToken().trim();
                    Vector cnEntry = (Vector)profiles.get(cn);

                    // Do we have an entry for this useragent?
                    if(cnEntry == null) {
                        // Create it and add it.
                        cnEntry = new Vector();
                        profiles.put(cn, cnEntry);
                    }

                    // Add the profile to this useragents list of profiles.
                    if(!cnEntry.contains(name)) {
                        cnEntry.addElement(name);
                    }
                }
            }
        } catch (Exception ex) {
            JspException throwExcep = new JspException("Unable to process SetProfileTag.");
            throwExcep.initCause(ex);
            throw throwExcep;
        }

        return Tag.SKIP_BODY;
    }

    /**
     * Get the profiles table
     * <p/>
     * The profiles table is a Hashtable keyed by useragent common name.  Each entry
     * in the table is a list of Strings listing the profiles of which the useragent
     * is a member.
     * @return The profiles table.
     */
    private Hashtable getProfiles() {
        ServletContext servletContext = pageContext.getServletContext();
        Hashtable profiles = (Hashtable)servletContext.getAttribute(ServletUAContext.PROFILES_KEY);

        if(profiles == null) {
            profiles = new Hashtable();
            servletContext.setAttribute(ServletUAContext.PROFILES_KEY, profiles);
        }

        return profiles;
    }

    /**
     * Process the doEnd event.
     * @return One of {@link Tag#SKIP_BODY}, {@link Tag#EVAL_BODY_INCLUDE}, {@link Tag#SKIP_PAGE}, {@link Tag#EVAL_PAGE}
     */
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }

    /**
     * Bean method for setting the name attribute ("name") value from the page.
     * @param name The name value.
     */
    public void setName(String name) {
        if(name != null) {
            this.name = name.toLowerCase();
        }
    }

    /**
     * Bean method for setting the list attribute ("list") value from the page.
     * @param cnList The list value.
     */
    public void setList(String cnList) {
        if(cnList != null) {
            this.cnList = cnList.toLowerCase();
        }
    }

    /**
     * Process the release event.
     */
    public void release() {
    }
}
