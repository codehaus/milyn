package org.milyn.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.annotation.Initialize;

public class RulesProviderFactory implements ContentHandler
{
    /**
     * Logger.
     */
    private static Log logger = LogFactory.getLog(RulesProviderFactory.class);

    /**
     * The Smooks {@link ApplicationContext}.
     */
    @AppContext
    private ApplicationContext applicationContext;

    /**
     * The rule name to be used.
     */
    @ConfigParam(use = Use.REQUIRED )
    private String name;

    /**
     * The {@link RuleProvider} implementation to be used.
     */
    @ConfigParam(name = "provider", use = Use.REQUIRED)
    private Class<RuleProvider> provider;

    /**
     * The source of the rule. Is implementation dependent, may be a file for example.
     */
    @ConfigParam(use = Use.OPTIONAL)
    private String src;


    @Initialize
    public void installRuleProvider() throws SmooksConfigurationException
    {
        logger.info(this);
        if(RuleProvider.class.isAssignableFrom(provider))
        {
            final RuleProvider providerImpl = createProvider(provider);
            providerImpl.setRuleName(name);
            providerImpl.setSrc(src);

            addProvider(applicationContext, providerImpl);
        }
        else
        {
            throw new SmooksConfigurationException("Invalid rule provider");
        }
    }

    @SuppressWarnings("unchecked")
    ApplicationContext addProvider(final ApplicationContext appContext, final RuleProvider provider)
    {
        AssertArgument.isNotNull(appContext, "appContext");
        AssertArgument.isNotNull(provider, "provider");

        Map<String, RuleProvider> providers = (Map<String, RuleProvider>) appContext.getAttribute(RuleProvider.class);
        if (providers == null)
        {
            providers = new HashMap<String, RuleProvider>();
            // Set the providers in the ApplicationContext.
            appContext.setAttribute(RuleProvider.class, providers);
        }

        // Add to the provider, overwriting any previous provider with the same name. We ignore the result from this method.
        providers.put(provider.getRuleName(), provider);

        return appContext;
    }

    RuleProvider createProvider(final Class<? extends RuleProvider> providerClass) throws SmooksException
    {
        try
        {
            return providerClass.newInstance();
        }
        catch (final InstantiationException e)
        {
            throw new SmooksException(e.getMessage(), e);
        }
        catch (final IllegalAccessException e)
        {
            throw new SmooksException(e.getMessage(), e);
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s [name=%s, src=%s, provider=%s]", getClass().getSimpleName(), name, src, provider);
    }


}
