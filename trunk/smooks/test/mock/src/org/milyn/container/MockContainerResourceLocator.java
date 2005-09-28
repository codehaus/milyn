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

package org.milyn.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.milyn.resource.ContainerResourceLocator;


public class MockContainerResourceLocator implements ContainerResourceLocator {
	private Hashtable streams = new Hashtable();
	
	public void setResource(String nameOrUri, InputStream stream) {
		streams.put(nameOrUri, stream);
	}
	
	public InputStream getResource(String configName, String defaultUri) throws IllegalArgumentException, IOException {
		InputStream res = (InputStream)streams.remove(configName);
		
		if(res == null) {
			throw new IllegalStateException("Resource [" + configName + "," + defaultUri + "] not set in MockContainerResourceLocator.  Use MockContainerResourceLocator.setResource()");
		}
		
		return res;
	}
	public InputStream getResource(String uri) throws IllegalArgumentException, IOException {
		InputStream res = (InputStream)streams.remove(uri);
		
		if(res == null) {
			throw new IllegalStateException("Resource [" + uri + "] not set in MockContainerResourceLocator.  Use MockContainerResourceLocator.setResource()");
		}
		
		return res;
	}
}