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

package org.milyn.delivery.serialize;

import org.milyn.cdr.CDRDef;

/**
 * Abstract SerializationUnit implementation.
 * <p/>
 * Really just defines the required public constructor that takes the
 * CDRDef param.
 * @author tfennelly
 */
public abstract class AbstractSerializationUnit implements SerializationUnit {
	
	/**
	 * Public constructor.
	 * @param unitDef Unit CDRDef.
	 */
	public AbstractSerializationUnit(CDRDef unitDef) {
	}
}
