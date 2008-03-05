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
package org.milyn.javabean.lifecycle;

import org.milyn.container.ExecutionContext;
import org.milyn.javabean.BeanAccessor;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanLifecycleEvent {
	
	private final ExecutionContext executionContext;
	
	private final BeanLifecycle lifecycle;
	
	private final String beanId;
	
	private Object bean;
	
	private boolean searchedForBean = false;
	

	/**
	 * 
	 */
	public BeanLifecycleEvent(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, Object bean) {
		this.executionContext = executionContext;
		this.lifecycle = lifecycle;
		this.beanId = beanId;
		this.bean = bean;
		
		searchedForBean = (bean != null);
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
	public String getBeanId() {
		return beanId;
	}

	/**
	 * @return the bean
	 */
	public Object getBean() {
		if(!searchedForBean) {
			bean = BeanAccessor.getBean(executionContext, beanId);		
			
			searchedForBean = true;
		}
		return bean;
	}

	/**
	 * @return the executionContext
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return lifecycle + ":" + beanId;
	}
	

}
