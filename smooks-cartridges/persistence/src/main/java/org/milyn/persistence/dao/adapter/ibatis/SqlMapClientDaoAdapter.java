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
package org.milyn.persistence.dao.adapter.ibatis;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.milyn.persistence.DaoException;
import org.milyn.persistence.dao.Lookupable;
import org.milyn.persistence.dao.NamedDao;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SqlMapClientDaoAdapter implements NamedDao<Object>, Lookupable<Object>  {

	private final SqlMapClient sqlMapClient;

	/**
	 * @param sqlMapClient
	 */
	public SqlMapClientDaoAdapter(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDAO#merge(java.lang.String, java.lang.Object)
	 */
	public Object merge(String name, Object entity) {
		try {
			sqlMapClient.update(name, entity);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing update with statement id '" + name + "' and entity '" + entity + "'", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDAO#persist(java.lang.String, java.lang.Object)
	 */
	public Object persist(String name, Object entity) {
		try {
			sqlMapClient.insert(name, entity);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing insert with statement id '" + name + "' and entity '" + entity + "'", e);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookup(String name, Map<String, ?> parameters) {
		try {
			return sqlMapClient.queryForList(name, parameters);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing query with statement id '" + name + "' and parameters '" + parameters + "'", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookup(String name, Object[] parameters) {
		try {
			return sqlMapClient.queryForList(name, parameters);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing query with statement id '" + name + "' and parameters '" + parameters + "'", e);
		}
	}

	/**
	 * @return the sqlMapClient
	 */
	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}

}
