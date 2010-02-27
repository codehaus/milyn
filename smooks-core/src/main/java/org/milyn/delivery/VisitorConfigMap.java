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
import org.milyn.cdr.SmooksResourceConfigurationFactory;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.expression.MVELExpressionEvaluator;
import org.milyn.event.types.ConfigBuilderEvent;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.assertion.AssertArgument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Visitor Configuration Map.
 * <p/>
 * A Map of configured visitors.  Used by the {@link org.milyn.delivery.ContentDeliveryConfigBuilder} to create the
 * {@link org.milyn.delivery.ContentDeliveryConfig} instance.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class VisitorConfigMap {

    /**
     * Logger.
     */
    private static Log logger = LogFactory.getLog(VisitorConfigMap.class);
    /**
     * App context.
     */
    private ApplicationContext applicationContext;
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
     * Visit lifecycle Cleanable visitors.
     */
    private ContentHandlerConfigMapTable<VisitLifecycleCleanable> visitCleanables = new ContentHandlerConfigMapTable<VisitLifecycleCleanable>();
    /**
     * Execution lifecycle Cleanable visitors.
     */
    private ContentHandlerConfigMapTable<ExecutionLifecycleCleanable> execCleanables = new ContentHandlerConfigMapTable<ExecutionLifecycleCleanable>();

    /**
     * Config builder events list.
     */
    private List<ConfigBuilderEvent> configBuilderEvents = new ArrayList<ConfigBuilderEvent>();

    private int visitorCount = 0;
    private int saxVisitorCount = 0;
    private int domVisitorCount = 0;

    public VisitorConfigMap(ApplicationContext applicationContext) {
        AssertArgument.isNotNull(applicationContext, "applicationContext");
        this.applicationContext = applicationContext;
    }

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

    public ContentHandlerConfigMapTable<VisitLifecycleCleanable> getVisitCleanables() {
        return visitCleanables;
    }

    public void setVisitCleanables(ContentHandlerConfigMapTable<VisitLifecycleCleanable> visitCleanables) {
        this.visitCleanables = visitCleanables;
    }

    public ContentHandlerConfigMapTable<ExecutionLifecycleCleanable> getExecCleanables() {
        return execCleanables;
    }

    public void setExecCleanables(ContentHandlerConfigMapTable<ExecutionLifecycleCleanable> execCleanables) {
        this.execCleanables = execCleanables;
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

    public SmooksResourceConfiguration addVisitor(Visitor visitor, String targetSelector, String targetSelectorNS, boolean configure) {
        AssertArgument.isNotNull(visitor, "visitor");
        AssertArgument.isNotNull(targetSelector, "targetSelector");

        SmooksResourceConfiguration resourceConfig;
        if (visitor instanceof SmooksResourceConfigurationFactory) {
            resourceConfig = ((SmooksResourceConfigurationFactory)visitor).createConfiguration();
            resourceConfig.setResource(visitor.getClass().getName());
            resourceConfig.setSelector(targetSelector);
        } else {
            resourceConfig = new SmooksResourceConfiguration(targetSelector, visitor.getClass().getName());
        }

        resourceConfig.setSelectorNamespaceURI(targetSelectorNS);
        addVisitor(visitor, resourceConfig, configure);

        return resourceConfig;
    }

    public void addVisitor(Visitor visitor, SmooksResourceConfiguration resourceConfig, boolean configure) {
        String elementName = resourceConfig.getTargetElement();

        if(configure) {
            // And configure/initialize the instance...
            Configurator.processFieldContextAnnotation(visitor, applicationContext);
            Configurator.processFieldConfigAnnotations(visitor, resourceConfig, false);
            Configurator.initialise(visitor);
            applicationContext.getStore().getInitializedObjects().add(visitor);
        }

        if(isSAXVisitor(visitor) || isDOMVisitor(visitor)) {
            visitorCount++;
        }

        if(isSAXVisitor(visitor)) {
            saxVisitorCount++;
            if(visitor instanceof SAXVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, visitor)) {
                saxVisitBefores.addMapping(elementName, resourceConfig, (SAXVisitBefore) visitor);
            }
            if(visitor instanceof SAXVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, visitor)) {
                saxVisitAfters.addMapping(elementName, resourceConfig, (SAXVisitAfter) visitor);
            }
            logExecutionEvent(resourceConfig, "Added as a SAX resource.");
        }

        if(isDOMVisitor(visitor)) {
            domVisitorCount++;

            if(visitor instanceof SerializationUnit) {
                domSerializationVisitors.addMapping(elementName, resourceConfig, (SerializationUnit) visitor);
                logExecutionEvent(resourceConfig, "Added as a DOM " + SerializationUnit.class.getSimpleName() + " resource.");
            } else {
                Phase phaseAnnotation = visitor.getClass().getAnnotation(Phase.class);
                String visitPhase = resourceConfig.getStringParameter("VisitPhase", VisitPhase.PROCESSING.toString());

                if(phaseAnnotation != null && phaseAnnotation.value() == VisitPhase.ASSEMBLY) {
                    // It's an assembly unit...
                    if(visitor instanceof DOMVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, visitor)) {
                        domAssemblyVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) visitor);
                    }
                    if(visitor instanceof DOMVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, visitor)) {
                        domAssemblyVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) visitor);
                    }
                } else if (visitPhase.equalsIgnoreCase(VisitPhase.ASSEMBLY.toString())) {
                    // It's an assembly unit...
                    if(visitor instanceof DOMVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, visitor)) {
                        domAssemblyVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) visitor);
                    }
                    if(visitor instanceof DOMVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, visitor)) {
                        domAssemblyVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) visitor);
                    }
                } else {
                    // It's a processing unit...
                    if(visitor instanceof DOMVisitBefore && VisitorConfigMap.visitBeforeAnnotationsOK(resourceConfig, visitor)) {
                        domProcessingVisitBefores.addMapping(elementName, resourceConfig, (DOMVisitBefore) visitor);
                    }
                    if(visitor instanceof DOMVisitAfter && VisitorConfigMap.visitAfterAnnotationsOK(resourceConfig, visitor)) {
                        domProcessingVisitAfters.addMapping(elementName, resourceConfig, (DOMVisitAfter) visitor);
                    }
                }

                logExecutionEvent(resourceConfig, "Added as a DOM " + visitPhase + " Phase resource.");
            }
        }

        if(visitor instanceof VisitLifecycleCleanable) {
            visitCleanables.addMapping(elementName, resourceConfig, (VisitLifecycleCleanable) visitor);
        }

        if(visitor instanceof ExecutionLifecycleCleanable) {
            execCleanables.addMapping(SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR, resourceConfig, (ExecutionLifecycleCleanable) visitor);
        }
    }

    public static void execCleanables(ContentHandlerConfigMapTable<ExecutionLifecycleCleanable> execCleanables, ExecutionContext executionContext) {
        List<ContentHandlerConfigMap<ExecutionLifecycleCleanable>> execCleanableList = execCleanables.getMappings(SmooksResourceConfiguration.DOCUMENT_FRAGMENT_SELECTOR);

        if(execCleanableList != null) {
            for(ContentHandlerConfigMap<ExecutionLifecycleCleanable> execCleanable : execCleanableList) {
                ExecutionLifecycleCleanable lifecycleCleanable = execCleanable.getContentHandler();
                try {
                    lifecycleCleanable.executeExecutionLifecycleCleanup(executionContext);
                } catch(Exception e) {
                    logger.error("Error while executing lifecycle cleanable '" + lifecycleCleanable.getClass().getName() + "'.", e);
                }
            }
        }
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

    public void addAll(VisitorConfigMap visitorConfigMap) {
        if(visitorConfigMap != null) {
            domAssemblyVisitBefores.addAll(visitorConfigMap.getDomAssemblyVisitBefores());
            domAssemblyVisitAfters.addAll(visitorConfigMap.getDomAssemblyVisitAfters());
            domProcessingVisitBefores.addAll(visitorConfigMap.getDomProcessingVisitBefores());
            domProcessingVisitAfters.addAll(visitorConfigMap.getDomProcessingVisitAfters());
            domSerializationVisitors.addAll(visitorConfigMap.getDomSerializationVisitors());
            saxVisitBefores.addAll(visitorConfigMap.getSaxVisitBefores());
            saxVisitAfters.addAll(visitorConfigMap.getSaxVisitAfters());
            visitCleanables.addAll(visitorConfigMap.getVisitCleanables());
            execCleanables.addAll(visitorConfigMap.getExecCleanables());

            visitorCount += visitorConfigMap.getVisitorCount();
            saxVisitorCount += visitorConfigMap.getSaxVisitorCount();
            domVisitorCount += visitorConfigMap.getDomVisitorCount();
        }
    }
}
