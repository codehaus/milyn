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

import org.milyn.rules.RuleEvalResult;

/**
 * A RuleEvalResult implementation.
 * </p>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class RegexRuleResult implements RuleEvalResult
{
    /**
     * The result for a rule evaluation
     */
    final boolean matched;

    /**
     * The name of the rules that produced this result.
     */
    final String ruleName;

    /**
     * The name of the provider that produced this result.
     */
    final String provider;

    /**
     * Creates a RuleEvalResult that indicates a successfully executed rule.
     */
    public RegexRuleResult(final boolean matched, final String ruleName, final String ruleProvider)
    {
        this.matched = matched;
        this.ruleName = ruleName;
        this.provider = ruleProvider;
    }

    public boolean matched()
    {
        return matched;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public String getRuleProvider()
    {
        return provider;
    }

    @Override
    public String toString()
    {
        return String.format("%s, ruleName=%s, matched=%b, provider=%s", getClass().getSimpleName(), matched, ruleName, provider);
    }

}
