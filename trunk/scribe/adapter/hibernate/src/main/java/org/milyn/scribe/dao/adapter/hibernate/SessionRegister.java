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
package org.milyn.scribe.dao.adapter.hibernate;

import org.hibernate.Session;
import org.milyn.assertion.AssertArgument;
import org.milyn.scribe.dao.AbstractDaoRegister;


/**
 * @author maurice_zeijen
 *
 */
public class SessionRegister extends AbstractDaoRegister<SessionDaoAdapter> {

	private final SessionDaoAdapter defaultSession;

	/**
	 *
	 */
	public SessionRegister(final Session session) {
		AssertArgument.isNotNull(session, "session");

		this.defaultSession = new SessionDaoAdapter(session);
	}

	/* (non-Javadoc)
	 * @see org.milyn.scribe.dao.AbstractDaoRegister#getDao()
	 */
	@Override
	public SessionDaoAdapter getDao() {
		return defaultSession;
	}


}
