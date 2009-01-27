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
package org.milyn.scribe.dao.reflection;

import static org.milyn.util.ClassUtil.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.milyn.annotation.AnnotatedClass;
import org.milyn.annotation.AnnotatedMethod;
import org.milyn.annotation.AnnotationManager;
import org.milyn.assertion.AssertArgument;
import org.milyn.scribe.dao.annotation.Dao;
import org.milyn.scribe.dao.annotation.Flush;
import org.milyn.scribe.dao.annotation.Insert;
import org.milyn.scribe.dao.annotation.Lookup;
import org.milyn.scribe.dao.annotation.LookupByQuery;
import org.milyn.scribe.dao.annotation.ReturnsNoEntity;
import org.milyn.scribe.dao.annotation.Update;
import org.milyn.scribe.dao.annotation.Delete;

/**
 * @author maurice_zeijen
 *
 */
public class AnnotatedDaoRuntimeInfo {

	private final Class<?> daoClass;

	private EntityMethod insertMethod;

	private EntityMethod updateMethod;

	private FlushMethod flushMethod;

	private EntityMethod deleteMethod;

	private LookupWithNamedQueryMethod lookupWithNamedQueryMethod;

	private LookupWithPositionalQueryMethod lookupWithPositionalQueryMethod;

	private final Map<String, LookupMethod> lookupWithNamedParameters = new HashMap<String, LookupMethod>();

