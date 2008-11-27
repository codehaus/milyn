/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.delivery.ConfigurationExpander;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.VisitPhase;
import org.milyn.javabean.BeanPopulator;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author maurice
 *
 */
public class EntityPopulator implements ConfigurationExpander {

    private static Log logger = LogFactory.getLog(BeanPopulator.class);

    @ConfigParam(use = Use.OPTIONAL)
    private String beanId;

    @ConfigParam(name="beanClass", use = Use.OPTIONAL)
    private String beanClassName;

    @Config
    private SmooksResourceConfiguration config;

    /*******************************************************************************************************
     *  Common Methods.
     *******************************************************************************************************/

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        // One of "beanId" or "beanClass" must be specified...
        if ( StringUtils.isBlank(beanClassName) && StringUtils.isBlank(beanId)) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'beanId' <param> or 'beanClass' <param> need to be specified.");
        }

        // May need to default the "beanId"...
        if (StringUtils.isEmpty(beanId)) {
            beanId = toBeanId(beanClassName);
            logger.debug("No 'beanId' specified for beanClass '" + beanClassName + "'.  Defaulting beanId to '" + beanId + "'.");
        }

        if(logger.isDebugEnabled()) {
        	final String strBeanClassName =  StringUtils.isEmpty(beanClassName) ? "" : beanClassName;
        	logger.debug("Bean Populator created for [" + beanId + ":" + strBeanClassName + "].");
        }
    }

    public List<SmooksResourceConfiguration> expandConfigurations() throws SmooksConfigurationException {
        final List<SmooksResourceConfiguration> resources = new ArrayList<SmooksResourceConfiguration>();

        buildInstanceCreatorConfig(resources);
        buildInstancePopulatorConfigs(resources);

        return resources;
    }

    private void buildInstanceCreatorConfig(final List<SmooksResourceConfiguration> resources) {
        final SmooksResourceConfiguration resource = (SmooksResourceConfiguration) config.clone();

        // Reset the beanId and beanClass parameters
        resource.removeParameter("beanId");
        resource.setParameter("beanId", beanId);
        resource.removeParameter("beanClass");

        // Reset the resource...
        resource.setResource(EntityPersister.class.getName());

        resources.add(resource);
    }

    private void buildInstancePopulatorConfigs(final List<SmooksResourceConfiguration> resources) {
        final Parameter bindingsParam = config.getParameter("bindings");

        if (bindingsParam != null) {
            final Element bindingsParamElement = bindingsParam.getXml();

            if(bindingsParamElement != null) {
                final NodeList lookupBindings = bindingsParamElement.getElementsByTagName("lookup");

                try {
                    for (int i = 0; lookupBindings != null && i < lookupBindings.getLength(); i++) {
                        resources.add(buildInstanceLookupPopulatorConfig((Element)lookupBindings.item(i)));
                    }
                } catch (final IOException e) {
                    throw new SmooksConfigurationException("Failed to read binding configuration for " + config, e);
                }
            } else {
                logger.error("Sorry, the Javabean populator bindings must be available as XML DOM.  Please configure using XML.");
            }
        }
    }

    private SmooksResourceConfiguration buildInstanceLookupPopulatorConfig(final Element bindingConfig) throws IOException, SmooksConfigurationException {
        SmooksResourceConfiguration resourceConfig;
        String selector;
        String selectorNamespace;
        String property;
        String setterMethod;
        String valueType;
        String entityClass;
        String defaultVal;
        String query;
        String onNoResult;
        String uniqueResult;
        String daoName;

        // Make sure there's is a 'selector attribute...
        selector = DomUtils.getAttributeValue(bindingConfig, "selector");
        if (selector == null) {
            throw new SmooksConfigurationException("Lookup Binding configuration must contain a 'selector' key: " + bindingConfig);
        }

        query = DomUtils.getAttributeValue(bindingConfig, "query");
        if (query == null) {
            throw new SmooksConfigurationException("Lookup Binding configuration must contain a 'query' key: " + bindingConfig);
        }

        entityClass = DomUtils.getAttributeValue(bindingConfig, "entityClass");
        if (entityClass == null) {
            throw new SmooksConfigurationException("Lookup Binding configuration must contain a 'entityClass' key: " + bindingConfig);
        }

        setterMethod = DomUtils.getAttributeValue(bindingConfig, "setterMethod");
        property = DomUtils.getAttributeValue(bindingConfig, "property");

        // Extract the binding config properties from the selector and property values...
        final String attributeNameProperty = getAttributeNameProperty(selector);
        final String selectorProperty = getSelectorProperty(selector);

        // Construct the configuraton...
        resourceConfig = new SmooksResourceConfiguration(selectorProperty, EntityInstanceLookupPopulator.class.getName());
        resourceConfig.setParameter(VisitPhase.class.getSimpleName(), config.getStringParameter(VisitPhase.class.getSimpleName(), VisitPhase.PROCESSING.toString()));
        resourceConfig.setParameter("beanId", beanId);

        resourceConfig.setParameter("query", query);

        resourceConfig.setParameter("entityClass", entityClass);

        if(setterMethod != null) {
            resourceConfig.setParameter("setterMethod", setterMethod);
        }
        if(property != null) {
            resourceConfig.setParameter("property", property);
        }

        if (attributeNameProperty != null && !"".equals( attributeNameProperty.trim() )) {
            // The value is comming out of a property on the target element.
            // If this attribute is not defined, the value will be taken from the element text...
            resourceConfig.setParameter("valueAttributeName", attributeNameProperty);
        }

        valueType = DomUtils.getAttributeValue(bindingConfig, "type");
        resourceConfig.setParameter("type", (valueType != null? valueType :"String"));

        resourceConfig.setTargetProfile(config.getTargetProfile());

        // Set the selector namespace...
        selectorNamespace = DomUtils.getAttributeValue(bindingConfig, "selector-namespace");
        if(selectorNamespace == null) {
            selectorNamespace = config.getSelectorNamespaceURI();
        }
        resourceConfig.setSelectorNamespaceURI(selectorNamespace);

        // Set the default value...
        defaultVal = DomUtils.getAttributeValue(bindingConfig, "default");
        if(defaultVal != null) {
            resourceConfig.setParameter("default", defaultVal);
        }

        // Set the onNoResult value...
        onNoResult = DomUtils.getAttributeValue(bindingConfig, "onNoResult");
        if(defaultVal != null) {
            resourceConfig.setParameter("onNoResult", onNoResult);
        }

        uniqueResult  = DomUtils.getAttributeValue(bindingConfig, "uniqueResult");
        if(uniqueResult != null) {
            resourceConfig.setParameter("uniqueResult", uniqueResult);
        }

        daoName  = DomUtils.getAttributeValue(bindingConfig, "daoName");
        if(daoName != null) {
            resourceConfig.setParameter("daoName", daoName);
        }

        return resourceConfig;
    }

    private String getSelectorProperty(final String selector) {
        final StringBuffer selectorProp = new StringBuffer();
        final String[] selectorTokens = selector.split(" ");

        for (final String selectorToken : selectorTokens) {
            if (!selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken).append(" ");
            }
        }

        return selectorProp.toString().trim();
    }

    private String getAttributeNameProperty(final String selector) {
        final StringBuffer selectorProp = new StringBuffer();
        final String[] selectorTokens = selector.split(" ");

        for (final String selectorToken : selectorTokens) {
            if (selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken.substring(1));
            }
        }

        return selectorProp.toString();
    }

    private String toBeanId(final String beanClazzName) {
        final String[] beanClassNameTokens = beanClazzName.split("\\.");
        final StringBuffer simpleClassName = new StringBuffer(beanClassNameTokens[beanClassNameTokens.length - 1]);

        // Lowercase the first char...
        simpleClassName.setCharAt(0, Character.toLowerCase(simpleClassName.charAt(0)));

        return simpleClassName.toString();
    }
}
