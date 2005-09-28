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

package org.milyn.delivery.trans;

import java.util.List;
import java.util.Vector;

/**
 * Transformation set.
 * <p/>
 * The set of TransUnit to be applied to an Element.  The set maintains 2 lists,
 * one for the TransUnits whose {@link org.milyn.delivery.trans.TransUnit#visitBefore()} 
 * method returns true and one for those that returned false.
 * <p/>
 * This class is really just a convienient placeholder for the list of TransUnit
 * instances to be applied to an element, pre-broken-out into "before" and "after"
 * lists.
 * @author tfennelly
 */
public class TransSet {
	
	/**
	 * TransUnit instances whose visitBefore method returns true.
	 */
	private Vector visitBeforeTransUnits;
	
	/**
	 * TransUnit instances whose visitBefore method returns false.
	 */
	private Vector visitAfterTransUnits;

	/**
	 * Add to the TransSet.
	 * <p/>
	 * Adds to the "before" list if {@link org.milyn.delivery.trans.TransUnit#visitBefore()}
	 * returns true, otherwise it adds to the "after" list.
	 * @param transUnit The TransUnit to be added.
	 */
	public void addTransUnit(TransUnit transUnit) {
		if(transUnit.visitBefore()) {
			if(visitBeforeTransUnits == null) {
				visitBeforeTransUnits = new Vector();
			}
			visitBeforeTransUnits.add(transUnit);
		} else {
			if(visitAfterTransUnits == null) {
				visitAfterTransUnits = new Vector();
			}
			visitAfterTransUnits.add(transUnit);
		}
	}
	
	/**
	 * Get the list of TransUnit instances to be applied before.
	 * <p/>
	 * See {@link org.milyn.delivery.trans.TransUnit#visitBefore()}.
	 * @return List of TransUnit instances.
	 */
	public List getVisitBeforeTransUnits() {
		return visitBeforeTransUnits;
	}
	
	/**
	 * Get the list of TransUnit instances to be applied after.
	 * <p/>
	 * See {@link org.milyn.delivery.trans.TransUnit#visitBefore()}.
	 * @return List of TransUnit instances.
	 */
	public List getVisitAfterTransUnits() {
		return visitAfterTransUnits;
	}
}
