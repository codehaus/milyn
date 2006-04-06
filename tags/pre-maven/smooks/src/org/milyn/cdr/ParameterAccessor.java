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

import java.util.List;

import org.milyn.delivery.ContentDeliveryConfig;

/**
 * Utility class for accessing device attributes defined in the Content
 * Delivery Resource List files (.cdrl).
 * <p id="decode"/>
 * Device parameters are looked up using the "device-parameters" string
 * as the selector (see {@link org.milyn.cdr.CDRDef}).  The parameter values are
 * stored in the &lt;param&gt; elements within this Content Delivery Resource definition.
 * This class iterates over the list of {@link org.milyn.cdr.CDRDef} (&lt;cdres&gt;) 
 * elements targeted at the requesting device.  It looks for a definition of the named
 * parameter.  If the &lt;param&gt; has a type attribute the 
 * {@link org.milyn.cdr.ParameterDecoder} for that type can be applied to the attribute
 * value through the {@link #getParameterObject(String, ContentDeliveryConfig)} method, 
 * returning whatever Java type defined by the {@link org.milyn.cdr.ParameterDecoder}
 * implementation.  As an example, see {@link org.milyn.cdr.TokenizedStringParameterDecoder}.
 * <p/>
 * Sample Parameters .cdrl configuration:
 * 
 * @author tfennelly
 */
public abstract class ParameterAccessor {
	
	/**
	 * Device parameters .cdrl lookup string.
	 */
	public static final String DEVICE_PARAMETERS = "device-parameters";

	/**
	 * Get the named parameter instance (decode).
	 * @param name Parameter name.
	 * @param config Device Delivery Configuration.
	 * @return The Parameter instance for the named parameter (<a href="#decode">decoded to an Object</a>), 
	 * or null if not defined.
	 */
	public static Object getParameterObject(String name, ContentDeliveryConfig config) {
		Parameter param = getParamter(name, config);
		
		if(param != null) {
			return param.getValue(config);
		}
		
		return null;
	}

	/**
	 * Get the named parameter String value.
	 * @param name Name of parameter to get. 
	 * @param config The {@link ContentDeliveryConfig} for the requesting device.
	 * @return Parameter value, or null if not set.
	 */
	public static String getStringParameter(String name, ContentDeliveryConfig config) {
		Parameter param = getParamter(name, config);
		
		if(param != null) {
			return param.getValue();
		}
		
		return null;
	}

	/**
	 * Get the named parameter String value.
	 * @param name Name of parameter to get. 
	 * @param defaultVal Default value returned if the parameter is not defined.
	 * @param config The {@link ContentDeliveryConfig} for the requesting device.
	 * @return Parameter value, or null if not set.
	 */
	public static String getStringParameter(String name, String defaultVal, ContentDeliveryConfig config) {
		Parameter param = getParamter(name, config);
		
		if(param != null) {
			return param.getValue();
		}
		
		return defaultVal;
	}

	/**
	 * Get the named CDRDef parameter as a boolean.
	 * @param name Name of parameter to get. 
	 * @param defaultVal The default value to be returned if there are no 
	 * parameters on the this CDRDef instance, or the parameter is not defined.
	 * @param config The {@link ContentDeliveryConfig} for the requesting device.
	 * @return true if the parameter is set to true, defaultVal if not defined, otherwise false.
	 */
	public static boolean getBoolParameter(String name, boolean defaultVal, ContentDeliveryConfig config) {
		Parameter param = getParamter(name, config);
		String paramVal;

		if(param == null) {
			return defaultVal;
		}
		
		paramVal = param.getValue();
		if(paramVal == null) {
			return defaultVal;
		}
		paramVal = paramVal.trim();
		if(paramVal.equals("true")) {
			return true;
		} else if(paramVal.equals("false")) {
			return false;
		} else {
			return defaultVal;
		}
	}

	/**
	 * Get the named parameter.
	 * @param name Parameter name.
	 * @param config Device Delivery Configuration.
	 * @return The Parameter instance for the named parameter, or null if not defined.
	 */
	public static Parameter getParamter(String name, ContentDeliveryConfig config) {
		List list;		
		
		if(name == null || (name = name.trim()).equals("")) {
			throw new IllegalArgumentException("null or empty 'name' arg in method call.");
		}
		if(config == null) {
			throw new IllegalArgumentException("null 'config' arg in method call.");
		}

		list = config.getCDRDefs(DEVICE_PARAMETERS);
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				CDRDef cdrDef = (CDRDef)list.get(i);
				Parameter param = cdrDef.getParameter(name);
				if(param != null) {
					return param;
				}
			}
		}
		
		return null;
	}
}
