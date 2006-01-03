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

package org.milyn.cdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarInputStream;

import org.milyn.cdr.cdrar.CDRArchive;
import org.milyn.cdr.cdrar.CDRArchiveAlreadyLoadedException;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;
import org.milyn.cdr.cdrar.InvalidCDRArchiveException;
import org.milyn.container.ContainerContext;
import org.milyn.device.UAContext;
import org.milyn.logging.SmooksLogger;
import org.milyn.resource.ContainerResourceLocator;
import org.xml.sax.SAXException;


/**
 * Content Delivery Resource (CDR) store object.
 * <p/>
 * This class is used to load CDRs.
 * @author tfennelly
 */
public class CDRStore {
	
	/**
	 * Table of loaded cdrars.
	 */
	private Vector cdrars = new Vector();
	/**
	 * All CDRArchive entries loaded by CDRStore (of any type).
	 */
	private Vector loadedCdrarEntries = new Vector();
	/**
	 * CDRClassLoader instance to be used on this CDRStore instance.
	 */
	private CDRClassLoader cdrarClassLoader = new CDRClassLoader(CDRStore.class.getClassLoader(), this);
	/**
	 * Container context in which this store lives.
	 */
	private ContainerContext containerContext;
	
	/**
	 * Public constructor.
	 * @param containerContext Container context in which this store lives.
	 */
	public CDRStore(ContainerContext containerContext) {
		if(containerContext == null) {
			throw new IllegalArgumentException("null 'containerContext' arg in constructor call.");
		}
		this.containerContext = containerContext;
	}
	
	/**
	 * Load a preconstructed CDRArchive instance.
	 * @param cdrar CDRArchive instance to load.
	 */
	public void load(CDRArchive cdrar) {
		if(cdrar == null) {
			throw new IllegalArgumentException("null 'cdrar' arg in method call.");
		}
		cdrars.addElement(cdrar);

		// Load all the CdrarEntries into a cache for speedy access.
		Enumeration cdrarEntries = cdrar.getEntries();			
		while(cdrarEntries.hasMoreElements()) {
			CDRArchiveEntry entry = (CDRArchiveEntry)cdrarEntries.nextElement();
			loadedCdrarEntries.addElement(entry);
		}			
	}

	/**
	 * Load an cdrar archive.
	 * @param name The archive name.
	 * @param cdrarStream The JarInputStream of the associated CDRArchive JAR.
	 * @throws CDRArchiveAlreadyLoadedException A cdrar by the supplied name has already 
	 * been loaded. 
	 * @throws InvalidCDRArchiveException The cdrar is invalid e.g. no archive definition file. 
	 * @throws IOException Error reading the JarInputStream.
	 */	
	public void load(String name, JarInputStream cdrarStream) throws CDRArchiveAlreadyLoadedException, InvalidCDRArchiveException, IOException {
        if(name == null) {
            throw new IllegalArgumentException("null 'name' arg in method call.");
        } else if(name.trim().equals("")) {
            throw new IllegalArgumentException("empty 'name' arg in method call.");
        } else if(cdrarStream == null) {
            throw new IllegalArgumentException("null 'cdrarStream' arg in method call.");
        } 	
		load(new CDRArchive(name, cdrarStream));
	}	

