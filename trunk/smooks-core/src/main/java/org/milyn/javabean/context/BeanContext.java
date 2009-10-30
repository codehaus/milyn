package org.milyn.javabean.context;

import java.util.Map;

import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;
import org.milyn.javabean.repository.BeanId;

public interface BeanContext {

	/**
	 * Add a bean instance under the specified {@link BeanId}.
	 *
	 * @param beanId The {@link BeanId} under which the bean is to be stored.
	 * @param bean The bean instance to be stored.
	 */
	public abstract void addBean(BeanId beanId, Object bean);

	/**
	 * Add a bean instance under the specified beanId.
	 * <p/>
	 * If performance is important, you should get (and cache) a {@link BeanId} instance
	 * for the beanId String and then use the {@link #addBean(BeanId, Object)} method.
	 *
	 * @param beanId The beanId under which the bean is to be stored.
	 * @param bean The bean instance to be stored.
	 */
	public abstract void addBean(String beanId, Object bean);

	/**
	 * Get the {@link BeanId} instance for the specified beanId String.
	 * <p/>
	 * Regsiters the beanId if it's not already registered.
	 *
	 * @param beanId The beanId String.
	 * @return The associated {@link BeanId} instance.
	 */
	public abstract BeanId getBeanId(String beanId);

	/**
	 * Looks if a bean instance is set under the {@link BeanId}
	 *
	 * @param beanId The {@link BeanId} under which is looked.
	 */
	public abstract boolean containsBean(BeanId beanId);

	/**
	 * Get the current bean, specified by the supplied {@link BeanId}.
	 * <p/>
	 * @param beanId The {@link BeanId} to get the bean instance from.
	 * @return The bean instance, or null if no such bean instance exists
	 */
	public abstract Object getBean(BeanId beanId);

	/**
	 * Changes a bean instance of the given {@link BeanId}. The difference to {@link #addBean(BeanId, Object)}
	 * is that the bean must exist, the associated beans aren't removed and the observers of the
	 * {@link BeanLifecycle#CHANGE} event are notified.
	 *
	 * @param beanId The {@link BeanId} under which the bean instance is to be stored.
	 * @param bean The bean instance to be stored.
	 */
	public abstract void changeBean(BeanId beanId, Object bean);

	/**
	 * Removes a bean and all its associated lifecycle beans from the bean map
	 *
	 * @param beanId The beanId to remove the beans from.
	 */
	public abstract Object removeBean(BeanId beanId);

	/**
	 * Removes a bean and all its associated lifecycle beans from the bean map
	 *
	 * @param beanId The beanId to remove the beans from.
	 */
	public abstract Object removeBean(String beanId);

	public abstract void clear();

	/**
	 * Associates the lifeCycle of the childBeanId with the parentBeanId. When the parentBean gets overwritten via the
	 * addBean method then the associated child beans will get removed from the bean map.
	 *
	 * @param parentBeanId The {@link BeanId} of the bean that controlles the lifecycle of its childs
	 * @param childBeanId The {@link BeanId} of the bean that will be associated to the parent
	 */
	public abstract void associateLifecycles(BeanId parentBeanId,
			BeanId childBeanId);

	/**
	 * Registers an observer which observers when a bean gets added.
	 *
	 * @param beanId The {@link BeanId} for which the observer is registered
	 * @param observerId The id of the observer. This is used to unregister the observer
	 * @param observer The actual BeanObserver instance
	 */
	public abstract void addBeanLifecycleObserver(BeanId beanId,
			BeanLifecycle lifecycle, String observerId, boolean notifyOnce,
			BeanRepositoryLifecycleObserver observer);

	/**
	 * Unregisters a bean observer
	 *
	 * @param beanId The {@link BeanId} for which the observer is registered
	 * @param observerId The id of the observer to unregister
	 */
	public abstract void removeBeanLifecycleObserver(BeanId beanId,
			BeanLifecycle lifecycle, String observerId);

	/**
	 * Returns the bean by it's beanId name.
	 *
	 * @return the bean instance or <code>null</code> if it not exists.
	 */
	public abstract Object getBean(String beanId);

	/**
	 * This returns a map which is backed by this repository. Changes made in the map
	 * are reflected back into the repository.
	 * There are some important side notes:
	 *
	 * <ul>
	 *   <li> The write performance of the map isn't as good as the write performance of the
	 *     	  BeanRepository because it needs to find or register the BeanId every time.
	 *        The read performance are as good as any normal Map.</li>
	 *   <li> The entrySet() method returns an UnmodifiableSet </li>
	 *   <li> When a bean gets removed from the BeanRepository then only the value of the
	 *        map entry is set to null. This means that null values should be regarded as
	 *        deleted beans. That is also why the size() of the bean map isn't accurate. It
	 *        also counts the null value entries.
	 * </ul>
	 *
	 * Only use the Map if you absolutely needed it else you should use the BeanRepository.
	 */
	public abstract Map<String, Object> getBeanMap();

	/**
	 * Mark the bean as being in context.
	 * <p/>
	 * This is "set" when we enter the fragment around which the bean is created and unset
	 * when we exit.
	 *
	 * @param beanId The bean ID.
	 * @param inContext True if the bean is in context, otherwise false.
	 */
	public abstract void setBeanInContext(BeanId beanId, boolean inContext);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

}