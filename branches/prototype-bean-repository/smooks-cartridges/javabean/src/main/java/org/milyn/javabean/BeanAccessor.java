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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;
import org.milyn.javabean.lifecycle.RepositoryBeanLifecycleEvent;
import org.milyn.javabean.lifecycle.RepositoryBeanLifecycleObserver;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryId;
import org.milyn.javabean.repository.BeanRepositoryIdList;
import org.milyn.javabean.repository.BeanRepositoryManager;

/**
 * Bean Accessor.
 * <p/>
 * This class provides support for saving and accessing Javabean instance.
 *
 * @author tfennelly
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 * @deprecated Use the {@link BeanRepository} to manager the beans
 */
@Deprecated
public class BeanAccessor {

	private static final Log log = LogFactory.getLog(BeanAccessor.class);

	private static boolean WARNED_USING_DEPRECATED_CLASS = false;

    /**
     * Public default constructor.
     */
    public BeanAccessor(ExecutionContext executionContext) {
    }

    /**
     * Public constructor.
     * <p/>
     * Creates an accessor based on the supplied result Map.
     *
     * @param resultMap The result Map.
     *
     */
    public BeanAccessor(ExecutionContext executionContext, Map<String, Object> resultMap) {
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
     *
     */
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
    	warnUsingDeprecatedMethod();

    	AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");

        return BeanRepositoryManager.getBeanRepository(executionContext).getBean(beanId);
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param executionContext The execution context.
     * @return The bean map associated with the supplied request.
     *
     */
	public static HashMap<String, Object> getBeans(ExecutionContext executionContext) {
		warnUsingDeprecatedMethod();

		AssertArgument.isNotNull(executionContext, "executionContext");

		return (HashMap<String, Object>) getBeanMap(executionContext);
    }

    /**
     * Get the bean map associated with the supplied request instance.
     * @param executionContext The execution context.
     * @return The bean map associated with the supplied request.
     */
    public static Map<String, Object> getBeanMap(ExecutionContext executionContext) {
    	warnUsingDeprecatedMethod();

    	AssertArgument.isNotNull(executionContext, "executionContext");

    	return BeanRepositoryManager.getBeanRepository(executionContext).getBeanMap();
    }



    /**
     * Add a bean instance to the specified request under the specified beanId.
     *
     * @param executionContext The execution context within which the bean is created.
     * @param beanId The beanId under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     */
    public static void addBean(ExecutionContext executionContext, String beanId, Object bean) {
		warnUsingDeprecatedMethod();

		AssertArgument.isNotNull(executionContext, "executionContext");
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		BeanRepositoryIdList beanRepositoryIdList = BeanRepositoryManager
		 		.getInstance(executionContext.getContext())
		 		.getBeanRepositoryIdList();

		BeanRepositoryId beanRepositoryId = getBeanRepositoryId(beanRepositoryIdList, beanId);

		BeanRepositoryManager.getBeanRepository(executionContext).addBean(beanRepositoryId, bean);

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
    	warnUsingDeprecatedMethod();

    	AssertArgument.isNotNull(executionContext, "executionContext");
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		BeanRepositoryIdList beanRepositoryIdList = BeanRepositoryManager
				.getInstance(executionContext.getContext())
				.getBeanRepositoryIdList();

		BeanRepositoryId beanRepositoryId = getBeanRepositoryId(beanRepositoryIdList, beanId);

		BeanRepositoryManager.getBeanRepository(executionContext).changeBean(beanRepositoryId, bean);
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
    	warnUsingDeprecatedMethod();

    	AssertArgument.isNotNull(executionContext, "executionContext");
		AssertArgument.isNotNullAndNotEmpty(parentBean, "parentBean");
		AssertArgument.isNotNullAndNotEmpty(childBean, "childBean");

		BeanRepositoryIdList beanRepositoryIdList = BeanRepositoryManager
				.getInstance(executionContext.getContext())
				.getBeanRepositoryIdList();

		BeanRepositoryId parentBeanRepositoryId = getBeanRepositoryId(beanRepositoryIdList, parentBean);

		BeanRepositoryId childBeanRepositoryId =  getBeanRepositoryId(beanRepositoryIdList, childBean);

		if (parentBeanRepositoryId == null) {
			throw new IllegalStateException(
					"The bean with child beanId '" + parentBean + "' is not registered in the BeanRepositoryIdList of the current ApplicationContext.");
		}

		BeanRepositoryManager.getBeanRepository(executionContext).associateLifecycles(parentBeanRepositoryId, childBeanRepositoryId);
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
    public static void addBeanLifecycleObserver(ExecutionContext executionContext, String beanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, final BeanLifecycleObserver observer) {
    	warnUsingDeprecatedMethod();

    	AssertArgument.isNotNull(executionContext, "executionContext");
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		AssertArgument.isNotNull(lifecycle, "lifecycle");
		AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");
		AssertArgument.isNotNull(observer, "observer");

		BeanRepositoryIdList beanRepositoryIdList = BeanRepositoryManager
				.getInstance(executionContext.getContext())
				.getBeanRepositoryIdList();

		BeanRepositoryId beanRepositoryId = getBeanRepositoryId(beanRepositoryIdList, beanId);

		RepositoryBeanLifecycleObserver repositoryBeanLifecycleObserver = new RepositoryBeanLifecycleObserver() {

			public void onBeanLifecycleEvent(RepositoryBeanLifecycleEvent event) {
				observer.onBeanLifecycleEvent(event.getExecutionContext(), event.getLifecycle(), event.getBeanRepositoryId().getBeanId(), event.getBean());
			}

		};

		BeanRepositoryManager.getBeanRepository(executionContext).addBeanLifecycleObserver(beanRepositoryId, lifecycle, observerId, notifyOnce, repositoryBeanLifecycleObserver);
    }


    /**
     * Unregisters a bean observer
     *
     * @param executionContext The execution context in which the observer is registered
     * @param beanId The bean id for which the observer is registered
     * @param observerId The id of the observer to unregister
     */
    public static void removeBeanLifecycleObserver(ExecutionContext executionContext, String beanId, BeanLifecycle lifecycle, String observerId) {
    	warnUsingDeprecatedMethod();

    	AssertArgument.isNotNull(executionContext, "executionContext");
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		AssertArgument.isNotNull(lifecycle, "lifecycle");
		AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");

		BeanRepositoryIdList beanRepositoryIdList = BeanRepositoryManager
				.getInstance(executionContext.getContext())
				.getBeanRepositoryIdList();

		BeanRepositoryId beanRepositoryId = getBeanRepositoryId(beanRepositoryIdList, beanId);

		BeanRepositoryManager.getBeanRepository(executionContext).removeBeanLifecycleObserver(beanRepositoryId, lifecycle, observerId);

    }

	/**
	 * @param beanId
	 * @param beanRepositoryIdList
	 */
	private static BeanRepositoryId getBeanRepositoryId(BeanRepositoryIdList beanRepositoryIdList, String beanId) {
		warnUsingDeprecatedMethod();

		BeanRepositoryId beanRepositoryId = beanRepositoryIdList.getRepositoryBeanId(beanId);

		if (beanRepositoryId == null) {
			beanRepositoryId = beanRepositoryIdList.register(beanId);
		}

		return beanRepositoryId;
	}

	private static void warnUsingDeprecatedMethod() {
		if(log.isWarnEnabled()) {
			if(!WARNED_USING_DEPRECATED_CLASS) {
				WARNED_USING_DEPRECATED_CLASS = true;

				log.warn("The deprecated class BeanAccessor is being used! It is strongly advised to switch to the new BeanRepository class. " +
						"The BeanAccessor is much slower then the BeanRepository. This class will be removed in a future release!");
			}
		}
	}

}
