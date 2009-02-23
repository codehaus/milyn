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

package org.milyn.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for locating stream resources from the container..
 * 
 * @author tfennelly
 */
public interface ContainerResourceLocator extends ExternalResourceLocator {

	/**
	 * Get the resource specified by the container 'config' value. <p/> If the
	 * config value isn't specified, uses the defaultLocation.
	 * 
	 * @param configName
	 *            The container configuration entry name whose value specifies
	 *            the location of the resource.
	 * @param defaultUri
	 *            The default location for the resource.
	 * @return The InputStream associated with resource.
	 * @throws IllegalArgumentException
	 *             Illegal argument. Check the cause exception for more
	 *             information.
	 * @throws IOException
	 *             Unable to get the resource stream.
	 */
	public InputStream getResource(String configName, String defaultUri)
			throws IllegalArgumentException, IOException;
}
