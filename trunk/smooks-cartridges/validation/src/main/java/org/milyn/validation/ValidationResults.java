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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.rules.RuleEvalResult;

/**
 * ValidationResults job is to handle the reporting of validation failures
 * at different levels.
 * <p>
 * Users can specify the following different levels of reporting:
 * <lu>
 *   <li>OnFail.OK</li> Ignored and cannot be accessed.
 *   <li>OnFail.WARN</li> addWarning, getWarnings
 *   <li>OnFail.ERROR</li> addError, getErrors
 *   <li>OnFail.FAIL</li> addFailure, getFailures
 * </lu>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidationResults
{
    /**
     * Key used to store the map of ValidationResults in the execution context.
     */
    private static final String CONTEXT_KEY = ValidationResults.class.getName();

    /**
     * Sole private constructor.
     */
    private ValidationResults()
    {
    }

    /**
     * Add {@link RuleEvalResult} with {@link OnFail#WARN} level.
     *
     * @param result The {@link RuleEvalResult}. Cannot be null.
     * @param context The Smooks {@link ExecutionContext}. Cannot be null.
     */
    public static void addWarning(final RuleEvalResult result, final ExecutionContext context)
    {
        addResult(result, OnFail.WARN, context);
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#WARN}
     * level.
     *
     * @param context The Smooks {@link ExecutionContext}. Cannot be null;
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#WARN}.
     */
    public static List<RuleEvalResult> getWarnings(final ExecutionContext context)
    {
        return Collections.unmodifiableList(getResultList(OnFail.WARN, context));
    }

    /**
     * Add {@link RuleEvalResult} with {@link OnFail#ERROR} level.
     *
     * @param result The {@link RuleEvalResult}. Cannot be null.
     * @param context The Smooks {@link ExecutionContext}. Cannot be null.
     */
    public static void addError(final RuleEvalResult result, final ExecutionContext context)
    {
        addResult(result, OnFail.ERROR, context);
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#ERROR}
     * level.
     *
     * @param context The Smooks {@link ExecutionContext}. Cannot be null;
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#ERROR}.
     */
    public static List<RuleEvalResult> getErrors(final ExecutionContext context)
    {
        return Collections.unmodifiableList(getResultList(OnFail.ERROR, context));
    }

    /**
     * Add {@link RuleEvalResult} with {@link OnFail#FAIL} level.
     *
     * @param result The {@link RuleEvalResult}. Cannot be null.
     * @param context The Smooks {@link ExecutionContext}. Cannot be null.
     */
    public static void addFailure(final RuleEvalResult result, final ExecutionContext context)
    {
        addResult(result, OnFail.FAIL, context);
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#FAIL}
     * level.
     *
     * @param context The Smooks {@link ExecutionContext}. Cannot be null;
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#FAIL}.
     */
    public static List<RuleEvalResult> getFailures(final ExecutionContext context)
    {
        return Collections.unmodifiableList(getResultList(OnFail.FAIL, context));
    }

    /**
     * Adds the {@link RuleEvalResult} with {@link OnFail} level passed in.
     *
     * @param result The {@link RuleEvalResult}. Cannot be null.
     * @param onFail The {@link OnFail} level for which this rule should be reported.
     * @param context The Smooks {@link ExecutionContext}. Cannot be null;
     */
    public static void addResult(final RuleEvalResult result, final OnFail onFail, final ExecutionContext context)
    {
        AssertArgument.isNotNull(result, "result");
        AssertArgument.isNotNull(context, "context");

        final Map<OnFail, List<RuleEvalResult>> resultsMap = getResults(context);
        final List<RuleEvalResult> resultsList = getResultList(onFail, context);

        // Add the RuleEvalResult to the specific list.
        resultsList.add(result);

        // Put the list into the resultMap. Overwrite the existing one.
        resultsMap.put(onFail, resultsList);

        // Set the result map into the execution context.
        context.setAttribute(CONTEXT_KEY, resultsMap);
    }

    private static List<RuleEvalResult> getResultList(final OnFail onFail, final ExecutionContext context)
    {
        AssertArgument.isNotNull(context, "context");
        final Map<OnFail, List<RuleEvalResult>> resultsMap = getResults(context);
        final List<RuleEvalResult> resultList = resultsMap.get(onFail);
        if (resultList == null)
        {
            return new ArrayList<RuleEvalResult>();
        }

        return resultList;
    }

    @SuppressWarnings("unchecked")
    private static Map<OnFail, List<RuleEvalResult>> getResults(final ExecutionContext context)
    {
        Map<OnFail, List<RuleEvalResult>> results = (Map<OnFail, List<RuleEvalResult>>) context.getAttribute(CONTEXT_KEY);
        if (results == null)
        {
            results = new ConcurrentHashMap<OnFail, List<RuleEvalResult>>();
            context.setAttribute(CONTEXT_KEY, results);
        }

        return results;
    }

}
