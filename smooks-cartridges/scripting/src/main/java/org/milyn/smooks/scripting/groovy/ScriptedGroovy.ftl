package org.milyn.smooks.scripting.groovy;

import groovy.xml.dom.DOMUtil;
import groovy.xml.dom.DOMCategory;
import groovy.xml.DOMBuilder;

import org.milyn.container.ExecutionContext
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.SmooksException;
import org.milyn.javabean.repository.BeanRepository;

import org.milyn.delivery.DomModelCreator
import org.milyn.delivery.dom.DOMVisitBefore
import org.milyn.delivery.dom.DOMVisitAfter
import org.milyn.xml.*;
import org.milyn.io.NullWriter;

import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXElement;

import java.io.IOException;
import org.w3c.dom.*;

${imports}

<#if visitBefore>
class ${visitorName} implements DOMVisitBefore, SAXVisitBefore {

    private SmooksResourceConfiguration config;

	public void setConfiguration(SmooksResourceConfiguration config) {
		this.config = config;
	}

    public void visitBefore(Element element, ExecutionContext executionContext) {
        Element ${elementName} = element;
        Document document = element.getOwnerDocument();

        ${visitorScript}
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        SAXElement ${elementName} = element;
        
        ${visitorScript}
    }
}
<#else>
class ${visitorName} implements DOMVisitAfter, SAXVisitBefore, SAXVisitAfter {

    private SmooksResourceConfiguration config;
    private DomModelCreator modelCreator;
    private boolean format = false;

	public void setConfiguration(SmooksResourceConfiguration config) {
		this.config = config;

		if(config.getBoolParameter("createDOMFragment", true)) {
		    modelCreator = new DomModelCreator();
		}
		format = config.getBoolParameter("format", false);
	}

    public void visitAfter(Element element, ExecutionContext executionContext) {
        visitAfter(element, executionContext, null);
    }

    public void visitAfter(Element element, ExecutionContext executionContext, Writer writer) {
        Element ${elementName} = element;
        Document document = element.getOwnerDocument();

        def writeFragment = { outNode ->
            XmlUtil.serialize((Node)outNode, format, writer);
        }

        ${visitorScript}
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        Writer currentWriter = element.getWriter(this);

        if(modelCreator != null) {
            if(executionContext.isDefaultSerializationOn()) {
                // If Default Serialization is on, we want to block output to the
                // output stream...
                element.setWriter(new NullWriter(currentWriter), this);
            }

            modelCreator.visitBefore(element, executionContext);
        }
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        SAXElement ${elementName} = element;

        if(modelCreator != null) {
            Document fragmentDoc = modelCreator.popCreator(executionContext);
            Element fragmentElement = fragmentDoc.getDocumentElement();

            Writer writer = element.getWriter(this);
            if(writer instanceof NullWriter) {
                // Reset the writer...
                writer = ((NullWriter)writer).getParentWriter();
                element.setWriter(writer, this);
            }

            if(executionContext.isDefaultSerializationOn()) {
                visitAfter(fragmentElement, executionContext);
                XmlUtil.serialize((Node) fragmentDoc, format, writer);
            } else {
                visitAfter(fragmentElement, executionContext, writer);
            }
        } else {
            ${visitorScript}
        }
    }
}
</#if>