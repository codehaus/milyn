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

import java.util.Collection;
import java.util.Map;

import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.annotation.FindByQuery;
import org.milyn.persistence.dao.annotation.Flush;
import org.milyn.persistence.dao.annotation.Merge;
import org.milyn.persistence.dao.annotation.Persist;
import org.milyn.persistence.dao.reflection.AnnotatedDaoRuntimeInfo;
import org.milyn.persistence.dao.reflection.AnnotatedDaoRuntimeInfoRepository;
import org.milyn.persistence.dao.reflection.FindByMethod;
import org.milyn.persistence.dao.reflection.FindByNamedQueryMethod;
import org.milyn.persistence.dao.reflection.FindByPositionalQueryMethod;
import org.milyn.persistence.dao.reflection.FlushMethod;
import org.milyn.persistence.dao.reflection.MergeMethod;
import org.milyn.persistence.dao.reflection.PersistMethod;


/**
 * @author maurice_zeijen
 *
 */
public class AnnotatedDaoInvoker implements DaoInvoker {

	final private Object dao;

	final private AnnotatedDaoRuntimeInfo daoRuntimeInfo;

	/**
	 * @param dao
	 */
	public AnnotatedDaoInvoker(final Object dao, final AnnotatedDaoRuntimeInfoRepository daoRuntimeInfoRepository) {
		AssertArgument.isNotNull(dao, "dao");
		AssertArgument.isNotNull(daoRuntimeInfoRepository, "daoRuntimeInfoRepository");

		this.dao = dao;
		this.daoRuntimeInfo = daoRuntimeInfoRepository.get(dao.getClass());
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#flush()
	 */
	public void flush() {
		final FlushMethod method = daoRuntimeInfo.getFlushMethod();

		assertMethod(method, Flush.class);

		method.invoke(dao);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#merge(java.lang.Object)
	 */
	public Object merge(final Object entity) {
		final MergeMethod method = daoRuntimeInfo.getMergeMethod();

		assertMethod(method, Merge.class);

		return method.invoke(dao, entity);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#persist(java.lang.Object)
	 */
	public void persist(final Object entity) {
		final PersistMethod method = daoRuntimeInfo.getPersistMethod();

		assertMethod(method, Persist.class);

		method.invoke(dao, entity);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#findByQuery(java.lang.String, java.lang.Object[])
	 */
	public Collection<?> findByQuery(final String query, final Object[] parameters) {

		final FindByPositionalQueryMethod method = daoRuntimeInfo.getFindByPositionalQueryMethod();

		if(method == null) {
			throw new RuntimeException("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated " +
					"with [" + FindByQuery.class.getName() + "] annotation and has an Array argument for the positional parameters.");
		}

		return method.invoke(dao, query, parameters);

	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#findByQuery(java.lang.String, java.util.Map)
	 */
	public Collection<?> findByQuery(final String query, final Map<String, ?> parameters) {

		final FindByNamedQueryMethod method = daoRuntimeInfo.getFindByNamedQueryMethod();

		if(method == null) {
			throw new RuntimeException("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated " +
					"with [" + FindByQuery.class.getName() + "] annotation and has a Map argument for the named parameters.");
		}

		return method.invoke(dao, query, parameters);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#findBy(java.lang.String, java.util.Map)
	 */
	public Object findBy(final String name, final Map<String, ?> parameters) {

		final FindByMethod method = daoRuntimeInfo.getFindByMethod(name);

		if(method == null) {
			throw new RuntimeException("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated " +
					"with [" + FindByQuery.class.getName() + "] and has the name [" + name + "]");
		}

		return method.invoke(dao, parameters);

	}

	private void assertMethod(final Object method, final Class<?> annotation) {

		if(method == null) {
			throw new RuntimeException("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated with [" + annotation.getName() + "] annotation.");
		}

	}

}
