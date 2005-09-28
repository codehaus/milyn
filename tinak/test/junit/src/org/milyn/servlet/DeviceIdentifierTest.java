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

import org.milyn.device.ident.UnknownDeviceException;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class DeviceIdentifierTest extends TestCase {
	
	/**
	 * @param arg0
	 */
	public DeviceIdentifierTest(String arg0) {
		super(arg0);
	}

	public void testMatchDevice_params() {
		try {
			DeviceIdentifier.matchDevice(null, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		try {
			DeviceIdentifier.matchDevice(new MockServletConfig(), null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
	}

	public void testMatchDevice() {
		MockServletConfig config = new MockServletConfig();
		MockServletContext context = new MockServletContext() {			
			public InputStream getResourceAsStream(String param) {
				return DeviceIdentifierTest.class.getResourceAsStream("device-ident.xml");
			}			
		};
		MockHttpServletRequest request = new MockHttpServletRequest(); 
		
		config.setServletContext(context);
		
		try {
			request.setupAddHeader("User-Agent", "Nokia3350.xxxxxx");
			String device = DeviceIdentifier.matchDevice(config, request);
			assertEquals("nokia3350", device);
		} catch (UnknownDeviceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			request.setupAddHeader("User-Agent", "xxxxxxxx.xxxxxx");
			String device = DeviceIdentifier.matchDevice(config, request);
			fail("expected UnknownDeviceException");
		} catch (UnknownDeviceException e) {
			// expected
		}
	}
}
