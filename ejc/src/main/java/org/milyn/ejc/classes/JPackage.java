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
package org.milyn.ejc.classes;

/**
 * JPackage is {@link org.milyn.ejc.classes.JClass} package.
 * @see org.milyn.ejc.classes.JClass
 * @author bardl
 */
public class JPackage {
    public static final JPackage JAVA_UTIL = new JPackage("java.util");
    public static final JPackage JAVA_LANG = new JPackage("java.lang");

    private String name;

    public JPackage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof  JPackage) {
            return ((JPackage)obj).getName().equals(name);
        }
        return false;
    }

    public String toString() {
        return "package " + name;
    }
}
