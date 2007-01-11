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

package org.milyn.device.profile;

import org.milyn.device.ident.UnknownDeviceException;

/**
 * ProfileStore interface.
 * <p/>
 * The ProfileStore stores and provides access to all the ProfileSet instances.
 * A ProfileSet is the set of profiles accociated with a specific device.
 * @author tfennelly
 */
public interface ProfileStore {

	/**
	 * Get the ProfileSet associated with the specified device.
	 * <p/>
	 * Implementations must be case insensitive.
	 * @param deviceName The device name.
	 * @return The ProfileSet for the specified device.
	 * @throws UnknownDeviceException The specified device is unknown.
	 */
	public ProfileSet getProfileSet(String deviceName) throws UnknownDeviceException;
}
