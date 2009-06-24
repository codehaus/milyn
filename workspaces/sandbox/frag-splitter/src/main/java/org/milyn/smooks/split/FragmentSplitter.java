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
package org.milyn.smooks.split;

import org.milyn.delivery.sax.DefaultSAXElementSerializer;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXText;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.javabean.BeanAccessor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class FragmentSplitter extends DefaultSAXElementSerializer {

    @ConfigParam
    private String bindTo;

    @ConfigParam(defaultVal = "false")
    private boolean childContentOnly;

    public void visitBefore(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
        saxElement.setWriter(new SplitWriter(saxElement.getWriter(this)), this);
        if(!childContentOnly) {
            super.visitBefore(saxElement, executionContext);
        }
    }

    public void visitAfter(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!childContentOnly) {
            super.visitAfter(saxElement, executionContext);
        }
        SplitWriter splitWriter = (SplitWriter) saxElement.getWriter(this);
        splitWriter.stringWriter.flush();
        BeanAccessor.addBean(executionContext, bindTo, splitWriter.stringWriter.toString().trim());
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!childContentOnly) {
            super.onChildElement(element, childElement, executionContext);
        }
    }
    public void onChildText(SAXElement element, SAXText text, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!childContentOnly) {
            writeStartElement(element);
        }
        text.toWriter(element.getWriter(this));
    }

    private class SplitWriter extends Writer {

        private StringWriter stringWriter = new StringWriter();
        private Writer baseWriter;

        private SplitWriter(Writer baseWriter) {
            this.baseWriter = baseWriter;
        }

        public void write(char cbuf[], int off, int len) throws IOException {
            try {
                stringWriter.write(cbuf, off, len);
            } finally {
                baseWriter.write(cbuf, off, len);
            }
        }

        public void flush() throws IOException {
            try {
                stringWriter.flush();
            } finally {
                baseWriter.flush();
            }
        }

        public void close() throws IOException {
            try {
                stringWriter.close();
            } finally {
                baseWriter.close();
            }
        }
    }
}
