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
import org.milyn.cdr.ParameterAccessor;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.delivery.Filter;
import org.milyn.delivery.VisitSequence;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.report.AbstractReportGenerator;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.ElementVisitEvent;
import org.milyn.event.types.ResourceTargetingEvent;
import org.milyn.xml.DocType;
import org.milyn.SmooksException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Stack;
import java.util.Map;

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
    private SAXContentDeliveryConfig deliveryConfig;
    private Map<String, SAXElementVisitorMap> visitorConfigMap;
    private SAXElementVisitorMap globalVisitorConfig;
    private boolean defaultSerializationOn;
    private SAXElementVisitor defaultSerializer = new DefaultSAXElementSerializer();
    private ContentHandlerConfigMap defaultSerializerMapping;
    private ExecutionEventListener eventListener;
    private boolean reverseVisitOrderOnVisitAfter;
    private boolean terminateOnVisitorException;

    public SAXHandler(ExecutionContext executionContext, Writer writer) {
        this.execContext = executionContext;
        this.writer = writer;
        eventListener = executionContext.getEventListener();

        deliveryConfig = ((SAXContentDeliveryConfig)executionContext.getDeliveryConfig());
        visitorConfigMap = deliveryConfig.getOptimizedVisitorConfig();
        globalVisitorConfig = visitorConfigMap.get("*");

        // Configure the default handler mapping...
        SmooksResourceConfiguration resource = new SmooksResourceConfiguration("*", DefaultSAXElementSerializer.class.getName());
        resource.setDefaultResource(true);

        defaultSerializationOn = ParameterAccessor.getBoolParameter(Filter.DEFAULT_SERIALIZATION_ON, true, executionContext.getDeliveryConfig());
        defaultSerializerMapping = new ContentHandlerConfigMap(new DefaultSAXElementSerializer(), resource);

        reverseVisitOrderOnVisitAfter = ParameterAccessor.getBoolParameter(Filter.REVERSE_VISIT_ORDER_ON_VISIT_AFTER, true, executionContext.getDeliveryConfig());
        if(!(executionContext.getEventListener() instanceof AbstractReportGenerator)) {
            terminateOnVisitorException = ParameterAccessor.getBoolParameter(Filter.TERMINATE_ON_VISITOR_EXCEPTION, true, executionContext.getDeliveryConfig());
        } else {
            terminateOnVisitorException = false;
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        WriterManagedSAXElement element;
        boolean isRoot = (currentProcessor == null);

        if(!isRoot) {
            // Push the existing "current" processor onto the stack and create a new current
            // based on this start event...
            element = new WriterManagedSAXElement(namespaceURI, localName, qName, atts, currentProcessor.element);
            element.setWriter(currentProcessor.element.getWriter());
            onChildElement(element);
            elementProcessorStack.push(currentProcessor);
        } else {
            element = new WriterManagedSAXElement(namespaceURI, localName, qName, atts, null);
            element.setWriter(writer);
        }

        // Register the "presence" of the element...
        if(eventListener != null) {
            eventListener.onEvent(new ElementPresentEvent(element));
        }

        String elementName = element.getName().getLocalPart();
        SAXElementVisitorMap elementVisitorConfig;
        if(isRoot) {
            elementVisitorConfig = deliveryConfig.getCombinedOptimizedConfig(new String[] {SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR, elementName});
        } else {
            elementVisitorConfig = visitorConfigMap.get(elementName.toLowerCase());
        }

        if(elementVisitorConfig == null) {
            elementVisitorConfig = globalVisitorConfig;
        }

        visitBefore(element, elementVisitorConfig);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if(currentProcessor.elementVisitorConfig != null) {
            List<ContentHandlerConfigMap<SAXVisitAfter>> visitAfterMappings = currentProcessor.elementVisitorConfig.getVisitAfters();

            if(visitAfterMappings != null) {
                if(reverseVisitOrderOnVisitAfter) {
                    // We work through the mappings in reverse order on the end element event...
                    int mappingCount = visitAfterMappings.size();
                    ContentHandlerConfigMap<SAXVisitAfter> mapping;

                    for(int i = mappingCount - 1; i >= 0; i--) {
                        mapping = visitAfterMappings.get(i);
                        visitAfter(mapping);
                    }
                } else {
                    for(ContentHandlerConfigMap<SAXVisitAfter> mapping : visitAfterMappings) {
                        visitAfter(mapping);
                    }
                }
            }
        }

        if(applyDefaultSerialization()) {
            try {
                defaultSerializer.visitAfter(currentProcessor.element, execContext);
                flushCurrentWriter();
                if(eventListener != null) {
                    eventListener.onEvent(new ElementVisitEvent(currentProcessor.element, defaultSerializerMapping, VisitSequence.AFTER));
                }
            } catch (IOException e) {
                throw new SmooksException("Unexpected exception applying defaultSerializer.", e);
            }
        }

        if(!elementProcessorStack.isEmpty()) {
            currentProcessor = elementProcessorStack.pop();
        }
    }

    private void visitBefore(WriterManagedSAXElement element, SAXElementVisitorMap elementVisitorConfig) {
        
        // Now create the new "current" processor...
        currentProcessor = new ElementProcessor();
        currentProcessor.element = element;
        currentProcessor.elementVisitorConfig = elementVisitorConfig;

        if(currentProcessor.elementVisitorConfig != null) {
            // And visit it with the targeted visitor...
            List<ContentHandlerConfigMap<SAXVisitBefore>> visitBeforeMappings = currentProcessor.elementVisitorConfig.getVisitBefores();

            if(visitBeforeMappings != null) {
                for(ContentHandlerConfigMap<SAXVisitBefore> mapping : visitBeforeMappings) {
                    try {
                        if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                            mapping.getContentHandler().visitBefore(currentProcessor.element, execContext);
                            // Register the targeting event.  No need to register this event again on the visitAfter...
                            if(eventListener != null) {
                                eventListener.onEvent(new ResourceTargetingEvent(element, mapping.getResourceConfig(), VisitSequence.BEFORE));
                                eventListener.onEvent(new ElementVisitEvent(element, mapping, VisitSequence.BEFORE));
                            }
                        }
                    } catch(Throwable t) {
                        String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the visitBefore event.";
                        processVisitorException(currentProcessor.element, t, mapping, VisitSequence.BEFORE, errorMsg);
                    }
                    flushCurrentWriter();
                }
            }
        }

        if(applyDefaultSerialization()) {
            try {
                defaultSerializer.visitBefore(currentProcessor.element, execContext);
                flushCurrentWriter();
                if(eventListener != null) {
                    eventListener.onEvent(new ElementVisitEvent(element, defaultSerializerMapping, VisitSequence.BEFORE));
                }
            } catch (IOException e) {
                throw new SmooksException("Unexpected exception applying defaultSerializer.", e);
            }
        }
    }

    private void onChildElement(SAXElement childElement) {
        if(currentProcessor.elementVisitorConfig != null) {
            List<ContentHandlerConfigMap<SAXVisitChildren>> visitChildMappings = currentProcessor.elementVisitorConfig.getChildVisitors();

            if(visitChildMappings != null) {
                for(ContentHandlerConfigMap<SAXVisitChildren> mapping : visitChildMappings) {
                    if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                        try {
                            mapping.getContentHandler().onChildElement(currentProcessor.element, childElement, execContext);
                        } catch(Throwable t) {
                            String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the onChildElement event.";
                            processVisitorException(currentProcessor.element, t, mapping, VisitSequence.AFTER, errorMsg);
                        }
                        flushCurrentWriter();
                    }
                }
            }
        }

        if(applyDefaultSerialization()) {
            try {
                defaultSerializer.onChildElement(currentProcessor.element, childElement, execContext);
                flushCurrentWriter();
            } catch (IOException e) {
                throw new SmooksException("Unexpected exception applying defaultSerializer.", e);
            }
        }
    }

    private void visitAfter(ContentHandlerConfigMap<SAXVisitAfter> afterMapping) {
        try {
            if(afterMapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                afterMapping.getContentHandler().visitAfter(currentProcessor.element, execContext);
                if(eventListener != null) {
                    eventListener.onEvent(new ElementVisitEvent(currentProcessor.element, afterMapping, VisitSequence.AFTER));
                }
            }
        } catch(Throwable t) {
            String errorMsg = "Error in '" + afterMapping.getContentHandler().getClass().getName() + "' while processing the visitAfter event.";
            processVisitorException(currentProcessor.element, t, afterMapping, VisitSequence.AFTER, errorMsg);
        }
        flushCurrentWriter();
    }

    private SAXText textWrapper = new SAXText();
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(currentProcessor != null) {
            if(currentProcessor.elementVisitorConfig != null) {
                List<ContentHandlerConfigMap<SAXVisitChildren>> visitChildMappings = currentProcessor.elementVisitorConfig.getChildVisitors();

                if(visitChildMappings != null) {
                    for(ContentHandlerConfigMap<SAXVisitChildren> mapping : visitChildMappings) {
                        try {
                            if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                                textWrapper.setText(ch, start, length, currentTextType);
                                mapping.getContentHandler().onChildText(currentProcessor.element, textWrapper, execContext);
                            }
                        } catch(Throwable t) {
                            String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the onChildText event.";
                            processVisitorException(currentProcessor.element, t, mapping, VisitSequence.AFTER, errorMsg);
                        }
                        flushCurrentWriter();
                    }
                }
            }

            if(applyDefaultSerialization()) {
                try {
                    textWrapper.setText(ch, start, length, currentTextType);
                    defaultSerializer.onChildText(currentProcessor.element, textWrapper, execContext);
                    flushCurrentWriter();
                } catch (IOException e) {
                    throw new SmooksException("Unexpected exception applying defaultSerializer.", e);
                }
            }
        }
    }

    private boolean applyDefaultSerialization() {
        if(!defaultSerializationOn) {
            return false;
        }

        return (currentProcessor.element.writerOwner == defaultSerializer || currentProcessor.element.writerOwner == null);
    }

    private void flushCurrentWriter() {
        Writer writer = currentProcessor.element.getWriter();
        if(writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                logger.error("Error flushing writer.", e);
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
        private WriterManagedSAXElement element;
        public SAXElementVisitorMap elementVisitorConfig;
    }

    private void processVisitorException(SAXElement element, Throwable error, ContentHandlerConfigMap configMapping, VisitSequence visitSequence, String errorMsg) throws SmooksException {
        if (eventListener != null) {
            eventListener.onEvent(new ElementVisitEvent(element, configMapping, visitSequence, error));
        }

        if(terminateOnVisitorException) {
            if(error instanceof SmooksException) {
                throw (SmooksException) error;
            } else {
                throw new SmooksException(errorMsg, error);
            }
        } else {
            logger.error(errorMsg, error);
        }
    }

    private class WriterManagedSAXElement extends SAXElement {

        private SAXVisitor writerOwner;

        public WriterManagedSAXElement(String namespaceURI, String localName, String qName, Attributes attributes, SAXElement parent) {
            super(namespaceURI, localName, qName, attributes, parent);
        }

        public Writer getWriter(SAXVisitor visitor) throws SAXWriterAccessException {
            if(writerOwner == null) {
                writerOwner = visitor;
                return super.getWriter(visitor);
            }
            if(visitor == writerOwner) {
                return super.getWriter(visitor);
            }

            throwSAXWriterAccessException(visitor);
            return null;
        }

        public void setWriter(Writer writer, SAXVisitor visitor) throws SAXWriterAccessException {
            if(writerOwner == null) {
                writerOwner = visitor;
                super.setWriter(writer, visitor);
            }
            if(visitor == writerOwner) {
                super.setWriter(writer, visitor);
            }

            throwSAXWriterAccessException(visitor);
        }

        private Writer getWriter() {
            return super.getWriter(null);
        }

        private void setWriter(Writer writer) {
            super.setWriter(writer, null);
        }

        private void throwSAXWriterAccessException(SAXVisitor visitor) {
            throw new SAXWriterAccessException("Illegal access to the element writer for element '" + this + "' by SAX visitor '" + visitor.getClass().getName() + "'.  Writer already acquired by SAX visitor '" + writerOwner.getClass().getName() + "'.  See SAXElement javadocs (http://milyn.codehaus.org/Smooks).  Change Smooks visitor resource configuration.");
        }
    }
}
