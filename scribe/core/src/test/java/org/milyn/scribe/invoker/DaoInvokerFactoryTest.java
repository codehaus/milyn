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
package org.milyn.scribe.invoker;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.milyn.scribe.dao.Dao;
import org.milyn.scribe.dao.MapObjectStore;
import org.milyn.scribe.dao.ObjectStore;
import org.milyn.scribe.dao.invoker.DaoInvoker;
import org.milyn.scribe.dao.invoker.DaoInvokerFactory;
import org.milyn.scribe.test.dao.FullAnnotatedDao;
import org.milyn.scribe.test.util.BaseTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class DaoInvokerFactoryTest extends BaseTestCase {

	ObjectStore objectStore;

	@Test(groups = "unit")
	public void test_getInstance() {

		DaoInvokerFactory factory  = DaoInvokerFactory.getInstance();

		assertNotNull(factory);

		DaoInvokerFactory factory2  = DaoInvokerFactory.getInstance();

		assertSame(factory, factory2);

	}

	@Test(groups = "unit")
	public void test_create_with_interfaced_dao() {

		DaoInvokerFactory factory  = DaoInvokerFactory.getInstance();

		@SuppressWarnings("unchecked")
		Dao<Object> daoMock = mock(Dao.class);

		DaoInvoker daoInvoker = factory.create(daoMock, objectStore);

		assertNotNull(daoInvoker);

		Object entity = new Object();

		daoInvoker.insert(entity);

		verify(daoMock).insert(same(entity));

	}

	@Test(groups = "unit")
	public void test_create_with_annotated_dao() {

		DaoInvokerFactory factory  = DaoInvokerFactory.getInstance();

		FullAnnotatedDao daoMock = mock(FullAnnotatedDao.class);

		DaoInvoker daoInvoker = factory.create(daoMock, objectStore);

		assertNotNull(daoInvoker);

		Object entity = new Object();

		daoInvoker.insert(entity);

		verify(daoMock).insertIt(same(entity));

		assertNotNull(objectStore.get(DaoInvokerFactory.REPOSITORY_KEY));

	}

	@BeforeMethod
	public void setup() {
		objectStore = new MapObjectStore();
	}
}
