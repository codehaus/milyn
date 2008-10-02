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

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.sax.DynamicSAXElementVisitorList;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Stack;

/**
 * DOM Node Model creator.
 * <p/>
 * Adds the visited element as a node model.
 * <p/>
 * For SAX filtering, this visitor will construct a deep DOM model of the visited element.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DomModelCreator implements DOMVisitBefore, SAXVisitBefore, SAXVisitAfter {

    DocumentBuilder documentBuilder;

    public DomModelCreator() throws ParserConfigurationException {
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        addNodeModel(element, executionContext);
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        // Push a new DOMCreator onto the DOMCreator stack and install it in the
        // Dynamic Vistor list in the SAX handler...
        pushCreator(new DOMCreator(), executionContext);
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        // Pop the DOMCreator off the DOMCreator stack and uninstall it from the
        // Dynamic Vistor list in the SAX handler...
        popCreator(executionContext);
    }

    private void addNodeModel(Element element, ExecutionContext executionContext) {
        DOMModel nodeModel = DOMModel.getModel(executionContext);
        nodeModel.getModels().put(DomUtils.getName(element), element);
    }

    private void pushCreator(DOMCreator domCreator, ExecutionContext executionContext) {
        Stack<DOMCreator> domCreatorStack = (Stack<DOMCreator>) executionContext.getAttribute(DOMCreator.class);

        if(domCreatorStack == null) {
            domCreatorStack = new Stack<DOMCreator>();
            executionContext.setAttribute(DOMCreator.class, domCreatorStack);
        } else if(!domCreatorStack.isEmpty()) {
            // We need to remove the current DOMCreator from the dynamic visitor list because
            // we want to stop nodes being added to it and instead, have them added to the new
            // DOM.  This prevents a single huge DOM being created for a huge message (being processed
            // via SAX) because it maintains a hierarchy of models. Inner models can represent collection
            // entry instances, with a single model for a single collection entry only being held in memory
            // at any point in time i.e. old ones are overwritten and so freed for GC.
            DynamicSAXElementVisitorList.removeDynamicVisitor(domCreatorStack.peek(), executionContext);
        }

        DynamicSAXElementVisitorList.addDynamicVisitor(domCreator, executionContext);
        domCreatorStack.push(domCreator);
    }

    private void popCreator(ExecutionContext executionContext) {
        Stack<DOMCreator> domCreatorStack = (Stack<DOMCreator>) executionContext.getAttribute(DOMCreator.class);

        if(domCreatorStack == null) {
            throw new IllegalStateException("No DOM Creator Stack available.");
        } else {
            // Remove the current DOMCreators from the dynamic visitor list...
            if(!domCreatorStack.isEmpty()) {
                DOMCreator removedCreator = domCreatorStack.pop();
                DynamicSAXElementVisitorList.removeDynamicVisitor(removedCreator, executionContext);
            }

            // Reinstate parent DOMCreators in the dynamic visitor list...
            if(!domCreatorStack.isEmpty()) {
                DynamicSAXElementVisitorList.addDynamicVisitor(domCreatorStack.peek(), executionContext);
            }
        }
    }

    private class DOMCreator implements SAXElementVisitor {

        private Document document;
        private Node currentNode;

        private DOMCreator() {
            document = documentBuilder.newDocument();
            currentNode = document;
        }

        public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
            Element domElement = element.toDOMElement(document);

            if(currentNode == document) {
                addNodeModel(domElement, executionContext);
            }

            currentNode.appendChild(domElement);
            currentNode = domElement;
        }

        public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
            if(currentNode == document) {
                // Just ignore for now...
                return;
            }

            if(childText.getText().trim().length() == 0) {
                // Ignore pure whitespace...
                return;
            }

            switch (childText.getType()) {
                case TEXT:
                    currentNode.appendChild(document.createTextNode(childText.getText()));
                    break;
                case CDATA:
                    currentNode.appendChild(document.createCDATASection(childText.getText()));
                    break;
                case COMMENT:
                    currentNode.appendChild(document.createComment(childText.getText()));
                    break;
                case ENTITY:
                    currentNode.appendChild(document.createEntityReference(childText.getText()));
                    break;
            }
        }

        public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
        }

        public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
            currentNode = currentNode.getParentNode();
        }
    }
}
