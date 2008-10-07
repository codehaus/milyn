package org.milyn.smooks.edi;

import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.container.ExecutionContext;
import org.milyn.SmooksException;
import org.milyn.payload.FilterResult;
import org.milyn.edisax.EDIParseException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.SmooksConfigurationException;
import org.w3c.dom.Element;

import javax.xml.transform.Result;
import java.io.*;

/**
 * The EDIModelCreator initializes the SmooksEDIModel by setting the uri to the
 * edi-mapping-model definition resource. Also, when reaching the end of the document-element
 * the ediModel is written to the {@link Result}.
 * @author bardl 
 */
public class EDIModelCreator implements SAXVisitBefore, SAXVisitAfter, DOMElementVisitor {    

    @ConfigParam
    private String ediModel;

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        createEdiModel(executionContext);
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        writeEdiModel(executionContext);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        createEdiModel(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        writeEdiModel(executionContext);
    }

    private void createEdiModel(ExecutionContext executionContext) {

        try {
            //Create EdifactModel.  
            SmooksEDIModel.getSmooksEDIModel(executionContext, ediModel);
        } catch (IOException e) {
            throw new SmooksConfigurationException( "Could not initialize ediModel set in configuration. EdiModel [" + ediModel + "].", e);
        } catch (EDIParseException e) {
            throw new SmooksConfigurationException( "Could not initialize ediModel set in configuration. EdiModel [" + ediModel + "].", e);
        }
    }

    private void writeEdiModel(ExecutionContext executionContext) {

        Result result = FilterResult.getResult(executionContext);
        
        if (result == null) {
            return;
        }

        try {
            SmooksEDIModel edifactModel = SmooksEDIModel.getSmooksEDIModel(executionContext, ediModel);        
            edifactModel.write(executionContext.getContentEncoding(), result);
        } catch (IOException e) {
            throw new SmooksException("Could not write EDI model to result.", e);
        } catch (EDIParseException e) {
            throw new SmooksException("Could not write EDI model to result.", e);
        }
    }
}
