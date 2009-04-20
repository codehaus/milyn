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

package org.milyn.rules;

/**
 * RuleEvalResult is the returned result from a {@link RuleProvider#evaluate(String, org.milyn.container.ExecutionContext)}
 * invocation.
 * <p/>
 * Concrete RuleProviders may implement their own custom result that are more specific to the technology
 * used.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public interface RuleEvalResult
{
    /**
     * The outcome of the rule evaluation.
     *
     * @return {@code true} if successful or false if it failed.
     */
    boolean matched();

    /**
     * Gets the name of the Rule that this class is a result of.
     * @return String The name of the rule that created this result.
     */
    String getRuleName();

    /**
     * The name of the provider that produced this rule result.
     * @return String The name of the Rule provider that produced this result.
     */
    String getRuleProvider();

}
