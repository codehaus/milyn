/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.javabean.observers;

import org.milyn.container.ExecutionContext;
import org.milyn.javabean.BeanInstancePopulator;
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.lifecycle.BeanContextLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanContextLifecycleObserver;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.repository.BeanId;

/**
 * {@link BeanContext} Observer for performing a bean wiring by Bean ID.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class WireByBeanIdObserver implements BeanContextLifecycleObserver {

	private BeanId watchedBean;
	private BeanId watchingBean;
	private BeanInstancePopulator populator;

	public WireByBeanIdObserver(BeanId watchedBean, BeanId watchingBean, BeanInstancePopulator populator) {
		this.watchedBean = watchedBean;
		this.watchingBean = watchingBean;
		this.populator = populator;		
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.javabean.lifecycle.BeanContextLifecycleObserver#onBeanLifecycleEvent(org.milyn.javabean.lifecycle.BeanContextLifecycleEvent)
	 */
	public void onBeanLifecycleEvent(BeanContextLifecycleEvent event) {
		BeanId beanId = event.getBeanId();
		BeanLifecycle lifecycle = event.getLifecycle();		
		
		if(beanId == watchedBean && lifecycle == BeanLifecycle.BEGIN) {
			ExecutionContext executionContext = event.getExecutionContext();
			populator.populateAndSetPropertyValue(event.getBean(), executionContext.getBeanContext(), watchingBean, executionContext);
		} else if(beanId == watchingBean && lifecycle == BeanLifecycle.END) {
			BeanContext beanContext = event.getExecutionContext().getBeanContext();
			
			beanContext.removeObserver(this);
			// Need to remove the watched bean from the bean context too because it's lifecycle is associated 
			// with the lifecycle of the watching bean, which has been removed...
			beanContext.removeBean(watchedBean);
		}
	}
}
