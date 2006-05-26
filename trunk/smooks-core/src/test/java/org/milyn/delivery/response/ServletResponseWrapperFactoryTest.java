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

package org.milyn.delivery.response;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockContainerRequest;

import com.mockobjects.servlet.MockHttpServletResponse;

import junit.framework.TestCase;

public class ServletResponseWrapperFactoryTest extends TestCase {

	protected void setUp() throws Exception {
	}

	public void testCreateServletResponseWrapper() {
		SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("testrespwrapfactory", "org.milyn.delivery.response.TestServletResponseWrapper");
		MockContainerRequest request = new MockContainerRequest();
		MockHttpServletResponse mockServletResponse = new MockHttpServletResponse();

		ServletResponseWrapper wrapper = ServletResponseWrapperFactory.createServletResponseWrapper(resourceConfig, request, mockServletResponse);
		assertNotNull(wrapper);
		if(!(wrapper instanceof TestServletResponseWrapper)) {
			fail("Expected TestServletResponseWrapper, got " + wrapper.getClass());
		}
	}
}
