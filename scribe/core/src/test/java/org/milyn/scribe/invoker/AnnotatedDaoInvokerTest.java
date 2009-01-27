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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.milyn.scribe.dao.NoMethodWithAnnotationFound;
import org.milyn.scribe.dao.invoker.AnnotatedDaoInvoker;
import org.milyn.scribe.dao.invoker.DaoInvoker;
import org.milyn.scribe.dao.reflection.AnnotatedDaoRuntimeInfo;
import org.milyn.scribe.dao.reflection.AnnotatedDaoRuntimeInfoFactory;
import org.milyn.scribe.test.dao.AnnotatedDaoNoEntityReturned;
import org.milyn.scribe.test.dao.FullAnnotatedDao;
import org.milyn.scribe.test.dao.MinimumAnnotatedDao;
import org.milyn.scribe.test.util.BaseTestCase;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class AnnotatedDaoInvokerTest extends BaseTestCase {


	@Mock
	FullAnnotatedDao fullDao;

	@Mock
	AnnotatedDaoNoEntityReturned daoNoEntityReturned;

	@Mock
	MinimumAnnotatedDao minimumDao;

	AnnotatedDaoRuntimeInfoFactory runtimeInfoFactory = new AnnotatedDaoRuntimeInfoFactory();

	AnnotatedDaoRuntimeInfo fullDaoRuntimeInfo = runtimeInfoFactory.create(FullAnnotatedDao.class);

	AnnotatedDaoRuntimeInfo daoNoEntityReturnedRuntimeInfo = runtimeInfoFactory.create(AnnotatedDaoNoEntityReturned.class);

	AnnotatedDaoRuntimeInfo minimumDaoRuntimeInfo = runtimeInfoFactory.create(MinimumAnnotatedDao.class);

	@Test(groups = "unit")
	public void test_persist_with_entity_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toPersist = new Object();

		Object expectedResult = new Object();

		when(fullDao.insertIt(toPersist)).thenReturn(expectedResult);

		Object result = invoker.insert(toPersist);

		verify(fullDao).insertIt(same(toPersist));

		assertSame(expectedResult, result);
	}

	@Test(groups = "unit")
	public void test_persist_with_null_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toPersist = new Object();

		when(fullDao.insertIt(toPersist)).thenReturn(null);

		Object result = invoker.insert(toPersist);

		verify(fullDao).insertIt(same(toPersist));

		assertNull(result);
	}

	@Test(groups = "unit")
	public void test_persist_noEntityReturned() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(daoNoEntityReturned, daoNoEntityReturnedRuntimeInfo);

		Object toPersist = new Object();

		when(daoNoEntityReturned.persistIt(toPersist)).thenReturn(toPersist);

		Object result = invoker.insert(toPersist);

		verify(daoNoEntityReturned).persistIt(same(toPersist));

		assertNull(result);
	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_persist_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toPersist = new Object();

		invoker.insert(toPersist);


	}


	@Test(groups = "unit")
	public void test_merge_with_entity_return() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toMerge = new Object();

		Object expectedResult = new Object();

		when(fullDao.updateIt(toMerge)).thenReturn(expectedResult);

		Object result = invoker.update(toMerge);

		verify(fullDao).updateIt(same(toMerge));

		assertSame(expectedResult, result);

	}

	@Test(groups = "unit")
	public void test_merge_with_null_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toMerge = new Object();

		when(fullDao.updateIt(toMerge)).thenReturn(null);

		Object result = invoker.update(toMerge);

		verify(fullDao).updateIt(same(toMerge));

		assertNull(result);
	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_merge_no_annotation() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toMerge = new Object();

		invoker.update(toMerge);

	}


	@Test(groups = "unit")
	public void test_merge_noEntityReturned() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(daoNoEntityReturned, daoNoEntityReturnedRuntimeInfo);

		Object toMerge = new Object();

		when(daoNoEntityReturned.mergeIt(toMerge)).thenReturn(toMerge);

		Object result = invoker.update(toMerge);

		verify(daoNoEntityReturned).mergeIt(same(toMerge));

		assertNull(result);
	}


	@Test(groups = "unit")
	public void test_flush() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		invoker.flush();

		verify(fullDao).flushIt();

	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_flush_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		invoker.flush();

	}

	@Test(groups = "unit")
	public void test_lookup() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 1L);

		invoker.lookup("id", params);

		verify(fullDao).findById(1L);

	}


	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_lookup_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookup("id", params);

	}

	@Test(groups = "unit")
	public void test_lookupByQuery_map_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookupByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}

	@Test(groups = "unit")
	public void test_lookupByQuery_array_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object[] params = new Object[0];

		invoker.lookupByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}


	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_lookupByQuery_non_query_finder_dao_map_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookupByQuery("id", params);

	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_lookupByQuery_non_query_finder_dao_array_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object[] params = new Object[0];

		invoker.lookupByQuery("id", params);

	}


}
