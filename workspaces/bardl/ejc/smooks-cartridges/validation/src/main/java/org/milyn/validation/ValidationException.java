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

/**
 * Exception that carries information about a validation failure.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidationException extends Exception
{
    /**
     * Serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The string that could not be validated
     */
    private String text;

    /**
     * Sole constructor.
     *
     * @param message The exception message.
     * @param text The String that could not be matched against the pattern.
     */
    public ValidationException(final String message, final String text)
    {
        super(message);
        this.text = text;
    }

    /**
     * @return {@code String} The String that could not be matched against the pattern.
     */
    public String getText()
    {
        return text;
    }
}