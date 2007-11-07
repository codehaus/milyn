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
import org.milyn.container.ExecutionContext;

/**
 * SAX specific {@link org.milyn.delivery.ContentDeliveryConfig} implementation.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXContentDeliveryConfig extends AbstractContentDeliveryConfig {
    
    private ContentHandlerConfigMapTable<SAXElementVisitor> saxVisitors;

    public ContentHandlerConfigMapTable<SAXElementVisitor> getSaxVisitors() {
        return saxVisitors;
    }

    public void setSaxVisitors(ContentHandlerConfigMapTable<SAXElementVisitor> saxVisitors) {
        this.saxVisitors = saxVisitors;
    }

    public Filter newFilter(ExecutionContext executionContext) {
        return new SmooksSAXFilter(executionContext);
    }
}