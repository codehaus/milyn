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
package org.milyn.delivery.dom.serialize;

import org.w3c.dom.*;
import org.milyn.container.ExecutionContext;
import org.milyn.xml.DomUtils;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.TextType;
import org.milyn.SmooksException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Writer;
import java.io.IOException;

/**
 * {@link ExecutionContext} object serializer.
 * <p/>
 * Outputs an object bound to the {@link ExecutionContext}.  The location of the object (context key)
 * must be specified on the "key" attribute.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ContextObjectSerializationUnit implements SerializationUnit, SAXElementVisitor {

    private static Log logger = LogFactory.getLog(ContextObjectSerializationUnit.class);

    public void writeElementStart(Element element, Writer writer, ExecutionContext executionContext) throws IOException {
        String key = DomUtils.getAttributeValue(element, "key");

        if(key != null) {
            Object object = executionContext.getAttribute(key);

            if(object != null) {
                writer.write(object.toString());
            } else {
                logger.debug("Invalid <context-object> specification at '" + DomUtils.getXPath(element) + "'. No Object instance found on context at '" + key + "'.");
            }
        } else {
            logger.warn("Invalid <context-object> specification at '" + DomUtils.getXPath(element) + "'. 'key' attribute not specified.");
        }
    }

    public void writeElementEnd(Element element, Writer writer, ExecutionContext executionContext) throws IOException {
    }

    public void writeElementText(Text text, Writer writer, ExecutionContext executionContext) throws IOException {
    }

    public void writeElementComment(Comment comment, Writer writer, ExecutionContext executionContext) throws IOException {
    }

    public void writeElementEntityRef(EntityReference entityRef, Writer writer, ExecutionContext executionContext) throws IOException {
    }

    public void writeElementCDATA(CDATASection cdata, Writer writer, ExecutionContext executionContext) throws IOException {
    }

    public void writeElementNode(Node node, Writer writer, ExecutionContext executionContext) throws IOException {
    }

    public boolean writeChildElements() {
        return false;
    }

    /**
     * Utility method for creating a &lt;context-object/&gt; element.
     * @param ownerDocument The owner document.
     * @param key The context key.
     * @return The &lt;context-object/&gt; element.
     */
    public static Element createElement(Document ownerDocument, String key) {
        Element resultElement = ownerDocument.createElement("context-object");
        Comment comment = ownerDocument.createComment(" The actual message payload is set on the associated Smooks ExecutionContext under the key '" + key + "'.  Alternatively, you can use Smooks to serialize the message. ");

        resultElement.setAttribute("key", key);
        resultElement.appendChild(comment);

        return resultElement;
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildText(SAXElement element, String text, TextType textType, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    }
}
