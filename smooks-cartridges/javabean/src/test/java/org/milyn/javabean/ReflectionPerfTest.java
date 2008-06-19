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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Not really a test at all.  Just some code for doing a rough performance
 * comparison between a reflective and non-reflective method call.
 * Up the LOOP_SIZE to about 1,000,000 to see anything of significance.
 * A reflective call seems to be quite expensive in comparison, but it's
 * still not too bad i.e. < 700ms for 1 million calls. 
 * @author tfennelly
 */
public class ReflectionPerfTest extends TestCase {

    // pump this up to 1 million to see the performance comparison.
    private static final int LOOP_SIZE = 1;
    
    public void test_noreflec_noparam() {
        long start = System.currentTimeMillis();
        
        for(int i = 0; i < LOOP_SIZE; i++) {
            RefX refX = new RefX();
            refX.method1();
        }
        System.out.println("Non-Reflection time: " + (System.currentTimeMillis() - start));
    }

    public void test_reflec_noparam() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method1 = RefX.class.getMethod("method1", null);
        
        long start = System.currentTimeMillis();
        for(int i = 0; i < LOOP_SIZE; i++) {
            RefX refX = new RefX();
            method1.invoke(refX, null);
        }
        
        System.out.println("Reflection time: " + (System.currentTimeMillis() - start));
    }
    
    public void test_noreflec_setparam() {
        long start = System.currentTimeMillis();
        String x = "";
        
        for(int i = 0; i < LOOP_SIZE; i++) {
            RefX refX = new RefX();
            refX.setX(x);
        }
        System.out.println("Non-Reflection time: " + (System.currentTimeMillis() - start));
    }

    public void test_reflec_setparam() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method setXMethod = RefX.class.getMethod("setX", new Class[] {String.class});
        String x = "";
        
        long start = System.currentTimeMillis();
        for(int i = 0; i < LOOP_SIZE; i++) {
            RefX refX = new RefX();
            setXMethod.invoke(refX, new Object[] {x});
        }
        
        System.out.println("Reflection time: " + (System.currentTimeMillis() - start));
    }
    
    private class RefX {
        public void method1() {
            // System.out.println("hey");
        }
        
        public void setX(String x) {
            
        }
    }
}
