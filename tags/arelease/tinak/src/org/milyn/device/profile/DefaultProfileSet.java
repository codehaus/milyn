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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

/**
 * Default ProfileSet implementation.
 * @author tfennelly
 */
public class DefaultProfileSet extends LinkedHashSet implements ProfileSet {
	
	/**
	 * Indexable list of profiles
	 */
	private Vector indexableLookup = new Vector();

	/* (non-Javadoc)
	 * @see org.milyn.device.profile.ProfileSet#isMember(java.lang.String)
	 */
	public boolean isMember(String profile) {
		assertProfileOK(profile);

		return contains(profile.trim().toLowerCase());
	}

	/**
	 * Add profile to the ProfileSet.
	 * @param profile The profile to add.
	 */
	public void addProfile(String profile) {
		assertProfileOK(profile);

		add(profile.trim().toLowerCase());
	}	
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		if(super.add(o)) {
			return indexableLookup.add(o);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		if(super.remove(o)) {
			return indexableLookup.remove(o);
		}
		return false;
	}
	
	/**
	 * Get the profile at the specified index.
	 * @param index Index of profile to be returned.
	 * @return Profile name to be returned.
	 */
	protected String getProfile(int index) {
		return (String)indexableLookup.get(index);
	}
	
	/**
	 * Add the profiles of the supplied DefaultProfileSet to this ProfileSet.
	 * @param profileSet The DefaultProfileSet whose profiles are to be added.
	 */
	protected void addProfileSet(DefaultProfileSet profileSet) {
		if(profileSet == null) {
			throw new IllegalArgumentException("null 'profileSet' arg in method call.");
		}
		
		addAll(profileSet);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer setDescription = new StringBuffer();
		Iterator iterator = iterator();
		
		while(iterator.hasNext()) {
			String profile = (String)iterator.next();
			
			setDescription.append(profile);
			if(iterator.hasNext()) {
				setDescription.append(",");
			}
		}
		
		return setDescription.toString();
	}
	
	/**
	 * Profile String assertion method.
	 * @param profile The profile String.
	 * @throws IllegalArgumentException If the profile is null or empty.
	 */
	private void assertProfileOK(String profile) throws IllegalArgumentException {
		if(profile == null) {
			throw new IllegalArgumentException("null 'profile' arg in method call.");
		} else if(profile.trim().equals("")) {
			throw new IllegalArgumentException("empty 'profile' arg in method call.");
		}
	}
}
