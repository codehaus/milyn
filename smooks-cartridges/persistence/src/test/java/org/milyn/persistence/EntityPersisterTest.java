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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.milyn.persistence.dao.Dao;
import org.milyn.persistence.dao.MappedDao;
import org.milyn.persistence.dao.register.MapRegister;
import org.milyn.persistence.dao.register.SingleDaoRegister;
import org.milyn.persistence.test.util.BaseTestCase;
import org.milyn.persistence.util.PersistenceUtil;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class EntityPersisterTest extends BaseTestCase {

	private static final boolean ENABLE_REPORTING = false;

	private static final String SIMPLE_XML =  "<root />";

	@Mock
	private Dao<String> dao;

	@Mock
	private MappedDao<String> mappedDao;

	@Test
	public void test_entity_persist_and_merge() throws Exception {
		String toPersist1 = new String("toPersist1");
		String toPersist2 = new String("toPersist2");
		String toMerge = new String("toMerge");

		Smooks smooks = new Smooks(getResourceAsStream("entity-persist-01.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "report_test_entity_persist_and_merge.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toPersist1", toPersist1);
		result.getResultMap().put("toPersist2", toPersist2);
		result.getResultMap().put("toMerge", toMerge);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(dao).merge(same(toMerge));
		verify(dao).persist(same(toPersist2));
		verify(dao).persist(same(toPersist1));
	}

	@Test
	public void test_entity_persist_and_merge_with_dao() throws Exception {
		String toPersist1 = new String("toPersist1");
		String toPersist2 = new String("toPersist2");
		String toMerge = new String("toMerge");

		Smooks smooks = new Smooks(getResourceAsStream("entity-persist-02.xml"));
		Map<String, Object> daoMap = new HashMap<String, Object>();
		daoMap.put("dao1", dao);

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, new MapRegister<Object>(daoMap));

		enableReporting(executionContext, "report_test_entity_persist_and_merge_with_dao.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toPersist1", toPersist1);
		result.getResultMap().put("toPersist2", toPersist2);
		result.getResultMap().put("toMerge", toMerge);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(dao).merge(same(toMerge));
		verify(dao).persist(same(toPersist2));
		verify(dao).persist(same(toPersist1));
	}

	@Test
	public void test_entity_persist_and_merge_to_other_beanId() throws Exception {
		String toPersist1 = new String("toPersist1");
		String toPersist2 = new String("toPersist2");
		String toMerge = new String("toMerge");

		String persisted1 = new String("persisted1");
		String persisted2 = new String("persisted2");
		String merged = new String("merged");

		Smooks smooks = new Smooks(getResourceAsStream("entity-persist-03.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext,  new SingleDaoRegister<Object>(dao));

		enableReporting(executionContext, "report_test_entity_persist_and_merge_to_other_beanId.html");

		stub(dao.merge(toMerge)).toReturn(merged);
		stub(dao.persist(toPersist2)).toReturn(persisted2);
		stub(dao.persist(toPersist1)).toReturn(persisted1);

		JavaResult result = new JavaResult();
		result.getResultMap().put("toPersist1", toPersist1);
		result.getResultMap().put("toPersist2", toPersist2);
		result.getResultMap().put("toMerge", toMerge);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		assertSame(persisted1, result.getBean("persisted1"));
		assertSame(persisted2, result.getBean("persisted2"));
		assertSame(merged, result.getBean("merged"));

	}

	@Test
	public void test_entity_persist_and_merge_with_mapped_dao() throws Exception {
		String toPersist1 = new String("toPersist1");
		String toPersist2 = new String("toPersist2");
		String toMerge = new String("toMerge");

		Smooks smooks = new Smooks(getResourceAsStream("entity-persist-04.xml"));

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext,  new SingleDaoRegister<Object>(mappedDao));

		enableReporting(executionContext, "report_test_entity_persist_and_merge_with_mapped_dao.html");

		JavaResult result = new JavaResult();
		result.getResultMap().put("toPersist1", toPersist1);
		result.getResultMap().put("toPersist2", toPersist2);
		result.getResultMap().put("toMerge", toMerge);

		smooks.filter(new StringSource(SIMPLE_XML), result, executionContext);

		verify(mappedDao).merge(eq("merge"), same(toMerge));
		verify(mappedDao).persist(eq("persist2"), same(toPersist2));
		verify(mappedDao).persist(eq("persist1"), same(toPersist1));
	}

	/**
	 * @param resource
	 * @return
	 */
	private InputStream getResourceAsStream(String resource) {
		return EntityPersisterTest.class.getResourceAsStream(resource);
	}

	private void enableReporting(ExecutionContext executionContext, String reportFilePath) throws IOException {
		if(ENABLE_REPORTING) {
			executionContext.setEventListener(new HtmlReportGenerator("target/" + reportFilePath));
		}
	}

}
