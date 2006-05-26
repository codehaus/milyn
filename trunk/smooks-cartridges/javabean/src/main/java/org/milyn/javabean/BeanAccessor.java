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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.milyn.container.ContainerRequest;

/**
 * Bean Accessor.
 * <p/>
 * This class provides support for saving and accessing Javabean instance.
 * @author tfennelly
 */
public class BeanAccessor {
    
    private static final String BEAN_MAP_REQUEST_KEY = BeanAccessor.class.getName() + "#BEAN_MAP_REQUEST_KEY";

    /**
     * Get the current bean, specified by the supplied beanId, from the supplied request.
     * <p/>
     * If the specified beanId refers to a bean instance list, this method returns the
     * last (current) bean from the list.
     * @param beanId Bean Identifier.
     * @param request The request on which the bean instance is stored.
     * @return The bean instance, or null if no such bean instance exists on the supplied
     * request.
     */
    public static Object getBean(String beanId, ContainerRequest request) {
        if(beanId == null) {
            throw new IllegalArgumentException("null 'beanId' arg in method call.");
        }
        if(request == null) {
            throw new IllegalArgumentException("null 'request' arg in method call.");
        }

        HashMap beans = getBeans(request);
        Object bean = beans.get(beanId);
        
        // If it's a bean list return the last (current) bean instance...
        if(bean instanceof List) {
            List beanList = (List) bean;
            bean = beanList.get(beanList.size() - 1);
        }
        
        return bean;
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param request The request instance.
     * @return The bean map associated with the supplied request.
     */
    public static HashMap getBeans(ContainerRequest request) {
        if(request == null) {
            throw new IllegalArgumentException("null 'request' arg in method call.");
        }

        HashMap beans = (HashMap) request.getAttribute(BEAN_MAP_REQUEST_KEY);
        
        if(beans == null) {
            beans = new LinkedHashMap();
            request.setAttribute(BEAN_MAP_REQUEST_KEY, beans);
        }
        
        return beans;
    }

    /**
     * Add a bean instance to the specified request under the specified beanId.
     * @param beanId The beanId under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     * @param request The request on which the bean is to be stored.
     * @param addToList Is the bean to be added to a bean list.
     */
    public static void addBean(String beanId, Object bean, ContainerRequest request, boolean addToList) {
        if(beanId == null) {
            throw new IllegalArgumentException("null 'beanId' arg in method call.");
        }
        if(bean == null) {
            throw new IllegalArgumentException("null 'bean' arg in method call.");
        }
        if(request == null) {
            throw new IllegalArgumentException("null 'request' arg in method call.");
        }

        HashMap beans = getBeans(request);
        
        if(addToList) {
            Object beanList = beans.get(beanId);
            
            if(beanList == null) {
                // Create the bean list and add it to the bean map...
                beanList = new Vector();
                beans.put(beanId, beanList);
            } else if(!(beanList instanceof List)) {
                throw new IllegalArgumentException("bean [" + beanId + "] already exists on request and is not a List.  Arg 'addToList' set to true - this is inconsistent!!");
            }
            // add the bean to the list...
            ((List)beanList).add(bean);
        } else {
            Object currentBean = beans.get(beanId);

            if(currentBean instanceof List) {
                throw new IllegalArgumentException("bean [" + beanId + "] already exists on request and is a List.  Arg 'addToList' set to false - this is inconsistent!!");
            }
            
            // Just set the bean on the bean map...
            beans.put(beanId, bean);
        }
    }
}
