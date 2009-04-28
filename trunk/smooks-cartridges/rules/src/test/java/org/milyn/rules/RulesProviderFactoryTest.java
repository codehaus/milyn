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

import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;
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
    public void extendedConfig() throws IOException, SAXException
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
        assertEquals("MockProvider", provider.getName());
    }

    public static class MockProvider implements RuleProvider
    {
        public String getName()
        {
            return getClass().getSimpleName();
        }

        public String getSrc()
        {
            return null;
        }

        public void setSrc(String src)
        {
        }

        public RuleEvalResult evaluate(String ruleName, CharSequence selectedData, ExecutionContext context) throws SmooksException
        {
            return new RegexRuleResult(true, ruleName, "MockProvider", null);
        }

        public void setName(String name)
        {
        }

    }

}
