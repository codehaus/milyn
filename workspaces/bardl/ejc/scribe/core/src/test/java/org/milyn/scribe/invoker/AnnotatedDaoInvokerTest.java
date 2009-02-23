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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.milyn.scribe.IllegalAnnotationUsageException;
import org.milyn.scribe.NoMethodWithAnnotationFoundException;
import org.milyn.scribe.annotation.Dao;
import org.milyn.scribe.annotation.Insert;
import org.milyn.scribe.invoker.AnnotatedDaoInvoker;
import org.milyn.scribe.invoker.DaoInvoker;
import org.milyn.scribe.reflection.AnnotatedDaoRuntimeInfo;
import org.milyn.scribe.reflection.AnnotatedDaoRuntimeInfoFactory;
import org.milyn.scribe.test.dao.AnnotatedDaoNoEntityReturned;
import org.milyn.scribe.test.dao.FullAnnotatedDao;
import org.milyn.scribe.test.dao.MinimumAnnotatedDao;
import org.milyn.scribe.test.dao.OnlyDefaultAnnotatedDao;
import org.milyn.scribe.test.util.BaseTestCase;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@Test(groups = "unit")
public class AnnotatedDaoInvokerTest extends BaseTestCase {

	@Mock
	FullAnnotatedDao fullDao;

	@Mock
	OnlyDefaultAnnotatedDao onlyDefaultAnnotatedDao;

	@Mock
	AnnotatedDaoNoEntityReturned daoNoEntityReturned;

	@Mock
	MinimumAnnotatedDao minimumDao;

	AnnotatedDaoRuntimeInfoFactory runtimeInfoFactory = new AnnotatedDaoRuntimeInfoFactory();

	AnnotatedDaoRuntimeInfo fullDaoRuntimeInfo = runtimeInfoFactory.create(FullAnnotatedDao.class);

	AnnotatedDaoRuntimeInfo daoNoEntityReturnedRuntimeInfo = runtimeInfoFactory.create(AnnotatedDaoNoEntityReturned.class);

	AnnotatedDaoRuntimeInfo minimumDaoRuntimeInfo = runtimeInfoFactory.create(MinimumAnnotatedDao.class);

	public void test_insert_with_entity_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toPersist = new Object();

		Object expectedResult = new Object();

		when(fullDao.insertIt(toPersist)).thenReturn(expectedResult);

		Object result = invoker.insert(toPersist);

		verify(fullDao).insertIt(same(toPersist));

