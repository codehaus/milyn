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

import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.milyn.device.UAContext;
import org.milyn.device.ident.UnknownDeviceException;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class ServletUAContextTest extends TestCase {

	/**
	 * Constructor for ServletUAContextTest.
	 * @param arg0
	 */
	public ServletUAContextTest(String arg0) {
		super(arg0);
	}

	public void testContructor() {
		try {
			ServletUAContext.UnitTest.getUAContext(null);
			fail("no exception on null 'commonName' arg");
		} catch (IllegalArgumentException arg) {
		}
		ServletUAContext.UnitTest.getUAContext("device");
	}

	public void testGetInstance_params() {
		try {
			ServletUAContext.getInstance(null, null);
			fail("no exception on null 'request' arg");
		} catch(IllegalArgumentException arg) {
		} catch(UnknownDeviceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			ServletUAContext.getInstance(new MockHttpServletRequest(), null);
			fail("no exception on null 'config' arg");
		} catch(IllegalArgumentException arg) {
		} catch(UnknownDeviceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Doing this because of the damn MockRequest "AssertionFailure: attributes has run out out of objects".
	 * Might be doing something wrong but for god sake, why is the default behaviour like this???
	 * Oh yeah, and you need to set the session too!!!
	 * @return
	 */
	private HttpServletRequest getMockRequest() {
		MockHttpServletRequest request = null;

		request = new MockHttpServletRequest() {
			Hashtable attributes = new Hashtable();
			public Object getAttribute(String name) {
				return attributes.get(name);
			}
			public void setAttribute(String name, Object value) {
				attributes.put(name, value);
			}
		};
		request.setSession(new MockHttpSession());
		
		return request;
	}
	
	public void testGetInstance_request_session() {
		HttpServletRequest request = getMockRequest();
		ServletConfig config = new MockServletConfig();
		UAContext uaContext = null;

		uaContext = ServletUAContext.UnitTest.getUAContext("device");
		request.getSession().setAttribute(ServletUAContext.CONTEXT_KEY, uaContext);
		try {
			// References should be the same
			if(ServletUAContext.getInstance(request, config) != uaContext) {
				fail("failed to retreive context from servlet session.");
			}
		} catch (UnknownDeviceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Create a new instance to get a different reference but set it
		// in the request this time.		
		uaContext = ServletUAContext.UnitTest.getUAContext("device");
		request.setAttribute(ServletUAContext.CONTEXT_KEY, uaContext);
		try {
			// References should be the same
			if(ServletUAContext.getInstance(request, config) != uaContext) {
				fail("failed to retreive context from servlet request.");
			}
		} catch (UnknownDeviceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		// TODO: Try get some tests in here for getting the ServletUAContext 
		// from the ServletContext - requires a refactor of ServletUAContext. 
	}

	public void testGetCommonName() {
		UAContext uaContext = ServletUAContext.UnitTest.getUAContext("device");
		assertEquals("device", uaContext.getCommonName());
	}

	public void testIsMember() {
		MockServletContext servletContext = new MockServletContext();
		UAContext uaContext = ServletUAContext.UnitTest.getUAContext("device1");

		try {
			uaContext.getProfileSet().isMember("prof1");
			fail("Expected IllegalStateException - call to isMember without matching.");
		} catch(IllegalStateException e) {
			// expected
		}
	}
}
