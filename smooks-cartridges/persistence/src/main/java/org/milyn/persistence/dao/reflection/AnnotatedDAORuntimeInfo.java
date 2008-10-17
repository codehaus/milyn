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

import static org.milyn.persistence.util.ClassUtils.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.milyn.annotation.AnnotatedClass;
import org.milyn.annotation.AnnotatedMethod;
import org.milyn.annotation.AnnotationManager;
import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.annotation.DAO;
import org.milyn.persistence.dao.annotation.FindBy;
import org.milyn.persistence.dao.annotation.FindByQuery;
import org.milyn.persistence.dao.annotation.Flush;
import org.milyn.persistence.dao.annotation.Merge;
import org.milyn.persistence.dao.annotation.Persist;

/**
 * @author maurice_zeijen
 *
 */
public class AnnotatedDAORuntimeInfo {

	private final Class<?> daoClass;

	private PersistMethod persistMethod;

	private MergeMethod mergeMethod;

	private FlushMethod flushMethod;

	private FindByNamedQueryMethod findByNamedQueryMethod;

	private FindByPositionalQueryMethod findByPositionalQueryMethod;

	private final Map<String, FindByMethod> findBy = new HashMap<String, FindByMethod>();

	/**
	 *
	 * @param daoClass
	 */
	public AnnotatedDAORuntimeInfo(final Class<?> daoClass) {
		AssertArgument.isNotNull(daoClass, "daoClass");

		this.daoClass = daoClass;

		analyze();
	}

	/**
	 * @return the daoClass
	 */
	public Class<?> getDaoClass() {
		return daoClass;
	}

	/**
	 * @return the persistMethod
	 */
	public PersistMethod getPersistMethod() {
		return persistMethod;
	}

	/**
	 * @return the mergeMethod
	 */
	public MergeMethod getMergeMethod() {
		return mergeMethod;
	}

	/**
	 * @return the flushMethod
	 */
	public FlushMethod getFlushMethod() {
		return flushMethod;
	}

	public FindByNamedQueryMethod getFindByNamedQueryMethod() {
		return findByNamedQueryMethod;
	}

	public FindByPositionalQueryMethod getFindByPositionalQueryMethod() {
		return findByPositionalQueryMethod;
	}

	public FindByMethod getFindByMethod(final String name) {
		return findBy.get(name);
	}

	/**
	 *
	 */
	private void analyze() {

		final AnnotatedClass annotatedClass =  AnnotationManager.getAnnotatedClass(daoClass);

		if(annotatedClass.getAnnotation(DAO.class) == null) {
			throw new RuntimeException("The class [" + daoClass.getName() + "] isn't annotated with the [" + DAO.class.getName() +"] annotation. Only class annotated with that annotation can be used as annotated DAO.");
		}

		final AnnotatedMethod[] annotatedMethods = annotatedClass.getAnnotatedMethods();
		for(final AnnotatedMethod method : annotatedMethods) {

			if(method.getAnnotation(Persist.class) != null) {

				analyzePersistMethod(method);

			} else 	if(method.getAnnotation(Merge.class) != null) {

				analyzeMergeMethod(method);

			} else if(method.getAnnotation(Flush.class) != null) {

				analyzeFlushMethod(method);

			} else if(method.getAnnotation(FindByQuery.class) != null) {

				analyzeFindByQueryMethod(method);

			} else if(method.getAnnotation(FindBy.class) != null) {

				analyzeFindByMethod(method);

			}
		}

	}

	/**
	 * @param method
	 */
	private void analyzeFlushMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		if(flushMethod != null) {
			throw new RuntimeException("A second method annotated with the ["+ Flush.class.getName() +"] annotation is found. Only one method per class is allowed to be annotated with this annotation.");
		}
		if(method.getParameterTypes().length > 0) {
			throw new RuntimeException("The Flush annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] has parameters, which isn't allowed.");
		}

