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

package org.milyn.edisax.model;

import org.milyn.xml.XmlUtil;
import org.milyn.xml.XsdDOMValidator;
import org.milyn.io.StreamUtils;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDITypeEnum;
import org.milyn.edisax.model.internal.*;
import org.milyn.javabean.decoders.CustomDecoder;
import org.xml.sax.SAXException;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;


/**
 * Digests an edi-message-mapping and populates a {@link org.milyn.edisax.model.internal.Edimap}.
 *
 * @author bardl
 */
public class EDIConfigDigester {

    public static final String XSD_V10 = "http://www.milyn.org/schema/edi-message-mapping-1.0.xsd";
    public static final String XSD_V11 = "http://www.milyn.org/schema/edi-message-mapping-1.1.xsd";
    public static final String XSD_V12 = "http://www.milyn.org/schema/edi-message-mapping-1.2.xsd";
    public static final String XSD_V13 = "http://www.milyn.org/schema/edi-message-mapping-1.3.xsd";
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

    /**
     * Assert that schema used for validation are valid, i.e. the schema is
     * either edi-message-mapping-1.0 or edi-message-mapping-1.1.
     * @param ediNS the schema used for validation.
     * @return true if ediNS is valid, false otherwise.
     */
    private static boolean assertValidXSD(String ediNS) {
        return XSD_V10.equals(ediNS) || XSD_V11.equals(ediNS) || XSD_V12.equals(ediNS) || XSD_V13.equals(ediNS);
    }

    /**
     * Digest child elements of edimap.
     * @param configDoc the Edimap element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     * @param schemaName the schema uri.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when unable to retrieve namespace in configuration.
     */
    private static void digestXSDValidatedConfig(Document configDoc, Edimap edimap, String schemaName) throws EDIConfigurationException {

        //Retrieve the namespace for the schema.
        String namespacePrefix = retrieveNamespace(configDoc.getDocumentElement(), schemaName);

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
        delimiters.setSegment(getAttributeValue(node, "segment"));
        delimiters.setField(getAttributeValue(node, "field"));
        delimiters.setComponent(getAttributeValue(node, "component"));
        delimiters.setSubComponent(getAttributeValue(node, "sub-component"));
        delimiters.setEscape(getAttributeValue(node, "escape"));
    }

    /**
     * Digest attributes of Description element and populate Description.
     * @param node the Description element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     */
    private static void digestDescription(Node node, Edimap edimap) {
        Description description = new Description();
        edimap.setDescription(description);
        description.setName(getAttributeValue(node, "name"));
        description.setVersion(getAttributeValue(node, "version"));
    }

    /**
     * Digest attributes of Import element and populate Import.
     * @param node the Import element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     */
    private static void digestImport(Node node, Edimap edimap) {
        Import edimapImport = new Import();
        edimap.getImport().add(edimapImport);
        edimapImport.setResource(getAttributeValue(node, "resource"));
        edimapImport.setNamespace(getAttributeValue(node, "namespace"));
        edimapImport.setTruncatableFields(getNodeValueAsBoolean(node, "truncatableFields"));
        edimapImport.setTruncatableComponents(getNodeValueAsBoolean(node, "truncatableComponents"));
        edimapImport.setTruncatableSegments(getNodeValueAsBoolean(node, "truncatableSegments"));
    }

