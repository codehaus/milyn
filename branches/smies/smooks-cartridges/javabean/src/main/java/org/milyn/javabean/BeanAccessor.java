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
import org.milyn.delivery.FilterResult;
import org.milyn.delivery.FilterSource;
import org.milyn.delivery.java.JavaResult;
import org.milyn.delivery.java.JavaSource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.util.*;

/**
 * Bean Accessor.
 * <p/>
 * This class provides support for saving and accessing Javabean instance.
 * @author tfennelly
 */
public class BeanAccessor {

    private static final String CONTEXT_KEY = BeanAccessor.class.getName() + "#CONTEXT_KEY";

    private Map<String, Object> beans;
    private Map<String, List<String>> lifecycleAssociations = new HashMap<String, List<String>>();

    private Map<String, Map<String, BeanObserver>> beanObservers = new HashMap<String, Map<String, BeanObserver>>();


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
        BeanAccessor accessor = (BeanAccessor) executionContext.getAttribute(CONTEXT_KEY);

        if(accessor == null) {
            Result result = FilterResult.getResult(executionContext);
            Source source = FilterSource.getSource(executionContext);
            Map beanMap = null;

            if(result instanceof JavaResult) {
                JavaResult javaResult = (JavaResult) result;
                beanMap = javaResult.getResultMap();
            }
            if(source instanceof JavaSource) {
                JavaSource javaSource = (JavaSource) source;
                Map sourceBeans = javaSource.getBeans();

                if(sourceBeans != null) {
                    if(beanMap != null) {
                        beanMap.putAll(sourceBeans);
                    } else {
                        beanMap = sourceBeans;
                    }
                }
            }

            if(beanMap != null) {
                accessor = new BeanAccessor(beanMap);
            } else {
                accessor = new BeanAccessor();
            }

            executionContext.setAttribute(CONTEXT_KEY, accessor);
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

        accessor.notifyObservers(executionContext, beanId, bean);

        // Set the bean on the bean map...
        accessor.beans.put(beanId, bean);
    }

    public static void associateLifecycles(ExecutionContext executionContext, String parentBean, String childBean, boolean addToList) {
        BeanAccessor accessor = getAccessor(executionContext);

        //Is this correct?
        if(addToList) {
            childBean += "List";
        }

        accessor.associateLifecycles(parentBean, childBean);
    }

    public static void registerBeanObserver(ExecutionContext executionContext, String beanId, String registrantId, BeanObserver binder) {
    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.registerBeanObserver(beanId, registrantId, binder);
    }

    public static void unregisterBeanObserver(ExecutionContext executionContext, String beanId, String registrantId) {
    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.unregisterBeanObserver(beanId, registrantId);
    }

    private void associateLifecycles(String parentBean, String childBean) {
        List<String> associations = lifecycleAssociations.get(parentBean);

        if(associations != null) {
            if(!associations.contains(childBean)) {
                associations.add(childBean);
            }
        } else {
            associations = new ArrayList<String>();
            associations.add(childBean);
            lifecycleAssociations.put(parentBean, associations);
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



    public void registerBeanObserver(String beanId, String registrantId, BeanObserver binder) {

    	Map<String, BeanObserver> observers = beanObservers.get(beanId);
    	if(observers == null) {
    		observers = new HashMap<String, BeanObserver>(1);

    		beanObservers.put(beanId, observers);
    	}

    	observers.put(registrantId, binder);

    }

    public void unregisterBeanObserver(String beanId, String registrantId) {

    	if(beanObservers.containsKey(beanId)) {

    		Map<String, BeanObserver> observers = beanObservers.get(beanId);
    		observers.remove(registrantId);

    		if(observers.size() == 0) {
    			beanObservers.remove(beanId);
    		}
    	}

    }

    private void notifyObservers(ExecutionContext executionContext, String beanId, Object bean) {

    	if(beanObservers.containsKey(beanId)) {

    		Map<String, BeanObserver> observers = beanObservers.get(beanId);

    		for(BeanObserver observer : observers.values()) {

    			String observerBeanId = observer.getBeanId();

    			associateLifecycles(observerBeanId, beanId);

    			observer.beanRegistrationNotify(executionContext, bean);

    		}

    	}

    }

    public String toString() {
        if(beans != null) {
            return beans.toString();
        } else {
            return "{}";
        }
    }
}
