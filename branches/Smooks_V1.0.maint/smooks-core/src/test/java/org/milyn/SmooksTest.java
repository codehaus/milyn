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

package org.milyn;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.SmooksDOMFilter;
import org.milyn.profile.DefaultProfileSet;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * 
 * @author tfennelly
 */
public class SmooksTest extends TestCase {

    private ExecutionContext execContext;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        Smooks smooks = new Smooks();
        SmooksUtil.registerProfileSet(DefaultProfileSet.create("device1", new String[] {"profile1"}), smooks);
        execContext = new StandaloneExecutionContext("device1", smooks.getApplicationContext());
    }
	
	public void test_applyTransform_bad_params() {
		SmooksDOMFilter smooks = new SmooksDOMFilter(execContext);
		
		try {
			smooks.filter((Reader)null);
			fail("Expected exception on null stream");
		} catch (IllegalArgumentException e) {
			//Expected
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
	}
	
	public void test_applyTransform_DocumentCheck() {
		SmooksDOMFilter smooks;
		InputStream stream = null;
		Node deliveryNode = null;
		
		stream = getClass().getResourceAsStream("html_1.html");
		smooks = new SmooksDOMFilter(execContext);
		try {
			deliveryNode = smooks.filter(new InputStreamReader(stream));
		} catch (SmooksException e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.getMessage());
		}
		assertNotNull("Null transform 'Document' return.", deliveryNode);
	}	
	
}
