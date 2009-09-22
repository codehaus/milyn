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

package org.milyn.rules.drools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.io.impl.InputStreamResource;
import org.drools.runtime.StatelessKnowledgeSession;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ExecutionLifecycleCleanable;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.resource.URIResourceLocator;
import org.milyn.rules.BasicRuleEvalResult;
import org.milyn.rules.RuleEvalResult;
import org.milyn.rules.RuleProvider;

/**
 * <a href="http://jboss.org/projects/drools">Drools</a> Drools Provider.
 * <p/>
 * Currently only support Drools stateless working memory.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class DroolsProvider implements RuleProvider, ExecutionLifecycleCleanable
{
    /**
     * Logger.
     */
    private static Log logger = LogFactory.getLog(DroolsProvider.class);

    /**
     * The rules source file. Could be a drl, dsl etc.
     */
    private String src;

    /**
     * The type of the 'src' file.
     */
    private ResourceType resourceType = ResourceType.DRL;

    /**
     * This providers name
     */
    private String providerName;

    /**
     * The Drools {@link KnowledgeBase}.
     */
    private KnowledgeBase knowledgeBase;

    private boolean initialized;

    private String packageName;

    /**
     * No-args constructor required by Smooks.
     */
    public DroolsProvider()
    {
    }

    /**
     * Constructs an instance using the specified src.
     *
     * @param src The rule source.
     */
    public DroolsProvider(final String src, final String resourceType)
    {
        this.src = src;
        setResourceType(resourceType);
    }

    public RuleEvalResult evaluate(final String ruleName, final CharSequence selectedData, final ExecutionContext context) throws SmooksException
    {
        if (initialized == false)
        {
            initialize();
            initialized = true;
        }

        // Create a statefule session.
        final StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();

        final BeanRepository beanContext = BeanRepository.getInstance(context);
        if (beanContext == null)
        {
            throw new SmooksException("Could not locate any bean context.");
        }

        // Add an listener for fired rules.
        final RuleFiredEventListener eventListener = new RuleFiredEventListener();
        session.addEventListener(eventListener);

        final Map<String, Object> beanMap = beanContext.getBeanMap();
        Collection<Object> values = beanMap.values();
        for (Object object : values)
        {
            session.execute(object);
        }

        // Execute the rules.
        return new BasicRuleEvalResult(eventListener.matched, ruleName, providerName);
    }

    private static class RuleFiredEventListener implements AgendaEventListener
    {
        private boolean matched;

        public void activationCancelled(ActivationCancelledEvent event)
        {
            matched = false;
        }

        public void activationCreated(ActivationCreatedEvent event)
        {
            matched = false;
        }

        public void afterActivationFired(AfterActivationFiredEvent event)
        {
            matched = true;
        }

        public void agendaGroupPopped(AgendaGroupPoppedEvent event)
        {
            // NoOp
        }

        public void agendaGroupPushed(AgendaGroupPushedEvent event)
        {
            // NoOp
        }

        public void beforeActivationFired(BeforeActivationFiredEvent event)
        {
            // NoOp
        }

        public boolean matched()
        {
            return matched;
        }

    }

    public void initialize()
    {
        final Collection<KnowledgePackage> packages = loadKnowledgePackages(src);
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(packages);
    }

    Collection<KnowledgePackage> loadKnowledgePackages(final String ruleFile)
    {
        InputStream ruleStream = null;
        try
        {
            // Find the file using the classpath, filesystem, or as a URL.
            ruleStream = new URIResourceLocator().getResource(ruleFile);

            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(new InputStreamResource(ruleStream), resourceType);

            if (kbuilder.hasErrors())
            {
                new SmooksException("The following error were reported during Drools knowledgebase construction: " + kbuilder.getErrors().toString());
            }

            final Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
            final KnowledgePackage knowledgePackage = knowledgePackages.iterator().next();
            packageName = knowledgePackage.getName();
            return knowledgePackages;
        }
        catch (final IOException e)
        {
            throw new SmooksException("Failed to open rule file '" + ruleFile + "'.", e);
        }
        finally
        {
            try
            {
                if (ruleStream != null)
                {
                    ruleStream.close();
                }
            }
            catch (final IOException e)
            {
                logger.error("Error closing InputStream to Regex Rule file '" + ruleFile + "'.", e);
            }
        }
    }

    public void setResourceType(final String type)
    {
        AssertArgument.isNotNullAndNotEmpty(type, "type");

        resourceType = ResourceType.getResourceType(type.toUpperCase());
    }

    public String getName()
    {
        return providerName;
    }

    public String getSrc()
    {
        return src;
    }

    public void setName(final String name)
    {
        providerName = name;
    }

    public void setSrc(final String src)
    {
        AssertArgument.isNotNullAndNotEmpty(src, "src");
        this.src = src;
    }

    @Override
    public String toString()
    {
        return String.format("%s providerName=%s, src=%s, packageName=%s, ", getClass().getSimpleName(), providerName, src, packageName);
    }

    public void executeExecutionLifecycleCleanup(ExecutionContext executionContext)
    {
        System.out.println("clean up...");
    }
}
