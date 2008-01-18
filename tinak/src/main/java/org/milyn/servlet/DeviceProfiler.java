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

package org.milyn.servlet;

import java.io.InputStream;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.milyn.profile.DefaultProfileConfigDigester;
import org.milyn.profile.HttpAcceptHeaderProfile;
import org.milyn.profile.ProfileConfigDigester;
import org.milyn.profile.ProfileSet;
import org.milyn.profile.ProfileStore;
import org.milyn.resource.ServletResourceLocator;
import org.milyn.resource.URIResourceLocator;

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
 * <p/>
 * This profiler also adds the requesting devices "Accept" header media types as profiles.  
 * See {@link HttpAcceptHeaderProfile}.
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

    private static final String PROFILE_STORE_CTX_KEY = DeviceProfiler.class.getName() + "#CONTEXT_KEY";

    /**
     * Get the ProfileSet for the named device.
     * @param deviceName The name of the device for which a ProfileSet is sought.
     * @param request Requesting device's HttpServletRequest.  Used to extract
     * request headers from which a set of profiles is created.
     * @param config Servlet config used to load the profile configuration.
     * @return DeviceIdent instance
     */
    protected static ProfileSet getDeviceProfile(String deviceName, HttpServletRequest request, ServletConfig config) {
        InputStream configStream;

        if(deviceName == null) {
        	throw new IllegalArgumentException("null 'deviceName' param in method call.");
        }
        deviceName = deviceName.trim();
        if(deviceName.equals("")) {
        	throw new IllegalArgumentException("empty 'deviceName' param in method call.");
        }
        if(request == null) {
        	throw new IllegalArgumentException("null 'request' param in method call.");
        }
        if(config == null) {
        	throw new IllegalArgumentException("null 'config' param in method call.");
        }
        
        try {
            ProfileStore profileStore;
            ProfileSet profileSet;

            profileStore = getProfileStore(config.getServletContext());
            if(profileStore == null) {
                synchronized (config) {
                    profileStore = getProfileStore(config.getServletContext());
                    if(profileStore == null) {
                        ServletResourceLocator resLocator = new ServletResourceLocator(config, new URIResourceLocator());

                        configStream = resLocator.getResource(DEVICE_PROFILE_CONFIG_PARAM, DEFAULT_CONFIG);
                        profileStore = (DeviceProfiler.getConfigDigester(config)).parse(configStream);
                        setProfileStore(profileStore, config.getServletContext());
                    }
                }
            }
        	profileSet = profileStore.getProfileSet(deviceName);
    		// Add the request accept header media types as profiles...
    		addAcceptHeaderProfiles(request, profileSet);
        	
        	return profileSet;
        } catch(Exception excep) {
            IllegalStateException state = new IllegalStateException("Error loading device profile config.");
            state.initCause(excep);
            throw state;
        }
    }

    private static ProfileStore getProfileStore(ServletContext servletContext) {
        return (ProfileStore) servletContext.getAttribute(PROFILE_STORE_CTX_KEY);
    }

    public static void setProfileStore(ProfileStore profileStore, ServletContext servletContext) {
        servletContext.setAttribute(PROFILE_STORE_CTX_KEY, profileStore);
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

    /**
     * Parse the request Accept header and add the media entities as profiles.
     * <p/>
     * See {@link HttpAcceptHeaderProfile} and RFC2068 section 14.1.
     * @param request The HTTP request containing the request headers.
     * @param profileSet The ProfileSet to be updated.
     */
    private static void addAcceptHeaderProfiles(HttpServletRequest request, ProfileSet profileSet) {
    	String acceptHeaderValue = request.getHeader("Accept");
    	
    	if(acceptHeaderValue != null) {
    		StringTokenizer acceptMediaRules = new StringTokenizer(acceptHeaderValue, ",");
    		
    		while(acceptMediaRules.hasMoreTokens()) {
    			String[] mediaRule = acceptMediaRules.nextToken().split(";");
    			String media = mediaRule[0];
    			
    			if(mediaRule.length > 1) {
    				// Just passing in the whole array of Strings.  The first entry is not a param
    				// at all (it's the actual media type) but that won't cause a problem for the 
    				// getParam methods - they'll simple always fail on this media type entry.
        			profileSet.addProfile(new HttpAcceptHeaderProfile(media, mediaRule));
    			} else {
        			profileSet.addProfile(new HttpAcceptHeaderProfile(media, new String[0]));
    			}
    		}
    	}
	}
}
