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

import org.milyn.cdr.SmooksResourceConfigurationStore;
import org.milyn.resource.ContainerResourceLocator;

/**
 * Smooks container context interface definition.
 * @author tfennelly
 */
public interface ContainerContext extends BoundAttributeStore {

	/**
	 * Get the container resource locator for the context.
	 * @return ContainerResourceLocator for the context.
	 */
	public abstract ContainerResourceLocator getResourceLocator();
	
	/**
	 * Get the Store for from the container application context.
	 * @return SmooksResourceConfigurationStore instance.
	 */
	public abstract SmooksResourceConfigurationStore getStore();
}
