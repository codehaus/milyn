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
 * The visibilty of a {@link org.milyn.ejc.classes.JClass}, {@link org.milyn.ejc.classes.JAttribute} and
 * {@link org.milyn.ejc.classes.JMethod}.
 * @author bardl
 */
public enum JVisibility {
    PUBLIC("public"),
    DEFAULT(""),
    PRIVATE("private"),
    PROTECTED("protected");

    private String name;

    JVisibility(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
