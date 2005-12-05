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

import org.milyn.cdr.CDRStore;
import org.milyn.cdr.CDRDef;


/**
 * ContentDeliveryUnit <b>Factory Method</b> (GoF) Creator interface.
 * <p/>
 * Implementations of this interface are responsible for creating ContentDeliveryUnit instances. 
 * This creation mechanism follows the Gang of Four "Factory Method" design pattern:
 * <ul>
 * 		<li><b>Creator</b>: {@link ContentDeliveryUnitCreator}</li>
 * 		<li><b>ConcreteCreator</b>: e.g. {@link org.milyn.delivery.JavaContentDeliveryUnitCreator} 
 * 				or {@link org.milyn.delivery.XslContentDeliveryUnitCreator}</li>
 * 		<li><b>Product</b>: {@link ContentDeliveryUnit}</li>
 * 		<li><b>ConcreteProduct</b>: Implementations of {@link ContentDeliveryUnit}</li>
 * </ul> 
 * The <b>ConcreteCreator</b> implemtations are loaded using the {@link org.milyn.ioc.BeanFactory#getContentDeliveryUnitCreator(String)} method. 
 * @see org.milyn.delivery.trans.TransUnit
 * @see org.milyn.delivery.trans.TransUnitPrototype
 * @author tfennelly
 */
public interface ContentDeliveryUnitCreator {
	
	/**
	 * Create the {@link ContentDeliveryUnit} instance. 
	 * @param cdrDef The CDRDef for the {@link ContentDeliveryUnit}.
	 * @param cdrarStore The CDRStore instance storing the specified unitDef instance.
	 * @return {@link ContentDeliveryUnit} instance.
	 * @throws InstantiationException Unable to create ContentDeliveryUnit instance.
	 */
	public ContentDeliveryUnit create(CDRDef cdrDef, CDRStore cdrarStore) throws InstantiationException;
}
