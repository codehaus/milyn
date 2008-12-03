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
package org.milyn.persistence.dao.reflection;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.dao.FullAnnotatedDao;
import org.milyn.persistence.test.dao.MinimumAnnotatedDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * TODO: Write more extensive tests to verify different method declarations
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class AnnotatedDaoRuntimeInfoTest extends BaseTestCase{

	private final AnnotatedDaoRuntimeInfo fullAnnotatedDaoRuntimeInfo = new AnnotatedDaoRuntimeInfo(FullAnnotatedDao.class);

	private final AnnotatedDaoRuntimeInfo minimumAnnotatedDaoRuntimeInfo = new AnnotatedDaoRuntimeInfo(MinimumAnnotatedDao.class);


	@Mock
	private FullAnnotatedDao fullAnnotatedDao;

	@Test( groups = TestGroup.UNIT )
	public void test_getDaoClass() {

		Class<?> daoClass = fullAnnotatedDaoRuntimeInfo.getDaoClass();

		assertNotNull(daoClass);
		assertSame(FullAnnotatedDao.class, daoClass);

	}

	@Test( groups = TestGroup.UNIT )
	public void test_getPersistMethod() {

		PersistMethod method = fullAnnotatedDaoRuntimeInfo.getPersistMethod();

		assertNotNull(method);

		Object toPersist = new Object();

		method.invoke(fullAnnotatedDao, toPersist);

		verify(fullAnnotatedDao).persistIt(same(toPersist));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getPersistMethod());

	}

	@Test( groups = TestGroup.UNIT )
	public void test_getMergeMethod() {

		MergeMethod method = fullAnnotatedDaoRuntimeInfo.getMergeMethod();

		assertNotNull(method);

		Object toMerge = new Object();

		method.invoke(fullAnnotatedDao, toMerge);

		verify(fullAnnotatedDao).mergeIt(same(toMerge));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getMergeMethod());

	}

	@Test( groups = TestGroup.UNIT )
	public void test_getFlushMethod() {

		FlushMethod method = fullAnnotatedDaoRuntimeInfo.getFlushMethod();

		assertNotNull(method);

		method.invoke(fullAnnotatedDao);

		verify(fullAnnotatedDao).flushIt();

		assertNull(minimumAnnotatedDaoRuntimeInfo.getFlushMethod());

	}

	@Test( groups = TestGroup.UNIT )
	public void test_getFindByNamedQueryMethod() {

		LookupByNamedQueryMethod method = fullAnnotatedDaoRuntimeInfo.getFindByNamedQueryMethod();

		assertNotNull(method);

		Map<String, Object> params = new HashMap<String, Object>();

		method.invoke(fullAnnotatedDao, "query", params);

		verify(fullAnnotatedDao).findByQuery(eq("query"), same(params));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getFindByNamedQueryMethod());

	}

	@Test( groups = TestGroup.UNIT )
	public void test_getFindByPositionalQueryMethod() {

		LookupByPositionalQueryMethod method = fullAnnotatedDaoRuntimeInfo.getFindByPositionalQueryMethod();

		assertNotNull(method);

		Object[] params = new Object[0];

		method.invoke(fullAnnotatedDao, "query", params);

		verify(fullAnnotatedDao).findByQuery(eq("query"), same(params));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getFindByPositionalQueryMethod());

	}

	@Test( groups = TestGroup.UNIT )
	public void test_getFindByMethod() {

		LookupMethod method = fullAnnotatedDaoRuntimeInfo.getFindByMethod("id");

		assertNotNull(method);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 1L);

		method.invoke(fullAnnotatedDao, params);

		verify(fullAnnotatedDao).findById(eq(1L));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getFindByMethod("id"));
	}
}
