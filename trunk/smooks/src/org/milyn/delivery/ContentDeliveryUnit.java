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

package org.milyn.delivery;

import org.w3c.dom.Node;

/**
 * Content Delivery Unit interface definition.
 * </p>
 * Base interface for all content delivery components.  See extending interfaces.
 * <p/>
 * All implementations must contain a public constructor that takes a {@link org.milyn.cdr.CDRDef} instance as 
 * a parameter.
 * @author tfennelly
 */
public interface ContentDeliveryUnit {
	
	/**
	 * Is the supplied node in the namespace of the element to which
	 * this CDU is targeted.
	 * @param node Node to be checked.
	 * @return True if the node is in the same namespace, otherwise false.
	 */
	public abstract boolean isInNamespace(Node node);
}