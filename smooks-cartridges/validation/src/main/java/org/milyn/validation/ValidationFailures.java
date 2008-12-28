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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;

/**
 * Helper class that sets and retrieves {@link ValidationException} into the Smooks
 * {@link ExecutionContext}.
 * <p>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidationFailures
{
    private static final String CONTEXT_KEY = ValidationFailures.class.getName() + "#failures";

    /**
     * Sole private constructor.
     */
    private ValidationFailures()
    {
    }

    @SuppressWarnings("unchecked")
    public static void add(final ValidationException valException, final ExecutionContext executionContext)
    {
        AssertArgument.isNotNull(valException, "valExeption");
        AssertArgument.isNotNull(executionContext, "executionContext");

        List<ValidationException> failures  = (List<ValidationException>) executionContext.getAttribute(CONTEXT_KEY);
        if (failures == null)
        {
            failures = new ArrayList<ValidationException>();
        }

        failures.add(valException);
        executionContext.setAttribute(CONTEXT_KEY, failures);
    }

    /**
     * Returns the failures that have been set or an empty list.
     *
     * @param executionContext The Smooks {@link ExecutionContext} where the failures are stored.
     * @return {@code List} All the failures that have been set of an empty list if none have been set.
     */
    @SuppressWarnings("unchecked")
    public static List<ValidationException> getAll(final ExecutionContext executionContext)
    {
        AssertArgument.isNotNull(executionContext, "executionContext");

        final List<ValidationException> failures = (List<ValidationException>) executionContext.getAttribute(CONTEXT_KEY);
        if (failures == null)
        {
            return Collections.EMPTY_LIST;
        }
        return failures;
    }

}
