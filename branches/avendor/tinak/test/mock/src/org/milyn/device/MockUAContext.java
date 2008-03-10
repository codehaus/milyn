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

package org.milyn.device;

import org.milyn.device.profile.DefaultProfileSet;
import org.milyn.device.profile.ProfileSet;

/**
 * 
 * @author tfennelly
 */
public class MockUAContext implements UAContext {
	
	public String commonName;
	public DefaultProfileSet profileSet;

	public MockUAContext(String commonName) {
		this.commonName = commonName;
		profileSet = new DefaultProfileSet();
	}

	/**
	 * Add profile to the ProfileSet.
	 * @param profile The profile to add.
	 */
	public void addProfile(String profile) {
		profileSet.addProfile(profile);
	}

	/**
	 * Add a list of profiles to the ProfileSet.
	 * @param profiles The array of profiles to add.
	 */
	public void addProfiles(String[] profiles) {		
		for(int i = 0; i < profiles.length; i++) {
			profileSet.addProfile(profiles[i]);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.device.UAContext#getCommonName()
	 */
	public String getCommonName() {
		return commonName;
	}

	/* (non-Javadoc)
	 * @see org.milyn.device.UAContext#getProfileSet()
	 */
	public ProfileSet getProfileSet() {
		return profileSet;
	}
}