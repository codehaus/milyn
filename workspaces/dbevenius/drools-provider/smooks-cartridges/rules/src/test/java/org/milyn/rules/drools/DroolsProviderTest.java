/*
 * Milyn - Copyright (C) 2006
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

package org.milyn.rules.drools;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.payload.StringSource;
import org.milyn.rules.RuleEvalResult;

/**
 * Unit test for {@link DroolsProvider}.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class DroolsProviderTest
{
    private static DroolsProvider provider;

    @BeforeClass public static void createProvider()
    {
        provider = new DroolsProvider("/org/milyn/rules/drools/test.drl", "drl");
        provider.initialize();
    }

    @Test public void evalutate()
    {
        final MockExecutionContext execContext = new MockExecutionContext();
        final BeanRepository beanContext = BeanRepository.getInstance(execContext);
        beanContext.addBean("test1", new TestPojo());

        final RuleEvalResult result = provider.evaluate("Rule1", "dummyData", execContext);
        assertTrue(result.matched());
    }

    @Test public void evalutateFalse()
    {
        final MockExecutionContext execContext = new MockExecutionContext();

        final RuleEvalResult result = provider.evaluate("Rule1", "dummyData", execContext);
        assertFalse(result.matched());
    }

    @Test public void evaluateUsingFramwork()
    {
        final Smooks smooks = new Smooks();
        final SmooksResourceConfiguration config = createConfig("testResource", "rule1" );
        smooks.addConfiguration(config);

        final String xml = "<element><data>dummyData</data></element>";
        StringSource source = new StringSource(xml);
        smooks.filterSource(source);
    }

    private SmooksResourceConfiguration createConfig(
            final String resourceName,
            final String ruleName)

    {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration( "element", DroolsProvider.class.getName() );
        config.setParameter( "resourceName", resourceName );
        config.setParameter( "ruleName", ruleName );
        return config;
    }

}
