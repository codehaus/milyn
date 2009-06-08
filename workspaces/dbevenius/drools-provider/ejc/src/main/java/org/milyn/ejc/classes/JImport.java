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
 * JImport are import statements found in JClass.
 * @see org.milyn.ejc.classes.JClass
 * @author bardl
 */
public class JImport {
    private JType type;    

    public JImport(JType type) {
        this.type = type;
    }

    public JType getType() {
        return type;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof JImport) {
//            return ((JImport)obj).getType().equals(getType());
            return toString().equals(obj.toString());
        }
        return false;
    }

    public String toString() {
        if (getType() instanceof JSimpleType) {
            if (getType().getPackage() != null) {
                return "import " + getType().getPackage().getName() + "." + getType().toString(); 
            }
        }
        return "import " + getType().toString();
    }
}
