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

import org.milyn.cdr.SmooksResourceConfiguration;

/**
 * Abstract ContentDeliveryUnit implementation.
 * @author tfennelly
 */
public abstract class AbstractContentDeliveryUnit implements ContentDeliveryUnit {

	/**
	 * Public constructor.
	 * @param resourceConfig Unit configuration.
	 */
	public AbstractContentDeliveryUnit(SmooksResourceConfiguration resourceConfig) {
		if(resourceConfig == null) {
			throw new IllegalArgumentException("null 'resourceConfig' arg in constructor call.");
		}
	}
}
