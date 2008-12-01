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
import org.milyn.persistence.dao.Lookupable;
import org.milyn.persistence.dao.MappedDao;
import org.milyn.persistence.dao.MappedFlushable;

/**
 * @author maurice_zeijen
 *
 */
public class InterfaceMappedDaoInvoker implements MappedDaoInvoker  {

	private final MappedDao<? super Object> dao;

	private Lookupable<? super Object>  finderDAO;

	private MappedFlushable flushableDAO;

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public InterfaceMappedDaoInvoker(final MappedDao<? super Object> dao) {
		this.dao = dao;

		if(dao instanceof Lookupable) {
			this.finderDAO = (Lookupable) dao;
		}

		if(dao instanceof MappedFlushable) {
			this.flushableDAO = (MappedFlushable) dao;
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#flush()
	 */
	public void flush(String id) {
		if(flushableDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + MappedFlushable.class.getName() + "] interface and there for can't flush the DAO.");
		}
		flushableDAO.flush(id);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#merge(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object merge(String id, final Object obj) {
		return dao.merge(id, obj);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.invoker.DAOInvoker#persist(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object persist(String id, final Object obj) {
		return dao.persist(id, obj);
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
	public Collection<?> lookup(final String id, final Map<String, ?> parameters) {
		if(finderDAO == null) {
			throw new NotImplementedException("The DAO [" + dao.getClass().getName() + "] doesn't implement the [" + Lookupable.class.getName() + "] interface and there for can't find by query.");
		}
		return finderDAO.lookup(id, parameters);
	}
}
