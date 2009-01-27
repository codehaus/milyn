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
package org.milyn.scribe.reflection;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.milyn.scribe.reflection.AnnotatedDaoRuntimeInfo;
import org.milyn.scribe.reflection.EntityMethod;
import org.milyn.scribe.reflection.FlushMethod;
import org.milyn.scribe.reflection.LookupMethod;
import org.milyn.scribe.reflection.LookupWithNamedQueryMethod;
import org.milyn.scribe.reflection.LookupWithPositionalQueryMethod;
import org.milyn.scribe.test.dao.FullAnnotatedDao;
import org.milyn.scribe.test.dao.MinimumAnnotatedDao;
import org.milyn.scribe.test.util.BaseTestCase;
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

	@Test( groups = "unit" )
	public void test_getDaoClass() {

		Class<?> daoClass = fullAnnotatedDaoRuntimeInfo.getDaoClass();

		assertNotNull(daoClass);
		assertSame(FullAnnotatedDao.class, daoClass);

	}

	@Test( groups = "unit" )
	public void test_getInsertMethod() {

		EntityMethod method = fullAnnotatedDaoRuntimeInfo.getInsertMethod();

		assertNotNull(method);

		Object toPersist = new Object();

		method.invoke(fullAnnotatedDao, toPersist);

		verify(fullAnnotatedDao).insertIt(same(toPersist));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getInsertMethod());

	}

	@Test( groups = "unit" )
	public void test_getUpdateMethod() {

		EntityMethod method = fullAnnotatedDaoRuntimeInfo.getUpdateMethod();

		assertNotNull(method);

		Object toMerge = new Object();

		method.invoke(fullAnnotatedDao, toMerge);

		verify(fullAnnotatedDao).updateIt(same(toMerge));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getUpdateMethod());

	}

	@Test( groups = "unit" )
	public void test_getDeleteMethod() {

		EntityMethod method = fullAnnotatedDaoRuntimeInfo.getDeleteMethod();

		assertNotNull(method);

		Object toDelete = new Object();

		method.invoke(fullAnnotatedDao, toDelete);

		verify(fullAnnotatedDao).deleteIt(same(toDelete));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getDeleteMethod());

	}

	@Test( groups = "unit" )
	public void test_getFlushMethod() {

		FlushMethod method = fullAnnotatedDaoRuntimeInfo.getFlushMethod();

		assertNotNull(method);

		method.invoke(fullAnnotatedDao);

		verify(fullAnnotatedDao).flushIt();

		assertNull(minimumAnnotatedDaoRuntimeInfo.getFlushMethod());

	}

	@Test( groups = "unit" )
	public void test_getFindByNamedQueryMethod() {

		LookupWithNamedQueryMethod method = fullAnnotatedDaoRuntimeInfo.getLookupByNamedQueryMethod();

		assertNotNull(method);

		Map<String, Object> params = new HashMap<String, Object>();

		method.invoke(fullAnnotatedDao, "query", params);

		verify(fullAnnotatedDao).findByQuery(eq("query"), same(params));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getLookupByNamedQueryMethod());

	}

	@Test( groups = "unit" )
	public void test_getFindByPositionalQueryMethod() {

		LookupWithPositionalQueryMethod method = fullAnnotatedDaoRuntimeInfo.getLookupByPositionalQueryMethod();

		assertNotNull(method);

		Object[] params = new Object[0];

		method.invoke(fullAnnotatedDao, "query", params);

		verify(fullAnnotatedDao).findByQuery(eq("query"), same(params));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getLookupByPositionalQueryMethod());

	}

	@Test( groups = "unit" )
	public void test_getFindByMethod() {

		LookupMethod method = fullAnnotatedDaoRuntimeInfo.getLookupWithNamedParametersMethod("id");

		assertNotNull(method);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 1L);

		method.invoke(fullAnnotatedDao, params);

		verify(fullAnnotatedDao).findById(eq(1L));

		assertNull(minimumAnnotatedDaoRuntimeInfo.getLookupWithNamedParametersMethod("id"));
	}
}
