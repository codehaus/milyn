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

import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.DAORegister;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ClientRegister implements DAORegister<ClientDAOAdapter>{

	private final SqlMapClient sqlMapClient;

	/**
	 *
	 */
	public ClientRegister(final SqlMapClient sqlMapClient) {
		AssertArgument.isNotNull(sqlMapClient, "sqlMapClient");

		this.sqlMapClient = sqlMapClient;
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegister#getDAO(java.lang.String)
	 */
	public ClientDAOAdapter getDAO(final String name) {
		return new ClientDAOAdapter(sqlMapClient);
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegister#returnDAO(java.lang.Object)
	 */
	public void returnDAO(final ClientDAOAdapter dao) {
	}
}
