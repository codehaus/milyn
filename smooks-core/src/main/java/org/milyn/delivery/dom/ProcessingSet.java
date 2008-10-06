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

package org.milyn.delivery.dom;

import java.util.List;
import java.util.ArrayList;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.dom.DOMElementVisitor;

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
	private List<ContentDeliveryUnitConfigMap> processingUnits = new ArrayList<ContentDeliveryUnitConfigMap>();

	/**
	 * Add to the ProcessingSet.
	 * @param processingUnit The Processing Unit to be added.
	 * @param resourceConfig Corresponding resource config.
	 */
	public void addProcessingUnit(DOMElementVisitor processingUnit, SmooksResourceConfiguration resourceConfig) {
        ContentDeliveryUnitConfigMap mapInst = 
            new ContentDeliveryUnitConfigMap(processingUnit, resourceConfig);

        processingUnits.add(mapInst);
	}
	
	/**
	 * Get the list of ProcessingUnit instances to be applied.
	 * @return List of ProcessingUnit instances.
	 */
	public List<ContentDeliveryUnitConfigMap> getProcessingUnits() {
		return processingUnits;
	}
}
