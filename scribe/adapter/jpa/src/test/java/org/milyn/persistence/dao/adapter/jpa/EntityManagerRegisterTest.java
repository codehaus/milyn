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
package org.milyn.persistence.dao.adapter.jpa;

import static junit.framework.Assert.*;

import javax.persistence.EntityManager;

import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.Mock;
import org.testng.annotations.Test;
/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class EntityManagerRegisterTest extends BaseTestCase {

	@Mock
	EntityManager entityManager;

	@Test( groups = TestGroup.UNIT )
	public void test_getDao() {

		EntityManagerRegister register = new EntityManagerRegister(entityManager);

		EntityManagerDaoAdapter entityManagerDaoAdapter = register.getDao();

		assertNotNull(entityManagerDaoAdapter);

		assertSame(entityManager, entityManagerDaoAdapter.getEntityManager());

	}


}
