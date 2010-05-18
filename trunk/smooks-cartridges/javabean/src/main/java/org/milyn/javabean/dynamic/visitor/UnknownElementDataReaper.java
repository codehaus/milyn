/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.javabean.dynamic.visitor;

import org.milyn.SmooksException;
import org.milyn.cdr.ConfigSearch;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.javabean.BeanInstanceCreator;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.List;

/**
 * Unknown element data reaper.
 * <p/>
 * Models can sometimes be created from XML which contains valid elements that are not being mapped into the model.
 * We don't want to loose this data in the model, so we capture it as "pre
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UnknownElementDataReaper implements SAXElementVisitor, DOMElementVisitor {

    @AppContext
    private ApplicationContext appContext;
    
    private List<SmooksResourceConfiguration> creatorConfigs;

    @Initialize
    public void initialize() {
        creatorConfigs = appContext.getStore().lookupResource(new ConfigSearch().resource(BeanInstanceCreator.class.getName()));
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        Element activeReaperElement = (Element) executionContext.getAttribute(UnknownElementDataReaper.class);

        if(activeReaperElement == null && !isBeanCreatingFragment(element, executionContext)) {
            executionContext.setAttribute(UnknownElementDataReaper.class, activeReaperElement);
        }
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        Element activeReaperElement = (Element) executionContext.getAttribute(UnknownElementDataReaper.class);

        if(element == activeReaperElement) {
            try {
                
            } finally {
                executionContext.removeAttribute(UnknownElementDataReaper.class);
            }
        }
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!isBeanCreatingFragment(element, executionContext)) {
        }
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    private boolean isBeanCreatingFragment(Element element, ExecutionContext executionContext) {
        for(SmooksResourceConfiguration creatorConfig : creatorConfigs) {
            if(creatorConfig.isTargetedAtElement(element, executionContext)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isBeanCreatingFragment(SAXElement element, ExecutionContext executionContext) {
        for(SmooksResourceConfiguration creatorConfig : creatorConfigs) {
            if(creatorConfig.isTargetedAtElement(element, executionContext)) {
                return true;
            }
        }

        return false;
    }
}
