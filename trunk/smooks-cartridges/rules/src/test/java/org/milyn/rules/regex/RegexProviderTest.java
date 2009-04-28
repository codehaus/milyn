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

package org.milyn.rules.regex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.milyn.container.MockExecutionContext;
import org.milyn.rules.RuleEvalResult;

/**
 * Unit test for RegexProviderTest.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class RegexProviderTest
{
    private RegexProvider provider = new RegexProvider();

    @Before
    public void createProvider()
    {
        provider = new RegexProvider();
        provider.setName(RegexProvider.class.getSimpleName());
    }

    @Test (expected = IllegalArgumentException.class )
    public void showThrowIfRuleNameIsNull()
    {
        provider.evaluate(null, "some text", new MockExecutionContext());
    }

    @Test (expected = IllegalArgumentException.class )
    public void showThrowIfSelectedDataIsNull()
    {
        provider.evaluate("ruleName", null , new MockExecutionContext());
    }

    @Test
    public void getProviderName()
    {
        assertEquals("RegexProvider", provider.getName());
    }

    @Test
    public void setSrc()
    {
        final String regexFile = "/regex.properties";
        provider.setSrc(regexFile);
        assertEquals(regexFile, provider.getSrc());
    }

    @Test
    public void evalutatePhoneNumberSE()
    {
        final String ruleName = "phoneNumberSE";
        final RuleEvalResult result = provider.evaluate(ruleName, "08-7549922", null);
        assertTrue(result.matched());
    }

    @Test
    public void evalutatePhoneNumberSEInValid()
    {
        final String ruleName = "phoneNumberSE";
        final RuleEvalResult result = provider.evaluate(ruleName, "7549922", null);
        assertFalse(result.matched());
    }

}
