package org.milyn.rules.regex;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.rules.RuleEvalResult;
import org.milyn.rules.RuleProvider;

public class RegexProvider implements RuleProvider
{
    public RuleEvalResult evaluate(final String ruleName, ExecutionContext context) throws SmooksException
    {
        return null;
    }

    public String getName()
    {
        return getClass().getSimpleName();
    }

    public String getRuleName()
    {
        return null;
    }

    public String getSrc()
    {
        return null;
    }

    public void setRuleName(String ruleName)
    {
    }

    public void setSrc(String src)
    {
    }

}
