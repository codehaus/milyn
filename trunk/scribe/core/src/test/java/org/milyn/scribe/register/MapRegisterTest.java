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
package org.milyn.scribe.register;

import static junit.framework.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.milyn.scribe.register.MapRegister;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@Test(groups = "unit")
public class MapRegisterTest {


	public void test_construt_empty() {
		MapRegister<Object> mapRegister = new MapRegister<Object>();

		assertEquals(0, mapRegister.size());
	}


	public void test_construt_map_and_getDAO() {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("1", new Object());
		hashMap.put("2", new Object());

		MapRegister<Object> mapRegister = new MapRegister<Object>(hashMap);

		assertEquals(2, mapRegister.size());
		assertSame(hashMap.get("1"), mapRegister.getDao("1"));
		assertSame(hashMap.get("2"), mapRegister.getDao("2"));

		Map<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
		linkedHashMap.put("1", new Object());
		linkedHashMap.put("2", new Object());

		mapRegister = new MapRegister<Object>(linkedHashMap);

		assertEquals(2, mapRegister.size());
		assertSame(linkedHashMap.get("1"), mapRegister.getDao("1"));
		assertSame(linkedHashMap.get("2"), mapRegister.getDao("2"));
	}


	public void test_put_and_getDAO() {
		Object bean = new Object();

		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.put("1", bean);

		assertEquals(1, mapRegister.size());
		assertSame(bean, mapRegister.getDao("1"));
	}


	public void test_containsKey() {
		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.put("1", new Object());

		assertTrue(mapRegister.containsKey("1"));
		assertFalse(mapRegister.containsKey("2"));
	}


	public void test_containsDAO() {
		Object bean1 = new Object();
		Object bean2 = new Object();

		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.put("1", bean1);

		assertTrue(mapRegister.containsDAO(bean1));
		assertFalse(mapRegister.containsDAO(bean2));
	}


	public void test_getAll() {
		Object bean1 = new Object();
		Object bean2 = new Object();

		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.put("1", bean1);
		mapRegister.put("2", bean2);

		Map<String, Object> all = mapRegister.getAll();

		assertEquals(2, all.size());
		assertSame(bean1, all.get("1"));
		assertSame(bean2, all.get("2"));
	}


	public void test_equals() {
		Object bean1 = new Object();
		Object bean2 = new Object();

		MapRegister<Object> mapRegister1 = new MapRegister<Object>();
		mapRegister1.put("1", bean1);
		mapRegister1.put("2", bean2);

		MapRegister<Object> mapRegister2 = new MapRegister<Object>();
		mapRegister2.put("1", bean1);
		mapRegister2.put("2", bean2);

		MapRegister<Object> mapRegister3 = new MapRegister<Object>();

		assertTrue(mapRegister1.equals(mapRegister2));
		assertFalse(mapRegister1.equals(mapRegister3));
	}


	public void test_clear_and_isEmpty() {

		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.put("1", new Object());

		assertEquals(1, mapRegister.size());
		assertFalse(mapRegister.isEmpty());

		mapRegister.clear();

		assertEquals(0, mapRegister.size());
		assertTrue(mapRegister.isEmpty());
	}


	public void test_remove() {
		Object bean1 = new Object();
		Object bean2 = new Object();

		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.put("1", bean1);
		mapRegister.put("2", bean2);

		assertEquals(2, mapRegister.size());

		assertEquals(bean1, mapRegister.remove("1"));

		assertEquals(1, mapRegister.size());

		assertNull(mapRegister.remove("3"));
	}


	public void test_putAll() {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("1", new Object());
		hashMap.put("2", new Object());

		MapRegister<Object> mapRegister = new MapRegister<Object>();
		mapRegister.putAll(hashMap);

		assertEquals(2, mapRegister.size());
		assertSame(hashMap.get("1"), mapRegister.getDao("1"));
		assertSame(hashMap.get("2"), mapRegister.getDao("2"));

	}

}
