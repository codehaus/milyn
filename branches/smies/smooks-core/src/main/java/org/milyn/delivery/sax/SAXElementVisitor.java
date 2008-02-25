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

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentHandler;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface SAXElementVisitor extends ContentHandler {

    /**
     * Visit the supplied element <b>before</b> visiting its child elements.
     *
     * @param element          The SAX element being visited.
     * @param executionContext Execution context.
     * @throws SmooksException Event processing failure.
     * @throws IOException     Error writing event to output writer.
     */
    public abstract void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException;

    /**
     * Process the onChildText event for the targeted element.
     * <p/>
     * Be careful when caching element data.  This is not a DOM.
     *
     * @param element          The element containing the text (parent).  The targeted element.
     * @param childText        The text.
     * @param executionContext Execution context.
     * @throws SmooksException Event processing failure.
     * @throws IOException     Error writing event to output writer.
     */
    public abstract void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException;

    /**
     * Process the onChildElement event for the targeted element.
     * <p/>
     * Be careful when caching element data.  This is not a DOM.
     *
     * @param element          The element containing the child element (parent). The targeted element.
     * @param childElement     The child element just added to the targeted element.
     * @param executionContext Execution context.
     * @throws SmooksException Event processing failure.
     * @throws IOException     Error writing event to output writer.
     */
    public abstract void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException;

    /**
     * Visit the supplied element <b>after</b> visiting its child elements.
     *
     * @param element          The SAX element being visited.
     * @param executionContext Execution context.
     * @throws SmooksException Event processing failure.
     * @throws IOException     Error writing event to output writer.
     */
    public abstract void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException;
}
