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


import org.junit.Before;
import org.junit.Test;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.rules.RuleProviderAccessor;
import org.milyn.rules.regex.RegexProvider;
import org.milyn.payload.FilterResult;

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
        regexProvider = new RegexProvider("/smooks-regex.properties");
    }

    @Test
    public void configure()
    {
        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.WARN);

        assertEquals(ruleName, validator.getCompositRuleName());
        assertEquals(OnFail.WARN, validator.getOnFail());
    }

    @Test
    public void validateWarn()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.WARN).setAppContext(appContext);
        final ValidationResult result = new ValidationResult();

        MockExecutionContext executionContext = new MockExecutionContext();
        FilterResult.setResults(executionContext, result);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        assertEquals(0, result.getOKs().size());
        assertEquals(3, result.getWarnings().size());
        assertEquals(0, result.getErrors().size());
    }

    @Test
    public void validateOks()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.OK).setAppContext(appContext);
        final ValidationResult result = new ValidationResult();

        MockExecutionContext executionContext = new MockExecutionContext();
        FilterResult.setResults(executionContext, result);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        assertEquals(3, result.getOKs().size());
        assertEquals(0, result.getWarnings().size());
        assertEquals(0, result.getErrors().size());
    }

    @Test
    public void validateErrors()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final Validator validator = new Validator(ruleName, OnFail.ERROR).setAppContext(appContext);
        final ValidationResult result = new ValidationResult();

        MockExecutionContext executionContext = new MockExecutionContext();
        FilterResult.setResults(executionContext, result);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        assertEquals(0, result.getOKs().size());
        assertEquals(0, result.getWarnings().size());
        assertEquals(3, result.getErrors().size());
    }

    @Test
    public void validateFatal()
    {
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String ruleName = "addressing.email";
        final String data = "xyz";
        final Validator validator = new Validator(ruleName, OnFail.FATAL).setAppContext(appContext);

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
