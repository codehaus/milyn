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
 * Abstract resource property mapper class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
abstract class MapToResourceConfig {

    @ConfigParam
    private String mapTo;

    public String getMapTo() {
        return mapTo;
    }

    public void setProperty(SmooksResourceConfiguration config, String value, ExecutionContext executionContext) throws SmooksException {
        if(mapTo.equals("selector")) {
            config.setSelector(value);
        } else if(mapTo.equals("selectorNamespaceURI(")) {
            config.setSelectorNamespaceURI(value);
        } else if(mapTo.equals("defaultResource")) {
            config.setDefaultResource(Boolean.parseBoolean(value));
        } else if(mapTo.equals("resourceType")) {
            config.setResourceType(value);
        } else if(mapTo.equals("targetProfile")) {
            config.setTargetProfile(value);
        } else if(mapTo.equals("conditionRef")) {
            ExtensionContext execentionContext = ExtensionContext.getExtensionContext(executionContext);
            config.setConditionEvaluator(execentionContext.getXmlConfigDigester().getConditionEvaluator(value));
        } else {
            config.setParameter(mapTo, value);
        }
    }
}