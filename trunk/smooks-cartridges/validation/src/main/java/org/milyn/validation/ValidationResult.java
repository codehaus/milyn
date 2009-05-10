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
import org.milyn.payload.FilterResult;

/**
 * ValidationResult's job is to handle the reporting of validation failures
 * at different levels.
 * <p>
 * Users can specify the following different levels of reporting:
 * <lu>
 *   <li>OnFail.OK</li> addOK, getOks
 *   <li>OnFail.WARN</li> addWarning, getWarnings
 *   <li>OnFail.ERROR</li> addError, getErrors
 *   <li>OnFail.FAIL</li> addFailure, getFailures
 * </lu>
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class ValidationResult extends FilterResult
{
    /**
     * The validation result Map, keyed by OnFail Type.
     */
    private Map<OnFail, List<RuleEvalResult>> results = new ConcurrentHashMap<OnFail, List<RuleEvalResult>>();

    /**
     * Public default constructor.
     */
    public ValidationResult()
    {
        results.put(OnFail.OK, new ArrayList<RuleEvalResult>());
        results.put(OnFail.WARN, new ArrayList<RuleEvalResult>());
        results.put(OnFail.ERROR, new ArrayList<RuleEvalResult>());
        results.put(OnFail.WARN, new ArrayList<RuleEvalResult>());
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#OK}
     * level.
     *
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#OK}.
     */
    public List<? extends RuleEvalResult> getOKs()
    {
        return Collections.unmodifiableList(results.get(OnFail.OK));
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#WARN}
     * level.
     *
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#WARN}.
     */
    public List<RuleEvalResult> getWarnings()
    {
        return Collections.unmodifiableList(results.get(OnFail.WARN));
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#ERROR}
     * level.
     *
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#ERROR}.
     */
    public List<RuleEvalResult> getErrors()
    {
        return Collections.unmodifiableList(results.get(OnFail.ERROR));
    }

    /**
     * Gets all the {@link RuleEvalResult}s that were reported at the {@link OnFail#FATAL}
     * level.
     *
     * @return List<RuleEvalResult> Containing all the {@link RuleEvalResult} reported at {@link OnFail#FATAL}.
     */
    public List<RuleEvalResult> getFatals()
    {
        return Collections.unmodifiableList(results.get(OnFail.FATAL));
    }

    /**
     * Adds the {@link RuleEvalResult} with {@link OnFail} level passed in.
     *
     * @param result The {@link RuleEvalResult}. Cannot be null.
     * @param onFail The {@link OnFail} level for which this rule should be reported.
     */
    protected void addResult(final RuleEvalResult result, final OnFail onFail)
    {
        AssertArgument.isNotNull(result, "result");

        // Add the RuleEvalResult to the specific list.
        results.get(onFail).add(result);
    }
}