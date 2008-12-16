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

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.milyn.scribe.Dao;
import org.milyn.scribe.Flushable;
import org.milyn.scribe.Lookupable;
import org.milyn.scribe.Queryable;


/**
 * @author maurice_zeijen
 *
 */
public class InterfaceDaoInvoker implements DaoInvoker  {

	private final Dao<Object> dao;

	private Queryable  queryFinderDAO;

	private Lookupable  finderDAO;

	private Flushable flushableDAO;

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public InterfaceDaoInvoker(final Dao dao) {
		this.dao = dao;

		if(dao instanceof Queryable) {
			this.queryFinderDAO = (Queryable) dao;
		}

		if(dao instanceof Lookupable) {
			this.finderDAO = (Lookupable) dao;
		}

		if(dao instanceof Flushable) {
			this.flushableDAO = (Flushable) dao;
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#flush()
	 */
	public void flush() {
		if(flushableDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Flushable.class.getName() + "] interface and there for can't flush the DAO.");
		}
		flushableDAO.flush();
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#merge(java.lang.Object)
	 */
	public Object merge(final Object obj) {
		return dao.merge(obj);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#persist(java.lang.Object)
	 */
	public Object persist(final Object obj) {
		return dao.persist(obj);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findByQuery(java.lang.String, java.lang.Object[])
	 */
	public Object lookupByQuery(final String query, final Object[] parameters) {
		if(queryFinderDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Queryable.class.getName() + "] interface and there for can't find by query.");
		}
		return queryFinderDAO.lookupByQuery(query, parameters);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findByQuery(java.lang.String, java.util.Map)
	 */
	public Object lookupByQuery(final String query, final Map<String, ?> parameters) {
		if(queryFinderDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Queryable.class.getName() + "] interface and there for can't find by query.");
		}
		return queryFinderDAO.lookupByQuery(query, parameters);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findByQuery(java.lang.String, java.lang.Object[])
	 */
	public Object lookup(final String name, final Object[] parameters) {
		if(finderDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Lookupable.class.getName() + "] interface and there for can't find by query.");
		}
		return finderDAO.lookup(name, parameters);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.invoker.DAOInvoker#findByQuery(java.lang.String, java.util.Map)
	 */
	public Object lookup(final String name, final Map<String, ?> parameters) {
		if(finderDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Lookupable.class.getName() + "] interface and there for can't find by query.");
		}
		return finderDAO.lookup(name, parameters);
	}
}
