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

import org.apache.commons.lang.NotImplementedException;
import org.milyn.persistence.dao.Finder;
import org.milyn.persistence.dao.NamedDAO;
import org.milyn.persistence.dao.NamedFlushable;


/**
 * @author maurice_zeijen
 *
 */
public class InterfaceNamedDAOInvoker implements NamedDAOInvoker  {

	private final NamedDAO<? super Object> dao;

	private Finder<? super Object>  finderDAO;

	private NamedFlushable flushableDAO;

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public InterfaceNamedDAOInvoker(final NamedDAO<? super Object> dao) {
		this.dao = dao;

		if(dao instanceof Finder) {
			this.finderDAO = (Finder) dao;
		}

		if(dao instanceof NamedFlushable) {
			this.flushableDAO = (NamedFlushable) dao;
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#flush()
	 */
	public void flush(String name) {
		flushableDAO.flush(name);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#merge(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object merge(String name, final Object obj) {
		return dao.merge(name, obj);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#persist(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void persist(String name, final Object obj) {
		dao.persist(name, obj);
	}

//	/* (non-Javadoc)
//	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#findByQuery(java.lang.String, java.lang.Object[])
//	 */
//	public Collection<?> findBy(final String name, final Object[] parameters) {
//		if(finderDAO == null) {
//			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Finder.class.getName() + "] interface and there for can't find by query.");
//		}
//		return finderDAO.findBy(name, parameters);
//	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#findByQuery(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<?> findBy(final String name, final Map<String, ?> parameters) {
		if(finderDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Finder.class.getName() + "] interface and there for can't find by query.");
		}
		return finderDAO.findBy(name, parameters);
	}
}
