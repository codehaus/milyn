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

import org.milyn.container.ApplicationContext;
import org.milyn.cdr.SmooksConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Java bean runtime info.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanRuntimeInfo {

    private static final String CONTEXT_KEY = BeanRuntimeInfo.class.getName() + "#CONTEXT_KEY";

    /**
     * The basic type that's created and populated for the associated bean.
     */
    private Class populateType;

    /**
     * The bean classification.
     * <p/>
     * We maintain this classification enum because it helps us avoid performing
     * instanceof checks, which are cheap when the instance being checked is
     * an instanceof, but is expensive if it's not.
     */
    private Classification classification;

    /**
     * If the bean classification is an ARRAY_COLLECTION, this member specifies the
     * actual array type.
     */
    private Class arrayType;

    /**
     * Bean type classification.
     * <p/>
     * We maintain this classification enum because it helps us avoid performing
     * instanceof checks, which are cheap when the instance being checked is
     * an instanceof, but expensive if it's not.
     */
    public static enum Classification {
        NON_COLLECTION,
        ARRAY_COLLECTION,
        COLLECTION_COLLECTION,
        MAP_COLLECTION,

    }

    public static void recordBeanRuntimeInfo(String beanId, BeanRuntimeInfo beanRuntimeInfo, ApplicationContext appContext) {
        Map<String, BeanRuntimeInfo> runtimeInfoMap = getRuntimeInfoMap(appContext);

        if(runtimeInfoMap.containsKey(beanId)) {
            throw new SmooksConfigurationException("Multiple configurations present with beanId='" + beanId + "'.");
        }

        runtimeInfoMap.put(beanId, beanRuntimeInfo);
    }

    public static BeanRuntimeInfo getBeanRuntimeInfo(String beanId, ApplicationContext appContext) {
        Map<String, BeanRuntimeInfo> runtimeInfoMap = getRuntimeInfoMap(appContext);

        return runtimeInfoMap.get(beanId);
    }

    private static Map<String, BeanRuntimeInfo> getRuntimeInfoMap(ApplicationContext appContext) {
        Map<String, BeanRuntimeInfo> runtimeInfoMap = (Map<String, BeanRuntimeInfo>) appContext.getAttribute(CONTEXT_KEY);

        if(runtimeInfoMap == null) {
            runtimeInfoMap = new HashMap<String, BeanRuntimeInfo>();
            appContext.setAttribute(CONTEXT_KEY, runtimeInfoMap);
        }

        return runtimeInfoMap;
    }

    public Class getPopulateType() {
        return populateType;
    }

    public void setPopulateType(Class populateType) {
        this.populateType = populateType;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public Class getArrayType() {
        return arrayType;
    }

    public void setArrayType(Class arrayType) {
        this.arrayType = arrayType;
    }
}
