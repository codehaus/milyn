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
package org.milyn.delivery;

import org.milyn.delivery.dom.DOMVisitAfter;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.dom.Phase;
import org.milyn.delivery.dom.VisitPhase;
import org.milyn.delivery.dom.serialize.SerializationUnit;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.expression.MVELExpressionEvaluator;
import org.milyn.event.types.ConfigBuilderEvent;

import java.util.List;
import java.util.ArrayList;

/**
 * Visitor Configuration Map.
 * <p/>
 * A Map of configured visitors.  Used by the {@link org.milyn.delivery.ContentDeliveryConfigBuilder} to create the
 * create a {@link org.milyn.delivery.ContentDeliveryConfig} instance.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class VisitorConfigMap {
    
    /**
	 * Assembly Visit Befores.
	 */
	private ContentHandlerConfigMapTable<DOMVisitBefore> domAssemblyVisitBefores = new ContentHandlerConfigMapTable<DOMVisitBefore>();
    /**
	 * Assembly Visit Afters.
	 */
	private ContentHandlerConfigMapTable<DOMVisitAfter> domAssemblyVisitAfters = new ContentHandlerConfigMapTable<DOMVisitAfter>();
    /**
	 * Processing Visit Befores.
	 */
	private ContentHandlerConfigMapTable<DOMVisitBefore> domProcessingVisitBefores = new ContentHandlerConfigMapTable<DOMVisitBefore>();
    /**
	 * Processing Visit Afters.
	 */
	private ContentHandlerConfigMapTable<DOMVisitAfter> domProcessingVisitAfters = new ContentHandlerConfigMapTable<DOMVisitAfter>();
    /**
	 * Table of SerializationUnit instances keyed by selector. Each table entry
	 * contains a single SerializationUnit instances.
	 */
	private ContentHandlerConfigMapTable<SerializationUnit> domSerializationVisitors = new ContentHandlerConfigMapTable<SerializationUnit>();
    /**
     * SAX Visit Befores.
     */
    private ContentHandlerConfigMapTable<SAXVisitBefore> saxVisitBefores = new ContentHandlerConfigMapTable<SAXVisitBefore>();
    /**
     * SAX Visit Afters.
     */
    private ContentHandlerConfigMapTable<SAXVisitAfter> saxVisitAfters = new ContentHandlerConfigMapTable<SAXVisitAfter>();
    /**
     * Config builder events list.
     */
    private List<ConfigBuilderEvent> configBuilderEvents = new ArrayList<ConfigBuilderEvent>();

    private int visitorCount = 0;
    private int saxVisitorCount = 0;
    private int domVisitorCount = 0;

    public ContentHandlerConfigMapTable<DOMVisitBefore> getDomAssemblyVisitBefores() {
        return domAssemblyVisitBefores;
    }

    public void setDomAssemblyVisitBefores(ContentHandlerConfigMapTable<DOMVisitBefore> domAssemblyVisitBefores) {
        this.domAssemblyVisitBefores = domAssemblyVisitBefores;
    }

    public ContentHandlerConfigMapTable<DOMVisitAfter> getDomAssemblyVisitAfters() {
        return domAssemblyVisitAfters;
    }

    public void setDomAssemblyVisitAfters(ContentHandlerConfigMapTable<DOMVisitAfter> domAssemblyVisitAfters) {
        this.domAssemblyVisitAfters = domAssemblyVisitAfters;
    }

    public ContentHandlerConfigMapTable<DOMVisitBefore> getDomProcessingVisitBefores() {
        return domProcessingVisitBefores;
    }

    public void setDomProcessingVisitBefores(ContentHandlerConfigMapTable<DOMVisitBefore> domProcessingVisitBefores) {
        this.domProcessingVisitBefores = domProcessingVisitBefores;
    }

    public ContentHandlerConfigMapTable<DOMVisitAfter> getDomProcessingVisitAfters() {
        return domProcessingVisitAfters;
    }

    public void setDomProcessingVisitAfters(ContentHandlerConfigMapTable<DOMVisitAfter> domProcessingVisitAfters) {
        this.domProcessingVisitAfters = domProcessingVisitAfters;
    }

    public ContentHandlerConfigMapTable<SerializationUnit> getDomSerializationVisitors() {
        return domSerializationVisitors;
    }

    public void setDomSerializationVisitors(ContentHandlerConfigMapTable<SerializationUnit> domSerializationVisitors) {
        this.domSerializationVisitors = domSerializationVisitors;
    }

    public ContentHandlerConfigMapTable<SAXVisitBefore> getSaxVisitBefores() {
        return saxVisitBefores;
    }

    public void setSaxVisitBefores(ContentHandlerConfigMapTable<SAXVisitBefore> saxVisitBefores) {
        this.saxVisitBefores = saxVisitBefores;
    }

    public ContentHandlerConfigMapTable<SAXVisitAfter> getSaxVisitAfters() {
        return saxVisitAfters;
    }

    public void setSaxVisitAfters(ContentHandlerConfigMapTable<SAXVisitAfter> saxVisitAfters) {
        this.saxVisitAfters = saxVisitAfters;
    }

    public void setConfigBuilderEvents(List<ConfigBuilderEvent> configBuilderEvents) {
        this.configBuilderEvents = configBuilderEvents;
    }

    public int getVisitorCount() {
        return visitorCount;
    }

    public int getSaxVisitorCount() {
        return saxVisitorCount;
    }

    public int getDomVisitorCount() {
        return domVisitorCount;
    }

    protected boolean addVisitor(String elementName, SmooksResourceConfiguration resourceConfig, ContentHandler contentHandler) {

        if(isSAXVisitor(contentHandler) || isDOMVisitor(contentHandler)) {
            visitorCount++;

            if(isSAXVisitor(contentHandler)) {
                saxVisitorCount++;
                if(contentHandler instanceof SAXVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                    saxVisitBefores.addMapping(elementName, resourceConfig, (SAXVisitBefore) contentHandler);
                }
                if(contentHandler instanceof SAXVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                    saxVisitAfters.addMapping(elementName, resourceConfig, (SAXVisitAfter) contentHandler);
                }
                logExecutionEvent(resourceConfig, "Added as a SAX resource.");
            }

            if(isDOMVisitor(contentHandler)) {
                domVisitorCount++;

                if(contentHandler instanceof SerializationUnit) {
                    domSerializationVisitors.addMapping(elementName, resourceConfig, (SerializationUnit) contentHandler);
                    logExecutionEvent(resourceConfig, "Added as a DOM " + SerializationUnit.class.getSimpleName() + " resource.");
                } else {
                    Phase phaseAnnotation = contentHandler.getClass().getAnnotation(Phase.class);
                    String visitPhase = resourceConfig.getStringParameter("VisitPhase", VisitPhase.PROCESSING.toString());

                    if(phaseAnnotation != null && phaseAnnotation.value() == VisitPhase.ASSEMBLY) {
                        // It's an assembly unit...
                        if(contentHandler instanceof DOMVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                            domAssemblyVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) contentHandler);
                        }
                        if(contentHandler instanceof DOMVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                            domAssemblyVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) contentHandler);
                        }
                        logExecutionEvent(resourceConfig, "Added as a DOM Assembly Phase resource.");
                    } else if (visitPhase.equalsIgnoreCase(VisitPhase.ASSEMBLY.toString())) {
                        // It's an assembly unit...
                        if(contentHandler instanceof DOMVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                            domAssemblyVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) contentHandler);
                        }
                        if(contentHandler instanceof DOMVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                            domAssemblyVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) contentHandler);
                        }
                        logExecutionEvent(resourceConfig, "Added as a DOM Assembly Phase resource.");
                    } else {
                        // It's a processing unit...
                        if(contentHandler instanceof DOMVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, contentHandler)) {
                            domProcessingVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) contentHandler);
                        }
                        if(contentHandler instanceof DOMVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, contentHandler)) {
                            domProcessingVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) contentHandler);
                        }
                        logExecutionEvent(resourceConfig, "Added as a DOM Processing Phase resource.");
                    }
                }
            }
        } else if(!(contentHandler instanceof ConfigurationExpander)) {
            // It's not a ContentHandler type we care about!  Leave for now - whatever's using it
            // can instantiate it itself.
            return false;
        }
        
        return true;
    }

    private void logExecutionEvent(SmooksResourceConfiguration resourceConfig, String message) {
        if(configBuilderEvents != null) {
            configBuilderEvents.add(new ConfigBuilderEvent(resourceConfig, message));
        }
    }

    protected static boolean isDOMVisitor(ContentHandler contentHandler) {
        return (contentHandler instanceof DOMVisitBefore || contentHandler instanceof DOMVisitAfter || contentHandler instanceof SerializationUnit);
    }

    protected static boolean isSAXVisitor(ContentHandler contentHandler) {
        // Intentionally not checking for SAXVisitChildren.  Must be incorporated into a visit before or after...
        return (contentHandler instanceof SAXVisitBefore || contentHandler instanceof SAXVisitAfter);
    }

    protected static boolean visitBeforeAnnotationsOK(SmooksResourceConfiguration resourceConfig, ContentHandler contentHandler) {
        Class<? extends ContentHandler> handlerClass = contentHandler.getClass();
        VisitBeforeIf visitBeforeIf = handlerClass.getAnnotation(VisitBeforeIf.class);

        if(visitBeforeIf != null) {
            MVELExpressionEvaluator conditionEval = new MVELExpressionEvaluator();

            conditionEval.setExpression(visitBeforeIf.condition());
            return conditionEval.eval(resourceConfig);
        }

        return true;
    }

    protected static boolean visitAfterAnnotationsOK(SmooksResourceConfiguration resourceConfig, ContentHandler contentHandler) {
        Class<? extends ContentHandler> handlerClass = contentHandler.getClass();
        VisitAfterIf visitAfterIf = handlerClass.getAnnotation(VisitAfterIf.class);

        if(visitAfterIf != null) {
            MVELExpressionEvaluator conditionEval = new MVELExpressionEvaluator();

            conditionEval.setExpression(visitAfterIf.condition());
            return conditionEval.eval(resourceConfig);
        }

        return true;
    }
}
