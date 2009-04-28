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

import java.util.regex.Pattern;

import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.rules.RuleEvalResult;
import org.milyn.rules.RuleProvider;

/**
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class RegexProvider implements RuleProvider
{
    /**
     * Option string identifying a file that contains regex mappings.
     */
    private String src;

    /**
     * The name of this rule provider.
     */
    private String providerName;

    /**
     *
     */
    public RuleEvalResult evaluate(final String ruleName, final CharSequence selectedData, final ExecutionContext context) throws SmooksException
    {
        AssertArgument.isNotNullAndNotEmpty(ruleName, "ruleName");
        AssertArgument.isNotNull(selectedData, "selectedData");

        final Pattern pattern = getPattern(ruleName);
        final boolean matched = pattern.matcher(selectedData).matches();

        return new RegexRuleResult(matched, ruleName, providerName, pattern);
    }

    private Pattern getPattern(String ruleName)
    {
        final String regex = RegexCache.getInstance().getRegexForAlias(ruleName);
        return getRegexPattern(regex);
    }

    public String getName()
    {
        return providerName;
    }

    public void setName(final String name)
    {
        this.providerName = name;
    }

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
        addUserDefinedAliases(src);
    }

    /**
     * Compiles the passed-in regular expression into a {@link Pattern}.
     *
     * @param regex The regular expression.
     */
    private Pattern getRegexPattern(final String regex)
    {
        try
        {
            return RegexCache.getInstance().get(regex);
        }
        catch (final InterruptedException e)
        {
            throw new SmooksException("Caught InterruptedException while trying to compile regex '" + regex +"'", e);
        }
    }

    /**
     * Adds all the alias in the properties file to the regex cache.
     *
     * @param aliasFile The file containing alias to regex mappings.
     */
    private void addUserDefinedAliases(final String aliasFile)
    {
        if (aliasFile != null)
        {
            RegexCache.getInstance().addAliasesFromFile(aliasFile);
        }
    }

}