	/**
	 *
	 * @param daoClass
	 */
	AnnotatedDaoRuntimeInfo(final Class<?> daoClass) {
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
	 * @return the insertMethod
	 */
	public EntityMethod getInsertMethod() {
		return insertMethod;
	}

	/**
	 * @return the updateMethod
	 */
	public EntityMethod getUpdateMethod() {
		return updateMethod;
	}

	/**
	 * @return the flushMethod
	 */
	public FlushMethod getFlushMethod() {
		return flushMethod;
	}

	/**
	 * @return the flushMethod
	 */
	public EntityMethod getDeleteMethod() {
		return deleteMethod;
	}

	public LookupWithNamedQueryMethod getLookupByNamedQueryMethod() {
		return lookupWithNamedQueryMethod;
	}

	public LookupWithPositionalQueryMethod getLookupByPositionalQueryMethod() {
		return lookupWithPositionalQueryMethod;
	}

	public LookupMethod getLookupWithNamedParametersMethod(final String name) {
		return lookupWithNamedParameters.get(name);
	}

	/**
	 *
	 */
	private void analyze() {

		final AnnotatedClass annotatedClass =  AnnotationManager.getAnnotatedClass(daoClass);

		if(annotatedClass.getAnnotation(Dao.class) == null) {
			throw new RuntimeException("The class [" + daoClass.getName() + "] isn't annotated with the [" + Dao.class.getName() +"] annotation. Only class annotated with that annotation can be used as annotated DAO.");
		}

		final AnnotatedMethod[] annotatedMethods = annotatedClass.getAnnotatedMethods();
		for(final AnnotatedMethod method : annotatedMethods) {

			if(method.getAnnotation(Insert.class) != null) {

				analyzeInsertMethod(method);

			} else 	if(method.getAnnotation(Update.class) != null) {

				analyzeUpdateMethod(method);

			} else if(method.getAnnotation(Flush.class) != null) {

				analyzeFlushMethod(method);

			} else if(method.getAnnotation(Delete.class) != null) {

				analyzeDeleteMethod(method);

			} else if(method.getAnnotation(LookupByQuery.class) != null) {

				analyzeFindByQueryMethod(method);

			} else if(method.getAnnotation(Lookup.class) != null) {

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
	private void analyzeUpdateMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		if(updateMethod != null) {
			throw new RuntimeException("A second method annotated with the ["+ Update.class.getName() +"] annotation is found. Only one method per class is allowed to be annotated with this annotation.");
		}
		if(method.getParameterTypes().length == 0) {
			throw new RuntimeException("The Update annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] doesn't have a parameter, which it need.");
		}
		if(method.getParameterTypes().length > 1) {
			throw new RuntimeException("The Update annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] has more then 1 parameter, which isn't allowed.");
		}

		boolean returnsEntity  = !method.isAnnotationPresent(ReturnsNoEntity.class);

		updateMethod = new EntityMethod(method, returnsEntity);
	}

	/**
	 * @param method
	 */
	private void analyzeInsertMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		if(insertMethod != null) {
			throw new RuntimeException("A second method annotated with the ["+ Insert.class.getName() +"] annotation is found. Only one method per class is allowed to be annotated with this annotation.");
		}
		if(method.getParameterTypes().length == 0) {
			throw new RuntimeException("The Insert annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] doesn't have a parameter, which it needs.");
		}
		if(method.getParameterTypes().length > 1) {
			throw new RuntimeException("The Insert annotated method ["+ method +"]  the DAO class [" + daoClass.getName() + "] has more then 1 parameter, which isn't allowed.");
		}

		boolean returnsEntity  = !method.isAnnotationPresent(ReturnsNoEntity.class);

		insertMethod = new EntityMethod(method, returnsEntity);
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

			if(lookupWithPositionalQueryMethod != null) {
				throw new RuntimeException("A second method annotated with the ["+ LookupByQuery.class.getName() +"] annotation is found for a Positional query. " +
						"Only one method, with a List or Object array parameter, per class is allowed to be annotated with this annotation.");
			}

			lookupWithPositionalQueryMethod = new LookupWithPositionalQueryMethod(method, queryIndex, parameterIndex);

		} else if(containsAssignableClass(Map.class, parameters)){

			if(lookupWithNamedQueryMethod != null) {
				throw new RuntimeException("A second method annotated with the ["+ LookupByQuery.class.getName() +"] annotation is found for a Positional query. " +
						"Only one method, with a Map parameter, per class is allowed to be annotated with this annotation.");
			}

			lookupWithNamedQueryMethod = new LookupWithNamedQueryMethod(method, queryIndex, parameterIndex);

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

		final Lookup findByAnnotation = aMethod.getAnnotation(Lookup.class);
		final String name = findByAnnotation.value();

		if(name.trim().length() == 0) {
			throw new RuntimeException("The method [" + method + "] has the ["+ Lookup.class.getName() +"] annotation with an empty name. An empty name is not allowed.");
		}

		if(lookupWithNamedParameters.containsKey(name)) {
			throw new RuntimeException("A second method annotated with the ["+ Lookup.class.getName() +"] annotation and the name ["+ name +"] is found." +
					"The name of the FindBy annotation must be uniqeue per DAO Class.");
		}

		if(void.class.equals(method.getReturnType())){
			throw new RuntimeException("The FindBy annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] " +
					"returns void, which isn't allowed. The method must return something.");
		}

		lookupWithNamedParameters.put(name, new LookupMethod(method));
	}

	/**
	 * @param method
	 */
	private void analyzeDeleteMethod(final AnnotatedMethod aMethod) {
		final Method method = aMethod.getMethod();

		if(deleteMethod != null) {
			throw new RuntimeException("A second method annotated with the ["+ Delete.class.getName() +"] annotation is found. Only one method per class is allowed to be annotated with this annotation.");
		}
		if(method.getParameterTypes().length == 0) {
			throw new RuntimeException("The Delete annotated method ["+ method +"] of the DAO class [" + daoClass.getName() + "] doesn't have a parameter, which it needs.");
		}
		if(method.getParameterTypes().length > 1) {
			throw new RuntimeException("The Delete annotated method ["+ method +"]  the DAO class [" + daoClass.getName() + "] has more then 1 parameter, which isn't allowed.");
		}

		boolean returnsEntity  = !method.isAnnotationPresent(ReturnsNoEntity.class);

		deleteMethod = new EntityMethod(method, returnsEntity);
	}


}
