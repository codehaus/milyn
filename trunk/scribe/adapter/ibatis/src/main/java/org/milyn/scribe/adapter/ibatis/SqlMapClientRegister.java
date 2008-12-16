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
package org.milyn.scribe.adapter.ibatis;

import org.milyn.assertion.AssertArgument;
import org.milyn.scribe.AbstractDaoRegister;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SqlMapClientRegister extends AbstractDaoRegister<SqlMapClientDaoAdapter>{

	private final SqlMapClientDaoAdapter sqlMapClient;

	/**
	 *
	 */
	public SqlMapClientRegister(final SqlMapClient sqlMapClient) {
		AssertArgument.isNotNull(sqlMapClient, "sqlMapClient");

		this.sqlMapClient = new SqlMapClientDaoAdapter(sqlMapClient);
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.scribe.dao.DAORegister#getDAO()
	 */
	@Override
	public SqlMapClientDaoAdapter getDao() {
		return sqlMapClient;
	}

}
