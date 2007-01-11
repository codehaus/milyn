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

/**
 * Device ProfileSet interface.
 * <p/>
 * The ProfileSet is associated with a particular device.  The implementation
 * maintains a list of profiles for the device.
 * @author tfennelly
 */
public interface ProfileSet {

	/**
	 * Is the associated device a member of the specified profile. 
	 * <p/>
	 * Implementations must be case insensitive.
	 * @param profile The profile to check against.
	 * @return True if the associated device a member of the specified 
	 * profile, otherwise false.
	 */
	public boolean isMember(String profile);
}
