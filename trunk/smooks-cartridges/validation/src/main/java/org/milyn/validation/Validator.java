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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.payload.FilterResult;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMVisitAfter;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.rules.RuleEvalResult;
import org.milyn.rules.RuleProvider;
import org.milyn.rules.RuleProviderAccessor;
import org.w3c.dom.Element;

/**
 *
 * </p>
 * A Validator uses a predefined Rule that performs the actual validator for a Validator. This way a Validator does not know
 * about the technology used for the validation and users can mix and max different rules as appropriate to the use case they
 * have. For example, one problem might be solve nicely with a regular expression but another might be easier to sovle using
 * a MVEL expression.
 *
 * Example configuration:
 * <pre>{@code
 * <rules:ruleBases>
 *    <rules:ruleBase name="addressing" src="usa_address.properties" provider="org.milyn.smooks.validation.RegexProvider" />
 * </rules:ruleBases>
 *
 * <validation:field on="order/header/email" rule="addressing.email" onFail="WARN" />
 *
 * }</pre>
 * Options:
 * <lu>
 *  <li><b><i>on</b></i>
 *  The fragement that the validation will be performed upon. </li>
 *
 *  <li><b><i>rule</b></i>
 *  Is the name of a previously defined in a rules element. The rule itself is identified by ruleProviderName.ruleName.
 *  So taking the above example addressing is the ruleProviderName and email is the rule name. In this case email
 *  identifies a regular expression but if you were to change the provider that might change and a differnet technology
 *  could be used to validate an email address.</li>
 *
 *  <li><b><i>onFail</b></i>
 *  The onFail attribute in the validation configuration specified what action should be taken when a rule matches.
 *  This is all about reporting back valdiation failures.
 *  </li>
 *
 * </lu>
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
@VisitBeforeReport(condition = "false")
public final class Validator implements SAXVisitBefore, SAXVisitAfter, DOMVisitAfter
{
    private static Log logger = LogFactory.getLog(Validator.class);

    /**
     * The name of the rule that will be used by this validator.
     */
    private String compositRuleName;
    /**
     * Rule provider name.
     */
    private String ruleProviderName;
    /**
     * Rule name.
     */
    private String ruleName;
    /**
     * Rule provider for this validator.
     */
    private RuleProvider ruleProvider;
    /**
     * The validation failure level. Default is OnFail.ERROR.
     */
    private OnFail onFail = OnFail.ERROR;
    /**
     * The Smooks {@link ApplicationContext}.
     */
    @AppContext
    private ApplicationContext appContext;
    /**
     * Config.
     */
    @Config
    private SmooksResourceConfiguration config;
    private String targetAttribute;

    /**
     * No-args constructor required by Smooks.
     */
    public Validator() {}

    /**
     * Initialize the visitor instance.
     */
    @Initialize
    public void initialize() {
        targetAttribute = config.getTargetAttribute();
    }

    /**
     * Public constructor.
     * @param compositRuleName The name of the rule that will be used by this validator.
     * @param onFail The failure level.
     */
    public Validator(final String compositRuleName, final OnFail onFail)
    {
        setCompositRuleName(compositRuleName);
        this.onFail = onFail;
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        if(targetAttribute == null) {
            // The selected text is not an attribute, which means it's the element text,
            // which means we need to turn on text accumulation for SAX...
            element.accumulateText();
        }
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException
    {
        if(targetAttribute != null) {
            validate(element.getAttribute(targetAttribute), executionContext);
        } else {
            validate(element.getTextContent(), executionContext);
        }
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException
    {
        if(targetAttribute != null) {
            validate(element.getAttribute(targetAttribute), executionContext);
        } else {
            validate(element.getTextContent(), executionContext);            
        }
    }

    /**
     * Validate will lookup the configured RuleProvider and validate the text against the
     * rule specfied by the composite rule name.
     *
     * @param text The selected data to perform the evaluation on.
     * @param executionContext The Smooks {@link ExecutionContext}.
     *
     * @throws ValidationException
     */
    void validate(final String text, final ExecutionContext executionContext) throws ValidationException
    {
        if(ruleProvider == null) {
            ruleProvider = RuleProviderAccessor.get(appContext, ruleProviderName);
            if(ruleProvider == null) {
                throw new SmooksException("Unknown rule provider '" + ruleProviderName + "'.");
            }
        }

        final RuleEvalResult result = ruleProvider.evaluate(ruleName, text, executionContext);
        logger.info(result);

        if (!result.matched())
        {
            if (onFail == OnFail.FATAL)
            {
                throw new ValidationException("Rule Validation failed : " + result, text, result);
            }

            ValidationResult validationResult = (ValidationResult) FilterResult.getResult(executionContext, ValidationResult.class);
            if(validationResult != null) {
                validationResult.addResult(result, onFail);
            }
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s [rule=%s, onFail=%s]", getClass().getSimpleName(), compositRuleName, onFail);
    }

    @ConfigParam (name="name")
    public void setCompositRuleName(final String compositRuleName)
    {
        this.compositRuleName = compositRuleName;
        this.ruleProviderName = RuleProviderAccessor.parseRuleProviderName(compositRuleName);
        this.ruleName = RuleProviderAccessor.parseRuleName(compositRuleName);
    }

    public String getCompositRuleName()
    {
        return compositRuleName;
    }

    @ConfigParam (defaultVal = "ERROR")
    public void setOnFail(final OnFail onFail)
    {
        this.onFail = onFail;
    }

    public OnFail getOnFail()
    {
        return onFail;
    }

    public Validator setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
        return this;
    }
}
