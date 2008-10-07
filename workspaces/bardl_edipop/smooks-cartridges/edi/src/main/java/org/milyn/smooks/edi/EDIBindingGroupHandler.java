package org.milyn.smooks.edi;

import org.milyn.SmooksException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.edisax.EDIParseException;
import org.milyn.smooks.edi.repository.BindingGroup;
import org.milyn.smooks.edi.repository.BindingGroupRepository;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Map;

/**
 * EDIBindingGroupHandler is used to handle {@link org.milyn.smooks.edi.repository.BindingGroup}
 * which in turn is used group segments together. A {@link org.milyn.smooks.edi.repository.BindingGroup}
 * is defined by the resource-config in the configuration file. All bindings within the same
 * resource-config is viewed as a BindingGroup.
 * EDIBindingGroupHandler handles creation of Segments in model if the newSegment param is stated in the
 * configuration file. When reaching the end element of {@link org.milyn.smooks.edi.repository.BindingGroup} all
 * visited bindings are inserted into the {@link org.milyn.smooks.edi.SmooksEDIModel}.
 * @see org.milyn.smooks.edi.repository.BindingGroup
 * @see org.milyn.smooks.edi.repository.BindingGroupRepository
 * @see org.milyn.smooks.edi.SmooksEDIModel
 * @author bardl
 */
public class EDIBindingGroupHandler implements SAXVisitBefore, SAXVisitAfter, DOMElementVisitor {

    @ConfigParam(name="bindingGroupId")
    private String bindingGroupId;

    @ConfigParam(name="newSegment", use= ConfigParam.Use.OPTIONAL)
    private String newSegment;

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        createSegment(executionContext);
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        insertEdiPathIntoModel(executionContext);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        createSegment(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        insertEdiPathIntoModel(executionContext);
    }

    private void createSegment(ExecutionContext executionContext) {
        try {
            SmooksEDIModel ediModel = SmooksEDIModel.getSmooksEDIModel(executionContext, null);
            BindingGroupRepository repository = BindingGroupRepository.getSegmentRepository(executionContext);
            BindingGroup batch = repository.getSegment(bindingGroupId);

            //Don't create a new Segment the first time, since a segment allready exist in the ediModel.
            if ( batch.getNrOfCreatedBatches() > 0 && newSegment != null) {
                ediModel.insertEDIModel(newSegment+"[]", null);
            }
            batch.setNrOfCreatedBatches(batch.getNrOfCreatedBatches()+1);

        } catch (EDIParseException e) {
            throw new SmooksException("Could not insert data into edi model.", e);
        } catch (IOException e) {
            throw new SmooksException("Could not insert data into edi model.", e);
        }
    }

    private void insertEdiPathIntoModel(ExecutionContext executionContext) {

        //TODO:: Add support for MVEL expression here. If expression evaluates to false return without inserting data into model.

        try {
            SmooksEDIModel ediModel = SmooksEDIModel.getSmooksEDIModel(executionContext, null);
            BindingGroupRepository repository = BindingGroupRepository.getSegmentRepository(executionContext);
            BindingGroup batch = repository.getSegment(bindingGroupId);
            for (Map.Entry<String,String> entry : batch.getMappingValues()) {                
                ediModel.insertEDIModel(entry.getKey(), entry.getValue());
            }
            //Need to clear mappingvalues since this BindingGroup can be used in the future. 
            batch.getMappingValues().clear();
        } catch (EDIParseException e) {
            throw new SmooksException("Could not insert data into edi model.", e);
        } catch (IOException e) {
            throw new SmooksException("Could not insert data into edi model.", e);
        }
    }


}
