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
package org.milyn.cdr.xpath.evaluators.equality;

import org.milyn.cdr.xpath.evaluators.XPathExpressionEvaluator;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.container.ExecutionContext;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple element index predicate evaluator.
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class IndexEvaluator extends XPathExpressionEvaluator {

    private int index;
    private ElementCounter counter;
    private String elementName;
    private String elementNS;

    public IndexEvaluator(int index) {
        this.index = index;
    }

    public ElementCounter getCounter() {
        return counter;
    }

    public void setCounter(ElementCounter counter) {
        this.counter = counter;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public void setElementNS(String elementNS) {
        this.elementNS = elementNS;
    }

    public boolean evaluate(SAXElement element, ExecutionContext executionContext) {
        return counter.getCount(element) == index;
    }

    public boolean evaluate(Element element, ExecutionContext executionContext) {
        Node parent = element.getParentNode();

        if(parent == null) {
            return (index == 0);
        }

        NodeList siblings = parent.getChildNodes();
        int count = 0;
        int siblingCount = siblings.getLength();

        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);

            if(sibling.getNodeType() == Node.ELEMENT_NODE && DomUtils.getName((Element) sibling).equalsIgnoreCase(elementName)) {
                if(elementNS == null || elementNS.equals(sibling.getNamespaceURI())) {
                    count++;
                }
            }

            if(sibling == element) {
                break;
            }
        }

        return (index == count);
    }

    public String toString() {
        return "[" + index + "]";
    }
}