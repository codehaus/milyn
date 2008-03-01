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
import java.util.ArrayList;
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
    private SAXElementVisitorMap defaultVisitorConfig;
    private ExecutionEventListener eventListener;
    private boolean reverseVisitOrderOnVisitAfter;
    private boolean terminateOnVisitorException;

    public SAXHandler(ExecutionContext execContext, Writer writer) {
        this.execContext = execContext;
        this.writer = writer;
        eventListener = execContext.getEventListener();

        deliveryConfig = ((SAXContentDeliveryConfig)execContext.getDeliveryConfig());
        visitorConfigMap = deliveryConfig.getOptimizedVisitorConfig();
        defaultVisitorConfig = visitorConfigMap.get("*");
        if(defaultVisitorConfig == null) {
            SmooksResourceConfiguration resource = new SmooksResourceConfiguration("*", DefaultSAXElementVisitor.class.getName());
            ContentHandlerConfigMap defaultHandlerMapping;

            resource.setDefaultResource(true);
            defaultHandlerMapping = new ContentHandlerConfigMap(new DefaultSAXElementVisitor(), resource);

            defaultVisitorConfig = new SAXElementVisitorMap();
            defaultVisitorConfig.setVisitBefores(new ArrayList<ContentHandlerConfigMap<SAXVisitBefore>>());
            defaultVisitorConfig.getVisitBefores().add(defaultHandlerMapping);
            defaultVisitorConfig.setChildVisitors(new ArrayList<ContentHandlerConfigMap<SAXVisitChildren>>());
            defaultVisitorConfig.getChildVisitors().add(defaultHandlerMapping);
            defaultVisitorConfig.setVisitAfters(new ArrayList<ContentHandlerConfigMap<SAXVisitAfter>>());
            defaultVisitorConfig.getVisitAfters().add(defaultHandlerMapping);
        }

        reverseVisitOrderOnVisitAfter = ParameterAccessor.getBoolParameter(Filter.REVERSE_VISIT_ORDER_ON_VISIT_AFTER, true, execContext.getDeliveryConfig());
        if(execContext.getReportConfiguration() == null) {
            terminateOnVisitorException = ParameterAccessor.getBoolParameter(Filter.TERMINATE_ON_VISITOR_EXCEPTION, true, execContext.getDeliveryConfig());
        } else {
            terminateOnVisitorException = false;
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        SAXElement element;
        boolean isRoot = (currentProcessor == null);

        if(!isRoot) {
            // Push the existing "current" processor onto the stack and create a new current
            // based on this start event...
            element = new SAXElement(namespaceURI, localName, qName, atts, currentProcessor.element);
            element.setWriter(currentProcessor.element.getWriter());
            onChildElement(element);
            elementProcessorStack.push(currentProcessor);
        } else {
            element = new SAXElement(namespaceURI, localName, qName, atts, null);
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
            elementVisitorConfig = defaultVisitorConfig;
        }

        visitBefore(element, elementVisitorConfig);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        List<ContentHandlerConfigMap<SAXVisitAfter>> visitAfterMappings = currentProcessor.elementVisitorConfig.getVisitAfters();

        if(visitAfterMappings == null) {
            visitAfterMappings = defaultVisitorConfig.getVisitAfters();
        }

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

        if(!elementProcessorStack.isEmpty()) {
            currentProcessor = elementProcessorStack.pop();
        }
    }

    private boolean visitBefore(SAXElement element, SAXElementVisitorMap elementVisitorConfig) {
        boolean applied = false;

        // Now create the new "current" processor...
        currentProcessor = new ElementProcessor();
        currentProcessor.element = element;
        currentProcessor.elementVisitorConfig = elementVisitorConfig;

        // And visit it with the targeted visitor...
        List<ContentHandlerConfigMap<SAXVisitBefore>> visitBeforeMappings = currentProcessor.elementVisitorConfig.getVisitBefores();

        if(visitBeforeMappings == null) {
            visitBeforeMappings = defaultVisitorConfig.getVisitBefores();
        }

        for(ContentHandlerConfigMap<SAXVisitBefore> mapping : visitBeforeMappings) {
            try {
                if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                    // Register the targeting event.  No need to register this event again on the visitAfter...
                    if(eventListener != null) {
                        eventListener.onEvent(new ResourceTargetingEvent(element, mapping.getResourceConfig(), VisitSequence.BEFORE));
                        eventListener.onEvent(new ElementVisitEvent(element, mapping.getResourceConfig(), VisitSequence.BEFORE));
                    }
                    applied = true;
                    mapping.getContentHandler().visitBefore(currentProcessor.element, execContext);
                }
            } catch(Throwable t) {
                String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the visitBefore event.";
                processVisitorException(currentProcessor.element, t, mapping.getResourceConfig(), VisitSequence.BEFORE, errorMsg);
            }
            flushCurrentWriter();
        }

        return applied;
    }

    private void onChildElement(SAXElement childElement) {
        List<ContentHandlerConfigMap<SAXVisitChildren>> visitChildMappings = currentProcessor.elementVisitorConfig.getChildVisitors();

        if(visitChildMappings == null) {
            visitChildMappings = defaultVisitorConfig.getChildVisitors();
        }

        for(ContentHandlerConfigMap<SAXVisitChildren> mapping : visitChildMappings) {
            if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                try {
                    mapping.getContentHandler().onChildElement(currentProcessor.element, childElement, execContext);
                } catch(Throwable t) {
                    String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the onChildElement event.";
                    processVisitorException(currentProcessor.element, t, mapping.getResourceConfig(), VisitSequence.AFTER, errorMsg);
                }
                flushCurrentWriter();
            }
        }
    }

    private void visitAfter(ContentHandlerConfigMap<SAXVisitAfter> afterMapping) {
        try {
            if(afterMapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                if(eventListener != null) {
                    eventListener.onEvent(new ElementVisitEvent(currentProcessor.element, afterMapping.getResourceConfig(), VisitSequence.AFTER));
                }
                afterMapping.getContentHandler().visitAfter(currentProcessor.element, execContext);
            }
        } catch(Throwable t) {
            String errorMsg = "Error in '" + afterMapping.getContentHandler().getClass().getName() + "' while processing the visitAfter event.";
            processVisitorException(currentProcessor.element, t, afterMapping.getResourceConfig(), VisitSequence.AFTER, errorMsg);
        }
        flushCurrentWriter();
    }

    private SAXText textWrapper = new SAXText();
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(currentProcessor != null) {
            List<ContentHandlerConfigMap<SAXVisitChildren>> visitChildMappings = currentProcessor.elementVisitorConfig.getChildVisitors();

            if(visitChildMappings == null) {
                visitChildMappings = defaultVisitorConfig.getChildVisitors();
            }

            for(ContentHandlerConfigMap<SAXVisitChildren> mapping : visitChildMappings) {
                try {
                    if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                        textWrapper.setText(ch, start, length, currentTextType);
                        mapping.getContentHandler().onChildText(currentProcessor.element, textWrapper, execContext);
                    }
                } catch(Throwable t) {
                    String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the onChildText event.";
                    processVisitorException(currentProcessor.element, t, mapping.getResourceConfig(), VisitSequence.AFTER, errorMsg);
                }
                flushCurrentWriter();
            }
        }
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
        private SAXElement element;
        public SAXElementVisitorMap elementVisitorConfig;
    }

    private void processVisitorException(SAXElement element, Throwable error, SmooksResourceConfiguration resourceConfig, VisitSequence visitSequence, String errorMsg) throws SmooksException {
        if (eventListener != null) {
            eventListener.onEvent(new ElementVisitEvent(element, resourceConfig, visitSequence, error));
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
}
