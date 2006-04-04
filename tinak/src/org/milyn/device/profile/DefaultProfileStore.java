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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.milyn.device.ident.UnknownDeviceException;

/**
 * Default ProfileStore implementation.
 * @author tfennelly
 */
public class DefaultProfileStore implements ProfileStore {
	
	/**
	 * The store table.
	 */
	private Hashtable store = new Hashtable(); 
	
	/* (non-Javadoc)
	 * @see org.milyn.device.profile.ProfileStore#getProfileSet(java.lang.String)
	 */
	public ProfileSet getProfileSet(String deviceName) throws UnknownDeviceException {
		ProfileSet set = null;
		
		assertDeviceNameOK(deviceName);
		
		set = (ProfileSet)store.get(deviceName.trim().toLowerCase());
		if(set == null) {
			throw new UnknownDeviceException("Failed to get device ProfileSet.  Unknown device name [" + deviceName + "]");
		}
		
		return set;
	}
	
	/**
	 * Add a ProfileSet for the named device to this ProfileStore
	 * @param deviceName The device name.
	 * @param profileSet The devices ProfileSet.
	 */
	public void addProfileSet(String deviceName, ProfileSet profileSet) {
		assertDeviceNameOK(deviceName);
		if(profileSet == null) {
			throw new IllegalArgumentException("null 'profileSet' arg in method call.");
		}
		if(!(profileSet instanceof DefaultProfileSet)) {
			throw new IllegalArgumentException("'profileSet' arg must be an instanceof DefaultProfileSet.");
		}
		
		store.put(deviceName.trim().toLowerCase(), profileSet);
	}
		
	protected void expandProfiles() {
		Iterator storeIterator = store.entrySet().iterator();
		
		while(storeIterator.hasNext()) {
			Map.Entry entry = (Map.Entry)storeIterator.next();
			DefaultProfileSet profileSet = (DefaultProfileSet)entry.getValue();
			Iterator iterator = profileSet.values().iterator();
			Vector addOns = new Vector();
			
			while(iterator.hasNext()) {
				Profile profile = (Profile)iterator.next();
				
				try {
					DefaultProfileSet deviceProfileSet = (DefaultProfileSet)getProfileSet(profile.getName());
					if(deviceProfileSet != null) {
						addOns.add(deviceProfileSet);
					}
				} catch (UnknownDeviceException e) {
					// Ignore - not an expandable profile
				}				
			}
			// perfomed after the above iteration simply to avoid concurrent mod exceptions
			// on the ProfileSet.
			for(int i = 0; i < addOns.size(); i++) {
				profileSet.addProfileSet((DefaultProfileSet)addOns.get(i));
			}
		}
	}
	
	/**
	 * Device name String assertion method.
	 * @param deviceName The device name String.
	 * @throws IllegalArgumentException If the deviceName is null or empty.
	 */
	private void assertDeviceNameOK(String deviceName) throws IllegalArgumentException {
		if(deviceName == null) {
			throw new IllegalArgumentException("null 'deviceName' arg in method call.");
		} else if(deviceName.trim().equals("")) {
			throw new IllegalArgumentException("empty 'deviceName' arg in method call.");
		}
	}	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer storeDescription = new StringBuffer();
		Iterator iterator = store.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String deviceName = (String)entry.getKey();
			ProfileSet profileSet = (ProfileSet)entry.getValue();
			
			storeDescription.append(deviceName).append(": ").append(profileSet).append("\r\n");
		}
		
		return storeDescription.toString();
	}
	/**
	 * Unit testing static inner to provide access. 
	 * @author tfennelly
	 */
	static class UnitTest {
		public static void addProfileSet(DefaultProfileStore store, String deviceName, ProfileSet profileSet) {
			store.addProfileSet(deviceName, profileSet);
		}
	}
}
