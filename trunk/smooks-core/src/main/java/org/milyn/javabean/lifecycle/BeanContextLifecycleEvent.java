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
import org.milyn.javabean.repository.BeanId;

/**
 * An event when a lifecycle event has happend to a bean.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanContextLifecycleEvent {

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
	public BeanContextLifecycleEvent(ExecutionContext executionContext, BeanLifecycle lifecycle, BeanId beanId, Object bean) {

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