		assertSame(expectedResult, result);
	}

	public void test_insert_with_null_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toPersist = new Object();

		when(fullDao.insertIt(toPersist)).thenReturn(null);

		Object result = invoker.insert(toPersist);

		verify(fullDao).insertIt(same(toPersist));

		assertNull(result);
	}

	public void test_insert_with_named_method() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toPersist = new Object();

		invoker.insert("insertIt", toPersist);
		invoker.insert("insertIt2", toPersist);
		invoker.insert("insertIt3", toPersist);

		verify(fullDao).insertIt(same(toPersist));
		verify(fullDao).insertIt2(same(toPersist));
		verify(fullDao).insertItDiff(same(toPersist));
	}

	public void test_insert_noEntityReturned() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(daoNoEntityReturned, daoNoEntityReturnedRuntimeInfo);

		Object toPersist = new Object();

		when(daoNoEntityReturned.persistIt(toPersist)).thenReturn(toPersist);

		Object result = invoker.insert(toPersist);

		verify(daoNoEntityReturned).persistIt(same(toPersist));

		assertNull(result);
	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_insert_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toPersist = new Object();

		invoker.insert(toPersist);


	}

	public void test_update_with_entity_return() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toUpdate = new Object();

		Object expectedResult = new Object();

		when(fullDao.updateIt(toUpdate)).thenReturn(expectedResult);

		Object result = invoker.update(toUpdate);

		verify(fullDao).updateIt(same(toUpdate));

		assertSame(expectedResult, result);

	}

	public void test_update_with_null_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toUpdate = new Object();

		when(fullDao.updateIt(toUpdate)).thenReturn(null);

		Object result = invoker.update(toUpdate);

		verify(fullDao).updateIt(same(toUpdate));

		assertNull(result);
	}

	public void test_update_with_named_method() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toUpdate = new Object();

		invoker.update("updateIt", toUpdate);
		invoker.update("updateIt2", toUpdate);
		invoker.update("updateIt3", toUpdate);

		verify(fullDao).updateIt(same(toUpdate));
		verify(fullDao).updateIt2(same(toUpdate));
		verify(fullDao).updateItDiff(same(toUpdate));
	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_update_no_annotation() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toMerge = new Object();

		invoker.update(toMerge);

	}

	public void test_update_noEntityReturned() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(daoNoEntityReturned, daoNoEntityReturnedRuntimeInfo);

		Object toDelete = new Object();

		when(daoNoEntityReturned.deleteIt(toDelete)).thenReturn(toDelete);

		Object result = invoker.delete(toDelete);

		verify(daoNoEntityReturned).deleteIt(same(toDelete));

		assertNull(result);
	}

	public void test_delete_with_entity_return() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toDelete = new Object();

		Object expectedResult = new Object();

		when(fullDao.deleteIt(toDelete)).thenReturn(expectedResult);

		Object result = invoker.delete(toDelete);

		verify(fullDao).deleteIt(same(toDelete));

		assertSame(expectedResult, result);

	}

	public void test_delete_with_null_return() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toDelete = new Object();

		when(fullDao.deleteIt(toDelete)).thenReturn(null);

		Object result = invoker.delete(toDelete);

		verify(fullDao).deleteIt(same(toDelete));

		assertNull(result);
	}

	public void test_delete_with_named_method() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object toDelete = new Object();

		invoker.delete("deleteIt", toDelete);
		invoker.delete("deleteIt2", toDelete);
		invoker.delete("deleteIt3", toDelete);

		verify(fullDao).deleteIt(same(toDelete));
		verify(fullDao).deleteIt2(same(toDelete));
		verify(fullDao).deleteItDiff(same(toDelete));
	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_delete_no_annotation() {


		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object toDelete = new Object();

		invoker.delete(toDelete);

	}

	public void test_delete_noEntityReturned() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(daoNoEntityReturned, daoNoEntityReturnedRuntimeInfo);

		Object toDelete = new Object();

		when(daoNoEntityReturned.deleteIt(toDelete)).thenReturn(toDelete);

		Object result = invoker.delete(toDelete);

		verify(daoNoEntityReturned).deleteIt(same(toDelete));

		assertNull(result);
	}

	public void test_flush() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		invoker.flush();

		verify(fullDao).flushIt();

	}

	@Test(groups = "unit", expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_flush_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		invoker.flush();

	}

	public void test_lookup() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 1L);

		invoker.lookup("id", params);

		verify(fullDao).findById(1L);

	}

	public void test_lookup_with_method_name() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		invoker.lookup("findBy", "param");

		verify(fullDao).findBy(eq("param"));
	}

	public void test_lookup_with_name_param() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("first", "henk");
		param.put("last", "janssen");

		invoker.lookup("name", param);

		verify(fullDao).findByName(eq("janssen"), eq("henk"));
	}

	public void test_lookup_with_positional_param() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		invoker.lookup("positional", "param1", 2, true);

		verify(fullDao).findBySomething(eq("param1"), eq(2), eq(true));
	}


	@Test(expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_lookup_no_annotation() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		invoker.lookup("id", Collections.emptyMap());

	}

	public void test_lookupByQuery_map_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookupByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}

	public void test_lookupByQuery_array_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(fullDao, fullDaoRuntimeInfo);

		Object[] params = new Object[0];

		invoker.lookupByQuery("query", params);

		verify(fullDao).findByQuery(eq("query"), same(params));

	}

	@Test(expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_lookupByQuery_non_query_finder_dao_map_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Map<String, Object> params = new HashMap<String, Object>();

		invoker.lookupByQuery("id", params);

	}

	@Test(expectedExceptions = NoMethodWithAnnotationFoundException.class)
	public void test_lookupByQuery_non_query_finder_dao_array_params() {

		DaoInvoker invoker = new AnnotatedDaoInvoker(minimumDao, minimumDaoRuntimeInfo);

		Object[] params = new Object[0];

		invoker.lookupByQuery("id", params);

	}


}
