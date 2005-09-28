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

package org.milyn.container.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.milyn.cdr.CDRStore;
import org.milyn.container.ContainerContext;
import org.milyn.resource.ContainerResourceLocator;
import org.milyn.resource.ServletResourceLocator;
import org.milyn.resource.URLResourceLocator;

/**
 * ContainerContext adapter for the javax.servlet.ServletContext interface.
 * @author tfennelly
 */
public class ServletContainerContext implements ContainerContext {

	/**
     * Context stream resource locator for the Servlet environment.
     */
    private ServletResourceLocator resourceLocator;
    /**
     * ServletContext instance for requested application resource.
     */
    private ServletContext servletContext;
	/**
	 * CDRStore instance for the context.
	 */
	private CDRStore cdrarStore;
    
	/**
	 * Public constructor.
	 * @param servletContext ServletContext instance.
	 * @param servletConfig ServletConfig instance.
	 */
	public ServletContainerContext(ServletContext servletContext, ServletConfig servletConfig) {
		if(servletContext == null) {
			throw new IllegalArgumentException("null 'servletContext' arg in constructor call.");
		}
		if(servletConfig == null) {
			throw new IllegalArgumentException("null 'servletConfig' arg in constructor call.");
		}
		this.servletContext = servletContext;
		resourceLocator = new ServletResourceLocator(servletConfig, new URLResourceLocator());		
		cdrarStore = new CDRStore(this);
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerContext#getResourceLocator()
	 */
	public ContainerResourceLocator getResourceLocator() {
		return resourceLocator;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
		servletContext.setAttribute(name, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return servletContext.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		servletContext.removeAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerContext#getCdrarStore()
	 */
	public CDRStore getCdrarStore() {
		return cdrarStore;
	}

	/**
	 * Get the associated ServletContext instance.
	 * @return ServletContext for this ServletContainerContext.
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}
}
