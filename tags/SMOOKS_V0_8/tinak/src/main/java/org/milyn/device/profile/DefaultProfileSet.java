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

package org.milyn.device.profile;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Default ProfileSet implementation.
 * @author tfennelly
 */
public class DefaultProfileSet extends LinkedHashMap implements ProfileSet {

    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
	 * @see org.milyn.device.profile.ProfileSet#isMember(java.lang.String)
	 */
	public boolean isMember(String profile) {
		if(profile == null) {
			throw new IllegalArgumentException("null 'profile' arg in method call.");
		}

		return containsKey(profile.trim().toLowerCase());
	}

	/**
	 * Add profile to the ProfileSet.
	 * @param profile The profile to add.
	 */
	public void addProfile(String profile) {
		addProfile(new BasicProfile(profile.toLowerCase()));
	}

	/**
	 * Add profile to the ProfileSet.
	 * @param profile The profile to add.
	 */
	public void addProfile(Profile profile) {
		if(profile == null) {
			throw new IllegalArgumentException("null 'profile' arg in method call.");
		}

		put(profile.getName().toLowerCase(), profile);
	}	

	/**
	 * Get a profile from the {@link ProfileSet}.
	 * @param profile The name of the profile.
	 * @return The requested Profile, or null if the profile is not a
	 * member of the {@link ProfileSet}.
	 */
	public Profile getProfile(String profile) {
		return (Profile)get(profile);
	}

	/**
	 * Get an {@link Iterator} to allow iteration over the 
	 * {@link Profile Profiles}in this {@link ProfileSet}.
	 * @return An {@link Iterator} that allows iteration over the 
	 * {@link Profile Profiles}in this {@link ProfileSet}.
	 */
	public Iterator iterator() {
		return values().iterator();
	}
	
	/**
	 * Add the profiles of the supplied DefaultProfileSet to this ProfileSet.
	 * @param profileSet The DefaultProfileSet whose profiles are to be added.
	 */
	protected void addProfileSet(DefaultProfileSet profileSet) {
		if(profileSet == null) {
			throw new IllegalArgumentException("null 'profileSet' arg in method call.");
		}
		
		putAll(profileSet);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer setDescription = new StringBuffer();
		Iterator iterator = keySet().iterator();
		
		while(iterator.hasNext()) {
			String profile = (String)iterator.next();
			
			setDescription.append(profile);
			if(iterator.hasNext()) {
				setDescription.append(",");
			}
		}
		
		return setDescription.toString();
	}
}
