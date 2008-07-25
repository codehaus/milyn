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
package org.milyn.javabean.ext;

import org.milyn.SmooksException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.extension.ExtensionContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.DataDecoder;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.UUID;

/**
 * Selector Attribute Extractor.
 * <p/>
 * Some binding selectors can be of the form "order/customer/@customerNumber", where the
 * last token in the selector represents an attribute on the customer element.  This
 * extension visitor breaks up this selector into "order/customer" plus a new property
 * on the BeanInstancePopulator config named "valueAttributeName" containing a value of
 * "customerNumber".
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SelectorAttributeExtractor implements DOMVisitBefore {

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        ExtensionContext extensionContext = ExtensionContext.getExtensionContext(executionContext);
        SmooksResourceConfiguration populatorConfig = extensionContext.getResourceStack().peek();
        String selector = populatorConfig.getSelector();
        String valueAttributeName = getAttributeNameProperty(selector);

        if(valueAttributeName != null && !valueAttributeName.trim().equals("")) {
            populatorConfig.setSelector(getSelectorProperty(selector));
            populatorConfig.setParameter("valueAttributeName", valueAttributeName);
        }
    }

    public static String getSelectorProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = SmooksResourceConfiguration.parseSelector(selector);

        for (String selectorToken : selectorTokens) {
            if (!selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken).append(" ");
            }
        }

        return selectorProp.toString().trim();
    }

    public static String getAttributeNameProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = SmooksResourceConfiguration.parseSelector(selector);

        for (String selectorToken : selectorTokens) {
            if (selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken.substring(1));
            }
        }

        return selectorProp.toString();
    }
}