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

/**
 * Exception that carries information about a regular expression pattern
 * match failure.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class RegexException extends Exception
{
    /**
     * Serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The regex pattern.
     */
    private Pattern pattern;

    /**
     * The text the regular expression was evaluated against.
     */
    private String text;

    /**
     * Sole constructor.
     *
     * @param message The exception message.
     * @param pattern The regex pattern.
     * @param text The String that could not be matched against the pattern.
     */
    public RegexException(final String message, final Pattern pattern, final String text)
    {
        super(message);
        this.pattern = pattern;
        this.text = text;
    }

    /**
     * @return {@code Pattern} The regex pattern.
     */
    public Pattern getPattern()
    {
        return pattern;
    }

    public String getText()
    {
        return text;
    }

}
