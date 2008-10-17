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
package org.milyn.persistence.dao.hibernate;

import org.hibernate.Session;
import org.milyn.assertion.AssertArgument;
import org.milyn.persistence.dao.DaoRegister;


/**
 * @author maurice_zeijen
 *
 */
public class SessionRegister implements DaoRegister<SessionDaoAdapter> {

	private final Session session;

	/**
	 *
	 */
	public SessionRegister(final Session session) {
		AssertArgument.isNotNull(session, "session");

		this.session = session;
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegistery#getDAO(java.lang.String)
	 */
	public SessionDaoAdapter getDAO(final String name) {

		return new SessionDaoAdapter(session);
	}

	/* (non-Javadoc)
	 * @see org.milyn.persistence.dao.DAORegistery#returnDAO(java.lang.Object)
	 */
	public void returnDAO(final SessionDaoAdapter dao) {
	}

}
