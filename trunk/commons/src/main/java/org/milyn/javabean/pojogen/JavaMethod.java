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

import java.util.List;
import java.util.ArrayList;

/**
 * Method model.
 * @author bardl
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class JavaMethod {

    private JavaType returnType;
    private String methodName;
    private List<JavaNamedType> parameters = new ArrayList<JavaNamedType>();
    private String body;

    public JavaMethod(String methodName) {
        AssertArgument.isNotNull(methodName, "methodName");
        this.returnType = new JavaType(void.class);
        this.methodName = methodName;
    }

    public JavaMethod(JavaType returnType, String methodName) {
        AssertArgument.isNotNull(returnType, "returnType");
        AssertArgument.isNotNull(methodName, "methodName");
        this.returnType = returnType;
        this.methodName = methodName;
    }

    public JavaType getReturnType() {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public JavaMethod addParameter(JavaType type, String parameterName) {
        return addParameter(new JavaNamedType(type, parameterName));
    }

    public JavaMethod addParameter(JavaNamedType parameter) {
        parameters.add(parameter);
        return this;
    }

    public List<JavaNamedType> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSignature() {
        StringBuilder signature = new StringBuilder();

        signature.append(returnType);
        signature.append(" ");
        signature.append(methodName);
        signature.append("(");
        for(int i = 0; i < parameters.size(); i++) {
            if(i > 0) {
                signature.append(", ");
            }
            signature.append(parameters.get(i));
        }
        signature.append(")");

        return signature.toString();
    }
}