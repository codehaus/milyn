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

import org.milyn.delivery.AbstractContentDeliveryConfig;
import org.milyn.delivery.Filter;
import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.container.ExecutionContext;

import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * SAX specific {@link org.milyn.delivery.ContentDeliveryConfig} implementation.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXContentDeliveryConfig extends AbstractContentDeliveryConfig {
    
    private ContentHandlerConfigMapTable<SAXVisitBefore> visitBefores;
    private ContentHandlerConfigMapTable<SAXVisitChildren> childVisitors = new ContentHandlerConfigMapTable<SAXVisitChildren>();
    private ContentHandlerConfigMapTable<SAXVisitAfter> visitAfters;

    public ContentHandlerConfigMapTable<SAXVisitBefore> getVisitBefores() {
        return visitBefores;
    }

    public void setVisitBefores(ContentHandlerConfigMapTable<SAXVisitBefore> visitBefores) {
        this.visitBefores = visitBefores;
    }

    public void setChildVisitors() {
        if(visitBefores == null || visitAfters == null) {
            throw new IllegalStateException("Illegal call to setChildVisitors() before setVisitBefores() and setVisitAfters() are called.");
        }

        // Need to extract the child visitor impls from the visitBefores and the visitAfters.  Need to make sure that we don't add
        // the same handler twice - handlers can impl both SAXVisitBefore and SAXVisitAfter. So, we don't add child handlers from the
        // visitBefores if they also impl SAXVisitAfter (avoiding adding where it impls both).  We add from the visitafters list
        // if it impls SAXVisitAfter without checking for SAXVisitBefore (catching the case where it impls both).

        Set<Map.Entry<String, List<ContentHandlerConfigMap<SAXVisitBefore>>>> beforeMappings = visitBefores.getTable().entrySet();
        for (Map.Entry<String, List<ContentHandlerConfigMap<SAXVisitBefore>>> beforeMapping : beforeMappings) {
            List<ContentHandlerConfigMap<SAXVisitBefore>> elementMappings = beforeMapping.getValue();
            for (ContentHandlerConfigMap<SAXVisitBefore> elementMapping : elementMappings) {
                String selector = beforeMapping.getKey();
                SAXVisitBefore handler = elementMapping.getContentHandler();

                // Wanna make sure we don't add the same handler twice, so if it also impls SAXVisitAfter, leave
                // that until we process the SAXVisitAfter handlers
                if(handler instanceof SAXVisitChildren && !(handler instanceof SAXVisitAfter)) {
                    childVisitors.addMapping(selector, elementMapping.getResourceConfig(), (SAXVisitChildren) handler);
                }
            }
        }

        Set<Map.Entry<String, List<ContentHandlerConfigMap<SAXVisitAfter>>>> afterMappings = visitAfters.getTable().entrySet();
        for (Map.Entry<String,List<ContentHandlerConfigMap<SAXVisitAfter>>> afterMapping : afterMappings) {
            List<ContentHandlerConfigMap<SAXVisitAfter>> elementMappings = afterMapping.getValue();
            for (ContentHandlerConfigMap<SAXVisitAfter> elementMapping : elementMappings) {
                String selector = afterMapping.getKey();
                SAXVisitAfter handler = elementMapping.getContentHandler();

                if(handler instanceof SAXVisitChildren) {
                    childVisitors.addMapping(selector, elementMapping.getResourceConfig(), (SAXVisitChildren) handler);
                }
            }
        }
    }

    public ContentHandlerConfigMapTable<SAXVisitChildren> getChildVisitors() {
        return childVisitors;
    }

    public ContentHandlerConfigMapTable<SAXVisitAfter> getVisitAfters() {
        return visitAfters;
    }

    public void setVisitAfters(ContentHandlerConfigMapTable<SAXVisitAfter> visitAfters) {
        this.visitAfters = visitAfters;
    }

    public Filter newFilter(ExecutionContext executionContext) {
        return new SmooksSAXFilter(executionContext);
    }
}