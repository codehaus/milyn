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

package org.milyn.cdr;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */
public class SmooksResourceConfigurationListTest extends TestCase {

    public void testConstructor() {
        testBadArgs(null);
        testBadArgs(" ");

        SmooksResourceConfigurationList list = new SmooksResourceConfigurationList("list-name");
        assertEquals("list-name", list.getName());
    }

    private void testBadArgs(String name) {
        try {
            new SmooksResourceConfigurationList(name);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // OK
        }
    }
    
    public void testAdd() {
        SmooksResourceConfigurationList list = new SmooksResourceConfigurationList("list-name");
        
        assertTrue(list.isEmpty());
        list.add(new SmooksResourceConfiguration("*", "a/b.zap"));
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals("a/b.zap", list.get(0).getPath());
        
        
        list.add(new SmooksResourceConfiguration("*", "c/d.zap"));
        assertEquals(2, list.size());        
        assertEquals("c/d.zap", list.get(1).getPath());
    }
}
