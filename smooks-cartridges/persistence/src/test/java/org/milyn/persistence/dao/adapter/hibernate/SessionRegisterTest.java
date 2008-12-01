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
package org.milyn.persistence.dao.adapter.hibernate;

import static junit.framework.Assert.*;

import org.hibernate.Session;
import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SessionRegisterTest extends BaseTestCase {

	@Mock
	Session session;

	@Test( groups = TestGroup.UNIT )
	public void test_getDao() {

		SessionRegister register = new SessionRegister(session);

		SessionDaoAdapter sessionDaoAdapter = register.getDao();

		assertNotNull(sessionDaoAdapter);

		assertSame(session, sessionDaoAdapter.getSession());

	}


}