    /**
     * Digest attributes and child elements of Segments element. Populates Segments.
     * @param node the Segments element.
     * @param edimap the {@link org.milyn.edisax.model.internal.Edimap} to populate.
     * @param namespacePrefix the prefix used to name elements in xml.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void digestSegments(Node node, Edimap edimap, String namespacePrefix) throws EDIConfigurationException {
        SegmentGroup segments = new SegmentGroup();
        setValuesForMappingNode(node, segments, namespacePrefix);
        edimap.setSegments(segments);

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);

            digestSegmentGroup(currentNode, edimap.getSegments().getSegments(), namespacePrefix);
        }
    }

    /**
     * Digests attributes and child elements of Segment element.
     * @param node the Segment element.
     * @param segmentGroup the {@link org.milyn.edisax.model.internal.SegmentGroup} to populate.
     * @param namespacePrefix the prefix used to name elements in xml.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void digestSegment(Node node, SegmentGroup segmentGroup, String namespacePrefix) throws EDIConfigurationException {

        if(segmentGroup instanceof Segment) {
            Segment segment = (Segment) segmentGroup;
            setValuesForSegment(segment, node, namespacePrefix);

            NodeList nodes = node.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node currentNode = nodes.item(i);

                if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "field")) {
                    Field field = new Field();
                    segment.getFields().add(field);
                    digestField(currentNode, field, namespacePrefix);
                } else {
                    digestSegmentGroup(currentNode, segment.getSegments(), namespacePrefix);
                }
            }
        } else {
            segmentGroup.setMaxOccurs(getNodeValueAsInteger(node, "maxOccurs"));
            segmentGroup.setMinOccurs(getNodeValueAsInteger(node, "minOccurs"));
            setValuesForMappingNode(node, segmentGroup, namespacePrefix);

            NodeList nodes = node.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node currentNode = nodes.item(i);

                digestSegmentGroup(currentNode, segmentGroup.getSegments(), namespacePrefix);
            }
        }
    }

    private static boolean digestSegmentGroup(Node currentNode, List<SegmentGroup> segmentGroupList, String namespacePrefix) throws EDIConfigurationException {

        if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "segmentGroup")) {
            SegmentGroup segment = new SegmentGroup();
            segment.setDocumentation(getNodeValue(currentNode, namespacePrefix + "documentation"));
            segmentGroupList.add(segment);
            digestSegment(currentNode, segment, namespacePrefix);

            return true;
        } else if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "segment")) {
            Segment segment = new Segment();
            segmentGroupList.add(segment);
            digestSegment(currentNode, segment, namespacePrefix);

            return true;
        }

        return false;
    }

    /**
     * Digests attributes and child elements of Field element.
     * @param node the Field element.
     * @param field the {@link org.milyn.edisax.model.internal.Field} to populate
     * @param namespacePrefix the prefix used to name elements in xml.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void digestField(Node node, Field field, String namespacePrefix) throws EDIConfigurationException {
        setValuesForField(field, node, namespacePrefix);

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
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void digestComponent(Node node, Component component, String namespacePrefix) throws EDIConfigurationException {
        setValuesForComponent(component, node, namespacePrefix);

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);

            if (currentNode.getNodeName().equalsIgnoreCase(namespacePrefix + "sub-component")) {
                SubComponent subComponent = new SubComponent();
                component.getSubComponent().add(subComponent);
                setValuesForSubComponent(currentNode, subComponent, namespacePrefix);
            }
        }
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.Segment}.
     * @param segment the {@link org.milyn.edisax.model.internal.Segment} to populate.
     * @param node the Segment element.
     */
    private static void setValuesForSegment(Segment segment, Node node, String namespacePrefix) {
        segment.setMaxOccurs(getNodeValueAsInteger(node, "maxOccurs"));
        segment.setMinOccurs(getNodeValueAsInteger(node, "minOccurs"));
        segment.setSegcode(getAttributeValue(node, "segcode"));
        segment.setSegref(getAttributeValue(node, "segref"));
        segment.setTruncatable(getNodeValueAsBoolean(node, "truncatable"));
        segment.setDescription(getAttributeValue(node, "description"));
        setValuesForMappingNode(node, segment, namespacePrefix);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.Field}.
     * @param field the {@link org.milyn.edisax.model.internal.Field} to populate.
     * @param node the Field element.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void setValuesForField(Field field, Node node, String namespacePrefix) throws EDIConfigurationException {
        field.setRequired(getNodeValueAsBoolean(node, "required"));
        field.setTruncatable(getNodeValueAsBoolean(node, "truncatable"));
        setValuesForValueNode(node, field, namespacePrefix);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.Component}.
     * @param component the {@link org.milyn.edisax.model.internal.Component} to populate.
     * @param node the Component element.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void setValuesForComponent(Component component, Node node, String namespacePrefix) throws EDIConfigurationException {
        component.setRequired(getNodeValueAsBoolean(node, "required"));
        component.setTruncatable(getNodeValueAsBoolean(node, "truncatable"));
        setValuesForValueNode(node, component, namespacePrefix);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.SubComponent}.
     * @param node the {@link org.milyn.edisax.model.internal.SubComponent} to populate.
     * @param subComponent the SubComponent element.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void setValuesForSubComponent(Node node, SubComponent subComponent, String namespacePrefix) throws EDIConfigurationException {
        subComponent.setRequired(getNodeValueAsBoolean(node, "required"));
        setValuesForValueNode(node, subComponent, namespacePrefix);
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.MappingNode}.
     * @param node the {@link org.milyn.edisax.model.internal.MappingNode} to populate.
     * @param mappingNode the MappingNode element.
     */
    private static void setValuesForMappingNode(Node node, MappingNode mappingNode, String namespacePrefix) {
        mappingNode.setXmltag(getAttributeValue(node, "xmltag"));
        mappingNode.setDocumentation(getNodeValue(node, namespacePrefix + "documentation"));
    }

