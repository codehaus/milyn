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

package org.milyn.cdr.cdrar;

import java.util.jar.JarEntry;

/**
 * Content Delivery Resource Archive entry.
 * <p/>
 * This class stores the cdrar JarEntry plus the entry data associated with a 
 * JarEntry.
 * @author tfennelly
 */
public final class CDRArchiveEntry {
	
	/**
	 * The JarEntry associated with the cdrar entry.
	 */
	private JarEntry jarEntry = null;
	/**
	 * The data bytes associated with the entry.
	 */
	private byte[] entryBytes = null;

	/**
	 * Public constructor. 
	 * @param jarEntry The JarEntry associated with the cdrar entry.
	 * @param entryBytes The data bytes associated with the entry.
	 */
	public CDRArchiveEntry(JarEntry jarEntry, byte[] entryBytes) {
		if(jarEntry == null) {
			throw new IllegalArgumentException("null 'jarEntry' arg in constructor call.");
		}
		if(entryBytes == null) {
			throw new IllegalArgumentException("null 'entryBytes' arg in constructor call.");
		}
		this.jarEntry = jarEntry;
		this.entryBytes = entryBytes;
	}

	/**
	 * Public constructor. 
	 * @param name The name to be associated with the cdrar entry.
	 * @param entryBytes The data bytes associated with the entry.
	 */
	public CDRArchiveEntry(String name, byte[] entryBytes) {
		if(name == null) {
			throw new IllegalArgumentException("null 'name' arg in constructor call.");
		}
		if(entryBytes == null) {
			throw new IllegalArgumentException("null 'entryBytes' arg in constructor call.");
		}
		this.jarEntry = new JarEntry(name);
		this.entryBytes = entryBytes;
	}
	
	/**
	 * Get the JarEntry name of this CDRArchiveEntry.
	 * @return The JarEntry name of of this CDRArchiveEntry.
	 */
	public String getName() {
		return jarEntry.getName();
	}

	/**
	 * Get the JarEntry associated with the cdrar entry.
	 * @return The JarEntry associated with the cdrar entry.
	 */
	public JarEntry getJarEntry() {
		return jarEntry;
	}

	/**
	 * Get the data bytes associated with the entry.
	 * @return entryBytes The data bytes associated with the entry.
	 */
	public byte[] getEntryBytes() {
		return entryBytes;
	}
}
