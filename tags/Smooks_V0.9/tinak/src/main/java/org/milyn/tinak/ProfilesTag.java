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

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;

/**
 * This tag nests set-profile tags.
 * <p/>
 * The profiles are added to the servlet context.  This tag stops the set-profile
 * tags from being processed 1+ times.
 * @author Tom Fennelly.
 */

public class ProfilesTag extends ContainerTag {
	
    /**
     * Profiles context reference.  Not actually used locally other than to save
     * a context lookup.
     */
    private Object profiles = null;

    /**
     * Process the doStart event.
     * @return One of {@link Tag#SKIP_BODY} or {@link BodyTag#EVAL_BODY_BUFFERED}
     */
    public int doStartTag() throws JspException {
        // The SetProfilesTag will create and add the profiles to the servlet context
        // under this key.
        if(profiles == null) {
            profiles = getPageContext().getServletContext().getAttribute(ServletUAContext.PROFILES_KEY);
            if(profiles == null) {
				return BodyTag.EVAL_BODY_BUFFERED;
            }
        }

        return Tag.SKIP_BODY;
    }
}

