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

import org.milyn.ejc.EJCUtils;

/**
 * JAttribute are attributes found in JClass.
 * @see org.milyn.ejc.classes.JClass
 * @author bardl
 */
public class JAttribute extends XmlElement {
    private JType type;
    private String name;

    public JAttribute(JType type, String name) {
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
     * A JAttribute equals another attribute when the name of the attributes are equal.
     * @param o the JAttribute to check for equality.
     * @return true when o equals this JAttribute.
     */
    public boolean equals(Object o) {
        if (o instanceof JAttribute) {
            JAttribute a = (JAttribute)o;
            return (a.getName()).equals(getName());
        }
        return false;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public String toString() {
        return name; 
    }
    
}
