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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.FilterResult;
import org.milyn.delivery.FilterSource;
import org.milyn.delivery.java.JavaResult;
import org.milyn.delivery.java.JavaSource;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;

/**
 * Bean Accessor.
 * <p/>
 * This class provides support for saving and accessing Javabean instance.
 *
 * @author tfennelly
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public class BeanAccessor {

    private static final String CONTEXT_KEY = BeanAccessor.class.getName() + "#CONTEXT_KEY";

    private final ExecutionContext executionContext;

    private final Map<String, Object> beans;
    private final Map<String, List<String>> lifecycleAssociations = new HashMap<String, List<String>>();

    private final Map<BeanLifecycle, Map<String, Map<String, BeanLifecycleObserver>>> lifecycleObservers = new HashMap<BeanLifecycle, Map<String, Map<String, BeanLifecycleObserver>>>();

    /**
     * Public default constructor.
     */
    public BeanAccessor(ExecutionContext executionContext) {
    	this(executionContext, new LinkedHashMap<String, Object>());
    }

    /**
     * Public constructor.
     * <p/>
     * Creates an accessor based on the supplied result Map.
     *
     * @param resultMap The result Map.
     */
    public BeanAccessor(ExecutionContext executionContext, Map<String, Object> resultMap) {
    	this.executionContext = executionContext;
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

        Map<String, Object> beans = getBeanMap(executionContext);
        Object bean = beans.get(beanId);

        return bean;
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param executionContext The execution context.
     * @return The bean map associated with the supplied request.
     * @deprecated Use {@link #getBeanMap(org.milyn.container.ExecutionContext)}.
     */
    @Deprecated
	public static HashMap<String, Object> getBeans(ExecutionContext executionContext) {
        return (HashMap<String, Object>) getBeanMap(executionContext);
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param executionContext The execution context.
     * @return The bean map associated with the supplied request.
     */
    public static Map<String, Object> getBeanMap(ExecutionContext executionContext) {
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
            Map<String, Object> beanMap = null;

            if(result instanceof JavaResult) {
                JavaResult javaResult = (JavaResult) result;
                beanMap = javaResult.getResultMap();
            }
            if(source instanceof JavaSource) {
                JavaSource javaSource = (JavaSource) source;
                Map<String, Object> sourceBeans = javaSource.getBeans();

                if(sourceBeans != null) {
                    if(beanMap != null) {
                        beanMap.putAll(sourceBeans);
                    } else {
                        beanMap = sourceBeans;
                    }
                }
            }

            if(beanMap != null) {
                accessor = new BeanAccessor(executionContext, beanMap);
            } else {
                accessor = new BeanAccessor(executionContext);
            }

            executionContext.setAttribute(CONTEXT_KEY, accessor);
        }

        return accessor;
    }

    /**
     * Add a bean instance to the specified request under the specified beanId.
     *
     * @param executionContext The execution context within which the bean is created.
     * @param beanId The beanId under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     */
    public static void addBean(ExecutionContext executionContext, String beanId, Object bean) {
    	addBean(beanId, bean, executionContext, false);
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
     * @deprecated Because of the new bean binding system, adding to a list this way is deprecated.
     * 			   Use the {@link #addBean(String, Object, ExecutionContext)} method.
     */
    @SuppressWarnings("unchecked")
	@Deprecated
    public static void addBean(String beanId, Object bean, ExecutionContext executionContext, boolean addToList) {
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	AssertArgument.isNotNull(bean, "bean");
    	AssertArgument.isNotNull(executionContext, "executionContext");

        BeanAccessor accessor = getAccessor(executionContext);

        // We don't call removeBean directly because we will replace the bean later on. Deleting
        // it is not necessary then.
        if(accessor.beans.containsKey(beanId)) {
    		accessor.cleanAssociatedLifecycleBeans(beanId);

    		accessor.endLifecycle(beanId);
    	}

        if(addToList) {
            // Beans that are added to lists take up 2 locations in the bean map... 1 for the current bean
            // instance (keyed under beanId) and one for the bean list (keyed under beanId + "List").
            // This was an unfortunate decision!!

            String beanListId = beanId + "List";
            Object beanList = accessor.beans.get(beanListId);

            if(beanList == null) {
                // Create the bean list and add it to the bean map...
                beanList = new ArrayList<Object>();
                accessor.beans.put(beanListId, beanList);

                //add = true;
            } else if(!(beanList instanceof List)) {
                throw new IllegalArgumentException("bean [" + beanId + "] list storage location [" + beanListId + "] is already in use and IS NOT a List (type=" + beanList.getClass().getName() + ").  Try explicitly setting the 'beanId' on one of these bean configs to avoid this name collision.");
            }
            // add the bean to the list...
            ((List<Object>)beanList).add(bean);
        }


        // Set the bean on the bean map...
        accessor.beans.put(beanId, bean);

        accessor.notifyObservers(BeanLifecycle.BEGIN, beanId, bean);

    }

    /**
     * Changes a bean object of the given beanId. The difference to addBean is that the
     * bean must exist, the associated beans aren't removed and the observers of the
     * {@link BeanLifecycle#CHANGE} event are notified.
     *
     * @param executionContext The execution context within which the bean is created.
     * @param beanId The beanId under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     */
    public static void changeBean(ExecutionContext executionContext, String beanId, Object bean) {
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	AssertArgument.isNotNull(bean, "bean");
    	AssertArgument.isNotNull(executionContext, "executionContext");

    	BeanAccessor accessor = getAccessor(executionContext);

    	if(accessor.beans.containsKey(beanId)) {
    		accessor.beans.put(beanId, bean);

    		accessor.notifyObservers(BeanLifecycle.CHANGE, beanId, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + beanId + "' can't be replaces because it doesn't exits in the map.");
    	}
    }


    /**
     * Associates the lifeCycle of the childBean with the parentBean. When the parentBean gets overwritten via the
     * addBean method then the associated child beans will get removed from the bean map.
     *
     * @param executionContext The execution context within which the beans are located.
     * @param parentBean The bean that controlles the lifecycle of its childs
     * @param childBean The bean that will be associated to the parent
     * @param addToList Is the child added to a bean list.
     *
     */
    public static void associateLifecycles(ExecutionContext executionContext, String parentBean, String childBean) {
    	AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNullAndNotEmpty(parentBean, "parentBean");
    	AssertArgument.isNotNullAndNotEmpty(childBean, "childBean");

    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.associateLifecycles(parentBean, childBean);
    }

    /**
     * Associates the lifeCycle of the childBean with the parentBean. When the parentBean gets overwritten via the
     * addBean method then the associated child beans will get removed from the bean map.
     *
     * @param executionContext The execution context within which the beans are located.
     * @param parentBean The bean that controlles the lifecycle of its childs
     * @param childBean The bean that will be associated to the parent
     * @param addToList Is the child added to a bean list.
     * @deprecated Because of the new bean binding system, adding to a list this way is deprecated.
     * 			   Use the {@link #associateLifecycles(ExecutionContext, String, String)} method.
     */
    @Deprecated
    public static void associateLifecycles(ExecutionContext executionContext, String parentBean, String childBean, boolean addToList) {
    	AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNullAndNotEmpty(parentBean, "parentBean");
    	AssertArgument.isNotNullAndNotEmpty(childBean, "childBean");


    	if(addToList) {
            childBean += "List";
        }

        associateLifecycles(executionContext, parentBean, childBean);
    }

    /**
     * Ends the lifecycles of all the beans
     *
     * @param executionContext The execution context of the bean Accessor
     */
    public static void endAllLifecycles(ExecutionContext executionContext) {
    	AssertArgument.isNotNull(executionContext, "executionContext");

    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.endAllLifecycles();
    }

    /**
     * Registers an observer which observers when a bean gets added.
     *
     *
     * @param executionContext The execution context in which the observer is registered
     * @param beanId The bean id for which the observer is registered
     * @param observerId The id of the observer. This is used to unregister the observer
     * @param observer The actual BeanObserver object
     */
    public static void registerBeanLifecycleObserver(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, String observerId, BeanLifecycleObserver observer) {
    	AssertArgument.isNotNull(executionContext, "executionContext");

    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.registerBeanLifecycleObserver(lifecycle, beanId, observerId, observer);
    }

    /**
     * Unregisters a bean observer
     *
     * @param executionContext The execution context in which the observer is registered
     * @param beanId The bean id for which the observer is registered
     * @param observerId The id of the observer to unregister
     */
    public static void unregisterBeanLifecycleObserver(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, String observerId) {
    	AssertArgument.isNotNull(executionContext, "executionContext");

    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.unregisterBeanLifecycleObserver(lifecycle, beanId, observerId);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return beans.toString();
    }


    private void registerBeanLifecycleObserver(BeanLifecycle lifecycle, String beanId, String observerId, BeanLifecycleObserver observer) {
    	AssertArgument.isNotNull(lifecycle, "lifecycle");
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");
    	AssertArgument.isNotNull(observer, "observer");

    	Map<String, Map<String, BeanLifecycleObserver>> observerMap = getObserversMap(lifecycle);

    	Map<String, BeanLifecycleObserver> observers = observerMap.get(beanId);
    	if(observers == null) {
    		observers = new LinkedHashMap<String, BeanLifecycleObserver>(1);

    		observerMap.put(beanId, observers);
    	}

    	observers.put(observerId, observer);

    }

    private void unregisterBeanLifecycleObserver(BeanLifecycle lifecycle, String beanId, String observerId) {
    	AssertArgument.isNotNull(lifecycle, "lifecycle");
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");

    	Map<String, Map<String, BeanLifecycleObserver>> observerMap = getObserversMap(lifecycle);

    	if(observerMap.containsKey(beanId)) {

    		Map<String, BeanLifecycleObserver> observers = observerMap.get(beanId);
    		observers.remove(observerId);

    		if(observers.size() == 0) {
    			observerMap.remove(beanId);
    		}
    	}

    }

    private void associateLifecycles(String parentBean, String childBean) {
    	AssertArgument.isNotNullAndNotEmpty(parentBean, "parentBean");
    	AssertArgument.isNotNullAndNotEmpty(childBean, "childBean");

    	List<String> associations = lifecycleAssociations.get(parentBean);

        if(associations != null) {
            if(!associations.contains(childBean)) {
                associations.add(childBean);
            }
        } else {
            associations = new ArrayList<String>(1);
            associations.add(childBean);
            lifecycleAssociations.put(parentBean, associations);
        }
    }

    private void cleanAssociatedLifecycleBeans(String parentBean) {
        List<String> associations = lifecycleAssociations.get(parentBean);

        if(associations != null) {
            for (String association : associations) {
            	removeBean(association);
            }
            lifecycleAssociations.remove(parentBean);
        }

    }

    private void removeBean(String beanId) {
    	cleanAssociatedLifecycleBeans(beanId);

    	endLifecycle(beanId);

    	beans.remove(beanId);
    }

    private void endAllLifecycles() {

    	List<String> keyList = new ArrayList<String>(beans.size());
    	for(String beanId: beans.keySet()) {
    		keyList.add(beanId);
    	}

    	for(int i = keyList.size()-1; i >= 0; i--) {
    		endLifecycle(keyList.get(i));
    	}

    }

    private void endLifecycle(String beanId) {
    	notifyObservers(BeanLifecycle.END, beanId);

    }

    private void notifyObservers(BeanLifecycle lifecycle, String beanId) {
    	notifyObservers(lifecycle, beanId, null);
    }

    private void notifyObservers(BeanLifecycle lifecycle, String beanId, Object bean) {

    	Map<String, Map<String, BeanLifecycleObserver>> observersMap = getObserversMap(lifecycle);

    	if(observersMap.containsKey(beanId)) {

    		Map<String, BeanLifecycleObserver> observers = observersMap.get(beanId);

    		if(observers.size() > 0) {
    			BeanLifecycleEvent event = new BeanLifecycleEvent(executionContext, lifecycle, beanId, bean);

    			List<BeanLifecycleObserver> observerList = new ArrayList<BeanLifecycleObserver>(observers.values());
    			for(BeanLifecycleObserver observer : observerList) {

        			observer.onBeanLifecycleEvent(event);

        		}
    		}


    	}

    }

    private Map<String, Map<String, BeanLifecycleObserver>> getObserversMap(BeanLifecycle lifecycle) {

    	Map<String, Map<String, BeanLifecycleObserver>> lifecycleObserverMap = lifecycleObservers.get(lifecycle);

    	if(lifecycleObserverMap == null) {
    		lifecycleObserverMap = new HashMap<String, Map<String,BeanLifecycleObserver>>();

    		lifecycleObservers.put(lifecycle, lifecycleObserverMap);
    	}

    	return lifecycleObserverMap;
    }
}
