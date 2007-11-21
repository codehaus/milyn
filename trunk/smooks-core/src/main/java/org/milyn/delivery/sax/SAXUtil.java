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
package org.milyn.delivery.sax;

import org.xml.sax.Attributes;

/**
 * SAX utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class SAXUtil {

    /**
     * Get the value of the named attribute.
     * @param attributeName The attribute name.
     * @param attributes The attribute list.
     * @return The attribute value, or an empty string if not available (as with DOM).
     */
    public static String getAttribute(String attributeName, Attributes attributes) {
        int attribCount = attributes.getLength();

        for(int i = 0; i < attribCount; i++) {
            String attribName = attributes.getLocalName(i);
            if(attribName.equalsIgnoreCase(attributeName)) {
                return attributes.getValue(i);
            }
        }

        return "";
    }

    public static String getXPath(SAXElement element) {
        // TODO
        return "SAX XPath feature not implemented!";
    }
}
