/*
 * Milyn - Copyright (C) 2006 - 2010
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.smooks.camel.routing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.milyn.SmooksException;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.javabean.context.BeanContext;

/**
 * Unit test for {@link BeanRouter}.
 * 
 * @author Daniel Bevenius
 *
 */
public class BeanRouterTest extends CamelTestSupport
{
	private static final String END_POINT_URI = "mock://beanRouterUnitTest";
	private static final String BEAN_ID = "testBeanId";
	
	private StandaloneExecutionContext smooksExecutionContext;
	private MockEndpoint endpoint;
	private MyBean myBean = new MyBean("bajja");
	private BeanContext beanContext;
	
	@Before
	public void setupSmooksExeceutionContext() throws Exception
	{
		endpoint = createAndConfigureMockEndpoint(END_POINT_URI);
		Exchange exchange = createExchangeAndSetFromEndpoint(endpoint);
		BeanContext beanContext = createBeanContextAndSetBeanInContext(BEAN_ID, myBean);
		
		smooksExecutionContext = createStandaloneExecutionContext();
		setExchangeAsAttributeInExecutionContext(exchange);
		makeExecutionContextReturnBeanContext(beanContext);
	}
	
	private MockEndpoint createAndConfigureMockEndpoint(String endpointUri) throws Exception
	{
		MockEndpoint mockEndpoint = new MockEndpoint(endpointUri);
		mockEndpoint.setCamelContext(context);
		context.addEndpoint(endpointUri, mockEndpoint);
		return mockEndpoint;
	}

	private Exchange createExchangeAndSetFromEndpoint(MockEndpoint endpoint)
	{
		Exchange exchange = endpoint.createExchange();
		exchange.setFromEndpoint(endpoint);
		return exchange;
	}

	private BeanContext createBeanContextAndSetBeanInContext(String beanId, Object bean)
	{
		beanContext = mock(BeanContext.class);
		when(beanContext.getBean(beanId)).thenReturn(bean);
		return beanContext;
	}

	private StandaloneExecutionContext createStandaloneExecutionContext()
	{
		return mock(StandaloneExecutionContext.class);
	}

	private void setExchangeAsAttributeInExecutionContext(Exchange exchange)
	{
		when(smooksExecutionContext.getAttribute(Exchange.class)).thenReturn(exchange);
	}
	
	private void makeExecutionContextReturnBeanContext(BeanContext beanContext)
	{
		when(smooksExecutionContext.getBeanContext()).thenReturn(beanContext);
	}
	
	@Test
	public void visitAfter() throws Exception
	{
		endpoint.setExpectedMessageCount(1);
		BeanRouter beanRouter = createBeanRouter(BEAN_ID, END_POINT_URI);
		beanRouter.visitAfter(null, smooksExecutionContext);
		endpoint.assertIsSatisfied();
		endpoint.expectedBodiesReceived(myBean);
	}
	
	@Test (expected = SmooksException.class)
	public void visitAfterWithMissingBeanInSmookBeanContext() throws SmooksException, IOException
	{
		when(beanContext.getBean(BEAN_ID)).thenReturn(null);
		BeanRouter beanRouter = createBeanRouter(BEAN_ID, END_POINT_URI);
		beanRouter.visitAfter(null, smooksExecutionContext);
	}
	
	@Test (expected = SmooksException.class)
	public void visitAfterWithMissingCamelExchangeInSmooksExecutionContext() throws Exception
	{
		when(smooksExecutionContext.getAttribute(Exchange.class)).thenReturn(null);
		BeanRouter beanRouter = createBeanRouter(BEAN_ID, END_POINT_URI);
		beanRouter.visitAfter(null, smooksExecutionContext);
	}
	
	private BeanRouter createBeanRouter(String beanId, String endpointUri)
	{
		BeanRouter beanRouter = new BeanRouter();
		beanRouter.setBeanId(beanId);
		beanRouter.setToEndpoint(endpointUri);
		return beanRouter;
	}
	
	public static class MyBean
	{
		private final String name;

		public MyBean(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
	}
}
