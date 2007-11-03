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

import java.util.List;
import java.util.Map;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.dtd.DTDStore;

/**
 * Content delivery configuration.
 * <p/>
 * Provides access to Content Delivery Resources 
 * (e.g. {@link ContentHandler Content Delivery Units})
 * and other information for the targeted profile.
 * @author tfennelly
 */
public interface ContentDeliveryConfig {

	/**
	 * Get the list of {@link SmooksResourceConfiguration}s for the specified selector definition.
	 * <p/>
	 * This list will be preselected and {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator preordered} 
	 * for the target execution context.
	 * @param selector Configuration {@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() selector}.  This 
	 * parameter is treated case incensitively.
	 * @return List of {@link SmooksResourceConfiguration} instances, or null if no {@link SmooksResourceConfiguration}s are 
	 * defined under that selector (for the device).
	 * @see #getObjects(String)
	 */
	public abstract List<ContentHandlerConfigMap> getSmooksResourceConfigurations(String selector);

	/**
	 * Get the {@link SmooksResourceConfiguration} map for the target execution context.
	 * <p/>
	 * This Map will be {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator preordered} 
	 * for the target execution context.
	 * 
	 * @return {@link SmooksResourceConfiguration} map for the target execution context, keyed by the configuration 
	 * {@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() selector}, with each value being a
	 * {@link List} of preordered {@link SmooksResourceConfiguration} instances.
	 */
	public abstract Map<String, List<ContentHandlerConfigMap>> getSmooksResourceConfigurations();
	
	/**
	 * Get a list of {@link Object}s from the {@link SmooksResourceConfiguration}s specified by the selector.
	 * <p/>
	 * Gets the {@link SmooksResourceConfiguration}s specified for the selector and attempts to instanciate
	 * a Java class instance from the resource specified by each of the {@link SmooksResourceConfiguration}s.
	 * <p/>
	 * Implementations should use {@link org.milyn.cdr.SmooksResourceConfigurationStore#getObject(org.milyn.cdr.SmooksResourceConfiguration)} to 
	 * construct each object.
	 * @param selector selector attribute value from the .cdrl file in the .cdrar.  This 
	 * parameter is treated case incensitively.
	 * @return List of Object instances.  An empty list is returned where no 
	 * selectors exist.
	 * @see org.milyn.cdr.SmooksResourceConfigurationStore#getObject(org.milyn.cdr.SmooksResourceConfiguration)
	 * @see #getSmooksResourceConfigurations(String)
	 */
	public abstract List getObjects(String selector);

	/**
	 * Get the DTD ({@link org.milyn.dtd.DTDStore.DTDObjectContainer}) for this delivery context.
	 * @return The DTD ({@link org.milyn.dtd.DTDStore.DTDObjectContainer}) for this delivery context.
	 */
	public abstract DTDStore.DTDObjectContainer getDTD();
}