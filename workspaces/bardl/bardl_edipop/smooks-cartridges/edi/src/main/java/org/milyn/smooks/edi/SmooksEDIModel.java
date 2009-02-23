package org.milyn.smooks.edi;

import org.milyn.container.ExecutionContext;
import org.milyn.edisax.EdifactModel;
import org.milyn.edisax.EDIParseException;
import org.milyn.io.StreamUtils;

import javax.xml.transform.Result;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Binds the {@link EdifactModel} to the Smooks framework.
 * @author bardl
 */
public class SmooksEDIModel {

    private static final String EDIFACT_MODEL_CONTEXT_KEY = SmooksEDIModel.class.getName() + "#CONTEXT_KEY";

    private EdifactModel edifactModel;

    private SmooksEDIModel() {
    }

    /**
     * Returns an instance of SmooksEDIModel.
     * @param executionContext the context that might contain the SmooksEDIModel.  
     * @param uri location of edi-message-mapping.
     * @return SmooksEDIModel containing a EdifactModel.
     * @throws IOException is thrown when reading edi-message-mapping.
     * @throws EDIParseException is thrown when problem occurs during parsing of edi-message-mapping.
     */
    public static SmooksEDIModel getSmooksEDIModel(ExecutionContext executionContext, String uri) throws IOException, EDIParseException {
		SmooksEDIModel repository = (SmooksEDIModel) executionContext.getAttribute(EDIFACT_MODEL_CONTEXT_KEY);

		if(repository == null) {
            InputStream input = new ByteArrayInputStream(StreamUtils.readStream(SmooksEDIModel.class.getResourceAsStream(uri)));
            EdifactModel edifactModel = new EdifactModel();
            edifactModel.parseSequence(input);

            repository = new SmooksEDIModel();
            repository.setEdifactModel(edifactModel);
            executionContext.setAttribute(EDIFACT_MODEL_CONTEXT_KEY, repository);
		}

		return repository;
	}

    /**
     * Insert value into {@link EdifactModel}.
     * @param ediPath the path to insert a value. Can be eparated by either '.' - or whitespace-character.
     * @param ediValue the value to insert.
     * @throws EDIParseException is thrown when insertion violates conditions set in the {@link EdifactModel}.    
     */
    public void insertEDIModel(String ediPath, String ediValue) throws EDIParseException {
        edifactModel.insertValue(ediPath, ediValue);
    }

    /**
     * Writes {@link EdifactModel} to {@link Result}.
     * @param encoding the encoding.
     * @param result the {@link Result}
     * @throws IOException is thrown when Result is not a StreamResult.
     * @throws EDIParseException is thrown when required Segments, Fields, Components or SubComponents are missing in model.
     */
    public void write(String encoding, Result result) throws IOException, EDIParseException {
        edifactModel.write(encoding, result);
    }

    /**
     * Returns the EdifactModel.
     * @return EdifactModel
     */
    public EdifactModel getEdifactModel() {
        return edifactModel;
    }

    /**
     * Sets the EdifactModel.
     * @param edifactModel the model.
     */
    public void setEdifactModel(EdifactModel edifactModel) {
        this.edifactModel = edifactModel;
    }
}
