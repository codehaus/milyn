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
package org.milyn.util;

import org.mvel.TemplateInterpreter;
import org.milyn.assertion.AssertArgument;

import java.util.Map;

/**
 *  <a href="http://mvel.codehaus.org/">MVEL</a> template.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
*/
public class MVELTemplate {

    private String template;
    private TemplateInterpreter interpreter;

    public MVELTemplate(String template) {
        AssertArgument.isNotNullAndNotEmpty(template, "template");
        this.template = template.replace("${", "@{");
    }

    public String getTemplate() {
        return template;
    }

    public String apply(Object contextObject) {
        return (String) TemplateInterpreter.eval(template, contextObject);
    }
}
