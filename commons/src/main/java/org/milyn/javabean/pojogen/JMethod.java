/*
	Milyn - Copyright (C) 2006 - 2010

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

import java.util.*;

/**
 * Method model.
 * @author bardl
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class JMethod {

    private JType returnType;
    private String methodName;
    private List<JNamedType> parameters = new ArrayList<JNamedType>();
    private Set<JType> exceptions = new LinkedHashSet<JType>();
    private StringBuilder bodyBuilder = new StringBuilder();

    public JMethod(String methodName) {
        AssertArgument.isNotNull(methodName, "methodName");
        this.returnType = new JType(void.class);
        this.methodName = methodName;
    }

    public JMethod(JType returnType, String methodName) {
        AssertArgument.isNotNull(returnType, "returnType");
        AssertArgument.isNotNull(methodName, "methodName");
        this.returnType = returnType;
        this.methodName = methodName;
    }

    public JType getReturnType() {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public JMethod addParameter(JType type, String parameterName) {
        return addParameter(new JNamedType(type, parameterName));
    }

    public JMethod addParameter(JNamedType parameter) {
        parameters.add(parameter);
        return this;
    }

    public List<JNamedType> getParameters() {
        return parameters;
    }

    public JMethod appendToBody(String codeString) {
        bodyBuilder.append(codeString);
        return this;
    }

    public int bodyLength() {
        return bodyBuilder.length();
    }

    public String getBody() {
        return bodyBuilder.toString();
    }

    public Set<JType> getExceptions() {
        return exceptions;
    }

    public String getParamSignature() {
        StringBuilder signature = new StringBuilder();

        signature.append("(");
        for(int i = 0; i < parameters.size(); i++) {
            if(i > 0) {
                signature.append(", ");
            }
            signature.append(parameters.get(i));
        }
        signature.append(")");

        if(!exceptions.isEmpty()) {
            signature.append(PojoGenUtil.getTypeDecl("throws", exceptions));
        }

        return signature.toString();
    }
}