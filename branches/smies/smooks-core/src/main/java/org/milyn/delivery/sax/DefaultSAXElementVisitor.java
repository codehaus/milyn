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

import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DefaultSAXElementVisitor implements SAXElementVisitor {
    
    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        // Do nothing here... see is there any child text/elements first...
        //System.out.println("");
    }

    public void onChildText(SAXElement element, SAXText text, ExecutionContext executionContext) throws SmooksException, IOException {
        writeStartElement(element);
        text.toWriter(element.getWriter());
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
        writeStartElement(element);
        // The child element is responsible for writing itself...
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        writeEndElement(element);
    }

    private void writeStartElement(SAXElement element) throws IOException {
        // We set a flag in the cache so as to mark the fact that the start element has been writen
        if(element.getCache() == null) {
            element.setCache(true);
            WriterUtil.writeStartElement(element, element.getWriter());
        }
    }

    private void writeEndElement(SAXElement element) throws IOException {
        if(element.getCache() == null) {
            // It's an empty element...
            WriterUtil.writeEmptyElement(element, element.getWriter());
        } else {
            WriterUtil.writeEndElement(element, element.getWriter());
        }
    }
}
