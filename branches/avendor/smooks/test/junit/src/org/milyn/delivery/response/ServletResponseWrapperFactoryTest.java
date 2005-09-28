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

package org.milyn.delivery.response;

import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.container.MockContainerRequest;
import org.milyn.util.SmooksUtil;

import com.mockobjects.servlet.MockHttpServletResponse;

import junit.framework.TestCase;

public class ServletResponseWrapperFactoryTest extends TestCase {

	protected void setUp() throws Exception {
	}

	public void testCreateServletResponseWrapper() {
		SmooksUtil util = new SmooksUtil();		
		CDRDef cdrDef = util.addCDRDef("testrespwrapfactory", "mydevice", "org.milyn.delivery.response.TestServletResponseWrapper", null);
		MockContainerRequest request = util.getRequest("mydevice");
		MockHttpServletResponse mockServletResponse = new MockHttpServletResponse();
		List cdresList = request.getDeliveryConfig().getCDRDefs("testrespwrapfactory");

		ServletResponseWrapper wrapper = ServletResponseWrapperFactory.createServletResponseWrapper((CDRDef)cdresList.get(0), request, mockServletResponse);
		assertNotNull(wrapper);
		if(!(wrapper instanceof TestServletResponseWrapper)) {
			fail("Expected TestServletResponseWrapper, got " + wrapper.getClass());
		}
	}
}
