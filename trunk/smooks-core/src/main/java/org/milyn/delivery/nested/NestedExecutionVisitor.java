/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.delivery.nested;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.AbstractParser;
import org.milyn.delivery.SmooksContentHandler;
import org.milyn.delivery.VisitLifecycleCleanable;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXHandler;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.xml.sax.XMLReader;

import java.io.IOException;

/**
 * Nested Smooks execution visitor.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NestedExecutionVisitor implements SAXVisitBefore, VisitLifecycleCleanable {

    @ConfigParam
    private String smooksConfig;

    @ConfigParam(defaultVal = "true")
    private boolean inheritBeanContext;

    private volatile Smooks smooksInstance;

    public void setSmooksConfig(String smooksConfig) {
        this.smooksConfig = smooksConfig;
    }

    public void setSmooksInstance(Smooks smooksInstance) {
        this.smooksInstance = smooksInstance;
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        Smooks smooks = getSmooksInstance();
        ExecutionContext nestedExecutionContext = smooks.createExecutionContext();
        SmooksContentHandler parentContentHandler = SmooksContentHandler.getHandler(executionContext);
        SmooksContentHandler nestedContentHandler = new SAXHandler(nestedExecutionContext, element.getWriter(this), parentContentHandler);
        XMLReader xmlReader;

        if(inheritBeanContext) {
            nestedExecutionContext.setBeanContext(executionContext.getBeanContext().newSubContext(nestedExecutionContext));
        }

        // In case there's an attached event listener...
        nestedExecutionContext.setEventListener(executionContext.getEventListener());

        // Attach the XMLReader instance to the nested ExecutionContext and then swap the content handler on
        // the XMLReader to be the nested handler created here.  All events wll be forwarded to the ..
        xmlReader = AbstractParser.getXMLReader(executionContext);
        AbstractParser.attachXMLReader(xmlReader, nestedExecutionContext);
        xmlReader.setContentHandler(nestedContentHandler);

        // Note we do not execute the Smooks filterSource methods for a nested instance... we just install
        // the content handler and redirect the reader events to it...
    }

    private Smooks getSmooksInstance() {
        // Lazily create the Smooks instance...
        if(smooksInstance == null) {
            synchronized (this) {
                if(smooksInstance == null) {
                    try {
                        smooksInstance = new Smooks(smooksConfig);
                    } catch (Exception e) {
                        throw new SmooksException("Error creating nested Smooks instance for Smooks configuration '" + smooksConfig + "'.", e);
                    }
                }
            }
        }
        return smooksInstance;
    }

    public void executeVisitLifecycleCleanup(ExecutionContext executionContext) {
        SmooksContentHandler handler = SmooksContentHandler.getHandler(executionContext);

        try {
            handler.getNestedContentHandler().cleanup();
        } finally {
            handler.resetNestedContentHandler();
        }
    }
}
