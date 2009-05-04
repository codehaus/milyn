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

package org.milyn.validation;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


import org.junit.Before;
import org.junit.Test;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.rules.RuleProviderAccessor;
import org.milyn.rules.regex.RegexProvider;

/**
 * Unit test for {@link Validator}
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidatorTest
{
    private MockApplicationContext appContext;
    private RegexProvider regexProvider;

    @Before
    public void setup()
    {
        appContext = new MockApplicationContext();
        regexProvider = new RegexProvider();
    }

    @Test
    public void configure()
    {
        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.WARN, null);

        assertEquals(ruleName, validator.getCompositRuleName());
        assertEquals(OnFail.WARN, validator.getOnFail());
    }

    @Test
    public void validateWarn()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.WARN, appContext);

        MockExecutionContext executionContext = new MockExecutionContext();
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        assertEquals(0, ValidationResults.getOKs(executionContext).size());
        assertEquals(3, ValidationResults.getWarnings(executionContext).size());
        assertEquals(0, ValidationResults.getErrors(executionContext).size());
    }

    @Test
    public void validateOks()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.OK, appContext);

        MockExecutionContext executionContext = new MockExecutionContext();
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        assertEquals(3, ValidationResults.getOKs(executionContext).size());
        assertEquals(0, ValidationResults.getWarnings(executionContext).size());
        assertEquals(0, ValidationResults.getErrors(executionContext).size());
    }

    @Test
    public void validateErrors()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.ERROR, appContext);

        MockExecutionContext executionContext = new MockExecutionContext();
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        assertEquals(0, ValidationResults.getOKs(executionContext).size());
        assertEquals(0, ValidationResults.getWarnings(executionContext).size());
        assertEquals(3, ValidationResults.getErrors(executionContext).size());
    }

    @Test
    public void validateFatal()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final String data = "xyz";
        final Validator validator = new Validator(ruleName, OnFail.FATAL, appContext);

        MockExecutionContext executionContext = new MockExecutionContext();
        try
        {
            validator.validate(data, executionContext);
        }
        catch (final Exception e)
        {
            assertTrue(e instanceof ValidationException);
            assertEquals(data, ((ValidationException)e).getText());
        }
    }
}
