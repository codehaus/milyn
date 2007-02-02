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

package org.milyn.delivery.assemble;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.AbstractContentDeliveryUnit;

/**
 * Abstract AssemblyUnit implementation.
 * <p/>
 * See {@link org.milyn.delivery.assemble.AssemblyUnit} docs.
 * @author tfennelly
 */
public abstract class AbstractAssemblyUnit extends AbstractContentDeliveryUnit implements AssemblyUnit {
	
	/**
	 * Public constructor.
	 * @param resourceConfig Unit SmooksResourceConfiguration.
	 */
	public AbstractAssemblyUnit(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
	}

	/**
	 * Set the resource configuration for the delivery unit.
	 */
	public void setConfiguration(SmooksResourceConfiguration resourceConfig) {
		// Empty implementation.
		// Extenders of this class receive their configuration via constructor.
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
