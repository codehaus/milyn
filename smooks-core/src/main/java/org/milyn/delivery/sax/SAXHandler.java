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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.xml.DocType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Stack;

/**
 * SAX Handler.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXHandler extends DefaultHandler2 {

    private static Log logger = LogFactory.getLog(SAXHandler.class);
    private ExecutionContext execContext;
    private Writer writer;
    private Stack<ElementProcessor> elementProcessorStack = new Stack<ElementProcessor>();
    private ElementProcessor currentProcessor = null;
    private TextType currentTextType = TextType.TEXT;
    private ContentHandlerConfigMapTable<SAXElementVisitor> saxVisitors;
    private SAXElementVisitor defaultVisitor;

    public SAXHandler(ExecutionContext execContext, Writer writer) {
        this.execContext = execContext;
        this.writer = writer;
        saxVisitors = ((SAXContentDeliveryConfig)execContext.getDeliveryConfig()).getSaxVisitors();

        List<ContentHandlerConfigMap<SAXElementVisitor>> defaultMappings = saxVisitors.getMappings("*");
        if(defaultMappings != null && !defaultMappings.isEmpty()) {
            defaultVisitor = defaultMappings.get(0).getContentHandler();
        } else {
            defaultVisitor = new DefaultSAXElementVisitor();
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        SAXElement element;

        if(currentProcessor != null) {
            // Push the existing "current" processor onto the stack and create a new current
            // based on this start event...
            element = new SAXElement(namespaceURI, localName, qName, atts, currentProcessor.element);
            element.setWriter(currentProcessor.element.getWriter());
            try {
                currentProcessor.visitor.onChildElement(currentProcessor.element, element, execContext);
            } catch(Throwable t) {
                logger.error("Error in '" + currentProcessor.visitor.getClass().getName() + "' while processing the onChildElement event.", t);
            }
            elementProcessorStack.push(currentProcessor);
        } else {
            element = new SAXElement(namespaceURI, localName, qName, atts, null);
            element.setWriter(writer);
        }

        List<ContentHandlerConfigMap<SAXElementVisitor>> mappings = saxVisitors.getMappings(element.getName().getLocalPart().toLowerCase());

        // Now create the new "current" processor...
        currentProcessor = new ElementProcessor();
        currentProcessor.element = element;
        if(mappings != null && !mappings.isEmpty()) {
            for(ContentHandlerConfigMap<SAXElementVisitor> mapping : mappings) {
                if(mapping.getResourceConfig().isTargetedAtElement(element)) {
                    currentProcessor.visitor = mapping.getContentHandler();
                    break;
                }
            }
        }
        if (currentProcessor.visitor == null) {
            currentProcessor.visitor = defaultVisitor;
        }

        // And visit it with the targeted visitor...
        try {
            currentProcessor.visitor.visitBefore(currentProcessor.element, execContext);
        } catch(Throwable t) {
            logger.error("Error in '" + currentProcessor.visitor.getClass().getName() + "' while processing the visitBefore event.", t);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            currentProcessor.visitor.visitAfter(currentProcessor.element, execContext);
        } catch(Throwable t) {
            logger.error("Error in '" + currentProcessor.visitor.getClass().getName() + "' while processing the visitAfter event.", t);
        }
        if(!elementProcessorStack.isEmpty()) {
            currentProcessor = elementProcessorStack.pop();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if(currentProcessor != null) {
            try {
                currentProcessor.visitor.onChildText(currentProcessor.element, new String(ch, start, length), currentTextType, execContext);
            } catch(Throwable t) {
                logger.error("Error in '" + currentProcessor.visitor.getClass().getName() + "' while processing the onChildText event.", t);
            }
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        characters(ch, start, length);
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        currentTextType = TextType.COMMENT;
        characters(ch, start, length);
        currentTextType = TextType.TEXT;
    }

    public void startCDATA() throws SAXException {
        currentTextType = TextType.CDATA;
    }

    public void endCDATA() throws SAXException {
        currentTextType = TextType.TEXT;
    }

    public void startEntity(String name) throws SAXException {
        currentTextType = TextType.ENTITY;
    }

    public void endEntity(String name) throws SAXException {
        currentTextType = TextType.TEXT;
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        DocType.setDocType(name, publicId, systemId, null, execContext);

        if(writer != null) {
            DocType.DocumentTypeData docTypeData = DocType.getDocType(execContext);
            if(docTypeData != null) {
                try {
                    DocType.serializeDoctype(docTypeData, writer);
                } catch (IOException e) {
                    throw new SAXException("Failed to serialize DOCTYPE.");
                }
            }
        }
    }

    private class ElementProcessor {
        private SAXElement element;
        private SAXElementVisitor visitor;
    }
}
