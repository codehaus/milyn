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
import org.milyn.persistence.dao.Dao;
import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.dao.FullInterfaceDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class InterfaceDaoInvokerTest extends BaseTestCase {

	@Mock
	private FullInterfaceDao<Object> fullDao;

	@Mock
	private Dao<Object> minimumDao;

	@Test(groups = TestGroup.UNIT)
	public void test_persist() {

		DaoInvoker invoker = new InterfaceDaoInvoker(fullDao);

		Object toPersist = new Object();

		invoker.persist(toPersist);

		verify(fullDao).persist(same(toPersist));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_merge() {


		DaoInvoker invoker = new InterfaceDaoInvoker(fullDao);

		Object toMerge = new Object();

		invoker.merge(toMerge);

		verify(fullDao).merge(same(toMerge));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_flush() {

		DaoInvoker invoker = new InterfaceDaoInvoker(fullDao);

		invoker.flush();

		verify(fullDao).flush();

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_flush_non_flushable_dao() {

		DaoInvoker invoker = new InterfaceDaoInvoker(minimumDao);

		invoker.flush();

	}

	@Test(groups = TestGroup.UNIT)
	public void test_findBy() {

		DaoInvoker invoker = new InterfaceDaoInvoker(fullDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findBy("id", params);

		verify(fullDao).findBy(eq("id"), same(params));

	}


	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_findBy_non_finder_dao() {

		DaoInvoker invoker = new InterfaceDaoInvoker(minimumDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findBy("id", params);

	}

	@Test(groups = TestGroup.UNIT)
	public void test_findBy_query_map_params() {

		DaoInvoker invoker = new InterfaceDaoInvoker(fullDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_findBy_query_array_params() {

		DaoInvoker invoker = new InterfaceDaoInvoker(fullDao);

		Object[] params = new Object[0];

		invoker.findByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}


	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_findBy_non_query_finder_dao_map_params() {

		DaoInvoker invoker = new InterfaceDaoInvoker(minimumDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findByQuery("id", params);

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_findBy_non_query_finder_dao_array_params() {

		DaoInvoker invoker = new InterfaceDaoInvoker(minimumDao);

		Object[] params = new Object[0];

		invoker.findByQuery("id", params);

	}

}
