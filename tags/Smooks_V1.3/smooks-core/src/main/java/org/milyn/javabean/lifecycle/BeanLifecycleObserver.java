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
package org.milyn.javabean.lifecycle;

import org.milyn.container.ExecutionContext;


/**
 * <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 *@deprecated Use the {@link BeanRepositoryLifecycleObserver} object
 */
@Deprecated
public interface BeanLifecycleObserver {
	
	void onBeanLifecycleEvent(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, Object bean);
	
}
