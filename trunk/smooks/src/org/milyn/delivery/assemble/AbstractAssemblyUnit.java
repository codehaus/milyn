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

package org.milyn.delivery.assemble;

import org.milyn.cdr.CDRDef;

/**
 * Abstract AssemblyUnit implementation.
 * @author tfennelly
 */
public abstract class AbstractAssemblyUnit implements AssemblyUnit {
	
	/**
	 * Public constructor.
	 * @param cdrDef Unit CDRDef.
	 */
	public AbstractAssemblyUnit(CDRDef cdrDef) {
	}

	/**
	 * By default we visit Assembly Units before Smooks
	 * iterates over the elements child content.
	 * @return True.  Override to change this default behaviour. 
	 */
	public boolean visitBefore() {
		return true;
	}
}
