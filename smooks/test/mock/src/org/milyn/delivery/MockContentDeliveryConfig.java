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

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.milyn.delivery.http.HeaderAction;
import org.milyn.delivery.trans.TransSet;
import org.milyn.dtd.DTDStore.DTDObjectContainer;

/**
 * 
 * @author tfennelly
 */
public class MockContentDeliveryConfig implements ContentDeliveryConfig {

	public Hashtable transSets = new Hashtable();
	public Hashtable unitDefs = new Hashtable();
	public Hashtable assemblyUnits = new Hashtable();
	public Hashtable serializationUnits = new Hashtable();
	public Hashtable objectsHash = new Hashtable();
	
	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getTransSet(java.lang.String)
	 */
	public TransSet getTransSet(String tag) {
		return (TransSet)transSets.get(tag);
	}

	public List getCDRDefs(String nodeDef) {
		return (List)unitDefs.get(nodeDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getSerailizationUnits()
	 */
	public Hashtable getSerailizationUnits() {
		return serializationUnits;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getObjects(java.lang.String)
	 */
	public List getObjects(String selector) {
		return (List)objectsHash.get(selector);
	}

	public void addObject(String selector, Object object) {
		List objects = (List)objectsHash.get(selector);
		
		if(objects == null) {
			objects = new Vector();
			objectsHash.put(selector, objects);
		}
		objects.add(object);
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getAssemblyUnits()
	 */
	public Hashtable getAssemblyUnits() {
		return assemblyUnits;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getDTD()
	 */
	public DTDObjectContainer getDTD() {
		// TODO Auto-generated method stub
		return null;
	}

}
