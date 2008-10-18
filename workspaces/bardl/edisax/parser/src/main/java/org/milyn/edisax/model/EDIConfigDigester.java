package org.milyn.edisax.model;

import org.milyn.xml.XmlUtil;
import org.milyn.xml.XsdDOMValidator;
import org.milyn.io.StreamUtils;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.model.internal.*;
import org.xml.sax.SAXException;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * Digests an edi-message-mapping and populates a {@link org.milyn.edisax.model.internal.Edimap}.
 *
 * @author bardl
 */
public class EDIConfigDigester {

    public static final String XSD_V10 = "http://www.milyn.org/schema/edi-message-mapping-1.0.xsd";
    public static final String XSD_V11 = "http://www.milyn.org/schema/edi-message-mapping-1.1.xsd";
    private static final String NAMESPACE_SUFFIX = ":";

    /**
     * Digest the XML edi-message-mapping configuration stream.
     * @param stream the edi-message-mapping stream.
     * @return the {@link org.milyn.edisax.model.internal.Edimap}.
     * @throws IOException Error parsing the XML stream.
     * @throws SAXException Error parsing the XML stream.
     * @throws EDIConfigurationException Multiple or no namespaces in edi-message-mapping. 
     */
    public static Edimap digestConfig(InputStream stream) throws IOException, SAXException, EDIConfigurationException {
        Document configDoc;
        byte[] streamBuffer = StreamUtils.readStream(stream);

        try {
            configDoc = XmlUtil.parseStream(new ByteArrayInputStream(streamBuffer));
        } catch (ParserConfigurationException ee) {
            throw new SAXException("Unable to parse Smooks configuration.", ee);
        }

        XsdDOMValidator validator = new XsdDOMValidator(configDoc);

        if (validator.getNamespaces().size() == 0) {
            throw new EDIConfigurationException("The edi-message-mapping configuration must contain a namespace.");
        }
        if (validator.getNamespaces().size() > 1) {
            throw new EDIConfigurationException("Unsupported use of multiple configuration namespaces from inside the edi-message-mapping configuration.");
        }

        String ediNS = validator.getNamespaces().get(0).toString();
        validator.validate();

        Edimap edimap = new Edimap();

        if(assertValidXSD(ediNS)) {
            digestXSDValidatedConfig(configDoc,  edimap, ediNS);
        } else {
            throw new SAXException("Cannot parse edi-message-mapping configuration.  Unsupported default Namespace '" + ediNS + "'.");
        }
        return edimap;
    }

    private static boolean assertValidXSD(String ediNS) {
        return XSD_V10.equals(ediNS) || XSD_V11.equals(ediNS);
    }

