package org.milyn.edisax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.schema.edi_message_mapping_1_0.*;
import org.milyn.schema.edi_message_mapping_1_0.Component;
import org.milyn.schema.edi_message_mapping_1_0.Edimap;
import org.milyn.schema.edi_message_mapping_1_0.Field;
import org.milyn.schema.edi_message_mapping_1_0.SubComponent;
import org.milyn.schema.edi_definition_1_0.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

/**
 * EdifactModel contains edi-message-mapping model and edi-definition if it exists.
 *
 *
 */
public class EdifactModel {
    private static Log LOG = LogFactory.getLog(EdifactModel.class);

    private org.milyn.schema.edi_message_mapping_1_0.Edimap sequence;
    private Map<String, List<Field>> definitionMap;

    private static JAXBContext JAXB_DEF;
    private static JAXBContext JAXB_SEQ;

    static {
        try {
            JAXB_DEF = JAXBContext.newInstance(org.milyn.schema.edi_definition_1_0.Edimap.class);
            JAXB_SEQ = JAXBContext.newInstance(org.milyn.schema.edi_message_mapping_1_0.Edimap.class);
        } catch (JAXBException e) {
            LOG.error("Could not create new instance of JAXBContext.", e);
        }
    }

    /**
     * Returns the edi-message-mapping containing the sequence definition of edifact format.
     * @return edi-message-mapping.
     */
    public Edimap getSequence() {
        return sequence;
    }

    /**
     * Sets the edi-message-mapping containing the sequence definition of edifact format.
     * @param sequence the edi-message-mapping
     */
    public void setSequence(Edimap sequence) {
        this.sequence = sequence;
    }

    /**
     * Returns the delimiters used in edifact format.
     * @return delimiters.
     */
    public Delimiters getDelimiters() {
        return sequence.getDelimiters();
    }

    /**
     * Returns a List<Field> given a segment code. All field-lists are
     * located in a Map with the segment code as key.
     * @param segCode specifies which segment definition that should be located.
     * @return list of fields.
     */
    public List<Field> getFields(String segCode) {
        if (definitionMap == null) {
            return null;
        }
        return definitionMap.get(segCode);
    }

    /**
     * Parse the edifact sequence specified in the edi-message-mapping.
     * @param inputStream the edi-message-mapping.
     */
    public void parseSequence(InputStream inputStream) {
        try {
            Unmarshaller _unmarshaller = JAXB_SEQ.createUnmarshaller();
            sequence = (org.milyn.schema.edi_message_mapping_1_0.Edimap)_unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            LOG.error("Could not parse xml containing edifact sequence.", e);
        }
    }

    /**
     * Parse the edifact definitions specified in the edi-definition. The edi-definition is not
     * stored in EdifactModel. Each SegmentDefinition is instead stored in the definitionMap for
     * fast lookup during parsing.
     * @param inputStream the edi-definition.
     */
    public void parseDefinition(InputStream inputStream) {
        try {
            Unmarshaller _unmarshaller = JAXB_DEF.createUnmarshaller();
            org.milyn.schema.edi_definition_1_0.Edimap definition = (org.milyn.schema.edi_definition_1_0.Edimap)_unmarshaller.unmarshal(inputStream);

            //Insert segment definitions into hashmap.
            definitionMap = new HashMap<String, List<Field>>();
            for (SegmentDefinition sdefinition : definition.getSegmentDefinitions().getSegmentDefinition()) {
                definitionMap.put(sdefinition.getSegcode(), convertFieldDefinitions(sdefinition.getField()));
            }
        } catch (JAXBException e) {
            LOG.error("Could not parse xml containing edifact definition.", e);
        }
    }

    /**
     * Converts all fields of type org.milyn.schema.edi_definition_1_0.Field into
     * type org.milyn.schema.edi_message_mapping_1_0.Field.
     * @param fields the fields to convert.
     * @return return converted fields.
     */
    private List<Field> convertFieldDefinitions(List<org.milyn.schema.edi_definition_1_0.Field> fields) {
        List<Field> result = new ArrayList<Field>();
        Field newField;

        for (org.milyn.schema.edi_definition_1_0.Field field : fields) {
            newField = new Field();
            newField.setRequired(field.isRequired());
            newField.setTruncatable( isTruncatable( sequence.getDefinition().getTruncatableFields(), field.isTruncatable()) );
            newField.setXmltag(field.getXmltag());
            newField.getComponent().addAll(convertComponentDefinitions(field.getComponent()));
            result.add(newField);
        }
        return result;
    }

    /**
     * Converts all Components of type org.milyn.schema.edi_definition_1_0.Component into
     * type org.milyn.schema.edi_message_mapping_1_0.Component. 
     * @param components the component to convert.
     * @return return converted components.
     */
    private List<Component> convertComponentDefinitions(List<org.milyn.schema.edi_definition_1_0.Field.Component> components) {
        List<Component> result = new ArrayList<Component>();
        Component newComponent;
        for (org.milyn.schema.edi_definition_1_0.Field.Component component : components) {
            newComponent = new Component();
            newComponent.setRequired(component.isRequired());
            newComponent.setTruncatable( isTruncatable(sequence.getDefinition().getTruncatableComponents(), component.isTruncatable()) );
            newComponent.setXmltag(component.getXmltag());
            newComponent.getSubComponent().addAll( convertSubComponentDefinitions(component.getSubComponent()) );
            result.add(newComponent);
        }
        return result;
    }

    /**
     * Converts all SubComponents of type org.milyn.schema.edi_definition_1_0.SubComponent into
     * type org.milyn.schema.edi_message_mapping_1_0.SubComponent.
     * @param subComponents the subComponents to convert.
     * @return return converted subComponents.
     */
    private List<SubComponent> convertSubComponentDefinitions(List<org.milyn.schema.edi_definition_1_0.SubComponent> subComponents) {
        List<SubComponent> result = new ArrayList<SubComponent>();
        SubComponent newSubComponent;
        for (org.milyn.schema.edi_definition_1_0.SubComponent component : subComponents) {
            newSubComponent = new SubComponent();
            newSubComponent.setRequired(component.isRequired());
            newSubComponent.setXmltag(component.getXmltag());
            result.add(newSubComponent);
        }
        return result;
    }

    /**
     * Returns truncatable set in edi-message-mapping if it exists. Otherwise it sets value
     * found in the edi-definition.
     * @param truncatableMessageMapping truncatable value found in edi-message-mapping
     * @param truncatableDefinition truncatable value found in edi-definition
     * @return truncatable from edi-message-mapping if it exists, otherwise return value from edi-definition.
     */
    private Boolean isTruncatable(String truncatableMessageMapping, boolean truncatableDefinition) {
        Boolean result = truncatableDefinition;
        if (truncatableMessageMapping != null) {
            result = Boolean.parseBoolean( truncatableMessageMapping );
        }
        return result;
    }
}


