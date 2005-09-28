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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;

/**
 * Content Delivery Resource (CDR) classloader.
 * <p/>
 * Classloading is delegated to the parent classloader.  If the class is not 
 * loaded by the parent classloader this classloader attempts to load the class from
 * the loaded CDRArchive files.  If this too fails a ClassNotFoundException results.
 * @author tfennelly
 */ 
public class CDRClassLoader extends ClassLoader {

	/**
	 * CDRStore associated with this classloader.
	 */
	private CDRStore cdrarStore;

	/**
	 * Public constructor.
	 * @param parent The parent classloader in the classloader hierarchy.
	 */
	public CDRClassLoader(ClassLoader parent, CDRStore cdrarStore) {
		super(parent);
		if(cdrarStore == null) {
			throw new IllegalArgumentException("null 'cdrarStore' arg in constructor call.");
		}
		this.cdrarStore = cdrarStore;
	}

	/**
	 * Load a runtime class from an archive CDRDef instance. 
	 * @param unitDef The CDRDef to be used for loading the runtime class.
	 * @return Runtime Class instance.
	 * @throws ClassNotFoundException See java.lang.ClassLoader.loadClass
	 */
	public synchronized Class loadClass(CDRDef unitDef) throws ClassNotFoundException {
		return loadClass(CDRClassLoader.toClassName(unitDef.getPath()));
	}
	/**
	 * Get the specified class from the CDRStore.
	 * @param name The name of the class.
	 * @throws ClassNotFoundException  Class cannot be loaded from this point
	 * in the classloader hierarchy. 
	 */
	public Class findClass(String name) throws ClassNotFoundException {
		Class clazz;
		
		if(name == null) {
			throw new IllegalArgumentException("null 'name' arg in method call.");  
		}
		
		// May have already loaded this class.
		clazz = findLoadedClass(name);
		if(clazz == null) {
			CDRArchiveEntry cdrarEntry; 
			
			// Get the class from the CDRStore - cast CDRArchiveEntryNotFoundException
			// to ClassNotFoundException.
			try {
				cdrarEntry = cdrarStore.getEntry(CDRClassLoader.toFileName(name));				
			} catch (CDRArchiveEntryNotFoundException e) {
				throw new ClassNotFoundException("Class [" + name + "] not found.", e);
			}
			
			// Found it!!
			// An entry for this class has been loaded by the CDRStore.
			clazz = defineClass(name, cdrarEntry.getEntryBytes(), 0, cdrarEntry.getEntryBytes().length);
		}
		
		return clazz;
	}

	/**
	 * Convert the Java-class-file-name to the equivalent Java-class-name (dot 
	 * delimited package name).
	 * <p/>
	 * EG:<br/>
	 * a/b/c/X.class converts to a.b.c.X<br/>
	 * a/b/c/X converts to a.b.c.X<br/>
	 * a.b.c.X converts to a.b.c.X<br/>
	 * a.b.c.X.class converts to a.b.c.X<br/>
	 * @param fileName The file name String to be translated.
	 * @return Java Class runtime name representation of the supplied file name String.
	 */
	public static String toClassName(String fileName) {
		StringBuffer className;
		
		if(fileName == null) {
			throw new IllegalArgumentException("null 'fileName' arg in method call.");
		}
		fileName = fileName.trim();
		if(fileName.equals("")) {
			throw new IllegalArgumentException("empty 'fileName' arg in method call.");
		}
		
		className = new StringBuffer(fileName);
		// Fixup the name - replace '/' with '.' and remove ".class" if
		// present.
		if(fileName.endsWith(".class") && fileName.length() > 6) {
			className.setLength(className.length() - 6);
		}
		for(int i = 0; i < className.length(); i++) {
			if(className.charAt(i) == '/') {
				className.setCharAt(i, '.');
			}
		}
		
		return className.toString();
	}


	/**
	 * Convert the Java-class-name (dot delimited package name)to the 
	 * equivalent Java-class-file-name .
	 * <p/>
	 * EG:<br/>
	 * a.b.c.X converts to a/b/c/X.class<br/>
	 * a.b.c.X.class converts to a/b/c/X.class<br/>
	 * a/b/c/X.class converts to a/b/c/X.class<br/>
	 * a/b/c/X converts to a/b/c/X.class<br/>
	 * @param className The class name string to be translated.
	 * @return The file name representaion of the supplied runtime class String.
	 */
	public static String toFileName(String className) {
		StringBuffer fileName;
		
		if(className == null) {
			throw new IllegalArgumentException("null 'className' arg in method call.");
		}
		className = className.trim();
		if(className.equals("")) {
			throw new IllegalArgumentException("empty 'className' arg in method call.");
		}
		
		fileName = new StringBuffer(className);
		// Fixup the name - replace '.' with '/' and append ".class" ( possibly 
		// after already removing it - to avoid it from being converted 
		// to "/class").
		if(className.endsWith(".class") && className.length() > 6) {
			fileName.setLength(className.length() - 6);
		}
		for(int i = 0; i < fileName.length(); i++) {
			if(fileName.charAt(i) == '.') {
				fileName.setCharAt(i, '/');
			}
		}
		fileName.append(".class");
		
		return fileName.toString();
	}
	
	/**
	 * Overriding getResourceAsStream to provide access to the CdrarLoaded
	 * resources through this Classloader.
	 * <p/>
	 * Calls the parent class loader and if the stream is not available from
	 * the parent it tries to load the org.milyn.resource from the CDRStore.
	 * @param name The name of the required org.milyn.resource.
	 * @return An input stream for the specified org.milyn.resource, or null if the org.milyn.resource
	 * cold not be located.
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String name) {
		ClassLoader parent = getParent();
		
		if(parent != null) {
			InputStream stream = parent.getResourceAsStream(name);
			
			if(stream != null) {
				return stream;
			}
		} 
		
		try {
			CDRArchiveEntry cdrarEntry = cdrarStore.getEntry(name);				
			return new ByteArrayInputStream(cdrarEntry.getEntryBytes());
		} catch (CDRArchiveEntryNotFoundException e) {
			// OK, return null.
		}
		
		return null;
	}

	/**
	 * Overriden to remove support for this method.  Proper URL support
	 * will need to be added at some stage.
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	protected URL findResource(String name) {
		throw new UnsupportedOperationException("URL org.milyn.resource access not supported.");
	}

	/**
	 * Overriden to remove support for this method.  Proper URL support
	 * will nee to be added at some stage.
	 * @see java.lang.ClassLoader#findResources(java.lang.String)
	 */
	protected Enumeration findResources(String name) throws IOException {
		throw new UnsupportedOperationException("URL org.milyn.resource access not supported.");
	}

	/**
	 * Overriden to remove support for this method.  Proper URL support
	 * will nee to be added at some stage.
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	public URL getResource(String name) {
		throw new UnsupportedOperationException("URL org.milyn.resource access not supported.");
	}

}