    /**
     * Digest child elements of edimap.
     * @param configDoc the Edimap element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     * @param schemaName the schema uri.
     */
    private static void digestXSDValidatedConfig(Document configDoc, Edimap edimap, String schemaName) {

        //Retrieve the namespace for the schema.
        String namespacePrefix = retrieveNamespace(configDoc.getDocumentElement(), schemaName) + NAMESPACE_SUFFIX;

        NodeList nodes = configDoc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeName().equalsIgnoreCase(namespacePrefix + "import")) {
                digestImport(node, edimap);
            } else if (node.getNodeName().equalsIgnoreCase(namespacePrefix + "description")) {
                digestDescription(node, edimap);
            } else if (node.getNodeName().equalsIgnoreCase(namespacePrefix + "delimiters")) {
                digestDelimiters(node, edimap);
            } else if (node.getNodeName().equalsIgnoreCase(namespacePrefix + "segments")) {
                digestSegments(node, edimap, namespacePrefix);
            }
        }
    }

    /**
     * Digest attributes of Delimiter element and populate Delimiter.
     * @param node the Delimiter element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     */
    private static void digestDelimiters(Node node, Edimap edimap) {
        Delimiters delimiters = new Delimiters();
        edimap.setDelimiters(delimiters);
        delimiters.setSegment(getNodeValue(node, "segment"));
        delimiters.setField(getNodeValue(node, "field"));
        delimiters.setComponent(getNodeValue(node, "component"));
        delimiters.setSubComponent(getNodeValue(node, "sub-component"));
    }

    /**
     * Digest attributes of Description element and populate Description.
     * @param node the Description element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     */
    private static void digestDescription(Node node, Edimap edimap) {
        Description description = new Description();
        edimap.setDescription(description);
        description.setName(getNodeValue(node, "name"));
        description.setVersion(getNodeValue(node, "version"));
    }

    /**
     * Digest attributes of Import element and populate Import.
     * @param node the Import element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     */
    private static void digestImport(Node node, Edimap edimap) {
        Import edimapImport = new Import();
        edimap.getImport().add(edimapImport);
        edimapImport.setName(getNodeValue(node, "name"));
        edimapImport.setNamespace(getNodeValue(node, "namespace"));
        edimapImport.setTruncatableFields(getNodeValueAsBoolean(node, "truncatableFields"));
        edimapImport.setTruncatableComponents(getNodeValueAsBoolean(node, "truncatableComponents"));
    }

    /**
     * Digest attributes and child elements of Segments element. Populates Segments.
     * @param node the Segments element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     * @param namespacePrefix the prefix used to name elements in xml. 
     */
    private static void digestSegments(Node node, Edimap edimap, String namespacePrefix) {
        Segments segments = new Segments();
        setValuesForMappingNode(node, segments);
        edimap.setSegments(segments);

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);

            if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "segment")) {
                Segment segment = new Segment();
                edimap.getSegments().getSegment().add(segment);
                digestSegment(currentNode, segment, namespacePrefix);
            }
        }
    }

    /**
     * Digests attributes and child elements of Segment element.
     * @param node the Segment element.
     * @param segment the {@link org.milyn.edisax.model.internal.Segment} to populate.
     * @param namespacePrefix the prefix used to name elements in xml.
     */
    private static void digestSegment(Node node, Segment segment, String namespacePrefix) {
        setValuesForSegment(segment, node);

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);

            if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "field")) {
                Field field = new Field();
                segment.getField().add(field);
                digestField(currentNode, field, namespacePrefix);
            } else if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "segment")) {
                Segment newSegment = new Segment();
                segment.getSegment().add(newSegment);
                digestSegment(currentNode, newSegment, namespacePrefix);
            }
        }
    }

    /**
     * Digests attributes and child elements of Field element.
     * @param node the Field element.
     * @param field the {@link org.milyn.edisax.model.internal.Field} to populate
     * @param namespacePrefix the prefix used to name elements in xml.
     */
    private static void digestField(Node node, Field field, String namespacePrefix) {
        setValuesForField(field, node);

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);

            if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "component")) {
                Component component = new Component();
                field.getComponent().add(component);
                digestComponent(currentNode, component, namespacePrefix);
            }
        }
    }

    /**
     * Digests attributes and child elements of Component element.
     * @param node the Component element.
     * @param component the {@link org.milyn.edisax.model.internal.Component} to populate.
     * @param namespacePrefix the prefix used to name elements in xml.
     */
    private static void digestComponent(Node node, Component component, String namespacePrefix) {
        setValuesForComponent(component, node);

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);

            if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "sub-component")) {
                SubComponent subComponent = new SubComponent();
                component.getSubComponent().add(subComponent);
                setValuesForSubComponent(currentNode, subComponent);
            }
        }
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.Segment}.
     * @param segment the {@link org.milyn.edisax.model.internal.Segment} to populate.
     * @param node the Segment element.
     */
    private static void setValuesForSegment(Segment segment, Node node) {
        segment.setMaxOccurs(getNodeValueAsInteger(node, "maxOccurs"));
        segment.setMinOccurs(getNodeValueAsInteger(node, "minOccurs"));
        segment.setSegcode(getNodeValue(node, "segcode"));
        segment.setSegref(getNodeValue(node, "segref"));
        segment.setTruncatable(getNodeValueAsBoolean(node, "truncatable"));
        setValuesForMappingNode(node, segment);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.Field}.
     * @param field the {@link org.milyn.edisax.model.internal.Field} to populate.
     * @param node the Field element.
     */
    private static void setValuesForField(Field field, Node node) {
        field.setRequired(getNodeValueAsBoolean(node, "required"));
        field.setTruncatable(getNodeValueAsBoolean(node, "truncatable"));
        setValuesForMappingNode(node, field);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.Component}.
     * @param component the {@link org.milyn.edisax.model.internal.Component} to populate.
     * @param node the Component element.
     */
    private static void setValuesForComponent(Component component, Node node) {
        component.setRequired(getNodeValueAsBoolean(node, "required"));
        component.setTruncatable(getNodeValueAsBoolean(node, "truncatable"));
        setValuesForMappingNode(node, component);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.SubComponent}.
     * @param node the {@link org.milyn.edisax.model.internal.SubComponent} to populate.
     * @param subComponent the SubComponent element.
     */
    private static void setValuesForSubComponent(Node node, SubComponent subComponent) {
        subComponent.setRequired(getNodeValueAsBoolean(node, "required"));
        setValuesForMappingNode(node, subComponent);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.MappingNode}.
     * @param node the {@link org.milyn.edisax.model.internal.MappingNode} to populate.
     * @param mappingNode the MappingNode element.
     */
    private static void setValuesForMappingNode(Node node, MappingNode mappingNode) {
        mappingNode.setXmltag(getNodeValue(node, "xmltag"));
    }

    /**
     * Gets attribute value from node if it exists. Otherwise returns null.
     * @param node the node.
     * @param name the name of the attribute.
     * @return Boolean value if attribute exists in node.
     */
    private static Boolean getNodeValueAsBoolean(Node node, String name) {
        String value = getNodeValue(node, name);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets attribute value from node if it exists. Otherwise returns null.
     * @param node the node.
     * @param name the name of the attribute.
     * @return Integer value if attribute exists in node.
     */
    private static Integer getNodeValueAsInteger(Node node, String name) {
        String value = getNodeValue(node, name);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    /**
     * Gets attribute value from node if it exists. Otherwise returns null.
     * @param node the node.
     * @param name the name of the attribute.
     * @return String value if attribute exists in node.
     */
    private static String getNodeValue(Node node, String name) {
        return node.getAttributes().getNamedItem(name) != null ? node.getAttributes().getNamedItem(name).getNodeValue() : null;
    }

    /**
     * Retrives the namespaceprefix from edimap element in edi-message-mapping.
     * @param documentElement the edimap element.
     * @param schemaName the schema uri.
     * @return the namespaceprefix used in the edi-message-mapping.
     */
    private static String retrieveNamespace(Element documentElement, String schemaName) {
        NamedNodeMap attributes = documentElement.getAttributes();
        Node node;
        for (int i = 0; i < attributes.getLength(); i++) {
            node = attributes.item(i);
            if (node.getNodeValue().equals(schemaName)) {
                return node.getNodeName().replace("xmlns:", "");
            }
        }
        return "";
    }
}
