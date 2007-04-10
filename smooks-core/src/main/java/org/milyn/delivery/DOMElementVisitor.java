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

import org.milyn.container.ExecutionContext;
import org.w3c.dom.Element;

/**
 * Element <b>Visitor</b> (GoF) interface for DOM.
 * <p/>
 * {@link org.milyn.delivery.SmooksXML} filters (analyses/transforms) XML/XHTML/HTML content
 * by "visting" the DOM {@link org.w3c.dom.Element} nodes through a series of iterations over
 * the source XML DOM.
 * <p/>
 * This interface defines the methods for a "visiting" filter.
 * Implementations of this interface provide a means of hooking analysis
 * and transformation logic into the {@link org.milyn.delivery.SmooksXML} filtering filter.
 * <p/>
 * Implementations must be stateless.  If state storage is required, attach the state to the
 * supplied {@link org.milyn.container.ExecutionContext}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface DOMElementVisitor extends ContentDeliveryUnit {

    /**
     * Visit the supplied element <b>before</a> visiting its child elements.
     *
     * @param element          The DOM element being visited.
     * @param executionContext Request relative instance.
     */
    public abstract void visitBefore(Element element, ExecutionContext executionContext);

    /**
     * Visit the supplied element <b>after</a> visiting its child elements.
     *
     * @param element          The DOM element being visited.
     * @param executionContext Request relative instance.
     */
    public abstract void visitAfter(Element element, ExecutionContext executionContext);
}