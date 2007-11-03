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

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * Element details as described by the SAX even model API.
 * <p/>
 * {@link org.milyn.delivery.sax.SAXElementVisitor} implementations will be passed
 * an instance of this class for each of the 
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXElement implements Serializable {

    private QName name;
    private Attributes attributes;
    private Object cache;

    /**
     * Public constructor.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *                     element has no Namespace URI or if Namespace
     *                     processing is not being performed.
     * @param localName    The local name (without prefix), or the
     *                     empty string if Namespace processing is not being
     *                     performed.
     * @param qName        The qualified name (with prefix), or the
     *                     empty string if qualified names are not available.
     * @param attributes   The attributes attached to the element.  If
     *                     there are no attributes, it shall be an empty
     *                     Attributes object.
     */
    public SAXElement(String namespaceURI, String localName, String qName, Attributes attributes) {
        if(namespaceURI != null && qName != null && !qName.equals("")) {
            String[] qNameTokens = qName.split(":");

            if(qNameTokens.length == 2) {
                name = new QName(namespaceURI.intern(), qNameTokens[1].intern(), qNameTokens[0].intern());
            } else if(localName != null) {
                name = new QName(localName.intern());
            } else {
                name = new QName(qName.intern());
            }
        } else {
            name = new QName(localName.intern());
        }
        this.attributes = attributes;
    }

    /**
     * Get the element naming details.
     * @return Element naming details.
     */
    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    /**
     * Get the element attributes.
     * @return Element attributes.
     */
    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Object getCache() {
        return cache;
    }

    public void setCache(Object cache) {
        this.cache = cache;
    }
}
