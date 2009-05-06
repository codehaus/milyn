package org.milyn.rules;

import java.util.regex.Pattern;

public class RuleEvalResultImpl implements RuleEvalResult
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
    public RuleEvalResultImpl(final boolean matched, final String ruleName, final String providerName, final Pattern pattern, final String text)
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

    @Override
    public String toString()
    {
        return String.format("%s, matched=%b, providerName=%s, ruleName=%s, text=%s, pattern=%s", getClass().getSimpleName(), matched, providerName, ruleName, text, pattern);
    }

}