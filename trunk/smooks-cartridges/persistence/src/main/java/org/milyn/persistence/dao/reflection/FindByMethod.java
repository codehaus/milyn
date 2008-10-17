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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.milyn.annotation.AnnotatedMethod;
import org.milyn.annotation.AnnotationManager;
import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.annotation.Param;

/**
 * @author maurice
 *
 * TODO: implement type checking for primative types...
 */
public class FindByMethod {

	final Method method;

	final Map<String, Integer> parameterPositions = new HashMap<String, Integer>();

	/**
	 *
	 */
	public FindByMethod(final Method method) {
		AssertArgument.isNotNull(method, "method");

		this.method = method;

		analyzeParameters();
	}

	/**
	 *
	 */
	private void analyzeParameters() {

		final AnnotatedMethod aMethod = AnnotationManager.getAnnotatedClass(method.getDeclaringClass()).getAnnotatedMethod(method);

		final Annotation[][] parameterAnnotations = aMethod.getParameterAnnotations();

		for(int i = 0; i < parameterAnnotations.length; i++) {

			boolean found = false;
			for(final Annotation annotation : parameterAnnotations[i]) {

				found = Param.class.equals(annotation.annotationType());
				if(found) {
					final Param param = (Param) annotation;

					final String name = param.value().trim();

					if(name.length() == 0) {
						throw new RuntimeException("Illegal empty parameter value encounterd on parameter " + i
								+ " of method [" + method + "] from class [" + method.getDeclaringClass().getName() +"].");
					}

					parameterPositions.put(param.value(), i);

				}

				if(!found) {
					throw new RuntimeException("Parameter " + i
							+ " of method [" + method + "] from class [" + method.getDeclaringClass().getName() +"] isn't annotated with a [" + Param.class.getName() + "] annotation.");
				}

			}


		}

	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.method.DAOMethod#invoke()
	 *
	 *
	 */
	public Object invoke(final Object obj, final Map<String, ?> parameters){

		final Object[] args = new Object[parameterPositions.values().size()];

		//TODO: evaluate a faster way to map the arguments but which is equally safe
		for(final String parameterName : parameters.keySet()) {

			final Integer position = parameterPositions.get(parameterName);

			if(position == null) {
				throw new RuntimeException("Parameter with the name " + parameterName + " isn't found on the method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "]");
			}

			args[position] = parameters.get(parameterName);
		}


		try {
			return method.invoke(obj, args);
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException("The method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "] threw an exception, while invoking it with the object [" + obj + "].", e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("The method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "] threw an exception, while invoking it with the object [" + obj + "].", e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException("The method [" + method + "] of the class [" + method.getDeclaringClass().getName() + "] threw an exception, while invoking it with the object [" + obj + "].", e);
		}
	}
}
