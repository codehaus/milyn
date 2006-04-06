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

import org.milyn.cdr.CDRDef;
import org.w3c.dom.Node;

/**
 * Abstract ContentDeliveryUnit implementation.
 * @author tfennelly
 */
public abstract class AbstractContentDeliveryUnit implements ContentDeliveryUnit {

	private String namespace;
	
	/**
	 * Public constructor.
	 * @param cdrDef Unit configuration.
	 */
	public AbstractContentDeliveryUnit(CDRDef cdrDef) {
		if(cdrDef == null) {
			throw new IllegalArgumentException("null 'cdrDef' arg in constructor call.");
		}
		namespace = cdrDef.getNamespaceURI();
		
		// A config namespace value of "*" means that this config
		// "matches all namespaces".  This is quivalent to no setting
		// the namespace value - hence we reset it to null.  This is 
		// required to support overriding of default namespacing.
		if("*".equals(namespace)) {
			namespace = null;
		}
	}
	
	/**
	 * Is the supplied node in the namespace of the element to which
	 * this CDU is targeted.
	 * @param node Node to be checked.
	 * @return True if the node is in the same namespace, otherwise false.
	 */
	public boolean isInNamespace(Node node) {
		if(namespace != null) {
			return (namespace.equals(node.getNamespaceURI()));
		}
		// Also see the constructor of this class re treatment of
		// namespace value of "*".
		
		return true;
	}
}
