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

package org.milyn.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;
import org.milyn.rules.RulesProviderFactoryTest.MockProvider;
import org.milyn.rules.regex.RegexRuleResult;
import org.xml.sax.SAXException;

/**
 * Unit test for RuleProviderFactory.
 * <p/>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class RulesProviderFactoryTest
{
    @Test
    public void test() throws IOException, SAXException
    {
        Smooks smooks = new Smooks("/smooks-configs/extended/1.0/smooks-rules-config.xml");
        StringSource source = new StringSource("<order></order>");
        StringResult result = new StringResult();
        smooks.filter(source, result);
    }

    @Test
    public void createProvider()
    {
        RuleProvider provider = new RulesProviderFactory().createProvider(MockProvider.class);
        assertNotNull(provider);
        assertTrue(provider instanceof MockProvider);
        assertEquals("MockProvider", provider.getRuleName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProviderNullProvider()
    {
        MockApplicationContext appContext = new MockApplicationContext();
        new RulesProviderFactory().addProvider(appContext, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addProviderNullAppContext()
    {
        new RulesProviderFactory().addProvider(null, new MockProvider());
    }

    @Test
    public void addProvider()
    {
        MockApplicationContext appContext = new MockApplicationContext();
        MockProvider provider = new MockProvider();
        new RulesProviderFactory().addProvider(appContext, provider);

        Map<String, RuleProvider> providers = (Map<String, RuleProvider>) appContext.getAttribute(RuleProvider.class);
        assertNotNull(providers);
    }


    public static class MockProvider implements RuleProvider
    {
        public RuleEvalResult evaluate(String ruleName, ExecutionContext context) throws SmooksException
        {
            return new RegexRuleResult(true, ruleName, "MockProvider");
        }

        public String getRuleName()
        {
            return getClass().getSimpleName();
        }

        public String getSrc()
        {

            return null;
        }

        public void setRuleName(String ruleName)
        {
        }

        public void setSrc(String src)
        {
        }

    }

}
