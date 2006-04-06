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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.milyn.cdr.*;
import org.milyn.device.UAContext;
import org.milyn.device.UAContextUtil;
import org.milyn.xml.XmlUtil;
import org.xml.sax.SAXException;

/**
 * Represents an actual <b>T</b>ransformation <b>U</b>nit <b>Ar</b>chive (cdrar) and its 
 * entries (JarEntry).
 * <p/>
 * A CDRAR is a container for transformation resource and/or the archive definitions.
 * @author tfennelly
 */
public final class CDRArchive {
	
	/**
	 * CDRArchive name.
	 */
	private String name = null;	
	/**
	 * Archive entries table.
	 */
	private Vector entries = new Vector();
	/**
	 * Archive definition files.
	 */
	private Vector archiveDefs = new Vector();
	
	/**
	 * Constructor.
	 * @param cdrarStream Archive stream.
	 * @throws IOException Error reading the Archive.
	 */
	public CDRArchive(String name, JarInputStream cdrarStream) throws InvalidCDRArchiveException, IOException {
		JarEntry entry = null;
		ByteArrayOutputStream entryData = null; 
		byte buffer[] = null;
		
		setName(name);
		if(cdrarStream == null) {
			throw new IllegalArgumentException("null 'cdrar' argument in constructor call.");
		}
		
		// Read each entry in the Jar and store them in the entry table.
		// Not interested in the directory entries.
		entry = cdrarStream.getNextJarEntry(); 
		entryData = new ByteArrayOutputStream(1024);
		buffer = new byte[56];
		while(entry !=  null) {
			entryData.reset();
			if(!entry.isDirectory()) {
				int readCount = 0;
				
				while((readCount = cdrarStream.read(buffer)) != -1) {
					entryData.write(buffer, 0, readCount);
				}
				
				entries.addElement(new CDRArchiveEntry(entry, entryData.toByteArray()));													
			}
			entry = cdrarStream.getNextJarEntry();
		}
		
		// Get the archive definition files.
		loadArchiveDefs();
	}

	/**
	 * Public Constructor.
	 * <p/>
	 * Creates a new, but empty, CDRArchive instance.  Populate the instance using the
	 * {@link #addEntries(CDRArchiveEntry[])} and {@link #addArchiveDef(CDRConfig)} methods.
	 * @param name The Archive name.
	 */
	public CDRArchive(String name) {
		setName(name);	
	}
	
	/**
	 * Set the name of the archive.
	 * @param name The name to be set on the archive.
	 * @throws IllegalArgumentException
	 */
	private void setName(String name) throws IllegalArgumentException {
		if(name == null) {
			throw new IllegalArgumentException("null 'name' arg in method call.");
		}
		name = name.trim();
		if(name.equals("")) {
			throw new IllegalArgumentException("empty 'name' arg in method call."); 
		}
		this.name = name;
	}

	/**
	 * Add the supplied CDRArchiveEntry instances to this CDRArchive.
	 * @param cdrarEntries Entries to be added.
	 */
	public void addEntries(CDRArchiveEntry[] cdrarEntries) {
		if(cdrarEntries == null || cdrarEntries.length == 0) {
			throw new IllegalArgumentException("null or zero length 'cdrarEntries' arg in method call.");
		}
		entries.addAll(Arrays.asList(cdrarEntries));													
	}

	/**
	 * Add and CDRConfig instance to this CDRArchive instance.
	 * @param archiveDef The CDRConfig instance to be added.
	 */
	public void addArchiveDef(CDRConfig archiveDef) {
		if(archiveDef == null) {
			throw new IllegalArgumentException("null 'archiveDef' arg in method call.");
		}
		archiveDefs.addElement(archiveDef);
	}
	
	/**
	 * Load the Achive Definition entries for this CDRArchive. 
	 */
	private void loadArchiveDefs() throws IOException {
		Enumeration entries = getEntries();
		
		while(entries.hasMoreElements()) {
			CDRArchiveEntry cdrarEntry = (CDRArchiveEntry)entries.nextElement();
			String name = cdrarEntry.getName(); 
			
			if(name.startsWith("META-INF/") && name.endsWith(".cdrl")) {
				ByteArrayInputStream archiveStream = new ByteArrayInputStream(cdrarEntry.getEntryBytes()); 
			
				try {
					archiveDefs.addElement(new CDRConfig(name, archiveStream));
				} catch(SAXException e) {
					InvalidCDRArchiveException archiveExcep = new InvalidCDRArchiveException("Invalid archive definition file [" + name + "] in cdrar archive " + getName()); 
					archiveExcep.initCause(e);
					throw archiveExcep;
				}
			}
		}
	}
	
