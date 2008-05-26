/**
 *
 */
package org.milyn.javabean.lifecycle;

import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanId;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanRepositoryLifecycleEvent {

	private final ExecutionContext executionContext;

	private final BeanLifecycle lifecycle;

	private final BeanId beanId;

	private final Object bean;


	/**
	 * @param executionContext
	 * @param beanId
	 * @param lifecycle
	 * @param bean
	 */
	public BeanRepositoryLifecycleEvent(ExecutionContext executionContext,
			 BeanLifecycle lifecycle, BeanId beanId, Object bean) {

		this.executionContext = executionContext;
		this.beanId = beanId;
		this.lifecycle = lifecycle;
		this.bean = bean;
	}
	/**
	 * @return the executionContext
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
	/**
	 * @return the lifecycle
	 */
	public BeanLifecycle getLifecycle() {
		return lifecycle;
	}
	/**
	 * @return the beanId
	 */
	public BeanId getBeanId() {
		return beanId;
	}
	/**
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}
}
