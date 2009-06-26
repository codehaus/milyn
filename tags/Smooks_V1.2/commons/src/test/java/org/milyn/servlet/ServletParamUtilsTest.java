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

import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class ServletParamUtilsTest extends TestCase {

	/**
	 * @param arg0
	 */
	public ServletParamUtilsTest(String arg0) {
		super(arg0);
	}

	private MockServletConfig getServletConfig() {
		MockServletConfig config = new MockServletConfig();
		config.setServletContext(new MockServletContext());
		return config;
	}

	public void testGetResource_exceptions() {
		try {
			ServletParamUtils.getParameterValue(null, null);
			fail("no IllegalArgumentException on null 'paramName' param");
		} catch (IllegalArgumentException e) {
		}
		try {
			ServletParamUtils.getParameterValue("", null);
			fail("no IllegalArgumentException on empty 'paramName' param");
		} catch (IllegalArgumentException e) {
		}
		try {
			ServletParamUtils.getParameterValue(" ", null);
			fail("no IllegalArgumentException on whitespace 'paramName' param");
		} catch (IllegalArgumentException e) {
		}
		try {
			ServletParamUtils.getParameterValue("paramName", null);
			fail("no IllegalStateException on null 'config' param");
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test that getResource loads based on the servlet config param -
	 * <init-param>
	 */
	public void testGetResource_initparam() {
		MockServletConfig config = getServletConfig();

		config.setInitParameter("paraX", "paraX-config");
		((MockServletContext) config.getServletContext()).setInitParameter(
				"paraX", "paraX-context");
		try {
			assertEquals("paraX-config", ServletParamUtils.getParameterValue(
					"paraX", config));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test that getResource loads based on the servlet context param -
	 * <context-param>
	 */
	public void testGetResource_contextparam() {
		MockServletConfig config = getServletConfig();

		// don't set the param in the servlet config
		((MockServletContext) config.getServletContext()).setInitParameter(
				"paraX", "paraX-context");
		try {
			assertEquals("paraX-context", ServletParamUtils.getParameterValue(
					"paraX", config));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
