/**
 * 
 */
package org.milyn.javabean.lifecycle;

import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanRepositoryId;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class RepositoryBeanLifecycleEvent {

	private final ExecutionContext executionContext;
	
	private final BeanLifecycle lifecycle;
	
	private final BeanRepositoryId beanRepositoryId;
	
	private final Object bean;
	
	
	
	/**
	 * @param executionContext
	 * @param beanRepositoryId
	 * @param lifecycle
	 * @param bean
	 */
	public RepositoryBeanLifecycleEvent(ExecutionContext executionContext,
			 BeanLifecycle lifecycle, BeanRepositoryId beanRepositoryId, Object bean) {
		
		this.executionContext = executionContext;
		this.beanRepositoryId = beanRepositoryId;
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
	 * @return the beanRepositoryId
	 */
	public BeanRepositoryId getBeanRepositoryId() {
		return beanRepositoryId;
	}
	/**
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}
}
