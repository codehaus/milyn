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
package org.milyn.persistence.dao.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.milyn.assertion.AssertArgument;

/**
 * @author maurice
 *
 */
public class PersistMethod {

	final Method method;

	/**
	 *
	 */
	public PersistMethod(final Method method) {
		AssertArgument.isNotNull(method, "method");

		this.method = method;
	}


	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.method.DAOMethod#invoke()
	 */
	public void invoke(final Object obj, final Object entity){
		try {

			method.invoke(obj, entity);

		} catch (final IllegalArgumentException e) {
			throw new RuntimeException("The method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "] threw an exception, while invoking it with the object [" + obj + "].", e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("The method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "] threw an exception, while invoking it with the object [" + obj + "].", e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException("The method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "] threw an exception, while invoking it with the object [" + obj + "].", e);
		}
	}
}
