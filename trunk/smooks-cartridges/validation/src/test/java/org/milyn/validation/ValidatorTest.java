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

import static org.junit.Assert.assertEquals;


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
        final MockApplicationContext appContext = new MockApplicationContext();
        final RegexProvider regexProvider = new RegexProvider();
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

}
