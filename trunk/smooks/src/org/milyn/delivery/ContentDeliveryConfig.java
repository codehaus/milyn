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

package org.milyn.delivery;

import java.util.Hashtable;
import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.delivery.trans.TransSet;
import org.milyn.dtd.DTDStore;

/**
 * Content delivery configuration.
 * <p/>
 * Provides access to Content Delivery Resources 
 * (e.g. {@link org.milyn.delivery.ContentDeliveryUnit Content Delivery Units})
 * and other information for the requesting browser e.g. see {@link #getAssemblyUnits()}.  
 * @author tfennelly
 */
public interface ContentDeliveryConfig {

	/**
	 * Get the list of {@link CDRDef}s for the specified selector definition.
	 * <p/>
	 * This list will be preselected and {@link org.milyn.cdr.CDRDefSortComparator preordered} 
	 * for the requesting browser.
	 * <p/>
	 * Use one or both of the following mechanisms to a access the resource bytes:
	 * <ol>
	 * 	<li>{@link org.milyn.cdr.CDRStore#getEntry(CDRDef)}</li>
	 * 	<li>{@link ClassLoader#getResourceAsStream(java.lang.String)} if the 
	 * 		resource is located in the classpath - using {@link CDRDef#getPath()} for
	 * 		the resource "name" parameter.  Prefix the name with "/".
	 * 	</li>
	 * </ol>
	 * @param selector selector attribute value from the .cdrl file in the .cdrar.  This 
	 * parameter is treated case incensitively.
	 * @return List of {@link CDRDef} instances, or null if no {@link CDRDef}s are 
	 * defined under that selector (for the device).
	 * @see #getObjects(String)
	 */
	public abstract List getCDRDefs(String selector);
	
	/**
	 * Get a list of {@link Object}s from the {@link CDRDef}s specified by the selector.
	 * <p/>
	 * Gets the {@link CDRDef}s specified for the selector and attempts to instanciate
	 * a Java class instance from the resource specified by each of the {@link CDRDef}s.
	 * <p/>
	 * Implementations should use {@link org.milyn.cdr.CDRStore#getObject(CDRDef)} to 
	 * construct each object.
	 * @param selector selector attribute value from the .cdrl file in the .cdrar.  This 
	 * parameter is treated case incensitively.
	 * @return List of Object instances.  An empty list is returned where no 
	 * selectors exist.
	 * @see org.milyn.cdr.CDRStore#getObject(CDRDef)
	 * @see #getCDRDefs(String)
	 */
	public abstract List getObjects(String selector);
	
	/**
	 * Get the {@link org.milyn.delivery.assemble.AssemblyUnit} table for this delivery 
	 * context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link org.milyn.delivery.assemble.AssemblyUnit} instances.
	 * @return The AssemblyUnit table for this delivery context.
	 */
	public abstract Hashtable getAssemblyUnits();

	/**
	 * Get the TransUnit configure instances, for the named tag, for the useragent 
	 * associated with this table.
	 * @param tag The tag name for which the TransUnits are being requested.
	 * @return TransSet for the specified tag name, or null if none is specified.  
	 */
	public abstract TransSet getTransSet(String tag);

	/**
	 * Get the SerializationUnit table for this delivery context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link org.milyn.delivery.serialize.SerializationUnit} instances.
	 * @return The SerializationUnit table for this delivery context.
	 */
	public abstract Hashtable getSerailizationUnits();

	/**
	 * Get the DTD ({@link org.milyn.dtd.DTDStore.DTDObjectContainer}) for this delivery context.
	 * @return The DTD ({@link org.milyn.dtd.DTDStore.DTDObjectContainer}) for this delivery context.
	 */
	public abstract DTDStore.DTDObjectContainer getDTD();
}