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

import org.milyn.delivery.sax.*;
import org.milyn.delivery.ordering.Producer;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;
import org.milyn.util.CollectionsUtil;

import java.io.IOException;
import java.util.Set;

/**
 * Element counter.
 * <p/>
 * Used for index based XPath predicates.
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class ElementCounter implements SAXVisitBefore, Producer {

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        ElementIndex index = getElementIndex(element);
        if(index != null) {
            index.i++;
        }
    }

    protected int getCount(SAXElement element) {
        ElementIndex index = getElementIndex(element);
        if(index != null) {
            return index.i;
        }
        return 0;
    }

    public Set<? extends Object> getProducts() {
        // The fact that it implements the Producer interface is all we need...
        return CollectionsUtil.toSet();
    }

    private ElementIndex getElementIndex(SAXElement element) {
        SAXElement parent = element.getParent();
        ElementIndex index;

        if(parent != null) {
            index = (ElementIndex) parent.getCache(this);
            if(index == null) {
                index = new ElementIndex();
                parent.setCache(this, index);
            }
            return index;
        }

        return null;
    }

    private class ElementIndex {
        private int i = 0;
    }
}
