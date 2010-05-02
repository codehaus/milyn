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
package org.milyn.javabean.dynamic;

import org.milyn.assertion.AssertArgument;

/**
 * Bean metadata.
 * <p/>
 * This class is used to hold additional data about a model bean instance.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanMetadata {
	
	private Object bean;
	private String namespace;

	public BeanMetadata(Object bean) {
		AssertArgument.isNotNull(bean, "bean");
		this.bean = bean;
	}

	public Object getBean() {
		return bean;
	}
	
	public BeanMetadata setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}
}