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

import org.milyn.persistence.NoMethodWithAnnotationFound;
import org.milyn.persistence.dao.reflection.AnnotatedDaoRuntimeInfo;
import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.dao.FullAnnotatedDao;
import org.milyn.persistence.test.dao.MinimumAnnotatedDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class AnnotatedDaoInvokerTest extends BaseTestCase {


	@Mock
	FullAnnotatedDao fullDao;

	@Mock
	MinimumAnnotatedDao minimumDao;

	AnnotatedDaoRuntimeInfo fullDaoRuntimeInfo = new AnnotatedDaoRuntimeInfo(FullAnnotatedDao.class);

	AnnotatedDaoRuntimeInfo minimumDaoRuntimeInfo = new AnnotatedDaoRuntimeInfo(MinimumAnnotatedDao.class);

	@Test(groups = TestGroup.UNIT)
	public void test_persist() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toPersist = new Object();

		invoker.persist(toPersist);

		verify(fullDao).persistIt(same(toPersist));
	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_persist_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toPersist = new Object();

		invoker.persist(toPersist);


	}

	@Test(groups = TestGroup.UNIT)
	public void test_merge() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toMerge = new Object();

		invoker.merge(toMerge);

		verify(fullDao).mergeIt(same(toMerge));

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_merge_no_annotation() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toMerge = new Object();

		invoker.merge(toMerge);

	}

	@Test(groups = TestGroup.UNIT)
	public void test_flush() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		invoker.flush();

		verify(fullDao).flushIt();

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_flush_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		invoker.flush();

	}

	@Test(groups = TestGroup.UNIT)
	public void test_findBy() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 1L);

		invoker.findBy("id", params);

		verify(fullDao).findById(1L);

	}


	@Test(groups = TestGroup.UNIT, expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_findBy_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findBy("id", params);

	}

	@Test(groups = TestGroup.UNIT)
	public void test_findBy_query_map_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}

	@Test(groups = TestGroup.UNIT)
	public void test_findBy_query_array_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object[] params = new Object[0];

		invoker.findByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}


	@Test(groups = TestGroup.UNIT, expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_findBy_non_query_finder_dao_map_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.findByQuery("id", params);

	}

	@Test(groups = TestGroup.UNIT, expectedExceptions = NoMethodWithAnnotationFound.class)
	public void test_findBy_non_query_finder_dao_array_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object[] params = new Object[0];

		invoker.findByQuery("id", params);

	}


}
