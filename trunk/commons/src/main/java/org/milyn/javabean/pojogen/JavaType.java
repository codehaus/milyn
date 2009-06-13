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
package org.milyn.javabean.pojogen;

import org.milyn.assertion.AssertArgument;

import java.util.Set;

/**
 * Java type model.
 * <p/>
 * Includes generic typing.
 *
 * @author bardl
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class JavaType {

    private Class type;
    private Class genericType;

    public JavaType(Class type) {
        AssertArgument.isNotNull(type, "type");
        this.type = type;
    }

    public JavaType(Class type, Class genericType) {
        AssertArgument.isNotNull(type, "type");
        AssertArgument.isNotNull(genericType, "genericType");
        this.type = type;
        this.genericType = genericType;
    }

    public Class getType() {
        return type;
    }

    public Class getGenericType() {
        return genericType;
    }

    public void addImports(Set<Class> importSet) {
        AssertArgument.isNotNull(importSet, "importSet");
        importSet.add(type);
        if(genericType != null) {
            importSet.add(genericType);
        }
    }

    public String toString() {
        if(genericType != null) {
            return type.getSimpleName() + "<" + genericType.getSimpleName() + ">";
        } else {
            return type.getSimpleName();
        }
    }
}