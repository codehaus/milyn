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

import java.util.List;

import org.junit.Test;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.ApplicationContext;
import org.milyn.container.MockApplicationContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.rules.RuleEvalResult;
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
        final String rule = "addressing.email";
        final Validator validator = new ConfigBuilder().rule(rule).onFail(OnFail.WARN).build();;

        assertEquals(rule, validator.getRule());
        assertEquals(OnFail.WARN, validator.getOnFail());
    }

    @Test
    public void validate()
    {
        final MockApplicationContext appContext = new MockApplicationContext();
        final RegexProvider regexProvider = new RegexProvider();
        regexProvider.setName("addressing");
        RuleProviderAccessor.add(appContext, regexProvider);

        final String rule = "addressing.email";

        final Validator validator = new ConfigBuilder().rule(rule).onFail(OnFail.WARN).appContext(appContext).build();

        MockExecutionContext executionContext = new MockExecutionContext();
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);
        validator.validate("xyz", executionContext);

        List<RuleEvalResult> all = ValidationResults.getWarnings(executionContext);
        assertEquals(3, all.size());
    }

    private static class ConfigBuilder
    {
        final SmooksResourceConfiguration config;
        ApplicationContext appContext;

        public ConfigBuilder()
        {
            config = new SmooksResourceConfiguration();
        }

        public ConfigBuilder rule(final String rule)
        {
            config.setParameter("rule", rule);
            return this;
        }

        public ConfigBuilder onFail(final OnFail onFail)
        {
            config.setParameter("onFail", onFail.toString());
            return this;
        }

        public ConfigBuilder appContext(final ApplicationContext context)
        {
            this.appContext = context;
            return this;
        }

        public Validator build()
        {
            final Validator validator = new Validator();
            Configurator.configure(validator, config);
            Configurator.processFieldContextAnnotation(validator, appContext);
            return validator;
        }
    }
}
