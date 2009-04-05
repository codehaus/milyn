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
 * JType represents the type of a {@link org.milyn.ejc.classes.JClass} or {@link org.milyn.ejc.classes.JAttribute}.
 * @see org.milyn.ejc.classes.JClass
 * @see org.milyn.ejc.classes.JAttribute
 * @author bardl
 */
public interface JType {

    /**
     * Return the simple name of the type.
     * @return the typename.
     */
    String getName();

    /**
     * Returns the package where the type is located.
     * @return the packagename.
     */
    JPackage getPackage();
}
