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
import org.milyn.SmooksException;
import org.milyn.cdr.ParameterAccessor;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.*;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.report.AbstractReportGenerator;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.ElementVisitEvent;
import org.milyn.event.types.ResourceTargetingEvent;
import org.milyn.util.Cleanable;
import org.milyn.xml.DocType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

/**
 * Readonly SAX Handler.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ReadOnlySAXHandler extends DefaultHandler2 implements Cleanable {

    private static Log logger = LogFactory.getLog(ReadOnlySAXHandler.class);
    private ExecutionContext execContext;
    private ElementProcessor currentProcessor = null;
    private ElementProcessor processorStackRoot = new ElementProcessor();
    private TextType currentTextType = TextType.TEXT;
    private SAXContentDeliveryConfig deliveryConfig;
    private Map<String, SAXElementVisitorMap> visitorConfigMap;
    private SAXElementVisitorMap globalVisitorConfig;
    private boolean rewriteEntities = true;
    private boolean maintainElementStack;
    private ExecutionEventListener eventListener;
    private boolean reverseVisitOrderOnVisitAfter;
    private boolean terminateOnVisitorException;
    private ExecutionLifecycleCleanableList cleanupList;
    private DynamicSAXElementVisitorList dynamicVisitorList;
    private StringBuilder cdataNodeBuilder = new StringBuilder();

    public ReadOnlySAXHandler(ExecutionContext executionContext) {
        this.execContext = executionContext;
        eventListener = executionContext.getEventListener();

        deliveryConfig = ((SAXContentDeliveryConfig)executionContext.getDeliveryConfig());
        visitorConfigMap = deliveryConfig.getOptimizedVisitorConfig();
        
        SAXElementVisitorMap starVisitorConfigs = visitorConfigMap.get("*");
        SAXElementVisitorMap starStarVisitorConfigs = visitorConfigMap.get("**");
        
        if(starVisitorConfigs != null) {
        	globalVisitorConfig = starVisitorConfigs.merge(starStarVisitorConfigs);
        } else {
        	globalVisitorConfig = starStarVisitorConfigs;
        }

        // Configure the default handler mapping...
        SmooksResourceConfiguration resource = new SmooksResourceConfiguration("*", DefaultSAXElementSerializer.class.getName());
        resource.setDefaultResource(true);

        rewriteEntities = ParameterAccessor.getBoolParameter(Filter.ENTITIES_REWRITE, true, execContext.getDeliveryConfig());
        maintainElementStack = ParameterAccessor.getBoolParameter(Filter.MAINTAIN_ELEMENT_STACK, true, executionContext.getDeliveryConfig());

        reverseVisitOrderOnVisitAfter = ParameterAccessor.getBoolParameter(Filter.REVERSE_VISIT_ORDER_ON_VISIT_AFTER, true, executionContext.getDeliveryConfig());
        if(!(executionContext.getEventListener() instanceof AbstractReportGenerator)) {
            terminateOnVisitorException = ParameterAccessor.getBoolParameter(Filter.TERMINATE_ON_VISITOR_EXCEPTION, true, executionContext.getDeliveryConfig());
        } else {
            terminateOnVisitorException = false;
        }

        cleanupList = new ExecutionLifecycleCleanableList(executionContext);

        dynamicVisitorList = new DynamicSAXElementVisitorList(executionContext, cleanupList);
    }

    public void clean() {
        try {
            cleanupList.cleanup();
        } finally {
            VisitorConfigMap.execCleanables(deliveryConfig.getExecCleanables(), execContext);
        }
    }    

	public SAXElementVisitorMap getElementVisitorConfig(String elementName, boolean isRoot) {
		SAXElementVisitorMap elementVisitorConfig;

		if(isRoot) {
            elementVisitorConfig = deliveryConfig.getCombinedOptimizedConfig(new String[] {SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR, elementName});
        } else {
            elementVisitorConfig = visitorConfigMap.get(elementName);
        }
		
		return elementVisitorConfig;
	}

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        boolean isRoot = (currentProcessor == null);
        SAXElementVisitorMap elementVisitorConfig;
        QName elementQName;
        String elementName;

        elementQName = SAXElement.toQName(namespaceURI, localName, qName);
        elementName = elementQName.getLocalPart();

        elementVisitorConfig = getElementVisitorConfig(elementName.toLowerCase(), isRoot);

        startElement(elementQName, SAXElement.copyAttributes(atts), elementVisitorConfig);
    }

	public void startElement(QName elementQName, Attributes atts, SAXElementVisitorMap elementVisitorConfig) {
		SAXElement element;
		
		if(elementVisitorConfig == null || elementVisitorConfig.isBlank) {
            elementVisitorConfig = globalVisitorConfig;
        }

        if(!maintainElementStack && elementVisitorConfig == null) {
        	// Drop down a level in the current processor stack...
        	moveDownCurrentProcessorStack();

        	currentProcessor.isNullProcessor = true;
            // Register the "presence" of the element...
            if(eventListener != null) {
                eventListener.onEvent(new ElementPresentEvent(new SAXElement(elementQName, atts, currentProcessor.element)));
            }
        } else {
            if(currentProcessor != null && currentProcessor.element != null) {
                // Push the existing "current" processor onto the stack and create a new current
                // based on this start event...
                element = new SAXElement(elementQName, atts, currentProcessor.element);
                onChildElement(element);
            } else {
                element = new SAXElement(elementQName, atts, null);
            }

            // Register the "presence" of the element...
            if(eventListener != null) {
                eventListener.onEvent(new ElementPresentEvent(element));
            }

            visitBefore(element, elementVisitorConfig);
        }
	}

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	endElement();
    }
    
    public void endElement() throws SAXException {
        // Apply the dynamic visitors...
        List<SAXVisitAfter> dynamicVisitAfters = dynamicVisitorList.getVisitAfters();
        if(!dynamicVisitAfters.isEmpty()) {
            for (SAXVisitAfter dynamicVisitAfter : dynamicVisitAfters) {
                try {
                    dynamicVisitAfter.visitAfter(currentProcessor.element, execContext);
                } catch(Throwable t) {
                    String errorMsg = "Error in '" + dynamicVisitAfter.getClass().getName() + "' while processing the visitAfter event.";
                    processVisitorException(t, errorMsg);
                }
            }
        }

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
                    int mappingCount = visitAfterMappings.size();
                    ContentHandlerConfigMap<SAXVisitAfter> mapping;

                    for(int i = 0; i < mappingCount; i++) {
                        mapping = visitAfterMappings.get(i);
                        visitAfter(mapping);
                    }
                }
            }
        }

        // Process cleanables after applying all the visit afters...
        if(currentProcessor.elementVisitorConfig != null) {
            List<ContentHandlerConfigMap<VisitLifecycleCleanable>> visitCleanables = currentProcessor.elementVisitorConfig.getVisitCleanables();

            if(visitCleanables != null) {
                int mappingCount = visitCleanables.size();
                ContentHandlerConfigMap<VisitLifecycleCleanable> mapping;

                for(int i = 0; i < mappingCount; i++) {
                    mapping = visitCleanables.get(i);
                    mapping.getContentHandler().executeVisitLifecycleCleanup(execContext);
                }
            }
        }

        currentProcessor = currentProcessor.parentProcessor;
    }

    private void visitBefore(SAXElement element, SAXElementVisitorMap elementVisitorConfig) {

    	// Drop down a level in the current processor stack...
    	moveDownCurrentProcessorStack();
    	
    	currentProcessor.element = element;
    	currentProcessor.elementVisitorConfig = elementVisitorConfig;    	
    	
        if(currentProcessor.elementVisitorConfig != null) {
            // And visit it with the targeted visitor...
            List<ContentHandlerConfigMap<SAXVisitBefore>> visitBeforeMappings = currentProcessor.elementVisitorConfig.getVisitBefores();

            if(visitBeforeMappings != null) {
                int mappingCount = visitBeforeMappings.size();

                for(int i = 0; i < mappingCount; i++) {
                    ContentHandlerConfigMap<SAXVisitBefore> mapping = visitBeforeMappings.get(i);
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
                }
            }
        }

        // Apply the dynamic visitors...
        List<SAXVisitBefore> dynamicVisitBefores = dynamicVisitorList.getVisitBefores();
        if(!dynamicVisitBefores.isEmpty()) {
            for (SAXVisitBefore dynamicVisitBefore : dynamicVisitBefores) {
                try {
                    dynamicVisitBefore.visitBefore(currentProcessor.element, execContext);
                } catch(Throwable t) {
                    String errorMsg = "Error in '" + dynamicVisitBefore.getClass().getName() + "' while processing the visitBefore event.";
                    processVisitorException(t, errorMsg);
                }
            }
        }
    }

    private void onChildElement(SAXElement childElement) {
        if(currentProcessor.elementVisitorConfig != null) {
            List<ContentHandlerConfigMap<SAXVisitChildren>> visitChildMappings = currentProcessor.elementVisitorConfig.getChildVisitors();

            if(visitChildMappings != null) {
                int mappingCount = visitChildMappings.size();

                for(int i = 0; i < mappingCount; i++) {
                    ContentHandlerConfigMap<SAXVisitChildren> mapping = visitChildMappings.get(i);
                    if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                        try {
                            mapping.getContentHandler().onChildElement(currentProcessor.element, childElement, execContext);
                        } catch(Throwable t) {
                            String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the onChildElement event.";
                            processVisitorException(currentProcessor.element, t, mapping, VisitSequence.AFTER, errorMsg);
                        }
                    }
                }
            }
        }

        // Apply the dynamic visitors...
        List<SAXVisitChildren> dynamicChildVisitors = dynamicVisitorList.getChildVisitors();
        if(!dynamicChildVisitors.isEmpty()) {
            for (SAXVisitChildren dynamicChildVisitor : dynamicChildVisitors) {
                try {
                    dynamicChildVisitor.onChildElement(currentProcessor.element, childElement, execContext);
                } catch(Throwable t) {
                    String errorMsg = "Error in '" + dynamicChildVisitor.getClass().getName() + "' while processing the onChildElement event.";
                    processVisitorException(t, errorMsg);
                }
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
    }

	private void moveDownCurrentProcessorStack() {
		if(currentProcessor == null) {
			currentProcessor = processorStackRoot;
		} else {
			if(currentProcessor.childProcessor == null) {
				currentProcessor.childProcessor = new ElementProcessor();
				currentProcessor.childProcessor.parentProcessor = currentProcessor;
			}
			currentProcessor = currentProcessor.childProcessor;
		}
		currentProcessor.isNullProcessor = false;
		currentProcessor.element = null;
		currentProcessor.elementVisitorConfig = null;
	}

    private SAXText textWrapper = new SAXText();
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(currentTextType != TextType.CDATA) {
            _characters(ch, start, length);
        } else {
            cdataNodeBuilder.append(ch, start, length);
        }
    }

    private StringBuilder entityBuilder = new StringBuilder(10);
    private void _characters(char[] ch, int start, int length) {

        if(!rewriteEntities && currentTextType == TextType.ENTITY) {
            entityBuilder.setLength(0);

            entityBuilder.append("&#").append((int)ch[start]).append(';');
            char[] newBuf = new char[entityBuilder.length()];
            entityBuilder.getChars(0, newBuf.length, newBuf, 0);

            textWrapper.setText(newBuf, 0, newBuf.length, TextType.TEXT);
        } else {
            textWrapper.setText(ch, start, length, currentTextType);
        }

        if(currentProcessor != null) {
            // Accumulate the text...
            if(currentProcessor.element != null) {
                List<SAXText> saxTextObjects = currentProcessor.element.getText();
                if(saxTextObjects != null) {
                    saxTextObjects.add(textWrapper);
                }
            }

            if(!currentProcessor.isNullProcessor) {
                if(currentProcessor.elementVisitorConfig != null) {
                    List<ContentHandlerConfigMap<SAXVisitChildren>> visitChildMappings = currentProcessor.elementVisitorConfig.getChildVisitors();

                    if(visitChildMappings != null) {
                        int mappingCount = visitChildMappings.size();

                        for(int i = 0; i < mappingCount; i++) {
                            ContentHandlerConfigMap<SAXVisitChildren> mapping = visitChildMappings.get(i);
                            try {
                                if(mapping.getResourceConfig().isTargetedAtElement(currentProcessor.element)) {
                                    mapping.getContentHandler().onChildText(currentProcessor.element, textWrapper, execContext);
                                }
                            } catch(Throwable t) {
                                String errorMsg = "Error in '" + mapping.getContentHandler().getClass().getName() + "' while processing the onChildText event.";
                                processVisitorException(currentProcessor.element, t, mapping, VisitSequence.AFTER, errorMsg);
                            }
                        }
                    }
                }
            }

            // Apply the dynamic visitors...
            List<SAXVisitChildren> dynamicChildVisitors = dynamicVisitorList.getChildVisitors();
            if(!dynamicChildVisitors.isEmpty()) {
                for (SAXVisitChildren dynamicChildVisitor : dynamicChildVisitors) {
                    try {
                        dynamicChildVisitor.onChildText(currentProcessor.element, textWrapper, execContext);
                    } catch(Throwable t) {
                        String errorMsg = "Error in '" + dynamicChildVisitor.getClass().getName() + "' while processing the onChildText event.";
                        processVisitorException(t, errorMsg);
                    }
                }
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
        cdataNodeBuilder.setLength(0);
    }

    public void endCDATA() throws SAXException {
        try {
            char[] chars = new char[cdataNodeBuilder.length()];

            cdataNodeBuilder.getChars(0, chars.length, chars, 0);
            _characters(chars, 0, chars.length);
            currentTextType = TextType.TEXT;
        } finally {
            cdataNodeBuilder.setLength(0);
        }
    }

    public void startEntity(String name) throws SAXException {
        currentTextType = TextType.ENTITY;
    }

    public void endEntity(String name) throws SAXException {
        currentTextType = TextType.TEXT;
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        DocType.setDocType(name, publicId, systemId, null, execContext);
    }

    private static class ElementProcessor {
        private ElementProcessor parentProcessor;
        private ElementProcessor childProcessor;
        private boolean isNullProcessor = false;
        private SAXElement element;
        public SAXElementVisitorMap elementVisitorConfig;
    }

    private void processVisitorException(SAXElement element, Throwable error, ContentHandlerConfigMap configMapping, VisitSequence visitSequence, String errorMsg) throws SmooksException {
        if (eventListener != null) {
            eventListener.onEvent(new ElementVisitEvent(element, configMapping, visitSequence, error));
        }

        processVisitorException(error, errorMsg);
    }

    private void processVisitorException(Throwable error, String errorMsg) {
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
