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
package org.milyn.javabean;

import org.milyn.assertion.AssertArgument;
import org.milyn.delivery.TransformResult;

import javax.xml.transform.Result;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Java transformation result.
 * <p/>
 * Used to extract a Java "{@link Result result}" Map from the transformation.
 * Simply set an instance of this class as the {@link Result} arg in the call
 * to {@link org.milyn.Smooks#filter(javax.xml.transform.Source,javax.xml.transform.Result,org.milyn.container.ExecutionContext)}.
 *
 * @see BeanAccessor
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JavaResult extends TransformResult {
    
    private Map<String, Object> resultMap;

    /**
     * Public default constructor.
     */
    public JavaResult() {
        resultMap = new LinkedHashMap<String, Object>();
    }

    /**
     * Public constructor.
     * <p/>
     * See {@link #setResultMap(java.util.Map)}.
     * 
     * @param resultMap Result Map. This is the map onto which Java "result" objects will be set.
     */
    public JavaResult(Map<String, Object> resultMap) {
        AssertArgument.isNotNull(resultMap, "resultMap");
        this.resultMap = resultMap;
    }

    /**
     * Get the Java result map.
     * @return The Java result map.
     */
    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    /**
     * Set the Java result map.
     * @param resultMap The Java result map.
     */
    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
