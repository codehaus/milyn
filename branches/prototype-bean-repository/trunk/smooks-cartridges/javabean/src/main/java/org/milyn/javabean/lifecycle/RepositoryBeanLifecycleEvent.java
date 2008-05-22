/**
 * 
 */
package org.milyn.javabean.lifecycle;

import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.RepositoryBeanId;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class RepositoryBeanLifecycleEvent {

	private final ExecutionContext executionContext;
	
	private final BeanLifecycle lifecycle;
	
	private final RepositoryBeanId repositoryBeanId;
	
	private final Object bean;
	
	
	
	/**
	 * @param executionContext
	 * @param repositoryBeanId
	 * @param lifecycle
	 * @param bean
	 */
	public RepositoryBeanLifecycleEvent(ExecutionContext executionContext,
			 BeanLifecycle lifecycle, RepositoryBeanId repositoryBeanId, Object bean) {
		
		this.executionContext = executionContext;
		this.repositoryBeanId = repositoryBeanId;
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
	 * @return the repositoryBeanId
	 */
	public RepositoryBeanId getBeanProvider() {
		return repositoryBeanId;
	}
	/**
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}
}
