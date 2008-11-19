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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.milyn.persistence.dao.NamedDao;
import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.dao.FullInterfaceNamedDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class InterfaceNamedDaoInvokerTest extends BaseTestCase {

	@Mock
	private FullInterfaceNamedDao<Object> fullDao;

	@Mock
	private NamedDao<Object> minimumDao;

	@Test(groups = TestGroup.UNIT)
	public void test_persist() {

		NamedDaoInvoker invoker = new InterfaceNamedDaoInvoker(fullDao);

		Object toPersist = new Object();

		invoker.persist("id", toPersist);

		verify(fullDao).persist(eq("id"), same(toPersist));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_merge() {


		NamedDaoInvoker invoker = new InterfaceNamedDaoInvoker(fullDao);

		Object toMerge = new Object();

		invoker.merge("id", toMerge);

		verify(fullDao).merge(eq("id"), same(toMerge));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_flush() {

		NamedDaoInvoker invoker = new InterfaceNamedDaoInvoker(fullDao);

		invoker.flush("id");

		verify(fullDao).flush(eq("id"));

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_flush_non_flushable_dao() {

		NamedDaoInvoker invoker = new InterfaceNamedDaoInvoker(minimumDao);

		invoker.flush("id");

	}

	@Test(groups = TestGroup.UNIT)
	public void test_lookup() {

		NamedDaoInvoker invoker = new InterfaceNamedDaoInvoker(fullDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookup("id", params);

		verify(fullDao).lookup(eq("id"), same(params));

	}


	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_lookup_non_finder_dao() {

		NamedDaoInvoker invoker = new InterfaceNamedDaoInvoker(minimumDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookup("id", params);

	}


}
