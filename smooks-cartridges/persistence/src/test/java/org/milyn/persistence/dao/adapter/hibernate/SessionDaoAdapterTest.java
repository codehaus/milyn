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
package org.milyn.persistence.dao.adapter.hibernate;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.milyn.persistence.test.TestGroup;
import org.milyn.persistence.test.util.BaseTestCase;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class SessionDaoAdapterTest extends BaseTestCase {

	@Mock
	private Session session;

	@Mock
	private Query query;

	private SessionDaoAdapter adapter;

	@Test( groups = TestGroup.UNIT )
	public void test_persist() {

		// EXECUTE

		Object toPersist = new Object();

		// VERIFY

		adapter.persist(toPersist);

		verify(session).persist(same(toPersist));

	}

	@Test( groups = TestGroup.UNIT )
	public void test_merge() {

		// EXECUTE

		Object toMerge = new Object();

		Object merged = adapter.merge(toMerge);

		// VERIFY

		verify(session).merge(same(toMerge));

		assertSame(toMerge, merged);

	}

	@Test( groups = TestGroup.UNIT )
	public void test_flush() {

		// EXECUTE

		adapter.flush();

		// VERIFY

		verify(session).flush();

	}

	@Test( groups = TestGroup.UNIT )
	public void test_lookupByQuery_map_parameters() {

		// STUB

		List<?> listResult = Collections.emptyList();

		stub(session.createQuery(anyString())).toReturn(query);
		stub(query.list()).toReturn(listResult);

		// EXECUTE

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("key1", "value1");
		params.put("key2", "value2");

		Collection<Object> result = adapter.lookupByQuery("query", params);

		// VERIFY

		assertSame(listResult, result);

		verify(session).createQuery(eq("query"));

		verify(query).setParameter(eq("key1"), eq("value1"));
		verify(query).setParameter(eq("key2"), eq("value2"));
		verify(query).list();

	}

	@Test( groups = TestGroup.UNIT )
	public void test_lookupByQuery_array_parameters() {

		// STUB

		List<?> listResult = Collections.emptyList();

		stub(session.createQuery(anyString())).toReturn(query);
		stub(query.list()).toReturn(listResult);

		// EXECUTE

		Object[] params = new Object[2];
		params[0] = "value1";
		params[1] = "value2";

		Collection<Object> result = adapter.lookupByQuery("query", params);

		// VERIFY

		assertSame(listResult, result);

		verify(session).createQuery(eq("query"));

		verify(query).setParameter(eq(1), eq("value1"));
		verify(query).setParameter(eq(2), eq("value2"));
		verify(query).list();

	}

	@Test( groups = TestGroup.UNIT )
	public void test_lookup_map_parameters() {

		// STUB

		List<?> listResult = Collections.emptyList();

		stub(session.getNamedQuery(anyString())).toReturn(query);
		stub(query.list()).toReturn(listResult);

		// EXECUTE

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("key1", "value1");
		params.put("key2", "value2");

		Collection<Object> result = adapter.lookup("name", params);

		// VERIFY

		assertSame(listResult, result);

		verify(session).getNamedQuery(eq("name"));

		verify(query).setParameter(eq("key1"), eq("value1"));
		verify(query).setParameter(eq("key2"), eq("value2"));
		verify(query).list();

	}

	@Test( groups = TestGroup.UNIT )
	public void test_lookup_array_parameters() {

		// STUB

		List<?> listResult = Collections.emptyList();

		stub(session.getNamedQuery(anyString())).toReturn(query);
		stub(query.list()).toReturn(listResult);

		// EXECUTE

		Object[] params = new Object[2];
		params[0] = "value1";
		params[1] = "value2";

		Collection<Object> result = adapter.lookup("name", params);

		// VERIFY

		assertSame(listResult, result);

		verify(session).getNamedQuery(eq("name"));

		verify(query).setParameter(eq(1), eq("value1"));
		verify(query).setParameter(eq(2), eq("value2"));
		verify(query).list();

	}


	/* (non-Javadoc)
	 * @see org.milyn.persistence.test.util.BaseTestCase#beforeMethod()
	 */
	@BeforeMethod(alwaysRun = true)
	@Override
	public void beforeMethod() {
		super.beforeMethod();

		adapter = new SessionDaoAdapter(session);
	}


}
