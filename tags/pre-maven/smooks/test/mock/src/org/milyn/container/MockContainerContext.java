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

package org.milyn.container;

import java.util.Hashtable;

import org.milyn.cdr.CDRStore;
import org.milyn.resource.ContainerResourceLocator;

/**
 * 
 * @author tfennelly
 */
public class MockContainerContext implements ContainerContext {
	public MockContainerResourceLocator containerResourceLocator = new MockContainerResourceLocator();
	private Hashtable attributes = new Hashtable();
	
	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerContext#getResourceLocator()
	 */
	public ContainerResourceLocator getResourceLocator() {
		return containerResourceLocator;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	private static String CDRAR_STORE_KEY = MockContainerContext.class.getName() + "#CDRStore";
	/* (non-Javadoc)
	 * @see org.milyn.container.ContainerContext#getCdrarStore()
	 */
	public CDRStore getCdrarStore() {
		CDRStore cdrarStore = (CDRStore)getAttribute(CDRAR_STORE_KEY);
		
		if(cdrarStore == null) {
			cdrarStore = new CDRStore(this);
			setAttribute(CDRAR_STORE_KEY, cdrarStore);
		}
		
		return cdrarStore;
	}	
}