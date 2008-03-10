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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.FilterResult;
import org.milyn.delivery.FilterSource;
import org.milyn.delivery.java.JavaResult;
import org.milyn.delivery.java.JavaSource;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;
import org.milyn.javabean.lifecycle.BeanLifecycleSubjectGroup;

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

    private final Map<String, BeanLifecycleSubjectGroup> beanLifecycleSubjectGroups = new HashMap<String, BeanLifecycleSubjectGroup>();

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
     * @deprecated use the {@link #getBean(ExecutionContext, String)}
     */
    @Deprecated
    public static Object getBean(String beanId, ExecutionContext executionContext) {
        return getBean(executionContext, beanId);
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
    public static Object getBean(ExecutionContext executionContext, String beanId) {
        AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
        AssertArgument.isNotNull(executionContext, "executionContext");

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

    		accessor.endLifecycle(beanId, false);
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
    public static void addBeanLifecycleObserver(ExecutionContext executionContext, String beanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanLifecycleObserver observer) {
    	AssertArgument.isNotNull(executionContext, "executionContext");

    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.addBeanLifecycleObserver(beanId, lifecycle, observerId, notifyOnce, observer);
    }

    /**
     * Unregisters a bean observer
     *
     * @param executionContext The execution context in which the observer is registered
     * @param beanId The bean id for which the observer is registered
     * @param observerId The id of the observer to unregister
     */
    public static void removeBeanLifecycleObserver(ExecutionContext executionContext, String beanId, BeanLifecycle lifecycle, String observerId) {
    	AssertArgument.isNotNull(executionContext, "executionContext");

    	BeanAccessor accessor = getAccessor(executionContext);

    	accessor.removeBeanLifecycleObserver(beanId, lifecycle, observerId);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return beans.toString();
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

    	endLifecycle(beanId, false);

    	beans.remove(beanId);
    }

    private void endAllLifecycles() {
    	
    	if(beans instanceof LinkedHashMap) {
	    	List<String> keyList = new ArrayList<String>(beans.size());
	    	for(String beanId: beans.keySet()) {
	    		keyList.add(beanId);
	    	}
	
	    	for(int i = keyList.size()-1; i >= 0; i--) {
	    		endLifecycle(keyList.get(i), false);
	    	}
    	} else {
    		// Ends lifecycle associations in a hierarchicly way, so
    		// that childs always get ended before there parents.
    		// TODO: This is really ugly! Can't this be done better?
    		Set<String> rootParents = new HashSet<String>();
    		Set<String> childs = new HashSet<String>();
    		for(List<String> childList : lifecycleAssociations.values()) {
    			childs.addAll(childList);
    		}
    		for(String parent : lifecycleAssociations.keySet()) {
    			if(!childs.contains(parent)) {
    				rootParents.add(parent);
    			}
    		}
    		
    		for(String rootParent : rootParents) {
    			endLifecycle(rootParent, true);
    		}
    		
    	}
    	
    }
    
    private void endLifecycle(String beanId, boolean associatedBeans) {
    	
    	if(associatedBeans) {
    		List<String> associations = lifecycleAssociations.get(beanId);

            if(associations != null) {
                for (String association : associations) {
                	endLifecycle(association, true);
                }
            }
    	}
    	
    	notifyObservers(BeanLifecycle.END, beanId);

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


    private void addBeanLifecycleObserver(String beanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanLifecycleObserver observer) {
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

    private void removeBeanLifecycleObserver(String beanId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.removeObserver(lifecycle, observerId);
    	}
    }

    private void notifyObservers(BeanLifecycle lifecycle, String beanId) {
    	notifyObservers(lifecycle, beanId, null);
    }

    private void notifyObservers(BeanLifecycle lifecycle, String beanId, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }

    private BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup(String beanId, boolean createIfNotExist) {
    	BeanLifecycleSubjectGroup subjectGroup = beanLifecycleSubjectGroups.get(beanId);

    	if(subjectGroup == null && createIfNotExist) {
    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, beanId);

    		beanLifecycleSubjectGroups.put(beanId, subjectGroup);
    		    		
    	}

    	return subjectGroup;
    }
}