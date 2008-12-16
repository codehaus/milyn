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
package org.milyn.persistence.dao.adapter.jpa;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.Dao;
import org.milyn.persistence.dao.Flushable;
import org.milyn.persistence.dao.Lookupable;
import org.milyn.persistence.dao.Queryable;


/**
 * This is an adapter for the EntityManager. This enables
 * simple queries on a EntityManager.<br>
 * <br>
 * Prefixing a query with a @ makes sure that
 * the query is handled as a named query. The @
 * is off course removed before the named query is called
 *
 * @author maurice_zeijen
 *
 */
public class EntityManagerDaoAdapter implements Dao<Object>, Lookupable, Queryable, Flushable {

	private final EntityManager entityManager;

	/**
	 * @param entityManager
	 */
	public EntityManagerDaoAdapter(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAO#flush()
	 */
	public void flush() {
		entityManager.flush();
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAO#merge(java.lang.Object)
	 */
	public Object merge(final Object entity) {
		AssertArgument.isNotNull(entity, "entity");

		return entityManager.merge(entity);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAO#persist(java.lang.Object)
	 */
	public Object persist(final Object entity) {
		AssertArgument.isNotNull(entity, "entity");

		entityManager.persist(entity);

		return null;
	}


	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookup(final String name, final Object[] parameters) {
		AssertArgument.isNotNullAndNotEmpty(name, "name");
		AssertArgument.isNotNull(parameters, "parameters");

		final Query emQuery = entityManager.createNamedQuery(name);

		for(int i = 0; i < parameters.length; i++) {

			emQuery.setParameter(i+1, parameters[i]);

		}

		return emQuery.getResultList();
	}


	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookup(final String name, final Map<String, ?> parameters) {
		AssertArgument.isNotNullAndNotEmpty(name, "name");
		AssertArgument.isNotNull(parameters, "parameters");

		final Query emQuery = entityManager.createNamedQuery(name);

		for(final String key : parameters.keySet()) {

			emQuery.setParameter(key, parameters.get(key));

		}
		return emQuery.getResultList();
	}


	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.QueryFinder#findByQuery(java.lang.String, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookupByQuery(final String query, final Object[] parameters) {
		AssertArgument.isNotNullAndNotEmpty(query, "query");
		AssertArgument.isNotNull(parameters, "parameters");

		// Is this useful?
		if(query.startsWith("@")) {
			return lookup(query.substring(1), parameters);
		}

		final Query emQuery = entityManager.createQuery(query);

		for(int i = 0; i < parameters.length; i++) {

			emQuery.setParameter(i+1, parameters[i]);

		}

		return emQuery.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.QueryFinder#findByQuery(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookupByQuery(final String query, final Map<String, ?> parameters) {
		AssertArgument.isNotNullAndNotEmpty(query, "query");
		AssertArgument.isNotNull(parameters, "parameters");

		// Is this useful?
		if(query.startsWith("@")) {
			return lookup(query.substring(1), parameters);
		}

		final Query emQuery = entityManager.createQuery(query);

		for(final String key : parameters.keySet()) {

			emQuery.setParameter(key, parameters.get(key));

		}
		return emQuery.getResultList();
	}

	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}



}
