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

import java.util.List;
import java.util.ArrayList;

/**
 * JMethod are methods found in JClass.
 * @see org.milyn.ejc.classes.JClass
 * @author bardl
 */
public class JMethod {
    public static String GET_PREFIX = "get";
    public static String SET_PREFIX = "set";

    private JVisibility visibility;
    private JType returnType;
    private String name;
    private List<JParameter> parameters;
    private List<JStatement> statements;


    public JMethod(JVisibility visibility, JType returnType, String name, List<JParameter> parameters, List<JStatement> statements) {
        this.visibility = visibility;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.statements = statements;
    }

    public JVisibility getVisibility() {
        return visibility;
    }

    public JType getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<JStatement> getStatements() {
        if (statements == null) {
            statements = new ArrayList<JStatement>();
        }
        return statements;
    }

    public List<JParameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<JParameter>();
        }
        return parameters;
    }

    public boolean equals(Object o) {
        return o instanceof JMethod && ((JMethod) o).equalityString().equals(equalityString());
    }

    

    public String equalityString() {
        StringBuilder result = new StringBuilder();
        result.append(getReturnType());
        result.append(getName());

        for (JParameter p : getParameters()) {
            result.append(p.getType());
        }
        return result.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

}
