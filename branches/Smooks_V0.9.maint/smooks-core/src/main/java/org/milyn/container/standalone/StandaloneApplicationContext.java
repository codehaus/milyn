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
import org.milyn.profile.*;
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
	private Hashtable attributes = new Hashtable();
	private Hashtable sessions = new Hashtable();
	private ContainerResourceLocator resourceLocator;	
	private SmooksResourceConfigurationStore resStore;
	private DefaultProfileStore profileStore = new DefaultProfileStore();
    
    /**
     * Public constructor.
     * <p/>
     * 
     */
    public StandaloneApplicationContext() {
        resourceLocator = new URIResourceLocator();
        ((URIResourceLocator)resourceLocator).setBaseURI(URI.create(URIResourceLocator.SCHEME_CLASSPATH + ":/"));
        resStore = new SmooksResourceConfigurationStore(this);
        // Add the open profile...
        profileStore.addProfileSet(new DefaultProfileSet(OPEN_PROFILE_NAME));
    }

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#setAttribute(java.lang.Object, java.lang.Object)
	 */
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#getAttribute(java.lang.Object)
	 */
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	/* (non-Javadoc)
	 * @see org.milyn.container.BoundAttributeStore#removeAttribute(java.lang.Object)
	 */
	public void removeAttribute(Object key) {
		attributes.remove(key);
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
	public DefaultProfileStore getProfileStore() {
		return profileStore;
	}
}
