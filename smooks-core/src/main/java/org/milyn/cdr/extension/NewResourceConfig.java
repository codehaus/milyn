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
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.w3c.dom.Element;

/**
 * Create a new {@link SmooksResourceConfiguration}.
 * <p/>
 * The new {@link SmooksResourceConfiguration} is added to the {@link ExtensionContext}. 
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NewResourceConfig implements DOMElementVisitor {

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String resource;

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        ExtensionContext execentionContext = ExtensionContext.getExtensionContext(executionContext);

        config.setResource(resource);
        config.setConditionEvaluator(execentionContext.getDefaultConditionEvaluator());

        execentionContext.addResource(config);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        ExtensionContext.getExtensionContext(executionContext).getResourceStack().pop();
    }
}
