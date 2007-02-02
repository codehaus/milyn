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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.milyn.device.profile.HttpAcceptHeaderProfile;
import org.milyn.device.profile.Profile;
import org.milyn.device.profile.ProfileSet;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

/**
 * 
 * @author tfennelly
 */
public class DeviceProfilerTest extends TestCase {

	MockServletConfig config = new MockServletConfig();
	MockServletContext context = new MockServletContext() {			
		public InputStream getResourceAsStream(String param) {
			return getClass().getResourceAsStream("device-profile.xml");
		}			
	};
	MockHttpServletRequest request = new FixedMockHttpServletRequest();
	
	/**
	 * @param arg0
	 */
	public DeviceProfilerTest(String arg0) {
		super(arg0);
	}
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetDeviceProfile_params() {
		testGetDeviceProfile_params(null, request, config);
		testGetDeviceProfile_params("", request, config);
		testGetDeviceProfile_params("xx", null, config);
		testGetDeviceProfile_params("xx", request, null);
	}

	/**
	 * 
	 */
	private void testGetDeviceProfile_params(String deviceName, HttpServletRequest request, ServletConfig config) {
		try {
			DeviceProfiler.getDeviceProfile(deviceName, request, config);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
	}

	public void testGetDeviceProfile() {
		ProfileSet profileSet;
		
		config.setServletContext(context);

		// Add an "Accept" request header - should be converted into profiles.
		request.setupAddHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		// Just make sure it's set up correctly
		assertTrue(request.getHeader("Accept") != null);
		
		profileSet = DeviceProfiler.getDeviceProfile("MSIE5", request, config);
		if(profileSet == null) {
			fail("Failed to load 'device-profile.xml'.  'html' profile not found.");
		}
		assertTrue(profileSet.isMember("html"));
		assertTrue(profileSet.isMember("html4"));
		assertTrue(profileSet.isMember("css-enabled"));
		assertTrue(profileSet.isMember("msie"));
		assertTrue(profileSet.isMember("large"));
		assertTrue(profileSet.isMember("accept:text/xml"));
		assertTrue(profileSet.isMember("accept:application/xhtml+xml"));
		assertTrue(!profileSet.isMember("medium"));
		assertTrue(!profileSet.isMember("small"));
		assertTrue(!profileSet.isMember("PDA"));
		assertTrue(!profileSet.isMember("nokia"));
		assertTrue(!profileSet.isMember("html32"));
		assertTrue(!profileSet.isMember("wml"));
		
		HttpAcceptHeaderProfile acceptProfile = (HttpAcceptHeaderProfile)profileSet.getProfile("accept:text/html");
		if(acceptProfile.getParamNumeric("q", 220.1) != 0.9) {
			fail("Failed to create the HttpAcceptHeaderProfile properly - didn't parse the accept params properly.");
		}		
		acceptProfile = (HttpAcceptHeaderProfile)profileSet.getProfile("accept:text/xml");
		if(acceptProfile.getParamNumeric("q", 1.0) != 1.0) {
			fail("Failed to create the HttpAcceptHeaderProfile properly - didn't parse the accept params properly.");
		}
		
		// Test that the iterator method returns an interator and that the iterator
		// spits out instance of the Profile.
		Iterator iterator = profileSet.iterator();
		Profile profile = (Profile)iterator.next();
	}
	
	private class FixedMockHttpServletRequest extends MockHttpServletRequest {
		private Vector names = new Vector();
		
		public void setupAddHeader(String name, String value) {
			super.setupAddHeader(name, value);
			names.add(name);
		}

		public Enumeration getHeaderNames() {
			return names.elements();
		}
	}
}
