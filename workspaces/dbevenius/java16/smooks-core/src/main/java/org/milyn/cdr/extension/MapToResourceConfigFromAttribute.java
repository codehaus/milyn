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
package org.milyn.cdr.extension;

import java.util.EmptyStackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Map a property value onto the current {@link SmooksResourceConfiguration} based on an
 * element attribute value.
 * <p/>
 * The value is set on the {@link SmooksResourceConfiguration} returned from the top
 * of the {@link org.milyn.cdr.extension.ExtensionContext#getResourceStack() ExtensionContext resourece stack}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MapToResourceConfigFromAttribute implements DOMVisitBefore {

    private static Log logger = LogFactory.getLog(MapToResourceConfigFromAttribute.class);

    @ConfigParam(use=Use.OPTIONAL)
    private String mapTo;

    @ConfigParam
    private String attribute;

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String defaultValue;

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        SmooksResourceConfiguration config;
        String value = DomUtils.getAttributeValue(element, attribute);

        String actualMapTo = mapTo;

        //If no mapTo is set then the attribute value becomes the mapTo value
        if(actualMapTo == null) {
        	actualMapTo = attribute;
        }

        try {
            config = ExtensionContext.getExtensionContext(executionContext).getResourceStack().peek();
        } catch (EmptyStackException e) {
            throw new SmooksException("No SmooksResourceConfiguration available in ExtensionContext stack.  Unable to set SmooksResourceConfiguration property '" + actualMapTo + "' with attribute '" + attribute + "' value '" + value + "'.");
        }

        if (value == null) {
            value = defaultValue;
        }

        if (value == null) {
        	if(logger.isDebugEnabled()) {
        		logger.debug("Not setting property '" + actualMapTo + "' on resource configuration.  Attribute '" + attribute + "' value on element '" + DomUtils.getName(element) + "' is null.  You may need to set a default value in the binding configuration.");
        	}
            return;
        } else {
        	if(logger.isDebugEnabled()) {
        		logger.debug("Setting property '" + actualMapTo + "' on resource configuration to a value of '" + value + "'.");
        	}
        }

        ResourceConfigUtil.setProperty(config, actualMapTo, value, executionContext);
    }
}