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

import java.util.Hashtable;

import org.milyn.container.ContainerSession;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryConfigImpl;
import org.milyn.device.UAContext;
import org.milyn.device.ident.UnknownDeviceException;
import org.milyn.device.profile.ProfileSet;

/**
 * Standalone Container Session.
 * @author tfennelly
 */
public class StandaloneContainerSession implements ContainerSession {
	private Hashtable attributes = new Hashtable();
	private String browserName;
	private StandaloneContainerContext context;
	private StandaloneUAContext uaContext;
	private ContentDeliveryConfig deliveryConfig;

	/**
	 * Public constructor.
	 * <p/>
	 * The session is constructed within the scope of the container context.
	 * @param browserName The name of the browser being emulated for this session.
	 * @param context Container Context instance.
	 * @throws UnknownDeviceException Thrown when the named device/browser is not known i.e. typically means
	 * the deviceName is not mapped into any profiles in device-profiles.xml.
	 */
	public StandaloneContainerSession(String browserName, StandaloneContainerContext context) throws UnknownDeviceException {
		if(browserName == null) {
			throw new IllegalArgumentException("null 'browserName' arg in constructor call.");
		}
		if(context == null) {
			throw new IllegalArgumentException("null 'context' arg in constructor call.");
		}
		this.browserName = browserName;
		this.context = context;
		uaContext = new StandaloneUAContext(browserName);
		deliveryConfig = ContentDeliveryConfigImpl.getInstance(uaContext, context);
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

	/**
	 * Get the name of the browser being emulated on this session.
	 * @return The browser name.
	 */
	public String getBrowserName() {
		return browserName;
	}

	/**
	 * Get the Standalone Container Context within which this Session is
	 * opperating.
	 * @return The context.
	 */
	public StandaloneContainerContext getContext() {
		return context;
	}

	/**
	 * Get the UAContext associated with this sessions browser.
	 * @return The UAContext.
	 */
	public UAContext getUseragentContext() {
		return uaContext;
	}

	/**
	 * Get the ContentDeliveryConfig associated with this sessions browser.
	 * @return The ContentDeliveryConfig.
	 */
	public ContentDeliveryConfig getDeliveryConfig() {
		return deliveryConfig;
	}
	
	/**
	 * Useragent Context for the Standalone Container.
	 * @author tfennelly
	 */
	private class StandaloneUAContext implements UAContext {
		private static final long serialVersionUID = 1L;
		private String commonName;
		private ProfileSet profileSet;
		
		private StandaloneUAContext(String commonName) throws UnknownDeviceException {
			this.commonName = commonName;
			profileSet = context.getProfileStore().getProfileSet(commonName);
		}
		
		public String getCommonName() {
			return commonName;
		}

		public ProfileSet getProfileSet() {
			return profileSet;
		}
	}
}
