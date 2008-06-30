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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdList;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.payload.StringSource;
import org.milyn.util.HsqlServer;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SQLExecutorTest extends TestCase {

    private HsqlServer hsqlServer;

    @Override
	protected void setUp() throws Exception {
        hsqlServer = new HsqlServer(9999);
        hsqlServer.execScript(getClass().getResourceAsStream("test.script"));
    }

    @Override
	protected void tearDown() throws Exception {
        hsqlServer.stop();
    }

    @SuppressWarnings("unchecked")
	public void test_appContextTimeout() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);

        smooks.filter(new StringSource("<doc/>"), null, execContext);
        List orders11 = (List) beanRepository.getBean("orders1");
        List orders12 = (List) beanRepository.getBean("orders2");

        smooks.filter(new StringSource("<doc/>"), null, execContext);
        List orders21 = (List) beanRepository.getBean("orders1");
        List orders22 = (List) beanRepository.getBean("orders2");

        assertTrue(orders11 != orders21);
        assertTrue(orders12 == orders22); // order12 should come from the app context cache

        // timeout the cached resultset...
        Thread.sleep(2050);

        smooks.filter(new StringSource("<doc/>"), null, execContext);
        List orders31 = (List) beanRepository.getBean("orders1");
        List orders32 = (List) beanRepository.getBean("orders2");

        assertTrue(orders11 != orders31);
        assertTrue(orders12 != orders32); // order12 shouldn't come from the app context cache - timed out ala TTL

        smooks.filter(new StringSource("<doc/>"), null, execContext);
        List orders41 = (List) beanRepository.getBean("orders1");
        List orders42 = (List) beanRepository.getBean("orders2");

        assertTrue(orders31 != orders41);
        assertTrue(orders32 == orders42); // order42 should come from the app context cache
    }

    @SuppressWarnings("unchecked")
	public void test_ResultsetRowSelector_01() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);

        smooks.filter(new StringSource("<doc/>"), null, execContext);
        Map<String, Object> myOrder = (Map<String, Object>) beanRepository.getBean("myOrder");

        assertEquals("{ORDERNUMBER=2, CUSTOMERNUMBER=2, PRODUCTCODE=456}", myOrder.toString());
    }


    @SuppressWarnings("unchecked")
	public void test_ResultsetRowSelector_02() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-failed-select-01.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);

        smooks.filter(new StringSource("<doc/>"), null, execContext);
        Map<String, Object> myOrder = (Map<String, Object>) beanRepository.getBean("myOrder");

        assertEquals(null, myOrder);
    }

    public void test_ResultsetRowSelector_03() throws IOException, SAXException, InterruptedException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-failed-select-02.xml"));
        ExecutionContext execContext = smooks.createExecutionContext();
        BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(execContext);
        BeanIdList beanIdList =  BeanRepositoryManager.getInstance(execContext.getContext()).getBeanIdList();

        BeanId requiredOrderNumId = beanIdList.register("requiredOrderNum");

        beanRepository.addBean(requiredOrderNumId, 9999);
        try {
            smooks.filter(new StringSource("<doc/>"), null, execContext);
            fail("Expected DataSelectionException");
        } catch(SmooksException e) {
            assertEquals("Order with ORDERNUMBER=9999 not found in Database", e.getCause().getMessage());
        }
    }
}
