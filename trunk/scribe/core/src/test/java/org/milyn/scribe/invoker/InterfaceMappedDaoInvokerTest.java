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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.milyn.scribe.MappedDao;
import org.milyn.scribe.invoker.InterfaceMappedDaoInvoker;
import org.milyn.scribe.invoker.MappedDaoInvoker;
import org.milyn.scribe.test.TestGroup;
import org.milyn.scribe.test.dao.FullInterfaceMappedDao;
import org.milyn.scribe.test.util.BaseTestCase;
import org.mockito.Mock;
import org.testng.annotations.Test;
/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class InterfaceMappedDaoInvokerTest extends BaseTestCase {

	@Mock
	private FullInterfaceMappedDao<Object> fullDao;

	@Mock
	private MappedDao<Object> minimumDao;

	@Test(groups = TestGroup.UNIT)
	public void test_persist() {

		MappedDaoInvoker invoker = new InterfaceMappedDaoInvoker(fullDao);

		Object toPersist = new Object();

		invoker.persist("id", toPersist);

		verify(fullDao).persist(eq("id"), same(toPersist));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_merge() {


		MappedDaoInvoker invoker = new InterfaceMappedDaoInvoker(fullDao);

		Object toMerge = new Object();

		invoker.merge("id", toMerge);

		verify(fullDao).merge(eq("id"), same(toMerge));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_flush() {

		MappedDaoInvoker invoker = new InterfaceMappedDaoInvoker(fullDao);

		invoker.flush("id");

		verify(fullDao).flush(eq("id"));

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_flush_non_flushable_dao() {

		MappedDaoInvoker invoker = new InterfaceMappedDaoInvoker(minimumDao);

		invoker.flush("id");

	}

	@Test(groups = TestGroup.UNIT)
	public void test_lookup() {

		MappedDaoInvoker invoker = new InterfaceMappedDaoInvoker(fullDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookup("id", params);

		verify(fullDao).lookup(eq("id"), same(params));

	}


	@Test(groups = TestGroup.UNIT, expectedExceptions = NotImplementedException.class)
	public void test_lookup_non_finder_dao() {

		MappedDaoInvoker invoker = new InterfaceMappedDaoInvoker(minimumDao);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookup("id", params);

	}


}
