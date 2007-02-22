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

package org.milyn.profile;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Device ProfileSet interface. <p/> The ProfileSet is associated with a
 * particular device. The implementation maintains a list of profiles for the
 * device.
 * 
 * @author tfennelly
 */
public interface ProfileSet extends Serializable {

	/**
	 * Is the associated device a member of the specified profile. <p/>
	 * Implementations must be case insensitive.
	 * 
	 * @param profile
	 *            The profile to check against.
	 * @return True if the associated device a member of the specified profile,
	 *         otherwise false.
	 */
	public boolean isMember(String profile);

	/**
	 * Add a profile to the ProfileSet.
	 * 
	 * @param profile
	 *            The profile to add.
	 */
	public abstract void addProfile(Profile profile);

	/**
	 * Get a profile from the {@link ProfileSet}.
	 * 
	 * @param profile
	 *            The name of the profile.
	 * @return The requested Profile, or null if the profile is not a member of
	 *         the {@link ProfileSet}.
	 */
	public abstract Profile getProfile(String profile);

	/**
	 * Get an {@link Iterator} to allow iteration over the
	 * {@link Profile Profiles}in this {@link ProfileSet}.
	 * 
	 * @return An {@link Iterator} that allows iteration over the
	 *         {@link Profile Profiles}in this {@link ProfileSet}.
	 */
	public abstract Iterator iterator();
}