	/**
	 * Load all .cdrar files listed in the BufferedReader stream.
	 * <p/>
	 * Because this method uses the ContainerResourceLocator it may be possible
	 * to load external cdrar files.  If the ContainerResourceLocator is a 
	 * ServletResourceLocator the lines in the BufferedReader param can contain
	 * external URLs.
	 * @param cdrarLoadList BufferedReader cdrar list - one cdrar def per line.
	 */
	public void load(BufferedReader cdrarLoadList) throws IOException {
		String cdrar;
		ContainerResourceLocator cdrarLocator = containerContext.getResourceLocator();
		
		while((cdrar = cdrarLoadList.readLine()) != null) {
			cdrar = cdrar.trim();
			if(cdrar.equals("") || cdrar.charAt(0) == '#') {
				continue;
			}
			
			try {
				InputStream resource = cdrarLocator.getResource(cdrar);
				
				if(cdrar.toLowerCase().endsWith(".cdrl")) {
					CDRArchive cdrlCdrar = new CDRArchive(cdrar);
					
					cdrlCdrar.addArchiveDef(new CDRConfig(cdrar, resource));
					load(cdrlCdrar);
				} else {
					load(cdrar, new JarInputStream(resource));
				}
				SmooksLogger.getLog().debug("[" + cdrar + "] Loaded.");
			} catch (InvalidCDRArchiveException e) {
				SmooksLogger.getLog().error("[" + cdrar + "] Load failure. " + e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				SmooksLogger.getLog().error("[" + cdrar + "] Load failure. " + e.getMessage(), e);
			} catch (CDRArchiveAlreadyLoadedException e) {
				SmooksLogger.getLog().error("[" + cdrar + "] Load failure. " + e.getMessage(), e);
			} catch (IOException e) {
				SmooksLogger.getLog().error("[" + cdrar + "] Load failure. " + e.getMessage(), e);
			} catch (SAXException e) {
				SmooksLogger.getLog().error("[" + cdrar + "] Load failure. " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Load all .cdrar files in the specified folder.
	 * @param cdrarDir Folder from which to load.
	 */
	public void load(File cdrarDir) throws IOException {
		File cdrars[] = null;
		boolean loaded = false; 

		if(cdrarDir == null) {
			throw new IllegalArgumentException("null 'cdrarFolder' arg in method call.");
		} else if(!cdrarDir.isDirectory()) {
			// This covers 2 tests: non-existance and not-a-dir.
			throw new IOException("Cannot read from 'cdrarFolder' [" + cdrarDir.getCanonicalPath() + "] - not a directory.");
		}
		
		cdrars = cdrarDir.listFiles();
		if(cdrars != null) {
			for(int i = 0; i < cdrars.length; i++) {
				if(cdrars[i].getName().endsWith(".cdrar")) {
					JarInputStream jar = new JarInputStream(new FileInputStream(cdrars[i]));
					try {
						load(cdrars[i].getCanonicalPath(), jar);
						loaded = true;
					} catch (InvalidCDRArchiveException e) {
						e.printStackTrace();
					} catch (CDRArchiveAlreadyLoadedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(!loaded) {
			throw new IOException("Cannot read from 'cdrarFolder' [" + cdrarDir.getCanonicalPath() + "] - no '.cdrar' files.");
		}
	}
	
	/**
	 * Get a loaded {@link CDRArchive} instance.
	 * <p/>
	 * The {@link CDRArchive} instance provides access to an enumeration of all
	 * it's {@link CDRArchiveEntry}s.
	 * @param name The name (archive path) of the cdrar entry.
	 * @return A {@link CDRArchive} instances if loaded, otherwise null.
	 */
	private CDRArchive getCdrar(String name) {
		CDRArchive cdrar = null;
			 
		for(int i = 0; i < cdrars.size(); i++) {
			cdrar = (CDRArchive)cdrars.elementAt(i);
			if(cdrar.getName().equals(name)) {
				return cdrar;
			}
		}
		
		return null;
	}
	
	/**
	 * Get an array of the loaded {@link CDRArchive} instances.
	 * <p/>
	 * Each {@link CDRArchive} instance provides access to an enumeration of all
	 * it's {@link CDRArchiveEntry}s.
	 * <p/>
	 * The order is equal to the order in which they were loaded.
	 * @return An array of the loaded {@link CDRArchive} instances.
	 */
	public CDRArchive[] getCdrars() {
		CDRArchive[] cdrarArray;
			 
		if(cdrars.isEmpty()) {
			throw new IllegalStateException("Call to getCdrars() before any cdrar files have been loaded i.e. before any calls to CDRStore.load()");
		}
		
		cdrarArray =  new CDRArchive[cdrars.size()];
		cdrars.toArray(cdrarArray);
		
		return cdrarArray;
	}
	
	/**
	 * Get the CDRArchiveEntry matching the name specified.
	 * @param name The Jar name of the entry.
	 * @return The CDRArchiveEntry for the specified name.
	 * @throws CDRArchiveEntryNotFoundException When the requested CDRArchiveEntry has not been loaded
	 * and is therefore unknown. 
	 */
	public CDRArchiveEntry getEntry(String name) throws CDRArchiveEntryNotFoundException {
		CDRArchiveEntry entry = null;
		
		for(int i = 0; i < loadedCdrarEntries.size(); i++) {
			entry = (CDRArchiveEntry)loadedCdrarEntries.elementAt(i);
			if(entry.getName().equals(name)) {
				return entry; 
			}
		}
		
		throw new CDRArchiveEntryNotFoundException(name);
	}
	
	/**
	 * Get the CDRArchiveEntry for the specificd CDRDef instance.
	 * @param unitDef The CDRDef whose CDRArchiveEntry is being requested.
	 * @return The CDRArchiveEntry for the specified CDRDef.
	 * @throws CDRArchiveEntryNotFoundException When the requested CDRArchiveEntry has not been loaded
	 * and is therefore unknown. 
	 */
	public CDRArchiveEntry getEntry(CDRDef unitDef) throws CDRArchiveEntryNotFoundException {
		return getEntry(unitDef.getPath());
	}
	
	/**
	 * Get all CDRDef entries for the specified device from all loaded CDRArchive
	 * archives.
	 * @param deviceContext The device.
	 * @return All CDRDef entries for the specified device.
	 */
	public CDRDef[] getCDRDefs(UAContext deviceContext) {
		CDRArchive[] cdrars = getCdrars();
		Vector allCDRDefsColl = new Vector();
		CDRDef[] allCDRDefs = null;
		
		// Iterate through each of the loaded CDRArchive files.
		for(int i = 0; i < cdrars.length; i++) {
			CDRDef[] unitDefs = cdrars[i].getCDRDefs(deviceContext);			
			allCDRDefsColl.addAll(Arrays.asList(unitDefs));
		}
		
		allCDRDefs = new CDRDef[allCDRDefsColl.size()];
		allCDRDefsColl.toArray(allCDRDefs);
		
		return allCDRDefs;
	}

	/**
	 * Get the CDRClassLoader to be used loading java class instances from
	 * this CDRStore instance.
	 * @return Returns the cdrarClassLoader.
	 */
	public CDRClassLoader getCdrarClassLoader() {
		return cdrarClassLoader;
	}
	
	/**
	 * Load a Java Object defined by the supplied CDRDef instance.
	 * <p/>
	 * The class implementation must contain a public constructor
	 * that takes a {@link CDRDef} parameter.
	 * @param unitDef CDRDef instance.
	 * @return An Object instance from the CDRDef.
	 */
	public Object getObject(CDRDef unitDef) {
		Object object = null;
		
		try {
			Class classRuntime = cdrarClassLoader.loadClass(unitDef);
			Constructor constructor = classRuntime.getConstructor(new Class[] {CDRDef.class});
			
			object = constructor.newInstance(new Object[] {unitDef});
		} catch (NoSuchMethodException e) {
			IllegalStateException state = new IllegalStateException("Unable to load Java Object [" + unitDef.getPath() + "]. Implementation must provide a public constructor that takes a CDRDef arg.");
			state.initCause(e);
			throw state;
		} catch (Exception e) {
			IllegalStateException state = new IllegalStateException("Java class resource [" + unitDef.getPath() + "] not loadable through " + cdrarClassLoader.getClass().getName() + ".");
			state.initCause(e);
			throw state;
		}
		
		return object;
	}
}
