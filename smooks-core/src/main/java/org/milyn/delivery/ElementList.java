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

package org.milyn.delivery;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.w3c.dom.Element;

/**
 * ElementList.
 * <p/>
 * An element list is used to store a list of references to elements that
 * colaborate with each other during one of the phases of content delivery. 
 * ElementList instances are accessed via the 
 * {@link org.milyn.container.ExecutionContext#getElementList(String)} method.
 * These lists are cleared at the end of the assembly and processing phases.
 * Therefore, they can't be used to attach element references for use between these
 * delivery phases.
 * @author tfennelly
 */
public class ElementList {

	/**
	 * Element list.  Keyed by element "id", "name" or index in the list. 
	 */
	private LinkedHashMap elementList = new LinkedHashMap();
	
	/**
	 * Add the supplied element to this list.
	 * <p/>
	 * If the element has an "id" or "name" attribute this will be used as
	 * the element key in the list, otherwise the element index will be used.
	 * @param element The element to be added.
	 */
	public void addElement(Element element) {
		if(element == null) {
			throw new IllegalArgumentException("null 'element' arg in method call.");
		}
		
		String keyAttrib = element.getAttribute("id");
		
		if(keyAttrib == null) {
			keyAttrib = element.getAttribute("name");
		}
		if(keyAttrib != null && !(keyAttrib = keyAttrib.trim()).equals("")) {
			elementList.put(keyAttrib, element);
		} else {
			elementList.put(Integer.toString(elementList.size()), element);
		}
	}
	
	/**
	 * Add the supplied element to this list using the specified key.
	 * @param key Key under which to store the element.
	 * @param element The element to be added.
	 */
	public void addElement(Object key, Element element) {
		if(key == null) {
			throw new IllegalArgumentException("null 'key' arg in method call.");
		} else if(element == null) {
			throw new IllegalArgumentException("null 'element' arg in method call.");
		}
		elementList.put(key, element);
	}	
	
	/**
	 * Get the element stored under the specified key.
	 * @param key Key under which the element element is stored.
	 * @return The element stored under the specified key, or null if none 
	 * stored under that key.
	 */
	public Element getElement(Object key) {
		if(key == null) {
			throw new IllegalArgumentException("null 'key' arg in method call.");
		} 
		return (Element)elementList.get(key);
	}

	/**
	 * Get the element stored in this list.
	 * @return The element list as a Collection.
	 */
	public Collection getElements() {
		return elementList.values();
	}
}
