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
package org.milyn.javabean.gen;

import junit.framework.TestCase;
import org.milyn.javabean.Order;

import java.io.StringWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ConfigGeneratortTest extends TestCase {

    public void test() throws ClassNotFoundException, IOException {
        Properties properties = new Properties();
        StringWriter writer = new StringWriter();

        properties.setProperty(ConfigGenerator.ROOT_BEAN_CLASS, Order.class.getName());

        ConfigGenerator generator = new ConfigGenerator(properties, writer);

        generator.generate();

        System.out.println(writer.toString());
    }

    public void test_commandLine() throws ClassNotFoundException, IOException {
        ConfigGenerator.main(new String[] {"-c", Order.class.getName(), "-o", "./target/binding-config-test.xml"});
    }
}
