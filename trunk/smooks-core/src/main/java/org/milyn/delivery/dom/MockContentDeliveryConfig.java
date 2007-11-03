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
import java.util.Vector;

import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.cdr.SmooksResourceConfiguration;

/**
 * Mock ContentDeliveryConfig for DOM. 
 * @author tfennelly
 */
public class MockContentDeliveryConfig extends DOMContentDeliveryConfig {

    private Hashtable<String, List<SmooksResourceConfiguration>> resourceConfigTable = new Hashtable<String, List<SmooksResourceConfiguration>>();
	public ContentHandlerConfigMapTable assemblyUnits = new ContentHandlerConfigMapTable();
	public ContentHandlerConfigMapTable processingSets = new ContentHandlerConfigMapTable();
    public ContentHandlerConfigMapTable serializationUnits = new ContentHandlerConfigMapTable();
	public Hashtable objectsHash = new Hashtable();

    public MockContentDeliveryConfig() {
        setSmooksResourceConfigurations(resourceConfigTable);
        setAssemblyUnits(assemblyUnits);
        setProcessingUnits(processingSets);
        setSerailizationUnits(serializationUnits);
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
}
