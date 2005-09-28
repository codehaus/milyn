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

import org.milyn.device.profile.ProfileSet;

import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class DeviceProfilerTest extends TestCase {

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
		try {
			DeviceProfiler.getDeviceProfile(null, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		try {
			DeviceProfiler.getDeviceProfile("", null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		try {
			DeviceProfiler.getDeviceProfile("xx", null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
	}

	public void testGetDeviceProfile() {
		MockServletConfig config = new MockServletConfig();
		MockServletContext context = new MockServletContext() {			
			public InputStream getResourceAsStream(String param) {
				return getClass().getResourceAsStream("device-profile.xml");
			}			
		};
		ProfileSet profile;
		
		config.setServletContext(context);
		profile = DeviceProfiler.getDeviceProfile("MSIE5", config);
		if(profile == null) {
			fail("Failed to load 'device-profile.xml'.  'html' profile not found.");
		}
		assertTrue(profile.isMember("html"));
		assertTrue(profile.isMember("html4"));
		assertTrue(profile.isMember("css-enabled"));
		assertTrue(profile.isMember("msie"));
		assertTrue(profile.isMember("large"));
		assertTrue(!profile.isMember("medium"));
		assertTrue(!profile.isMember("small"));
		assertTrue(!profile.isMember("PDA"));
		assertTrue(!profile.isMember("nokia"));
		assertTrue(!profile.isMember("html32"));
		assertTrue(!profile.isMember("wml"));
	}
	
	
}
