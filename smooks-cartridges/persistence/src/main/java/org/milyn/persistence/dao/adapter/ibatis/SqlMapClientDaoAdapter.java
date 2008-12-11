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
import org.milyn.persistence.dao.MappedDao;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SqlMapClientDaoAdapter implements MappedDao<Object>, Lookupable  {

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
	public Object merge(String id, Object entity) {
		try {
			sqlMapClient.update(id, entity);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing update with statement id '" + id + "' and entity '" + entity + "'", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDAO#persist(java.lang.String, java.lang.Object)
	 */
	public Object persist(String id, Object entity) {
		try {
			sqlMapClient.insert(id, entity);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing insert with statement id '" + id + "' and entity '" + entity + "'", e);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookup(String id, Map<String, ?> parameters) {
		try {
			return sqlMapClient.queryForList(id, parameters);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing query with statement id '" + id + "' and parameters '" + parameters + "'", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> lookup(String id, Object[] parameters) {
		try {
			return sqlMapClient.queryForList(id, parameters);
		} catch (SQLException e) {
			throw new DaoException("Exception throw while executing query with statement id '" + id + "' and parameters '" + parameters + "'", e);
		}
	}

	/**
	 * @return the sqlMapClient
	 */
	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}

}
