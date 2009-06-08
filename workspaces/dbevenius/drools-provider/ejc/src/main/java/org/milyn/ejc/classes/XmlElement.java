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
package org.milyn.ejc.classes;

/**
 * XmlElement holds information about which element-name the edi-mapping-file will have.
 * @see org.milyn.ejc.classes.JClass
 * @see org.milyn.ejc.classes.JAttribute
 * @see org.milyn.ejc.classes.JJavaClass
 */
public class XmlElement {
    private String xmlElementName;

    public String getXmlElementName() {
        return xmlElementName;
    }

    public void setXmlElementName(String xmlElementName) {
        this.xmlElementName = xmlElementName;
    }
}
