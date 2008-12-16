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
package org.milyn.scribe.invoker;

import java.util.Collection;
import java.util.Map;

import org.milyn.assertion.AssertArgument;
import org.milyn.scribe.NoMethodWithAnnotationFound;
import org.milyn.scribe.annotation.Flush;
import org.milyn.scribe.annotation.LookupByQuery;
import org.milyn.scribe.annotation.Merge;
import org.milyn.scribe.annotation.Persist;
import org.milyn.scribe.reflection.AnnotatedDaoRuntimeInfo;
import org.milyn.scribe.reflection.FlushMethod;
import org.milyn.scribe.reflection.LookupMethod;
import org.milyn.scribe.reflection.LookupWithNamedQueryMethod;
import org.milyn.scribe.reflection.LookupWithPositionalQueryMethod;
import org.milyn.scribe.reflection.MergeMethod;
import org.milyn.scribe.reflection.PersistMethod;


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
	public AnnotatedDaoInvoker(final Object dao, final AnnotatedDaoRuntimeInfo daoRuntimeInfo) {
		AssertArgument.isNotNull(dao, "dao");
		AssertArgument.isNotNull(daoRuntimeInfo, "daoRuntimeInfo");

		this.dao = dao;
		this.daoRuntimeInfo = daoRuntimeInfo;
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#flush()
	 */
	public void flush() {
		final FlushMethod method = daoRuntimeInfo.getFlushMethod();

		assertMethod(method, Flush.class);

		method.invoke(dao);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#merge(java.lang.Object)
	 */
	public Object merge(final Object entity) {
		final MergeMethod method = daoRuntimeInfo.getMergeMethod();

		assertMethod(method, Merge.class);

		return method.invoke(dao, entity);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#persist(java.lang.Object)
	 */
	public Object persist(final Object entity) {
		final PersistMethod method = daoRuntimeInfo.getPersistMethod();

		assertMethod(method, Persist.class);

		return method.invoke(dao, entity);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findByQuery(java.lang.String, java.lang.Object[])
	 */
	public Object lookupByQuery(final String query, final Object[] parameters) {

		final LookupWithPositionalQueryMethod method = daoRuntimeInfo.getLookupByPositionalQueryMethod();

		if(method == null) {
			throw new NoMethodWithAnnotationFound("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated " +
					"with [" + LookupByQuery.class.getName() + "] annotation and has an Array argument for the positional parameters.");
		}

		return method.invoke(dao, query, parameters);

	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findByQuery(java.lang.String, java.util.Map)
	 */
	public Object lookupByQuery(final String query, final Map<String, ?> parameters) {

		final LookupWithNamedQueryMethod method = daoRuntimeInfo.getLookupByNamedQueryMethod();

		if(method == null) {
			throw new NoMethodWithAnnotationFound("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated " +
					"with [" + LookupByQuery.class.getName() + "] annotation and has a Map argument for the named parameters.");
		}

		return method.invoke(dao, query, parameters);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findBy(java.lang.String, java.util.Map)
	 */
	public Object lookup(final String name, final Map<String, ?> parameters) {

		final LookupMethod method = daoRuntimeInfo.getLookupWithNamedParametersMethod(name);

		if(method == null) {
			throw new NoMethodWithAnnotationFound("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated " +
					"with [" + LookupByQuery.class.getName() + "] and has the name [" + name + "]");
		}

		return method.invoke(dao, parameters);

	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DaoInvoker#lookup(java.lang.String, java.lang.Object[])
	 */
	public Object lookup(String name, Object[] parameters) {
		throw new UnsupportedOperationException("A lookup with positional parameters is not supported for the annotation based DAO.");
	}

	private void assertMethod(final Object method, final Class<?> annotation) {

		if(method == null) {
			throw new NoMethodWithAnnotationFound("No method found in DAO class [" + dao.getClass().getName() + "] that is annotated with [" + annotation.getName() + "] annotation.");
		}

	}



}
