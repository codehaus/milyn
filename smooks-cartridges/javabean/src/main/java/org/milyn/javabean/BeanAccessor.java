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

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.TransformResult;

import javax.xml.transform.Result;
import java.util.*;

/**
 * Bean Accessor.
 * <p/>
 * This class provides support for saving and accessing Javabean instance.
 * @author tfennelly
 */
public class BeanAccessor {
    
    private static final String BEAN_MAP_REQUEST_KEY = BeanAccessor.class.getName() + "#BEAN_MAP_REQUEST_KEY";

    private Map<String, Object> beans;
    private Map<String, List<String>> lifecycleAssociations = new HashMap<String, List<String>>();

    /**
     * Public default constructor.
     */
    public BeanAccessor() {
        beans = new LinkedHashMap<String, Object>();
    }

    /**
     * Public constructor.
     * <p/>
     * Creates an accessor based on the supplied result Map.
     * 
     * @param resultMap The result Map.
     */
    public BeanAccessor(Map<String, Object> resultMap) {
        beans = resultMap;
    }

    /**
     * Get the current bean, specified by the supplied beanId, from the supplied request.
     * <p/>
     * If the specified beanId refers to a bean instance list, this method returns the
     * last (current) bean from the list.
     * @param beanId Bean Identifier.
     * @param executionContext The request on which the bean instance is stored.
     * @return The bean instance, or null if no such bean instance exists on the supplied
     * request.
     */
    public static Object getBean(String beanId, ExecutionContext executionContext) {
        if(beanId == null) {
            throw new IllegalArgumentException("null 'beanId' arg in method call.");
        }
        if(executionContext == null) {
            throw new IllegalArgumentException("null 'request' arg in method call.");
        }

        Map beans = getBeanMap(executionContext);
        Object bean = beans.get(beanId);
        
        return bean;
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param executionContext The execution context.
     * @return The bean map associated with the supplied request.
     * @deprecated Use {@link #getBeanMap(org.milyn.container.ExecutionContext)}.
     */
    public static HashMap getBeans(ExecutionContext executionContext) {
        return (HashMap) getBeanMap(executionContext);
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param executionContext The execution context.
     * @return The bean map associated with the supplied request.
     */
    public static Map getBeanMap(ExecutionContext executionContext) {
        if(executionContext == null) {
            throw new IllegalArgumentException("null 'request' arg in method call.");
        }

        BeanAccessor accessor = getAccessor(executionContext);
        
        return accessor.beans;
    }

    private static BeanAccessor getAccessor(ExecutionContext executionContext) {
        BeanAccessor accessor = (BeanAccessor) executionContext.getAttribute(BEAN_MAP_REQUEST_KEY);

        if(accessor == null) {
            Result result = TransformResult.getResult(executionContext);

            if(result instanceof JavaResult) {
                JavaResult javaResult = (JavaResult) result;
                accessor = new BeanAccessor(javaResult.getResultMap());
            } else {
                accessor = new BeanAccessor();
            }

            executionContext.setAttribute(BEAN_MAP_REQUEST_KEY, accessor);
        }

        return accessor;
    }

    /**
     * Add a bean instance to the specified request under the specified beanId.
     * <p/>
     * When "addToList" is set to true, a new bean of type {@link List} is added to the bean map.  This
     * bean is stored under a new key generated from the "beanId" appended with the string "List" e.g.
     * the list associated with the beanId "customer" is called "customerList".
     * @param beanId The beanId under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     * @param executionContext The execution context within which the bean is created.
     * @param addToList Is the bean to be added to a bean list.
     */
    public static void addBean(String beanId, Object bean, ExecutionContext executionContext, boolean addToList) {
        if(beanId == null) {
            throw new IllegalArgumentException("null 'beanId' arg in method call.");
        }
        if(bean == null) {
            throw new IllegalArgumentException("null 'bean' arg in method call.");
        }
        if(executionContext == null) {
            throw new IllegalArgumentException("null 'request' arg in method call.");
        }

        BeanAccessor accessor = getAccessor(executionContext);

        accessor.cleanAssociatedLifecycleBeans(beanId);

        if(addToList) {
            // Beans that are added to lists take up 2 locations in the bean map... 1 for the current bean
            // instance (keyed under beanId) and one for the bean list (keyed under beanId + "List").
            // This was an unfortunate decision!!

            String beanListId = beanId + "List";
            Object beanList = accessor.beans.get(beanListId);

            if(beanList == null) {
                // Create the bean list and add it to the bean map...
                beanList = new Vector();
                accessor.beans.put(beanListId, beanList);
            } else if(!(beanList instanceof List)) {
                throw new IllegalArgumentException("bean [" + beanId + "] list storage location [" + beanListId + "] is already in use and IS NOT a List (type=" + beanList.getClass().getName() + ").  Try explicitly setting the 'beanId' on one of these bean configs to avoid this name collision.");
            }
            // add the bean to the list...
            ((List)beanList).add(bean);
        } else {
            Object currentBean = accessor.beans.get(beanId);

            if(currentBean instanceof List) {
                throw new IllegalArgumentException("bean [" + beanId + "] already exists on request and IS a List.  Arg 'addToList' set to false - this is inconsistent!!");
            }
        }

        // Set the bean on the bean map...
        accessor.beans.put(beanId, bean);
    }

    public static void associateLifecycles(ExecutionContext executionContext, String parentBean, String childBean, boolean addToList) {
        BeanAccessor accessor = getAccessor(executionContext);
        List<String> associations = accessor.lifecycleAssociations.get(parentBean);

        if(addToList) {
            childBean += "List";
        }

        if(associations != null) {
            if(!associations.contains(childBean)) {
                associations.add(childBean);
            }
        } else {
            associations = new ArrayList<String>();
            associations.add(childBean);
            accessor.lifecycleAssociations.put(parentBean, associations);
        }
    }

    private void cleanAssociatedLifecycleBeans(String parentBean) {
        List<String> associations = lifecycleAssociations.get(parentBean);

        if(associations != null) {
            for (String association : associations) {
                beans.remove(association);
            }
        }
    }
}
