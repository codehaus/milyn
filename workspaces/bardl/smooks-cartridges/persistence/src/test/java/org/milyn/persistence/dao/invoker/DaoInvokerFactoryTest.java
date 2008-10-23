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
package org.milyn.persistence.dao.invoker;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.milyn.container.ApplicationContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.persistence.dao.Dao;
import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.dao.FullAnnotatedDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class DaoInvokerFactoryTest extends BaseTestCase {

	ApplicationContext appContext = new MockApplicationContext();

	@Test(groups = TestGroup.UNIT)
	public void test_getInstance() {

		DaoInvokerFactory factory  = DaoInvokerFactory.getInstance();

		assertNotNull(factory);

		DaoInvokerFactory factory2  = DaoInvokerFactory.getInstance();

		assertSame(factory, factory2);

	}

	@Test(groups = TestGroup.UNIT)
	public void test_create_with_interfaced_dao() {

		DaoInvokerFactory factory  = DaoInvokerFactory.getInstance();

		@SuppressWarnings("unchecked")
		Dao<Object> daoMock = mock(Dao.class);

		DaoInvoker daoInvoker = factory.create(daoMock, appContext);

		assertNotNull(daoInvoker);

		Object entity = new Object();

		daoInvoker.persist(entity);

		verify(daoMock).persist(same(entity));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_create_with_annotated_dao() {

		DaoInvokerFactory factory  = DaoInvokerFactory.getInstance();

		FullAnnotatedDao daoMock = mock(FullAnnotatedDao.class);

		DaoInvoker daoInvoker = factory.create(daoMock, appContext);

		assertNotNull(daoInvoker);

		Object entity = new Object();

		daoInvoker.persist(entity);

		verify(daoMock).persistIt(same(entity));

		assertNotNull(appContext.getAttribute(DaoInvokerFactory.REPOSITORY_KEY));

	}


}
