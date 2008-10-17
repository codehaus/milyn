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
package org.milyn.persistence.dao.ibatis;

import java.sql.*;
import java.util.*;

import org.milyn.persistence.*;
import org.milyn.persistence.dao.*;

import com.ibatis.sqlmap.client.*;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ClientDAOAdapter implements NamedDAO<Object>, Finder<Object>, NamedFlushable  {

	private final SqlMapClient sqlMapClient;

	/**
	 * @param sqlMapClient
	 */
	public ClientDAOAdapter(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDAO#merge(java.lang.String, java.lang.Object)
	 */
	public Object merge(String name, Object entity) {
		try {
			sqlMapClient.update(name, entity);
		} catch (SQLException e) {
			throw new DAOException("Exception throw while executing update with statement id '" + name + "' and entity '" + entity + "'", e);
		}
		return entity;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedDAO#persist(java.lang.String, java.lang.Object)
	 */
	public void persist(String name, Object entity) {
		try {
			sqlMapClient.insert(name, entity);
		} catch (SQLException e) {
			throw new DAOException("Exception throw while executing insert with statement id '" + name + "' and entity '" + entity + "'", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.Finder#findBy(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> findBy(String name, Map<String, ?> parameters) {
		try {
			return sqlMapClient.queryForList(name, parameters);
		} catch (SQLException e) {
			throw new DAOException("Exception throw while executing query with statement id '" + name + "' and parameters '" + parameters + "'", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.NamedFlushable#flush(java.lang.String)
	 */
	public void flush(String name) {
		sqlMapClient.flushDataCache(name);
	}

}
