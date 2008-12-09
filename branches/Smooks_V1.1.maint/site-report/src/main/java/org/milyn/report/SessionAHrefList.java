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

package org.milyn.report;

import java.util.List;
import java.util.Vector;

import org.milyn.container.ContainerSession;

/**
 * Session AHrefList.
 * <p/>
 * Used to store a list of a-href URLs (anchors) encountered over the 
 * scope of a test session.  Used to perform deep test runs.
 * @author tfennelly
 */
public abstract class SessionAHrefList {

	private static String REQUEST_AHREFLIST_KEY = SessionAHrefList.class.getName() + "#REQUEST_AHREFLIST_KEY";

	/**
	 * Private Constructor.
	 */
	private SessionAHrefList() {		
	}

	/**
	 * Get the list of anchors (as {@link java.net.URI}) for the supplied request.
	 * @param containerSession Container session.
	 * @return The SessionAHrefList instance for the supplied session.
	 */
	public static List getList(ContainerSession containerSession) {
		List ahrefList = null;
		
		if(containerSession == null) {
			throw new IllegalArgumentException("null 'containerSession' arg in method call.");
		}
		
		ahrefList = (List)containerSession.getAttribute(REQUEST_AHREFLIST_KEY);
		if(ahrefList == null) {
			ahrefList = new Vector();
			containerSession.setAttribute(REQUEST_AHREFLIST_KEY, ahrefList);
		}
		
		return ahrefList;
	}
}
