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

import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.DaoRegister;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SqlMapClientRegister implements DaoRegister<SqlMapClientDaoAdapter>{

	private final SqlMapClient sqlMapClient;

	/**
	 *
	 */
	public SqlMapClientRegister(final SqlMapClient sqlMapClient) {
		AssertArgument.isNotNull(sqlMapClient, "sqlMapClient");

		this.sqlMapClient = sqlMapClient;
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegister#getDAO(java.lang.String)
	 */
	public SqlMapClientDaoAdapter getDao(final String name) {
		return new SqlMapClientDaoAdapter(sqlMapClient);
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegister#returnDAO(java.lang.Object)
	 */
	public void returnDao(final SqlMapClientDaoAdapter dao) {
	}
}