    /**
     * Set values in {@link org.milyn.edisax.model.internal.ValueNode}.
     * @param node the {@link org.milyn.edisax.model.internal.ValueNode} to populate.
     * @param valueNode the ValueNode element.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when values are badly formatted.
     */
    private static void setValuesForValueNode(Node node, ValueNode valueNode, String namespacePrefix) throws EDIConfigurationException {
        setValuesForMappingNode(node, valueNode, namespacePrefix);
        valueNode.setType(getAttributeValue(node, "type"));
        valueNode.setMinLength(getNodeValueAsInteger(node, "minLength"));
        valueNode.setMaxLength(getNodeValueAsInteger(node, "maxLength"));
        digestParameters(valueNode, getAttributeValue(node, "typeParameters"));
    }

    /**
     * Digests parameters from parameters attribute and insertsthe parameters into
     * the valueNode. If first parameter is not a key-value-pair the parameter is
     * considered to be a custom class name.
     * @param valueNode the valueNode to populate.
     * @param value the parameters as a string value.
     * @throws EDIConfigurationException is thrown when parameters are used incorrectly.
     */
    private static void digestParameters(ValueNode valueNode, String value) throws EDIConfigurationException {

        if (value != null && !value.equals("")) {
            List<Map.Entry<String, String>> result = new ArrayList<Map.Entry<String, String>>();
            String[] parameters = value.split(";");
            String[] entry;
            String customClass = null;
            for (int i = 0; i < parameters.length; i++) {
                String parameter = parameters[i];
                entry = parameter.split("=");
                if (entry.length == 1) {
                    if (i == 0) {
                        customClass = entry[0];
                        result.add(new ParamEntry<String, String>(CustomDecoder.CLASS_PROPERTY_NAME, entry[0]));
                    } else {
                        throw new EDIConfigurationException("Invalid use of paramaters in ValueNode. A parameter-entry should consist of a key-value-pair separated with the '='-character. Example: [parameters=\"key1=value1;key2=value2\"]");
                    }
                } else if (entry.length == 2) {
                    result.add(new ParamEntry<String, String>(entry[0], entry[1]));
                } else {
                    throw new EDIConfigurationException("Invalid use of paramaters in ValueNode. A parameter-entry should consist of a key-value-pair separated with the '='-character. Example: [parameters=\"key1=value1;key2=value2\"]");
                }
            }
            valueNode.setTypeParameters(result);

            if ( valueNode.getType().equals(EDITypeEnum.CUSTOM_NAME) && customClass == null) {
                throw new EDIConfigurationException("When using the Custom type in ValueNode the custom class type must exist as the first element in parameters");
            } else if ( customClass != null && !valueNode.getType().equals(EDITypeEnum.CUSTOM_NAME)) {
                throw new EDIConfigurationException("When first parameter in list of parameters is not a key-value-pair the type of the ValueNode should be Custom.");
            }

        }
    }

    /**
     * Gets attribute value from node if it exists. Otherwise returns null.
     * @param node the node.
     * @param name the name of the attribute.
     * @return Boolean value if attribute exists in node.
     */
    private static Boolean getNodeValueAsBoolean(Node node, String name) {
        String value = getAttributeValue(node, name);
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
        String value = getAttributeValue(node, name);
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
    private static String getAttributeValue(Node node, String name) {
        return node.getAttributes().getNamedItem(name) != null ? node.getAttributes().getNamedItem(name).getNodeValue() : null;
    }

    /**
     * Gets attribute value from node if it exists. Otherwise returns null.
     * @param node the node.
     * @param name the name of the attribute.
     * @return String value if attribute exists in node.
     */
    private static String getNodeValue(Node node, String name) {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals(name)) {
                return n.getTextContent();
            } else if (node.hasChildNodes()) {
                String value = getNodeValue(n, name);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;  //().getNamedItem(name) != null ? node.getAttributes().getNamedItem(name).getNodeValue() : null;
    }

    /**
     * Retrives the namespaceprefix from edimap element in edi-message-mapping.
     * @param documentElement the edimap element.
     * @param schemaName the schema uri.
     * @return the namespaceprefix used in the edi-message-mapping.
     * @throws org.milyn.edisax.EDIConfigurationException is thrown when no namespace exists in configuration.
     */
    private static String retrieveNamespace(Element documentElement, String schemaName) throws EDIConfigurationException {
        NamedNodeMap attributes = documentElement.getAttributes();
        Node node;
        for (int i = 0; i < attributes.getLength(); i++) {
            node = attributes.item(i);
            if (node.getNodeValue().equals(schemaName)) {
                String prefix = node.getNodeName();
                if (prefix.startsWith("xmlns:")) {
                    return node.getNodeName().replace("xmlns:", "") + NAMESPACE_SUFFIX;
                } else if (prefix.startsWith("xmlns")) {
                    return node.getNodeName().replace("xmlns", "");
                } else {
                    throw new EDIConfigurationException("No namespace exists in edi-message-mapping. A namespace must be declared in order to digest configuration file.");
                }
            }
        }
        return "";
    }
}
