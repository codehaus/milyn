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

import org.milyn.persistence.MapObjectStore;
import org.milyn.persistence.ObjectStore;
import org.milyn.persistence.dao.MappedDao;
import org.milyn.persistence.test.TestGroup;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class NamedDaoInvokerFactoryTest {

	@Test(groups = TestGroup.UNIT)
	public void test_getInstance() {

		MappedDaoInvokerFactory factory  = MappedDaoInvokerFactory.getInstance();

		assertNotNull(factory);

		MappedDaoInvokerFactory factory2  = MappedDaoInvokerFactory.getInstance();

		assertSame(factory, factory2);

	}

	@Test(groups = TestGroup.UNIT)
	public void test_create() {

		MappedDaoInvokerFactory factory  = MappedDaoInvokerFactory.getInstance();
		ObjectStore objectStore =  new MapObjectStore();

		@SuppressWarnings("unchecked")
		MappedDao<Object> namedDaoMock = mock(MappedDao.class);

		MappedDaoInvoker namedDaoInvoker = factory.create(namedDaoMock, objectStore);

		assertNotNull(namedDaoInvoker);

		Object entity = new Object();

		namedDaoInvoker.persist("id", entity);

		verify(namedDaoMock).persist(eq("id"), same(entity));

	}

}
