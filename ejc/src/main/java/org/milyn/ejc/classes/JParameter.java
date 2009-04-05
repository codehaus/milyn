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
 * JParameter are paramters found in {@link org.milyn.ejc.classes.JMethod}.
 * @see org.milyn.ejc.classes.JMethod
 * @author bardl
 */
public class JParameter {
    private JType type;
    private String name;

    public JParameter(JType type, String name) {
        this.type = type;
        this.name = name;
    }

    public JType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * A JParameter equals another JParameter if both are of same JType. The name
     * of the parameter does not matter.
     * @param o the object to check for equality.
     * @return true if o equals this JMethod, otherwise false.
     */
    public boolean equals(Object o) {
        if (o instanceof JAttribute) {
            JAttribute a = (JAttribute)o;
            return (a.getType()).equals(getType());
        }
        return false;
    }

    public int hashCode() {
        return getType().hashCode();
    }

    public String toString() {
        return type + " " + name;
    }
}
