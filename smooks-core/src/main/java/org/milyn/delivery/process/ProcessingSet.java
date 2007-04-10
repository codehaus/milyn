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
 * The set of ProcessingUnit to be applied to an Element.
 * @author tfennelly
 */
public class ProcessingSet {

	/**
	 * ProcessingUnit instances.
	 */
	private Vector processingUnits;

	/**
	 * Add to the ProcessingSet.
	 * @param processingUnit The ProcessingUnit to be added.
	 * @param resourceConfig Corresponding resource config.
	 */
	public void addProcessingUnit(ProcessingUnit processingUnit, SmooksResourceConfiguration resourceConfig) {
        ContentDeliveryUnitConfigMap mapInst = 
            new ContentDeliveryUnitConfigMap(processingUnit, resourceConfig);

        if(processingUnits == null) {
            processingUnits = new Vector();
        }
        processingUnits.add(mapInst);
	}
	
	/**
	 * Get the list of ProcessingUnit instances to be applied.
	 * @return List of ProcessingUnit instances.
	 */
	public List getProcessingUnits() {
		return processingUnits;
	}
}
