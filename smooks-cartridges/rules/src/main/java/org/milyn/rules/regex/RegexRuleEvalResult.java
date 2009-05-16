/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.rules.regex;

import org.milyn.rules.RuleEvalResult;

import java.util.regex.Pattern;

/**
 * Regex RuleEvalResult.
 * 
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class RegexRuleEvalResult implements RuleEvalResult
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
    final String providerName;

    /**
     * The regex pattern.
     */
    final Pattern pattern;

    /**
     * The test used in the match.
     */
    private String text;

    /**
     * Creates a RuleEvalResult that indicates a successfully executed rule.
     */
    public RegexRuleEvalResult(final boolean matched, final String ruleName, final String providerName, final Pattern pattern, final String text)
    {
        this.matched = matched;
        this.ruleName = ruleName;
        this.providerName = providerName;
        this.pattern = pattern;
        this.text = text;
    }

    public boolean matched()
    {
        return matched;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public String getRuleProviderName()
    {
        return providerName;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString()
    {
        return String.format("%s, matched=%b, providerName=%s, ruleName=%s, text=%s, pattern=%s", getClass().getSimpleName(), matched, providerName, ruleName, text, pattern);
    }

}