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
package org.milyn.persistence;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.milyn.persistence.test.util.BaseTestCase;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.Dao;
import org.milyn.scribe.MappedDao;
import org.milyn.scribe.register.MapRegister;
import org.milyn.scribe.register.SingleDaoRegister;
import org.mockito.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class EntitySaverTest extends BaseTestCase {

	private static final boolean ENABLE_REPORTING = false;

	private static final String SIMPLE_XML =  "<root />";

	@Mock
	private Dao<String> dao;

	@Mock
	private MappedDao<String> mappedDao;

	@Test
	public void test_entity_insert_and_update() throws Exception {
		String toInsert1 = new String("toInsert1");
		String toInsert2 = new String("toInsert2");
		String toUpdate = new String("toUpdate");

		Smooks smooks = new Smooks(getResourceAsStream("entity-saver-01.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "report_test_entity_insert_and_update.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toInsert1", toInsert1);
		result.getResultMap().put("toInsert2", toInsert2);
		result.getResultMap().put("toUpdate", toUpdate);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(dao).update(same(toUpdate));
		verify(dao).insert(same(toInsert2));
		verify(dao).insert(same(toInsert1));
	}

	@Test
	public void test_entity_insert_and_update_with_named_dao() throws Exception {
		String toInsert1 = new String("toInsert1");
		String toInsert2 = new String("toInsert2");
		String toUpdate = new String("toUpdate");

		Smooks smooks = new Smooks(getResourceAsStream("entity-saver-02.xml"));
		Map<String, Object> daoMap = new HashMap<String, Object>();
		daoMap.put("dao1", dao);

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new MapRegister<Object>(daoMap));

		enableReporting(executionContext, "report_test_entity_insert_and_update_with_named_dao.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toInsert1", toInsert1);
		result.getResultMap().put("toInsert2", toInsert2);
		result.getResultMap().put("toUpdate", toUpdate);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(dao).update(same(toUpdate));
		verify(dao).insert(same(toInsert2));
		verify(dao).insert(same(toInsert1));
	}

	@Test
	public void test_entity_insert_and_update_to_other_beanId() throws Exception {
		String toInsert1 = new String("toInsert1");
		String toInsert2 = new String("toInsert2");
		String toUpdate = new String("toUpdate");

		String inserted1 = new String("inserted1");
		String inserted2 = new String("inserted2");
		String updated = new String("updated");

		Smooks smooks = new Smooks(getResourceAsStream("entity-saver-03.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext,  new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "report_test_entity_insert_and_update_to_other_beanId.html");

		when(dao.update(toUpdate)).thenReturn(updated);
		when(dao.insert(toInsert2)).thenReturn(inserted2);
		when(dao.insert(toInsert1)).thenReturn(inserted1);

		JavaResult result = new JavaResult();
		result.getResultMap().put("toInsert1", toInsert1);
		result.getResultMap().put("toInsert2", toInsert2);
		result.getResultMap().put("toUpdate", toUpdate);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		assertSame(inserted1, result.getBean("inserted1"));
		assertSame(inserted2, result.getBean("inserted2"));
		assertSame(updated, result.getBean("updated"));

	}

	@Test
	public void test_entity_insert_and_update_with_mapped_dao() throws Exception {
		String toInsert1 = new String("toInsert1");
		String toInsert2 = new String("toInsert2");
		String toUpdate = new String("toUpdate");

		Smooks smooks = new Smooks(getResourceAsStream("entity-saver-04.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext,  new SingleDaoRegister<Object>(mappedDao));

		enableReporting(executionContext, "report_test_entity_insert_and_update_with_mapped_dao.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toInsert1", toInsert1);
		result.getResultMap().put("toInsert2", toInsert2);
		result.getResultMap().put("toUpdate", toUpdate);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(mappedDao).update(eq("update"), same(toUpdate));
		verify(mappedDao).insert(eq("insert2"), same(toInsert2));
		verify(mappedDao).insert(eq("insert1"), same(toInsert1));
	}

	@Test
	public void test_entity_insert_with_saveBefore() throws Exception {
		String toInsert1 = new String("toInsert1");
		String toInsert2 = new String("toInsert2");

		Smooks smooks = new Smooks(getResourceAsStream("entity-saver-05.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "report_test_entity_insert_with_saveBefore.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toInsert1", toInsert1);
		result.getResultMap().put("toInsert2", toInsert2);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(dao).insert(same(toInsert1));
		verify(dao).insert(same(toInsert2));

	}

	/**
	 * @param resource
	 * @return
	 */
	private InputStream getResourceAsStream(String resource) {
		return EntitySaverTest.class.getResourceAsStream(resource);
	}

	private void enableReporting(ExecutionContext executionContext, String reportFilePath) throws IOException {
		if(ENABLE_REPORTING) {
			executionContext.setEventListener(new HtmlReportGenerator("target/" + reportFilePath));
		}
	}

}
