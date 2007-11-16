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

package org.milyn.javabean;

import java.util.List;

import org.milyn.container.MockExecutionContext;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */
public class BeanAccessorTest extends TestCase {

    /*
     * Test method for 'org.milyn.javabean.BeanAccessor.getBean(String, ExecutionContext)'
     */
    public void test_BeanAccessor() {
        MockExecutionContext request = new MockExecutionContext();
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();
        
        assertNull(BeanAccessor.getBean("bean1", request));
        
        // Test that we get an error if calling addBean twice with different 'addToList' flags...
        BeanAccessor.addBean("blah", bean1, request, false);
        try {
            BeanAccessor.addBean("blah", bean1, request, true);
        } catch(IllegalArgumentException e) {
            assertEquals("bean [blah] already exists on request and is not a List.  Arg 'addToList' set to true - this is inconsistent!!", e.getMessage());
        }
        BeanAccessor.addBean("blahx", bean1, request, true);
        try {
            BeanAccessor.addBean("blahx", bean1, request, false);
        } catch(IllegalArgumentException e) {
            assertEquals("bean [blahx] already exists on request and is a List.  Arg 'addToList' set to false - this is inconsistent!!", e.getMessage());
        }
 
        // Add a non-List bean...
        BeanAccessor.addBean("a", bean1, request, false);
        assertEquals(bean1, BeanAccessor.getBean("a", request));
        BeanAccessor.addBean("a", bean2, request, false);
        assertEquals(bean2, BeanAccessor.getBean("a", request));
        assertEquals(bean2, BeanAccessor.getBeanMap(request).get("a"));
        
        // Add a bean to a bean list...
        BeanAccessor.addBean("b", bean1, request, true);
        assertEquals(bean1, BeanAccessor.getBean("b", request));
        BeanAccessor.addBean("b", bean2, request, true);
        assertEquals(bean2, BeanAccessor.getBean("b", request));
        List list = (List)BeanAccessor.getBeanMap(request).get("bList");
        assertEquals(2, list.size());
    }
}
