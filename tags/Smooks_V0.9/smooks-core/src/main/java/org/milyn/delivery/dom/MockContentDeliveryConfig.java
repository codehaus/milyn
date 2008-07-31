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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.milyn.delivery.dom.ProcessingSet;
import org.milyn.delivery.ContentDeliveryUnitConfigMap;
import org.milyn.delivery.ContentDeliveryUnitConfigMapTable;
import org.milyn.dtd.DTDStore.DTDObjectContainer;

/**
 * Mock ContentDeliveryConfig for DOM. 
 * @author tfennelly
 */
public class MockContentDeliveryConfig implements DOMContentDeliveryConfig {

	public Map<String, List<ContentDeliveryUnitConfigMap>> resourceConfigs = new Hashtable<String, List<ContentDeliveryUnitConfigMap>>();
	public ContentDeliveryUnitConfigMapTable assemblyUnits = new ContentDeliveryUnitConfigMapTable();
	public ContentDeliveryUnitConfigMapTable processingSets = new ContentDeliveryUnitConfigMapTable();
    public ContentDeliveryUnitConfigMapTable serializationUnits = new ContentDeliveryUnitConfigMapTable();
	public Hashtable objectsHash = new Hashtable();
	

    public ContentDeliveryUnitConfigMapTable getAssemblyUnits() {
        return assemblyUnits;
    }

	public ContentDeliveryUnitConfigMapTable getProcessingUnits() {
		return processingSets;
	}

    public ContentDeliveryUnitConfigMapTable getSerailizationUnits() {
        return serializationUnits;
    }

	public List<ContentDeliveryUnitConfigMap> getSmooksResourceConfigurations(String nodeDef) {
		return resourceConfigs.get(nodeDef);
	}
	
	public Map<String, List<ContentDeliveryUnitConfigMap>> getSmooksResourceConfigurations() {
		return resourceConfigs;
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
	 * @see org.milyn.delivery.ContentDeliveryConfig#getDTD()
	 */
	public DTDObjectContainer getDTD() {
		// TODO Auto-generated method stub
		return null;
	}

}