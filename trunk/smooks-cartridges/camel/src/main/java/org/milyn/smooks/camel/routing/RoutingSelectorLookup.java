/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.smooks.camel.routing;

import java.util.List;

import org.milyn.SmooksException;
import org.milyn.cdr.extension.ExtensionContext;
import org.milyn.cdr.ConfigSearch;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.javabean.BeanInstanceCreator;
import org.w3c.dom.Element;

/**
 * If the routeOnElement config is not configured, lookup the selector from the
 * bean creator config and use that instead.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class RoutingSelectorLookup implements DOMVisitBefore
{

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException
    {
        ExtensionContext extensionContext = ExtensionContext.getExtensionContext(executionContext);

        // The current config on the stack must be a BeanRouter as per the
        // namespace config mapping...
        SmooksResourceConfiguration routingConfig = (SmooksResourceConfiguration) extensionContext.getResourceStack()
                .peek();
        String beanId = routingConfig.getStringParameter("beanId");

        // If the selector is not configured... lookup the bean creator for the
        // bean and set the selector
        // for the BeanRouter to be the same...
        if (routingConfig.getSelector() == null || routingConfig.getSelector().equals("none"))
        {
            List<SmooksResourceConfiguration> creatorConfigs = extensionContext.lookupResource(new ConfigSearch()
                    .resource(BeanInstanceCreator.class.getName()).param("beanId", beanId));

            if (creatorConfigs.isEmpty())
            {
                throw new SmooksConfigurationException(
                        "Camel route for beanId '"
                                + beanId
                                + "' must be reordered after <jb:bean> config, or the 'routeOnElement' attribute must be configured.  Unable to locate <jb:bean> binding config to lookup routing event.");
            } else if (creatorConfigs.size() > 1)
            {
                throw new SmooksConfigurationException(
                        "Camel route for beanId '"
                                + beanId
                                + "' must have its 'routeOnElement' attribute configured.  Multiple <jb:bean> binding configs available for this bean.");
            }

            // Copy the selector...
            routingConfig.setSelector(creatorConfigs.get(0).getSelector());
        }
    }
}