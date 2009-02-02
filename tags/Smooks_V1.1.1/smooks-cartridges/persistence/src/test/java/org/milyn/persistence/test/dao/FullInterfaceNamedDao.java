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
package org.milyn.persistence.test.dao;

import java.util.Collection;
import java.util.Map;

import org.milyn.persistence.dao.Finder;
import org.milyn.persistence.dao.NamedDao;
import org.milyn.persistence.dao.NamedFlushable;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public interface FullInterfaceNamedDao<T> extends NamedDao<T>, NamedFlushable, Finder<T> {

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDao#merge(java.lang.String, java.lang.Object)
	 */
	public T merge(String name, T entity);

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDao#persist(java.lang.String, java.lang.Object)
	 */
	public void persist(String name, T entity);

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedFlushable#flush(java.lang.String)
	 */
	public void flush(String name);

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	public Collection<T> findBy(String name, Map<String, ?> parameters);

}
