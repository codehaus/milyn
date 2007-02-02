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

package org.milyn.delivery;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.process.ProcessingSet;
import org.milyn.dtd.DTDStore;

/**
 * Content delivery configuration.
 * <p/>
 * Provides access to Content Delivery Resources 
 * (e.g. {@link org.milyn.delivery.ContentDeliveryUnit Content Delivery Units})
 * and other information for the requesting useragent e.g. see {@link #getAssemblyUnits()}.  
 * @author tfennelly
 */
public interface ContentDeliveryConfig {

	/**
	 * Get the list of {@link SmooksResourceConfiguration}s for the specified selector definition.
	 * <p/>
	 * This list will be preselected and {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator preordered} 
	 * for the requesting useragent.
	 * @param selector Configuration {@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() selector}.  This 
	 * parameter is treated case incensitively.
	 * @return List of {@link SmooksResourceConfiguration} instances, or null if no {@link SmooksResourceConfiguration}s are 
	 * defined under that selector (for the device).
	 * @see #getObjects(String)
	 */
	public abstract List getSmooksResourceConfigurations(String selector);

	/**
	 * Get the {@link SmooksResourceConfiguration} map for the requesting useragent.
	 * <p/>
	 * This Map will be {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator preordered} 
	 * for the requesting useragent.
	 * 
	 * @return {@link SmooksResourceConfiguration} map for the requesting useragent, keyed by the configuration 
	 * {@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() selector}, with each value being a
	 * {@link List} of preordered {@link SmooksResourceConfiguration} instances.
	 */
	public abstract Map getSmooksResourceConfigurations();
	
	/**
	 * Get a list of {@link Object}s from the {@link SmooksResourceConfiguration}s specified by the selector.
	 * <p/>
	 * Gets the {@link SmooksResourceConfiguration}s specified for the selector and attempts to instanciate
	 * a Java class instance from the resource specified by each of the {@link SmooksResourceConfiguration}s.
	 * <p/>
	 * Implementations should use {@link org.milyn.cdr.CDRStore#getObject(SmooksResourceConfiguration)} to 
	 * construct each object.
	 * @param selector selector attribute value from the .cdrl file in the .cdrar.  This 
	 * parameter is treated case incensitively.
	 * @return List of Object instances.  An empty list is returned where no 
	 * selectors exist.
	 * @see org.milyn.cdr.CDRStore#getObject(SmooksResourceConfiguration)
	 * @see #getSmooksResourceConfigurations(String)
	 */
	public abstract List getObjects(String selector);
	
	/**
	 * Get the {@link org.milyn.delivery.assemble.AssemblyUnit} table for this delivery 
	 * context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link ContentDeliveryUnitConfigMap} instances where the contained 
     * {@link ContentDeliveryUnit} is an {@link org.milyn.delivery.assemble.AssemblyUnit}.
	 * @return The AssemblyUnit table for this delivery context.
	 */
	public abstract Hashtable getAssemblyUnits();

	/**
	 * Get the ProcessingUnit configure instances, for the named tag, for the useragent 
	 * associated with this table.
	 * @param tag The tag name for which the ProcessingUnits are being requested.
	 * @return ProcessingSet for the specified tag name, or null if none is specified.  
	 */
	public abstract ProcessingSet getProcessingSet(String tag);

	/**
	 * Get the SerializationUnit table for this delivery context.
	 * <p/>
     * The table is keyed by element name and the values are 
     * {@link ContentDeliveryUnitConfigMap} instances where the contained 
     * {@link ContentDeliveryUnit} is a {@link org.milyn.delivery.serialize.SerializationUnit}.
	 * @return The SerializationUnit table for this delivery context.
	 */
	public abstract Hashtable getSerailizationUnits();

	/**
	 * Get the DTD ({@link org.milyn.dtd.DTDStore.DTDObjectContainer}) for this delivery context.
	 * @return The DTD ({@link org.milyn.dtd.DTDStore.DTDObjectContainer}) for this delivery context.
	 */
	public abstract DTDStore.DTDObjectContainer getDTD();
}