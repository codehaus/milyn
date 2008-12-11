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
package org.milyn.persistence.dao.adapter.hibernate;

import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.Dao;
import org.milyn.persistence.dao.Flushable;
import org.milyn.persistence.dao.Lookupable;
import org.milyn.persistence.dao.Queryable;


/**
 * @author maurice_zeijen
 *
 */
public class SessionDaoAdapter implements Dao<Object>, Lookupable, Queryable, Flushable {

	private final Session session;

	/**
	 *
	 */
	public SessionDaoAdapter(final Session session) {
		AssertArgument.isNotNull(session, "session");

		this.session = session;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAO#flush()
	 */
	public void flush() {
		session.flush();
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAO#merge(java.lang.Object)
	 */
	public Object merge(final Object entity) {
		AssertArgument.isNotNull(entity, "entity");

		session.merge(entity);

		return entity;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAO#persist(java.lang.Object)
	 */
	public Object persist(final Object entity) {
		AssertArgument.isNotNull(entity, "entity");

		session.persist(entity);

		return null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	public Object lookup(final String name, final Object[] parameters) {

		AssertArgument.isNotNullAndNotEmpty(name, "name");
		AssertArgument.isNotNull(parameters, "parameters");

		final Query query = session.getNamedQuery(name);

		for(int i = 0; i < parameters.length; i++) {

			query.setParameter(i+1, parameters[i]);

		}

		return query.list();

	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Object lookup(final String name, final Map<String, ?> parameters) {
		AssertArgument.isNotNullAndNotEmpty(name, "name");
		AssertArgument.isNotNull(parameters, "parameters");

		final Query query = session.getNamedQuery(name);

		for(Entry<String, ?> entry : parameters.entrySet()) {

			query.setParameter(entry.getKey(), entry.getValue());

		}
		return query.list();
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.QueryFinder#findByQuery(java.lang.String, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	public Object lookupByQuery(final String query, final Object[] parameters) {
		AssertArgument.isNotNullAndNotEmpty(query, "query");
		AssertArgument.isNotNull(parameters, "parameters");

		// Is this useful?
		if(query.startsWith("@")) {
			return lookup(query.substring(1), parameters);
		}

		final Query sesQuery = session.createQuery(query);

		for(int i = 0; i < parameters.length; i++) {

			sesQuery.setParameter(i+1, parameters[i]);

		}

		return sesQuery.list();
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.QueryFinder#findByQuery(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Object lookupByQuery(final String query,
			final Map<String, ?> parameters) {

		AssertArgument.isNotNullAndNotEmpty(query, "query");
		AssertArgument.isNotNull(parameters, "parameters");

		// Is this useful?
		if(query.startsWith("@")) {
			return lookup(query.substring(1), parameters);
		}

		final Query sesQuery = session.createQuery(query);

		for(Entry<String, ?> entry : parameters.entrySet()) {

			sesQuery.setParameter(entry.getKey(), entry.getValue());

		}
		return sesQuery.list();
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

}
