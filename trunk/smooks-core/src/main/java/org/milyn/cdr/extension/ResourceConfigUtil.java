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

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.ConfigParam;

/**
 * Resource Configuration Extension utility class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class ResourceConfigUtil {

    public static void setProperty(SmooksResourceConfiguration config, String setOn, String value, ExecutionContext executionContext) throws SmooksException {
        if(setOn.equals("selector")) {
            config.setSelector(value);
        } else if(setOn.equals("selectorNamespaceURI(")) {
            config.setSelectorNamespaceURI(value);
        } else if(setOn.equals("defaultResource")) {
            config.setDefaultResource(Boolean.parseBoolean(value));
        } else if(setOn.equals("resourceType")) {
            config.setResourceType(value);
        } else if(setOn.equals("targetProfile")) {
            config.setTargetProfile(value);
        } else if(setOn.equals("conditionRef")) {
            ExtensionContext execentionContext = ExtensionContext.getExtensionContext(executionContext);
            config.setConditionEvaluator(execentionContext.getXmlConfigDigester().getConditionEvaluator(value));
        } else {
            config.setParameter(setOn, value);
        }
    }

    public static void unsetProperty(SmooksResourceConfiguration config, String property) {
        if(property.equals("selector")) {
            config.setSelector(null);
        } else if(property.equals("selectorNamespaceURI(")) {
            config.setSelectorNamespaceURI(null);
        } else if(property.equals("defaultResource")) {
            config.setDefaultResource(false);
        } else if(property.equals("resourceType")) {
            config.setResourceType(null);
        } else if(property.equals("targetProfile")) {
            config.setTargetProfile(null);
        } else if(property.equals("conditionRef")) {
            config.setConditionEvaluator(null);
        } else {
            config.removeParameter(property);
        }
    }

    public static void mapProperty(SmooksResourceConfiguration fromConfig, String fromProperty, SmooksResourceConfiguration toConfig, String toProperty, String defaultValue, ExecutionContext executionContext) throws SmooksException {
        if(fromProperty.equals("selector")) {
            setProperty(toConfig, toProperty, fromConfig.getSelector(), executionContext);
        } else if(fromProperty.equals("selectorNamespaceURI(")) {
            setProperty(toConfig, toProperty, fromConfig.getSelectorNamespaceURI(), executionContext);
        } else if(fromProperty.equals("defaultResource")) {
            setProperty(toConfig, toProperty, Boolean.toString(fromConfig.isDefaultResource()), executionContext);
        } else if(fromProperty.equals("resourceType")) {
            setProperty(toConfig, toProperty, fromConfig.getResourceType(), executionContext);
        } else if(fromProperty.equals("targetProfile")) {
            setProperty(toConfig, toProperty, fromConfig.getTargetProfile(), executionContext);
        } else if(fromProperty.equals("conditionRef")) {
            toConfig.setConditionEvaluator(fromConfig.getConditionEvaluator());
        } else {
            setProperty(toConfig, toProperty, fromConfig.getStringParameter(fromProperty, defaultValue), executionContext);
        }
    }
}