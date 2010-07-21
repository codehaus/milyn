/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.edisax.util;

import org.milyn.edisax.model.internal.*;
import org.milyn.util.ClassUtil;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;

/**
 * EdimapWriter
 * @author bardl
 */
public class EdimapWriter {

    private static DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    static {
        docBuilderFactory.setValidating(false);
        docBuilderFactory.setNamespaceAware(true);
    }

    private static final String NS = "http://www.milyn.org/schema/edi-message-mapping-1.4.xsd";

    private Document doc;

    private EdimapWriter() throws ParserConfigurationException {
        doc = docBuilderFactory.newDocumentBuilder().newDocument();
    }

    public static void write(Edimap edimap, Writer writer) throws IOException {
        try {
            EdimapWriter edimapWriter = new EdimapWriter();

            edimapWriter.write(edimap);

            XmlUtil.serialize(edimapWriter.doc, true, writer);
            writer.flush();
        } catch (ParserConfigurationException e) {
            IOException ioE = new IOException("Error constructing EDI Mapping Model");
            ioE.initCause(e);
            throw ioE;
        }
    }

    private void write(Edimap edimap) {
        Element edimapEl = newElement("edimap", doc);

        addImports(edimap.getImports(), edimapEl);
        addDescription(edimap.getDescription(), edimapEl);
        addDelimiters(edimap.getDelimiters(), edimapEl);

        SegmentGroup segments = edimap.getSegments();
        Element segmentsEl = newElement("segments", edimapEl, segments);

        mapBeanProperties(segments, segmentsEl, "xmltag");
        addChildSegments(segments, segmentsEl);
    }

    private void addImports(List<Import> imports, Element edimapEl) {
        for(Import importInst : imports) {
            mapBeanProperties(importInst, newElement("import", edimapEl), "resource", "namespace", "truncatableComponents", "truncatableFields", "truncatableSegments");
        }
    }

    private void addDescription(Description description, Element edimapEl) {
        mapBeanProperties(description, newElement("description", edimapEl), "name", "version");
    }

    private void addDelimiters(Delimiters delimiters, Element edimapEl) {
        mapBeanProperties(delimiters, newElement("delimiters", edimapEl), "segment", "field", "component", "subComponent|sub-component", "escape", "fieldRepeat");
    }

    private void addChildSegments(SegmentGroup segmentGroup, Element parentSegment) {
        List<SegmentGroup> childSegments = segmentGroup.getSegments();

        for(SegmentGroup childSegment : childSegments) {
            Element segmentEl;

            if(childSegment instanceof Segment) {
                segmentEl = newElement("segment", parentSegment, childSegment);
                mapBeanProperties(childSegment, segmentEl, "segcode", "description", "ignoreUnmappedFields", "nodeTypeRef", "truncatable");

                addFields(((Segment)childSegment).getFields(), segmentEl);
            } else {
                segmentEl = newElement("segmentGroup", parentSegment, childSegment);
            }

            mapBeanProperties(childSegment, segmentEl, "xmltag", "minOccurs", "maxOccurs");

            addChildSegments(childSegment, segmentEl);
        }
    }

    private void addFields(List<Field> fields, Element segmentEl) {
        for(Field field : fields) {
            Element fieldEl = newElement("field", segmentEl, field);

            mapBeanProperties(field, fieldEl, "xmltag", "truncatable", "maxLength", "minLength", "required", "dataType", "dataTypeParametersString|dataTypeParameters");
            addComponents(field.getComponents(), fieldEl);
        }
    }

    private void addComponents(List<Component> components, Element fieldEl) {
        for(Component component : components) {
            Element componentEl = newElement("component", fieldEl, component);

            mapBeanProperties(component, componentEl, "xmltag", "truncatable", "maxLength", "minLength", "required", "dataType", "dataTypeParametersString|dataTypeParameters");
            addSubComponents(component.getSubComponents(), componentEl);
        }
    }

    private void addSubComponents(List<SubComponent> subComponents, Element componentEl) {
        for(SubComponent subComponent : subComponents) {
            Element subComponentEl = newElement("sub-component", componentEl, subComponent);

            mapBeanProperties(subComponent, subComponentEl, "xmltag", "maxLength", "minLength", "required", "dataType", "dataTypeParametersString|dataTypeParameters");
        }
    }

    private void mapBeanProperties(Object bean, Element target, String... properties) {
        for(String property : properties) {
            String[] propertyTokens = property.split("\\|");
            String propertyName;
            String attributeName;

            if(propertyTokens.length == 2) {
                propertyName = propertyTokens[0];
                attributeName = propertyTokens[1];                
            } else {
                propertyName = property;
                attributeName = property;
            }

            Object value = getBeanValue(bean, propertyName);

            if(value != null) {
                target.setAttribute(attributeName, XmlUtil.removeEntities(value.toString()));
            }
        }
    }

    private Object getBeanValue(Object bean, String property) {
        String getterMethodName = ClassUtil.toGetterName(property);
        Method getterMethod = ClassUtil.getGetterMethod(getterMethodName, bean, null);

        if(getterMethod == null) {
            getterMethodName = ClassUtil.toIsGetterName(property);
            getterMethod = ClassUtil.getGetterMethod(getterMethodName, bean, null);
        }

        if(getterMethod != null) {
            try {
                return getterMethod.invoke(bean);
            } catch (Exception e) {
                throw new IllegalStateException("Error invoking getter method '" + getterMethodName + "' on Object type '" + bean.getClass().getName() + "'.", e);
            }
        }

        return null;
    }

    private Element newElement(String name, Node parent) {
        Element element =  doc.createElementNS(NS, "medi:" + name);
        parent.appendChild(element);
        return element;
    }

    private Element newElement(String name, Node parent, MappingNode mappingNode) {
        Element element = newElement(name, parent);

        if(mappingNode != null && mappingNode.getDocumentation() != null) {
            Element documentation = newElement("documentation", element);
            documentation.appendChild(doc.createTextNode(mappingNode.getDocumentation()));
        }

        return element;
    }
}
