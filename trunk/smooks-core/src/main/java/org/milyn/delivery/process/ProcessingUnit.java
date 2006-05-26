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

package org.milyn.delivery.process;

import org.milyn.delivery.ElementVisitor;

/**
 * Processing unit interface.
 * <p/>
 * Implementations of this interface are applied to the content during the
 * <a href="../SmooksXML.html#phases">Processing phase</a>.
 * <p/>
 * ProcessingUnit implementations should contain analysis or transformation logic to perform a
 * discreet operation on the element being visited.  They should try not perform 
 * to much within the scope of a single ProcessingUnit.  
 * <p/>
 * Multiple ProcessingUnit implementations can be applied to a single element.  The order in which 
 * they are applied is determined by the {@link org.milyn.cdr.SmooksResourceConfiguration} specificity. 
 * See {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}.
 * <p/>
 * ProcessingUnit implementations should be stateless objects.  A single instance is created 
 * and used to visit all relevant Document objects.  Statefull ProcessingUnit implementations 
 * should be implemented through the ProcessingUnitPrototype interface.
 * <p/>
 * All implementations must contain a public constructor that takes a {@link org.milyn.cdr.SmooksResourceConfiguration} instance as 
 * a parameter.  For this reason consider extending {@link org.milyn.delivery.process.AbstractProcessingUnit} for
 * convienience (your IDE should auto-add the constructor).
 * <p/>
 * See <a href="../package-summary.html">Delivery Overview</a>.
 * <p/>
 * @see org.milyn.delivery.process.ProcessingUnitPrototype
 * @see org.milyn.delivery.ContentDeliveryUnitCreator
 * @author tfennelly
 */
public interface ProcessingUnit extends ElementVisitor {
}