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

package org.milyn;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.milyn.container.MockContainerRequest;
import org.milyn.delivery.SmooksXML;
import org.milyn.util.SmooksUtil;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class SmooksTest extends TestCase {

	private MockContainerRequest request;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		request = new SmooksUtil().getRequest("devicename");
	}
	
	public void test_applyTransform_bad_params() {
		InputStream stream = getClass().getResourceAsStream("html_1.html");
		SmooksXML smooks = new SmooksXML(request);
		
		try {
			smooks.applyTransform((Reader)null);
			fail("Expected exception on null stream");
		} catch (IllegalArgumentException e) {
			//Expected
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
	}
	
	public void test_applyTransform_DocumentCheck() {
		SmooksXML smooks;
		InputStream stream = null;
		Node deliveryNode = null;
		
		stream = getClass().getResourceAsStream("html_1.html");
		smooks = new SmooksXML(request);
		try {
			deliveryNode = smooks.applyTransform(new InputStreamReader(stream));
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		assertNotNull("Null transform 'Document' return.", deliveryNode);
	}	
	
}