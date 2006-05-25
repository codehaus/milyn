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

package org.milyn.delivery.process;

/**
 * Processing Unit <b>Prototype</b> (GoF) Interface.
 * <p/>
 * ProcessingUnit implementations should be stateless objects.  Statefull ProcessingUnit implementations 
 * should be implemented through the ProcessingUnitPrototype interface.  This provides the
 * Smooks framework with the clone method definition ({@link #cloneProcessingUnit}) needed to create 
 * multiple runtime instances of a statefull ProcessingUnit.
 * <p/>
 * See <a href="../package.html>Delivery Overview</a>.
 * <p/>
 * @see org.milyn.delivery.process.ProcessingUnit
 * @see org.milyn.delivery.ContentDeliveryUnitCreator
 * @author tfennelly
 */
public interface ProcessingUnitPrototype extends ProcessingUnit {

	/**
	 * Clone this statefull ProcessingUnit.
	 * @return ProcessingUnit clone
	 */
	public ProcessingUnit cloneProcessingUnit();
}
