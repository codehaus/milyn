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
package org.milyn.classpath;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ScannerTest extends TestCase {

    public void test() throws IOException {
        InstanceOfFilter filter = new InstanceOfFilter(Filter.class);
        Scanner scanner = new Scanner(filter);

        long start = System.currentTimeMillis();
        scanner.scanClasspath(Thread.currentThread().getContextClassLoader());
        System.out.println("Took: " + (System.currentTimeMillis() - start));
        List<Class> classes = filter.getClasses();

        System.out.println(classes);
        assertEquals(2, classes.size());
        assertEquals(InstanceOfFilter.class, classes.get(0));
        assertEquals(Filter.class, classes.get(1));
    }
}
