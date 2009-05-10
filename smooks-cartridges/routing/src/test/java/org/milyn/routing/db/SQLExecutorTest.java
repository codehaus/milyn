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
package org.milyn.routing.db;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.db.DirectDataSource;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdRegister;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.payload.StringSource;
import org.milyn.util.HsqlServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import org.hsqldb.jdbcDriver;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Test ( groups = "unit" )
public class SQLExecutorTest
{
    private HsqlServer hsqlServer;

    @BeforeClass
	public void setUp() throws Exception {
        hsqlServer = new HsqlServer(9992);
        hsqlServer.execScript(getClass().getResourceAsStream("test.script"));
    }


    @AfterClass
	public void tearDown() throws Exception {
        hsqlServer.stop();
    }

    @Test
    public void test_appContextTime() throws Exception {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config.xml"));
        try {
            test_appContextTime(smooks);
        } finally {
            smooks.close();
        }
    }

    @Test
    public void test_appContextTimeExtendedConfig() throws Exception {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-extended-config.xml"));
    	test_appContextTime(smooks);
        try {
            test_appContextTime(smooks);
        } finally {
            smooks.close();
        }
    }

    @Test
    public void test_appContextTimeProgrammatic() throws Exception {
        Smooks smooks = new Smooks();

        try {
            // Now programmaticly configure...
            DirectDataSource datasource = new DirectDataSource()
                              .setDriver(jdbcDriver.class)
                              .setName("OrdersDS")
                              .setUrl("jdbc:hsqldb:hsql://localhost:9992/milyn-hsql-9992")
                              .setUsername("sa")
                              .setPassword("")
                              .setAutoCommit(true);
            SQLExecutor orderSelector = new SQLExecutor()
                              .setDatasource(datasource)
                              .setStatement("select * from ORDERS")
                              .setResultSetName("orders1")
                              .setExecuteBefore(true);

            smooks.addVisitor(datasource);
            smooks.addVisitor(orderSelector);

            smooks.addVisitor(new ResultsetRowSelector()
                              .setSelector(orderSelector)
                              .setBeanId("myOrder")
                              .setWhereClause("row.ORDERNUMBER == 2")
                              .setFailedSelectError("Order with ORDERNUMBER=2 not found in Database"));

            smooks.addVisitor(new SQLExecutor()
                              .setDatasource(datasource)
                              .setStatement("select * from ORDERS")
                              .setResultSetName("orders2")
                              .setResultSetScope(ResultSetScope.APPLICATION)
                              .setResultSetTTL(2000L)
                              .setExecuteBefore(true));

            test_appContextTime(smooks);
        } finally {
            smooks.close();
        }
    }

    @SuppressWarnings("unchecked")
	private void test_appContextTime(Smooks smooks) throws IOException, SAXException, InterruptedException {
        ExecutionContext execContext = smooks.createExecutionContext();
        BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);

        smooks.filter(execContext, new StringSource("<doc/>"), null);
        List orders11 = (List) beanRepository.getBean("orders1");
        List orders12 = (List) beanRepository.getBean("orders2");

        smooks.filter(execContext, new StringSource("<doc/>"), null);
        List orders21 = (List) beanRepository.getBean("orders1");
        List orders22 = (List) beanRepository.getBean("orders2");

        assertTrue(orders11 != orders21);
        assertTrue(orders12 == orders22); // order12 should come from the app context cache

        // timeout the cached resultset...
        Thread.sleep(2050);

        smooks.filter(execContext, new StringSource("<doc/>"), null);
        List orders31 = (List) beanRepository.getBean("orders1");
        List orders32 = (List) beanRepository.getBean("orders2");

        assertTrue(orders11 != orders31);
        assertTrue(orders12 != orders32); // order12 shouldn't come from the app context cache - timed out ala TTL

        smooks.filter(execContext, new StringSource("<doc/>"), null);
        List orders41 = (List) beanRepository.getBean("orders1");
        List orders42 = (List) beanRepository.getBean("orders2");

        assertTrue(orders31 != orders41);
        assertTrue(orders32 == orders42); // order42 should come from the app context cache
    }

    @Test
    @SuppressWarnings("unchecked")
	public void test_ResultsetRowSelector_01() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config.xml"));

        try {
            ExecutionContext execContext = smooks.createExecutionContext();
            BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);

            smooks.filter(execContext, new StringSource("<doc/>"), null);
            Map<String, Object> myOrder = (Map<String, Object>) beanRepository.getBean("myOrder");

            assertEquals("{ORDERNUMBER=2, CUSTOMERNUMBER=2, PRODUCTCODE=456}", myOrder.toString());
        } finally {
            smooks.close();
        }
    }


    @Test
    @SuppressWarnings("unchecked")
	public void test_ResultsetRowSelector_02() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-failed-select-01.xml"));

        try {
            ExecutionContext execContext = smooks.createExecutionContext();
            BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);

            smooks.filter(execContext, new StringSource("<doc/>"), null);
            Map<String, Object> myOrder = (Map<String, Object>) beanRepository.getBean("myOrder");

            assertEquals(null, myOrder);
        } finally {
            smooks.close();
        }
    }

    @Test
    public void test_ResultsetRowSelector_03() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-failed-select-02.xml"));

        try {
            ExecutionContext execContext = smooks.createExecutionContext();
            BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);
            BeanIdRegister beanIdRegister =  BeanRepositoryManager.getInstance(execContext.getContext()).getBeanIdRegister();

            BeanId requiredOrderNumId = beanIdRegister.register("requiredOrderNum");

            beanRepository.addBean(requiredOrderNumId, 9999);
            try {
                smooks.filter(execContext, new StringSource("<doc/>"), null);
                fail("Expected DataSelectionException");
            } catch(SmooksException e) {
                assertEquals("Order with ORDERNUMBER=9999 not found in Database", e.getCause().getMessage());
            }
        } finally {
            smooks.close();
        }
    }
}
