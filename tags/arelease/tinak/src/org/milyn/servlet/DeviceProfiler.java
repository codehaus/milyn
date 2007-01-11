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

package org.milyn.servlet;

import java.io.InputStream;

import javax.servlet.ServletConfig;

import org.milyn.device.profile.DefaultProfileConfigDigester;
import org.milyn.device.profile.ProfileConfigDigester;
import org.milyn.device.profile.ProfileSet;
import org.milyn.device.profile.ProfileStore;
import org.milyn.resource.ServletResourceLocator;
import org.milyn.resource.URLResourceLocator;

/**
 * J2EE Servlet Device Profiler class.
 * <p/>
 * The DeviceProfiler is controlled by 2 Servlet Configuration parameters 
 * (See {@link org.milyn.servlet.ServletParamUtils#getParameterValue(String, ServletConfig)}).
 * These are:
 * <ol>
 * 		<li><b>DeviceProfileDigester</b>: This is the runtime class name for of the {@link ProfileConfigDigester}
 * 			implementation responsible for converting the profile stream into a {@link ProfileStore}.
 * 			This parameter defaults to {@link DefaultProfileConfigDigester}.
 * 		</li>
 * 		<li><b>DeviceProfileUrl</b>: This is the location of the profile.  This parameter
 * 			defaults to "/WEB-INF/device-profile.xml".
 * 		</li>
 * </ol>
 * @author tfennelly
 */
public abstract class DeviceProfiler {
	
    /**
     * Device profile Digester runtime application property name.
     */
    private static final String DEVICE_PROFILE_DIGESTER_PARAM = "DeviceProfileDigester";
    /**
     * Device profile URL application property name.
     */
    private static final String DEVICE_PROFILE_CONFIG_PARAM = "DeviceProfileUrl";
    /**
     * Default device profile config file.
     */
    private static final String DEFAULT_CONFIG = "/device-profile.xml";
	/**
	 * ProfileStore for the VM.
	 */
	private static ProfileStore profileStore;

    /**
     * Get the ProfileSet for the named device.
     * @param deviceName The name of the device for which a ProfileSet is sought.
     * @param config Servlet config used to load the profile configuration.
     * @return DeviceIdent instance
     */
    protected static ProfileSet getDeviceProfile(String deviceName, ServletConfig config) {
        InputStream configStream;

        if(deviceName == null) {
        	throw new IllegalArgumentException("null 'deviceName' param in method call.");
        }
        deviceName = deviceName.trim();
        if(deviceName.equals("")) {
        	throw new IllegalArgumentException("empty 'deviceName' param in method call.");
        }
        if(config == null) {
        	throw new IllegalArgumentException("null 'config' param in method call.");
        }
        
        try {
        	if(profileStore == null) {
        		ServletResourceLocator resLocator = new ServletResourceLocator(config, new URLResourceLocator());
        		
				configStream = resLocator.getResource(DEVICE_PROFILE_CONFIG_PARAM, DEFAULT_CONFIG);
				profileStore = (DeviceProfiler.getConfigDigester(config)).parse(configStream);
        	}
        	
        	return profileStore.getProfileSet(deviceName);
        } catch(Exception excep) {
            IllegalStateException state = new IllegalStateException("Error loading device profile config.");
            state.initCause(excep);
            throw state;
        }
    }

    /**
     * Construct an instance of the configured (or default) ProfileConfigDigester.
     * @param config The ServletConfig instance.
     * @return An instance of the configured ProfileConfigDigester.
     */
    private static ProfileConfigDigester getConfigDigester(ServletConfig config) {
    	try {
			return (ProfileConfigDigester)Class.forName(ServletParamUtils.getParameterValue(DEVICE_PROFILE_DIGESTER_PARAM, config, DefaultProfileConfigDigester.class.getName())).newInstance();
		} catch (Exception e) {
			IllegalStateException state = new IllegalStateException("Unable to construct ProfileConfigDigester instance.");
			state.initCause(e);
			throw state;
		}
    }
}
