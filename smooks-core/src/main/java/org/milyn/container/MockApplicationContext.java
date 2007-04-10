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

package org.milyn.container;

import java.util.Hashtable;

import org.milyn.cdr.SmooksResourceConfigurationStore;
import org.milyn.resource.ContainerResourceLocator;

/**
 * 
 * @author tfennelly
 */
public class MockApplicationContext implements ApplicationContext {
	public MockContainerResourceLocator containerResourceLocator = new MockContainerResourceLocator();
	private Hashtable attributes = new Hashtable();
	
	/* (non-Javadoc)
	 * @see org.milyn.container.ApplicationContext#getResourceLocator()
	 */
	public ContainerResourceLocator getResourceLocator() {
		return containerResourceLocator;
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.Object, java.lang.Object)
	 */
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.Object)
	 */
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.Object)
	 */
	public void removeAttribute(Object key) {
		attributes.remove(key);
	}

	private static String STORE_KEY = MockApplicationContext.class.getName() + "#CDRStore";
	/* (non-Javadoc)
	 * @see org.milyn.container.ApplicationContext#getCdrarStore()
	 */
	public SmooksResourceConfigurationStore getStore() {
        SmooksResourceConfigurationStore cdrarStore = (SmooksResourceConfigurationStore)getAttribute(STORE_KEY);
		
		if(cdrarStore == null) {
			cdrarStore = new SmooksResourceConfigurationStore(this);
			setAttribute(STORE_KEY, cdrarStore);
		}
		
		return cdrarStore;
	}	
}
