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
package org.milyn.persistence.dao.invoker;

import org.milyn.annotation.AnnotatedClass;
import org.milyn.annotation.AnnotationManager;
import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.ObjectStore;
import org.milyn.persistence.dao.Dao;
import org.milyn.persistence.dao.reflection.AnnotatedDaoRuntimeInfoFactory;

/**
 * @author maurice_zeijen
 *
 */
public class DaoInvokerFactory {

	private static final DaoInvokerFactory instance = new DaoInvokerFactory();

	public static final String REPOSITORY_KEY = DaoInvokerFactory.class.getName() + "#REPOSITORY_KEY";

	public static final DaoInvokerFactory getInstance() {
		return instance;
	}

	private DaoInvokerFactory() {
	}


	public DaoInvoker create(final Object dao, final ObjectStore objectStore) {
		AssertArgument.isNotNull(dao, "dao");
		AssertArgument.isNotNull(objectStore, "objectStore");

		if(dao instanceof Dao) {
			return new InterfaceDaoInvoker((Dao<?>) dao);
		} else {

			final AnnotatedClass annotatedClass =  AnnotationManager.getAnnotatedClass(dao.getClass());

			if(annotatedClass.isAnnotationPresent(org.milyn.persistence.dao.annotation.Dao.class)) {

				final AnnotatedDaoRuntimeInfoFactory repository = getAnnotatedDAORuntimeInfoRepository(objectStore);

				return new AnnotatedDaoInvoker(dao, repository.create(dao.getClass()));

			} else {
				throw new IllegalArgumentException("The DAO argument doesn't implement the [" + Dao.class.getName() + "] interface " +
						"or is annotated with the [" + org.milyn.persistence.dao.annotation.Dao.class.getName() + "] annotation");
			}
		}
	}

	/**
	 * @param attributestore
	 * @return
	 */
	private AnnotatedDaoRuntimeInfoFactory getAnnotatedDAORuntimeInfoRepository(final ObjectStore objectStore) {
		AnnotatedDaoRuntimeInfoFactory repository = (AnnotatedDaoRuntimeInfoFactory) objectStore.get(REPOSITORY_KEY);

		if(repository == null) {
			repository = new AnnotatedDaoRuntimeInfoFactory();

			objectStore.set(REPOSITORY_KEY, repository);
		}
		return repository;
	}

}
