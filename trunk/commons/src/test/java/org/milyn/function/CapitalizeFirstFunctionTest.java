/*
 * Milyn - Copyright (C) 2006
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (version 2.1) as published by the Free Software
 * Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.function;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public class CapitalizeFirstFunctionTest extends TestCase {

    public void test_execute() {
        CapitalizeFirstFunction function = new CapitalizeFirstFunction();

        assertEquals("Maurice", function.execute("maurice"));
        assertEquals("MAURICE", function.execute("MAURICE"));
        assertEquals(" Maurice", function.execute(" maurice"));
        assertEquals("Maurice zeijen", function.execute("maurice zeijen"));
    }

}
