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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.payload.StringSource;
import org.milyn.persistence.dao.DaoRegister;
import org.milyn.persistence.dao.register.SingleDaoRegister;
import org.milyn.persistence.test.dao.FullAnnotatedDao;
import org.milyn.persistence.test.util.BaseTestCase;
import org.milyn.persistence.util.PersistenceUtil;
import org.mockito.MockitoAnnotations.Mock;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class EntityPersisterTest extends BaseTestCase {

	private static final boolean ENABLE_REPORTING = true;

	@Mock
	private FullAnnotatedDao fullDao;

	private final Source singleElemXml =  new StringSource("<root />");

	@Test
	public void test_entity_persist() throws Exception {
		Smooks smooks = new Smooks(getResourceAsStream("entity-persist-01.xml"));
		DaoRegister<Object> daoRegister = new SingleDaoRegister<Object>(fullDao);

		Object toPersist = new Object();

		ExecutionContext executionContext = smooks.createExecutionContext();

		PersistenceUtil.setDAORegister(executionContext, daoRegister);

		enableReporting(executionContext, "report_test_entity_persist.html");

		BeanRepository.getInstance(executionContext).addBean("toPersist", toPersist);

		smooks.filter(singleElemXml, null, executionContext);

		verify(fullDao).persistIt(same(toPersist));
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
