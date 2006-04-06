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

package org.milyn.container.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.milyn.cdr.CDRStore;
import org.milyn.container.ContainerContext;
import org.milyn.device.profile.DefaultProfileConfigDigester;
import org.milyn.device.profile.DefaultProfileStore;
import org.milyn.device.profile.ProfileStore;
import org.milyn.ioc.BeanFactory;
import org.milyn.resource.ClasspathResourceLocator;
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
public class StandaloneContainerContext implements ContainerContext {

	private static final String DEVICE_PROFILE_XML = "/device-profile.xml";
	private Hashtable attributes = new Hashtable();
	private Hashtable sessions = new Hashtable();
	private ContainerResourceLocator resourceLocator;	
	private CDRStore cdrStore;
	private ProfileStore profileStore;

	/**
	 * Public constructor.
	 * <p/>
	 * Context instances constructed in this way can be populated manually with
	 * {@link org.milyn.device.profile.Profile} and {@link org.milyn.cdr.CDRDef}
	 * info.  This supports non-XML type configuration.
	 * @param profileStore The {@link ProfileStore} for tis context.
	 * @param resourceLocator The {@link ContainerResourceLocator} for this context.
	 */
	public StandaloneContainerContext(ProfileStore profileStore, ContainerResourceLocator resourceLocator) {
		if(profileStore == null) {
			throw new IllegalArgumentException("null 'profileStore' arg in constructor call.");
		}
		if(resourceLocator == null) {
			throw new IllegalArgumentException("null 'resourceLocator' arg in constructor call.");
		}
		
		this.profileStore = profileStore;
		this.resourceLocator = resourceLocator;
		cdrStore = new CDRStore(this);
	}
	
	/**
	 * Public constructor.
	 * @param baseDir Directory in which the Smooks Standalone Deployment 
	 * is located. 
	 */
	public StandaloneContainerContext(File baseDir) {
		if(baseDir == null) {
			throw new IllegalArgumentException("null 'baseDir' arg in constructor call.");
		}
		if(!baseDir.exists() || !baseDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid 'baseDir' arg in constructor call.  Directory [" + baseDir.getAbsolutePath() + "] does'nt exist.");
		}
		resourceLocator = (ContainerResourceLocator)BeanFactory.getBean("standaloneResourceLocator");
		if(resourceLocator instanceof URIResourceLocator) {
			((URIResourceLocator)resourceLocator).setBaseURI(baseDir.toURI());
		}
		cdrStore = new CDRStore(this);
		initProfileStore();
		loadCdrarStore();
	}

	
	private void initProfileStore() {
		ContainerResourceLocator resLocator = getResourceLocator();
		DefaultProfileConfigDigester profileDigester = new DefaultProfileConfigDigester();
		InputStream configStream;
		try {
			configStream = resLocator.getResource("device-profiles", DEVICE_PROFILE_XML);
			profileStore = profileDigester.parse(configStream);
		} catch (IOException e) {
			IllegalStateException state = new IllegalStateException("Unable to read [" + DEVICE_PROFILE_XML + "] from container context.");
			state.initCause(e);
			throw state;
		} catch (SAXException e) {
			IllegalStateException state = new IllegalStateException("SAX excepting parsing [" + DEVICE_PROFILE_XML + "].");
			state.initCause(e);
			throw state;
		}
	}


	/**
	 * Load the CDRStore for this instance.
	 */
	private void loadCdrarStore() {
		ContainerResourceLocator containerResLocator;
		BufferedReader listBufferedReader;
		InputStream cdrarListStream;
		
		try {
			containerResLocator = getResourceLocator();
			cdrarListStream = containerResLocator.getResource("smooks-cdr", "/smooks-cdr.lst");
			listBufferedReader = new BufferedReader(new InputStreamReader(cdrarListStream));
			getCdrarStore().load(listBufferedReader);
		} catch (IOException e) {
			IllegalStateException state = new IllegalStateException("Unable to load [/smooks-cdr.lst].");
			state.initCause(e);
			throw state;
		}
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

	public CDRStore getCdrarStore() {
		return cdrStore;
	}

	/**
	 * Get the ProfileStore in use within the Standalone Context.
	 * @return The ProfileStore.
	 */
	public ProfileStore getProfileStore() {
		return profileStore;
	}

	/**
	 * Get a session instance for the specified useragent.
	 * @param useragent Useragent identification.
	 * @return Standalone Session instance.
	 */
	public StandaloneContainerSession getSession(String useragent) {
		StandaloneContainerSession session;
		
		if(useragent == null || useragent.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'useragent' arg in method call.");
		}
		session = (StandaloneContainerSession) sessions.get(useragent);
		if(session == null) {
			// create a new session for the requesting useragent
			session = new StandaloneContainerSession(useragent, this);
			sessions.put(useragent, session);
		}
		
		return session;
	}

}
