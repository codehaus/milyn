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

package org.milyn.delivery.serialize;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.AbstractContentDeliveryUnit;

/**
 * Abstract SerializationUnit implementation.
 * <p/>
 * Really just defines the required public constructor that takes the
 * SmooksResourceConfiguration param.
 * @author tfennelly
 */
public abstract class AbstractSerializationUnit extends AbstractContentDeliveryUnit implements SerializationUnit {
	
	/**
	 * Public constructor.
	 * @param resourceConfig Unit SmooksResourceConfiguration.
	 */
	public AbstractSerializationUnit(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
	}
}
