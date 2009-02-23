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
package org.milyn.payload;

import org.milyn.container.ExecutionContext;

import javax.xml.transform.Result;

/**
 * Filtration/Transformation {@link Result}.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class FilterResult implements Result {

    private static final String CONTEXT_KEY = FilterResult.class.getName() + "#CONTEXT_KEY";
    private String systemId;

    public static Result getResult(ExecutionContext executionContext) {
        return (Result) executionContext.getAttribute(CONTEXT_KEY);
    }

    public static void setResult(Result result, ExecutionContext executionContext) {
        if(result != null) {
            executionContext.setAttribute(CONTEXT_KEY, result);
        } else {
            executionContext.removeAttribute(CONTEXT_KEY);
        }
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemId() {
        return systemId;
    }
}
