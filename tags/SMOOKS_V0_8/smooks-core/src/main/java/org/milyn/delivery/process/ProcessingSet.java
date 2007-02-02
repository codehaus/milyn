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

import java.util.List;
import java.util.Vector;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;

/**
 * Processing set.
 * <p/>
 * The set of ProcessingUnit to be applied to an Element.  The set maintains 2 lists,
 * one for the ProcessingUnits whose {@link org.milyn.delivery.process.ProcessingUnit#visitBefore()} 
 * method returns true and one for those that returned false.
 * <p/>
 * This class is really just a convienient placeholder for the list of ProcessingUnit
 * instances to be applied to an element, pre-broken-out into "before" and "after"
 * lists.
 * @author tfennelly
 */
public class ProcessingSet {
	
	/**
	 * ProcessingUnit instances whose visitBefore method returns true.
	 */
	private Vector visitBeforeProcessingUnits;
	
	/**
	 * ProcessingUnit instances whose visitBefore method returns false.
	 */
	private Vector visitAfterProcessingUnits;

	/**
	 * Add to the ProcessingSet.
	 * <p/>
	 * Adds to the "before" list if {@link org.milyn.delivery.process.ProcessingUnit#visitBefore()}
	 * returns true, otherwise it adds to the "after" list.
	 * @param processingUnit The ProcessingUnit to be added.
	 * @param resourceConfig Corresponding resource config.
	 */
	public void addProcessingUnit(ProcessingUnit processingUnit, SmooksResourceConfiguration resourceConfig) {
        ContentDeliveryUnitConfigMap mapInst = 
            new ContentDeliveryUnitConfigMap(processingUnit, resourceConfig);

        if(processingUnit.visitBefore()) {
			if(visitBeforeProcessingUnits == null) {
				visitBeforeProcessingUnits = new Vector();
			}
			visitBeforeProcessingUnits.add(mapInst);
		} else {
			if(visitAfterProcessingUnits == null) {
				visitAfterProcessingUnits = new Vector();
			}
			visitAfterProcessingUnits.add(mapInst);
		}
	}
	
	/**
	 * Get the list of ProcessingUnit instances to be applied before.
	 * <p/>
	 * See {@link org.milyn.delivery.process.ProcessingUnit#visitBefore()}.
	 * @return List of ProcessingUnit instances.
	 */
	public List getVisitBeforeProcessingUnits() {
		return visitBeforeProcessingUnits;
	}
	
	/**
	 * Get the list of ProcessingUnit instances to be applied after.
	 * <p/>
	 * See {@link org.milyn.delivery.process.ProcessingUnit#visitBefore()}.
	 * @return List of ProcessingUnit instances.
	 */
	public List getVisitAfterProcessingUnits() {
		return visitAfterProcessingUnits;
	}
}
