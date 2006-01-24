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
 * This interface defines 2 methods for accessing descriptive information on the 
 * content delivery unit:
 * <ul>
 * 		<li>{@link #getShortDescription()}</li>
 * 		<li>{@link #getDetailDescription()()}</li>
 * </ul>
 * All implementations must contain a public constructor that takes a {@link org.milyn.cdr.CDRDef} instance as 
 * a parameter.
 * @author tfennelly
 */
public interface ContentDeliveryUnit {
	/**
	 * Get a short description of the Content Delivery Unit and what it does.
	 * <p/>
	 * This description will be used as the short description text in UI tools.
	 * <br/>
	 * Example: "<i>Unclose EMPTY Element</i>" 
	 * <p/>
	 * <b>Note:</b> Implementations of this method should declare this 
	 * string statically.
	 * @return Short description String.
	 * @see #getDetailDescription()
	 */
	public abstract String getShortDescription();

	/**
	 * Get a detail (more verbose) description of the Content Delivery Unit and 
	 * what it does.
	 * <p/>
	 * This description will be used as the detail description text in UI tools.
	 * <br/>
	 * Example: "<i>Replace element with an unclosed (badly formed!) equivalent if the 
	 * element contains no content i.e. is empty.  This is required by some older
	 * useragents.</i>" 
	 * <p/>
	 * <b>Note:</b> Implementations of this method should declare this 
	 * string statically.
	 * @return Detail description String.
	 * @see #getShortDescription()
	 */
	public abstract String getDetailDescription();
	
	/**
	 * Is the supplied node in the namespace of the element to which
	 * this CDU is targeted.
	 * @param node Node to be checked.
	 * @return True if the node is in the same namespace, otherwise false.
	 */
	public abstract boolean isInNamespace(Node node);
}