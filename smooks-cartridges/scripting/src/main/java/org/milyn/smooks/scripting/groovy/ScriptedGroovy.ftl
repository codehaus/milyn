package org.milyn.smooks.scripting.groovy

import groovy.xml.dom.DOMUtil;
import groovy.xml.dom.DOMCategory;
import groovy.xml.DOMBuilder;

import org.milyn.container.ExecutionContext
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.SmooksException;
import org.milyn.javabean.repository.BeanRepository;

import org.milyn.delivery.dom.DOMVisitBefore
import org.milyn.delivery.dom.DOMVisitAfter
import org.milyn.xml.DomUtils;

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
class ${visitorName} implements DOMVisitAfter, SAXVisitAfter {

    private SmooksResourceConfiguration config;

	public void setConfiguration(SmooksResourceConfiguration config) {
		this.config = config;
	}

    public void visitAfter(Element element, ExecutionContext executionContext) {
        Element ${elementName} = element;
        Document document = element.getOwnerDocument();

        ${visitorScript}
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        SAXElement ${elementName} = element;

        ${visitorScript}
    }
}
</#if>