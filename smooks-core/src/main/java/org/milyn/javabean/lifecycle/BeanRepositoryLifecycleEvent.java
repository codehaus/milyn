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
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@Deprecated
public class BeanRepositoryLifecycleEvent {

	private final BeanContextLifecycleEvent event;

	public BeanRepositoryLifecycleEvent(ExecutionContext executionContext,
			 BeanLifecycle lifecycle, BeanId beanId, Object bean) {

		this.event = new BeanContextLifecycleEvent(executionContext, lifecycle, beanId, bean);
	}

	public BeanRepositoryLifecycleEvent(BeanContextLifecycleEvent event) {
		super();
		this.event = event;
	}

	public boolean equals(Object obj) {
		return event.equals(obj);
	}

	public Object getBean() {
		return event.getBean();
	}

	public BeanId getBeanId() {
		return event.getBeanId();
	}

	public ExecutionContext getExecutionContext() {
		return event.getExecutionContext();
	}

	public BeanLifecycle getLifecycle() {
		return event.getLifecycle();
	}



}