	/**
	 * Get the name of this cdrar file.
	 * @return The cdrar file name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the CDRArchiveEntry for the given name.
	 * @param name The CDRArchiveEntry name.
	 * @return The CDRArchiveEntry for the given name, or null if no such entry
	 * exists.
	 */
	protected CDRArchiveEntry getEntry(String name) {
		if(name == null) {
			throw new IllegalArgumentException("null 'name' arg in method call.");
		}
		name = name.trim();
		if(name.equals("")) {
			throw new IllegalArgumentException("empty 'name' arg in method call.");
		}
		if(name.length() > 1 && name.charAt(0) == '/') {
			name = name.substring(1);
		}
		
		for(int i = 0; i < entries.size(); i++) {
			CDRArchiveEntry entry = (CDRArchiveEntry)entries.elementAt(i);
			
			if(entry.getName().equals(name)) {
				return entry;
			}
		}
		
		return null;
	}

	/**
	 * Get an enumeration of all the CDRArchiveEntry objects in this CDRArchive. 
	 * @return An enumeration of all the CDRArchiveEntry objects in this CDRArchive.
	 */
	public Enumeration getEntries() {
		return entries.elements();	
	}
	
	/**
	 * Get all CDRDef entries for the specified device from this CDRArchive
	 * archive.
	 * @param deviceContext The device.
	 * @return All CDRDef entries for the specified device.
	 */
	public CDRDef[] getCDRDefs(UAContext deviceContext) {
		Vector matchingCDRDefsColl = new Vector();
		CDRDef[] matchingCDRDefs = null;
		
		// TODO: The following loops need to be cleaned up
		
		// Iterate over archive definitions on this cdrar file.
		for(int archiveDefsIndex = 0; archiveDefsIndex < archiveDefs.size(); archiveDefsIndex++) {
			CDRConfig arcDef = (CDRConfig)archiveDefs.elementAt(archiveDefsIndex);
			CDRDef[] cdrDefs = arcDef.getCDRDefs();
							
			if(cdrDefs != null) {
				// Iterate over the CDRDefs defined on the current CDRConfig
				for(int unitDefIndex = 0; unitDefIndex < cdrDefs.length; unitDefIndex++) {
					CDRDef cdrDef = cdrDefs[unitDefIndex];
					UATargetExpression[] uaTargetExpressions = cdrDef.getUaTargetExpressions();
					
					for(int expIndex = 0; expIndex < uaTargetExpressions.length; expIndex++) {
						UATargetExpression expression = uaTargetExpressions[expIndex];
						
						if(expression.isMatchingDevice(deviceContext)) {
							matchingCDRDefsColl.addElement(cdrDef);
							break;
						}
					}
				}
			}
		}

		matchingCDRDefs = new CDRDef[matchingCDRDefsColl.size()];
		matchingCDRDefsColl.toArray(matchingCDRDefs);
		
		return matchingCDRDefs;
	}

	/**
	 * Is the uatarget expression for the specified device.
	 * <p/>
	 * The uatarget value may have
	 * @param uaTargetExpression
	 * @param deviceContext
	 * @return
	 */
	public static boolean isMatchingDevice(String uaTargetExpression, UAContext deviceContext) {
		StringTokenizer tokenizer = new StringTokenizer(uaTargetExpression, "+");
		
		// So, the uaTargetExpression will in one of the following
		// forms (note: only supports "AND" operations):
		// 1. "deviceX" (or "profileX") i.e. a single entity.
		// 2. "deviceX + profileY" i.e. a compound entity.
		// 3. "profileX + profileY" i.e. a compound entity.
		// 4. "profileX + not:profileY" i.e. a compound entity.
		while(tokenizer.hasMoreTokens()) {
			String uatarget = XmlUtil.removeEntities(tokenizer.nextToken().trim());
			boolean negated = uatarget.startsWith("not:");
	
			if(!negated) {
				// Match against a wildcard astrix.
				if(uatarget.equals("*")) {
					continue; // matches!
				} else if(UAContextUtil.isDeviceOrProfile(uatarget, deviceContext)) {
					// Is this cdres device name the commonname on the deviceContext,
					// or one of its profiles.
					continue; // matches!
				} 
			} else {
				// Trim off the "not:" prefix.
				uatarget = uatarget.substring(4);
				if(!UAContextUtil.isDeviceOrProfile(uatarget, deviceContext)) {
					continue; // matches!
				} 
			}

			// Not a match => the rest of the expression is ignored
			// because we only support AND operations (but you can "not"
			// an entity) meaning all entities must be a match.
			return false;
		}
		
		return true;
	}
	
	/**
	 * Get the number of entries in the cdrar.
	 * @return The number of entries in the cdrar.
	 */
	protected int size() {
		return entries.size();
	}
}
