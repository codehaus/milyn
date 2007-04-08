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

package org.milyn.container.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfigurationStore;
import org.milyn.container.ApplicationContext;
import org.milyn.profile.DefaultProfileConfigDigester;
import org.milyn.profile.ProfileStore;
import org.milyn.profile.Profile;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.resource.ContainerResourceLocator;
import org.milyn.resource.URIResourceLocator;
import org.xml.sax.SAXException;

/**
 * Standalone container execution context for Smooks.
 * <p/>
 * This context allows Smooks to be executed outside the likes of a 
 * Servlet Container.
 * @author tfennelly
 */
public class StandaloneApplicationContext implements ApplicationContext {

    /**
     * The open profile is a special profile for {@link org.milyn.container.ExecutionContext}
     * instances that are not interested in using profiles.
     */
    public static final String OPEN_PROFILE_NAME = Profile.class.getName() + "#OPEN_PROFILE_NAME";

    private static Log logger = LogFactory.getLog(StandaloneApplicationContext.class);
	private static final String DEVICE_PROFILE_XML = "/device-profile.xml";
	private Hashtable attributes = new Hashtable();
	private Hashtable sessions = new Hashtable();
	private ContainerResourceLocator resourceLocator;	
	private SmooksResourceConfigurationStore resStore;
	private ProfileStore profileStore;
    
    /**
     * Public constructor.
     * <p/>
     * 
     */
    public StandaloneApplicationContext() {
        resourceLocator = new URIResourceLocator();
        ((URIResourceLocator)resourceLocator).setBaseURI(URI.create(URIResourceLocator.SCHEME_CLASSPATH + ":/"));
        resStore = new SmooksResourceConfigurationStore(this);
        initProfileStore();
    }

	/**
	 * Public constructor.
	 * <p/>
	 * Context instances constructed in this way can be populated manually with
	 * {@link org.milyn.profile.Profile} and {@link org.milyn.cdr.SmooksResourceConfiguration}
	 * info.  This supports non-XML type configuration.
	 * @param profileStore The {@link ProfileStore} for tis context.
	 * @param resourceLocator The {@link ContainerResourceLocator} for this context.
	 */
	public StandaloneApplicationContext(ProfileStore profileStore, ContainerResourceLocator resourceLocator) {
        this();
		if(profileStore == null) {
			throw new IllegalArgumentException("null 'profileStore' arg in constructor call.");
		}
		if(resourceLocator == null) {
			throw new IllegalArgumentException("null 'resourceLocator' arg in constructor call.");
		}
		
		this.profileStore = profileStore;
		this.resourceLocator = resourceLocator;
	}
	
	private void initProfileStore() {
		ContainerResourceLocator resLocator = getResourceLocator();
		DefaultProfileConfigDigester profileDigester = new DefaultProfileConfigDigester();
		InputStream configStream;

        try {
			configStream = resLocator.getResource("device-profiles", DEVICE_PROFILE_XML);
		} catch (IOException e) {
            logger.warn("Device profile config file [" + DEVICE_PROFILE_XML + "] not available from container.");
            return ;
        }
		try {
            profileStore = profileDigester.parse(configStream);
		} catch (IOException e) {
            logger.warn("Device profile config file [" + DEVICE_PROFILE_XML + "] not available from container. " + e.getMessage());
		} catch (SAXException e) {
			IllegalStateException state = new IllegalStateException("SAX excepting parsing [" + DEVICE_PROFILE_XML + "].");
			state.initCause(e);
			throw state;
		}

        // Add the open profile...
        profileStore.addProfileSet(OPEN_PROFILE_NAME, new DefaultProfileSet(OPEN_PROFILE_NAME));
    }

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	public ContainerResourceLocator getResourceLocator() {
		return resourceLocator;
	}
    public void setResourceLocator(ContainerResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

	public SmooksResourceConfigurationStore getStore() {
		return resStore;
	}

	/**
	 * Get the ProfileStore in use within the Standalone Context.
	 * @return The ProfileStore.
	 */
	public ProfileStore getProfileStore() {
		return profileStore;
	}
}
