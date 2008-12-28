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

package org.milyn.validation.regex;

import java.util.regex.Pattern;

import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.validation.ValidationFailures;

/**
 * Validator that validates a string against a regular expression.
 * <p>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class RegexValidator
{
    /**
     * An alias that exists in either the default default-regex-validator.properties file or
     * in an optionally user defined properties file.
     */
    private String alias;

    /**
     * The compiled regex pattern.
     */
    private Pattern pattern;

    /**
     * Constructs a RegexValidator using the passed-in regex.
     *
     * @param regex The regular expression that will be used.
     */
    public RegexValidator(final String regex)
    {
        AssertArgument.isNotNull(regex, "regex");
        compilePattern(regex);
    }

    /**
     * Constructs a RegexValidator using the passed-in alias which is mapped
     * against a regular expression that must exist is either the default
     * regular expressions file or in the passed-in aliasFile properties file.
     *
     * @param alias The alias to be looked up.
     * @param aliasFile Optional alias to regex mapping file(a properties file).
     */
    public RegexValidator(final String alias, final String aliasFile)
    {
        AssertArgument.isNotNull(alias, "alias");
        this.alias = alias;
        addUserDefinedAliases(aliasFile);
        compilePattern(getRegexForAlias(alias));
    }

    /**
     * Compiles the passed-in regular expression into a {@link Pattern}.
     *
     * @param regex The regular expression.
     */
    private void compilePattern(final String regex)
    {
        try
        {
            pattern = RegexCache.getInstance().get(regex);
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

    /**
     * Retrieves the regex for the passed-in alias.
     *
     * @param alias The alias of the regex.
     * @return {@code String} The regular expression that matches the alias.
     * @throws SmooksException If the alias could not be found.
     */
    private String getRegexForAlias(final String alias)
    {
        final String regexFromAlias = RegexCache.getInstance().getRegexForAlias(alias);
        if (regexFromAlias == null)
        {
            throw new SmooksException("Could not find a regular expression for alias '" + alias + "'. Please double check the alias name, and that it exist in either the regex-default.properties or the in 'aliasFile'");
        }
        return regexFromAlias;
    }

    /**
     * Will validate the passed in text against the regular expression and
     * throw a SmooksException if the validator fails.
     *
     * @param text The text to validate.
     */
    public void validate(final String text)
    {
        AssertArgument.isNotNull(text, "text");
        if (notValid(text))
        {
            final RegexException regexException = createException(text);
            throw new SmooksException(regexException.getMessage(), regexException);
        }
    }

    /**
     * Validates the passed in text agaist the compiled regex. This method does not
     * throw and Exception if the validation fails, instead it will add a {@link RegexException}
     * to the {@link ExecutionContext}. All failures can then get retrieved using
     * {@link ValidationFailures#getAll(ExecutionContext)}.
     *
     * @param text The text to validate.
     * @param executionContext The Smooks exceution context which will be used to store and failures.
     */
    public void validate(final String text, final ExecutionContext executionContext)
    {
        if (notValid(text))
        {
            ValidationFailures.add(createException(text), executionContext);
        }
    }

    /**
     * Validates the passed-in text against the pattern. This method is negative in that
     * it will return true if the regex did not match.
     *
     * @param text The text to be matched/validated.
     * @return {@code true} If the text could not be matched/validated.
     */
    private boolean notValid(final String text)
    {
        return !pattern.matcher(text).matches();
    }

    /**
     * Util method that just creates an exception.
     * @param text The text that will be used as the message of the exception.
     * @return {@code RegexException} The newly created excpetion.
     */
    private RegexException createException(final String text)
    {
        return new RegexException("Could not validate '" + text + "' with '" + this + "'", pattern, text);
    }

    /**
     * Will return a String representation of this instance in the form:
     * RegexValidator [alias='aliasName', pattern='pattern.toString()']
     *
     * @return {@code String} The string representation of this instance.
     */
    @Override
    public String toString()
    {
        return "RegexValidator [alias='" + alias +"', pattern='" + pattern + "']";
    }

}
