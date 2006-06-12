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

package org.milyn.servlet.container;

import javax.servlet.http.HttpSession;

import org.milyn.container.ContainerSession;

/**
 * ContainerSession adapter for the javax.servlet.http.HttpSession interface.
 * @author tfennelly
 */
public class HttpServletContainerSession implements ContainerSession {

	/**
	 * Session instance.
	 */
	private HttpSession httpSession;

	/**
	 * Public constructor.
	 * @param httpSession HttpSession instance.
	 */
	public HttpServletContainerSession(HttpSession httpSession) {
		if(httpSession == null) {
			throw new IllegalArgumentException("null 'httpSession' arg in constructor call.");
		}
		this.httpSession = httpSession;
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
		httpSession.setAttribute(name, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return httpSession.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		httpSession.removeAttribute(name);
	}

	/**
	 * Get the HttpSession instance associated with this ContainerSession 
	 * implementation.
	 * @return HttpSession instance.
	 */
	public HttpSession getHttpSession() {
		return httpSession;
	}
}
