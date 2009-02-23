package org.milyn.smooks.edi;

import org.milyn.delivery.sax.*;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;
import org.milyn.smooks.edi.repository.BindingGroupRepository;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Config;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;

/**
 * EDIBindingGroupPopulator reacts to DOM/SAX events from the Smooks framework and inserts values into the
 * BindingGroup bound in the BindingGroupRepository matching the batchId.
 * @see org.milyn.smooks.edi.repository.BindingGroupRepository
 * @author bardl 
 */
public class EDIBindingGroupPopulator implements SAXVisitBefore, SAXVisitAfter, DOMElementVisitor, SAXVisitChildren {

    @ConfigParam(name="ediPath")
    private String ediPath;

    @ConfigParam(name="modelId")
    private String modelId;

    @ConfigParam(name="bindingGroupId")
    private String bindingGroupId;

    @Config
    private SmooksResourceConfiguration config;

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        element.setCache(new StringWriter());
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        insertEdiPath(executionContext, element.getCache().toString());
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
        childText.toWriter((Writer) element.getCache());
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        insertEdiPath(executionContext, element.getTextContent());
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {       
    }

    private void insertEdiPath(ExecutionContext executionContext, String value) {
        BindingGroupRepository repository = BindingGroupRepository.getSegmentRepository(executionContext);         
        repository.getSegment(bindingGroupId).addMappingValue(ediPath, value);
    }
}
