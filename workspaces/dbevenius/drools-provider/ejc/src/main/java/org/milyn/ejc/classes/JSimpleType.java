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

import java.util.Map;
import java.util.List;

/**
 * JSimpleType consists of types defined in the Smooks edifact model found in Field,
 * Component and SubComponent. In addition the JSimpleType contains the void-type to
 * used when generating implementation for set-methods in {@link org.milyn.ejc.classes.JMethod}.
 *
 * @author bardl
 */
public class JSimpleType implements JType {
    public static final String DEFAULT_TYPE = "String";
    
    public static final JSimpleType VOID = new JSimpleType("void", null, null);

    private String name;
    private JPackage classPackage;
    private List<Map.Entry<String,String>> parameters;

    public JSimpleType(String name, List<Map.Entry<String,String>> parameters, JPackage classPackage) {
        this.name = name;
        this.parameters = parameters;
        this.classPackage = classPackage;
    }

    public String getName() {
        return name;
    }

    public JPackage getPackage() {
        return classPackage;
    }

    public String toString() {
        return name;
    }

    public List<Map.Entry<String,String>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map.Entry<String,String>> parameters) {
        this.parameters = parameters;
    }
}