		flushMethod = new FlushMethod(method);
	}

	/**
	 * @param method
	 */
	private void analyzeMergeMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		if(mergeMethod != null) {
			throw new RuntimeException("A second method annotated with the ["+ Merge.class.getName() +"] annotation is found. Only one method per class is allowed to be annotated with this annotation.");
		}
		if(method.getParameterTypes().length == 0) {
			throw new RuntimeException("The Merge annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] doesn't have a parameter, which it need.s");
		}
		if(method.getParameterTypes().length > 1) {
			throw new RuntimeException("The Merge annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] has more then 1 parameter, which isn't allowed.");
		}

		mergeMethod = new MergeMethod(method);
	}

	/**
	 * @param method
	 */
	private void analyzePersistMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		if(persistMethod != null) {
			throw new RuntimeException("A second method annotated with the ["+ Persist.class.getName() +"] annotation is found. Only one method per class is allowed to be annotated with this annotation.");
		}
		if(method.getParameterTypes().length == 0) {
			throw new RuntimeException("The Persist annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] doesn't have a parameter, which it needs.");
		}
		if(method.getParameterTypes().length > 1) {
			throw new RuntimeException("The Persist annotated method ["+ method +"]  the DAO class [" + daoClass.getName() + "] has more then 1 parameter, which isn't allowed.");
		}

		persistMethod = new PersistMethod(method);
	}


	/**
	 * @param method
	 */
	private void analyzeFindByQueryMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		final Class<?>[] parameters = method.getParameterTypes();

		if(method.getParameterTypes().length != 2) {
			throw new RuntimeException("The FindByQuery annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] " +
					"doesn't have exactly two parameters.");
		}

		if(!Collection.class.isAssignableFrom(method.getReturnType())) {
			throw new RuntimeException("The FindByQuery annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] " +
				"doesn't return an instance of Collection.");

		}

		final int queryIndex = indexOffFirstAssignableClass(String.class, parameters);
		if(queryIndex == -1) {
			throw new RuntimeException("The FindByQuery annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] " +
				"doesn't have a String parameter. This parameter is needed to receive the query string.");
		}

		final int parameterIndex = (queryIndex == 0) ? 1 : 0;

		if(containsAssignableClass(List.class, parameters) || containsAssignableClass(Object[].class, parameters)) {

			if(findByPositionalQueryMethod != null) {
				throw new RuntimeException("A second method annotated with the ["+ FindByQuery.class.getName() +"] annotation is found for a Positional query. " +
						"Only one method, with a List or Object array parameter, per class is allowed to be annotated with this annotation.");
			}

			findByPositionalQueryMethod = new FindByPositionalQueryMethod(method, queryIndex, parameterIndex);

		} else if(containsAssignableClass(Map.class, parameters)){

			if(findByNamedQueryMethod != null) {
				throw new RuntimeException("A second method annotated with the ["+ FindByQuery.class.getName() +"] annotation is found for a Positional query. " +
						"Only one method, with a Map parameter, per class is allowed to be annotated with this annotation.");
			}

			findByNamedQueryMethod = new FindByNamedQueryMethod(method, queryIndex, parameterIndex);

		} else {
			throw new RuntimeException("The FindByQuery annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] " +
				"doesn't have a List, Object array or Map parameter. This parameter is needed to receive the query parameters.");
		}


	}

	/**
	 * @param method
	 */
	private void analyzeFindByMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		final FindBy findByAnnotation = aMethod.getAnnotation(FindBy.class);
		final String name = findByAnnotation.value();

		if(name.trim().length() == 0) {
			throw new RuntimeException("The method [" + method + "] has the ["+ FindBy.class.getName() +"] annotation with an empty name. An empty name is not allowed.");
		}

		if(findBy.containsKey(name)) {
			throw new RuntimeException("A second method annotated with the ["+ FindBy.class.getName() +"] annotation and the name ["+ name +"] is found." +
					"The name of the FindBy annotation must be uniqeue per DAO Class.");
		}

		if(void.class.equals(method.getReturnType())){
			throw new RuntimeException("The FindBy annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] " +
					"returns void, which isn't allowed. The method must returns something.");
		}

		findBy.put(name, new FindByMethod(method));
	}


}
