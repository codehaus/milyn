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
package org.milyn.ejc;

import org.milyn.ejc.classes.*;
import org.apache.commons.logging.Log;

import java.io.*;
import java.util.Map;

/**
 * BindingWriter generates a bindingfile based on classstructure found in ClassModel.
 * @author bardl
 */
public class BindingWriter {

    private static Log LOG = EJCLogFactory.getLog(EdiConfigReader.class);

    private static final String CONFIG_START = "<?xml version=\"1.0\"?>\n<smooks-resource-list xmlns=\"http://www.milyn.org/xsd/smooks-1.1.xsd\" xmlns:edi=\"http://www.milyn.org/xsd/smooks/edi-1.1.xsd\" xmlns:jb=\"http://www.milyn.org/xsd/smooks/javabean-1.2.xsd\">";
    private static final String CONFIG_END = "</smooks-resource-list>";
    private static final String FILTER_PARAM = "<params><param name=\"stream.filter.type\">SAX</param></params>";
    private static final String EDI_READER = "<edi:reader mappingModel=\"%1$s\" />";

    private static final String WIRING_ELEMENT = "jb:wiring";

    private static final String PROPERTY_ATTRIBUTE = "property";
    private static final String BEAN_ID_REF_ATTRIBUTE = "beanIdRef";
    private static final String DATA_ATTRIBUTE = "data";
    private static final String DECODER_ATTRIBUTE = "decoder";
    private static final String VALUE_ELEMENT = "jb:value";
    private static final String BEAN_ID_ATTRIBUTE = "beanId";
    private static final String BINDINGS_ELEMENT = "jb:bean";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String CREATE_ON_ELEMENT_ATTRIBUTE = "createOnElement";

    private static final String ELEMENT_START = "<";
    private static final String END_ELEMENT_START = "</";
    private static final String ELEMENT_END = ">\n";
    private static final String LEAF_ELEMENT_END = "/>\n";
    private static final String NEWLINE = "\n";
    private static final String DECODE_PARAM_ELEMENT = "jb:decodeParam";
    private static final String NAME_ATTRIBUTE = "name";

    public static void parse(ClassModel model, String bindingFile, String ediConfigFile) throws IOException, IllegalNameException {

        JClass root = model.getRoot();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(bindingFile));
            writer.write(CONFIG_START);
            addEdiReaderConfig(writer, ediConfigFile);
            parseClass(root, writer);
            writer.write(CONFIG_END);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void addEdiReaderConfig(Writer writer, String ediConfigFile) throws IOException {
        writer.write ("\n" + FILTER_PARAM + "\n");        
        writer.write (String.format(EDI_READER, ediConfigFile));
    }

    private static void parseClass(JClass clazz, Writer writer) throws IOException, IllegalNameException {
        LOG.debug("Creating binding for class " + clazz.getFullName());

        createStartElement(writer, BINDINGS_ELEMENT);
        addAttribute(writer, BEAN_ID_ATTRIBUTE, getBeanId(clazz));
        addAttribute(writer, CLASS_ATTRIBUTE, clazz.getFullName());
        addAttribute(writer, CREATE_ON_ELEMENT_ATTRIBUTE, clazz.getXmlElementName());
        endStartElement(writer, true, true);

        for (JAttribute attribute : clazz.getAttributes()) {
            if (attribute.getType() instanceof JSimpleType) {
                JSimpleType simpleType = (JSimpleType)attribute.getType();
                createStartElement(writer, VALUE_ELEMENT);
                addAttribute(writer, PROPERTY_ATTRIBUTE, attribute.getName());
                addAttribute(writer, DATA_ATTRIBUTE, clazz.getXmlElementName() + "/" + attribute.getXmlElementName());

                if (!attribute.getType().equals(JJavaClass.STRING)) {
                    addAttribute(writer, DECODER_ATTRIBUTE, attribute.getType().getName());
                    if ( simpleType.getParameters() != null ) {
                        endStartElement(writer, true, true);
                        
                        for (Map.Entry<String, String> entry : simpleType.getParameters()) {
                            createStartElement(writer, DECODE_PARAM_ELEMENT);
                            addAttribute(writer, NAME_ATTRIBUTE, entry.getKey());
                            endStartElement(writer, true, false);

                            addValue(writer, entry.getValue());
                            createEndElement(writer, DECODE_PARAM_ELEMENT);
                        }

                        createEndElement(writer, VALUE_ELEMENT);
                    } else {
                        endStartElement(writer, false, true);
                    }
                } else {
                    endStartElement(writer, false, true);
                }
            } else { // ClassType                
                createStartElement(writer, WIRING_ELEMENT);
                addAttribute(writer, PROPERTY_ATTRIBUTE, attribute.getName());                
                addAttribute(writer, BEAN_ID_REF_ATTRIBUTE, getBeanId(attribute.getType()));                
                endStartElement(writer, false, true);
            }
        }

        if (clazz.getGenericType() != null) {
            createStartElement(writer, WIRING_ELEMENT);            
            addAttribute(writer, BEAN_ID_REF_ATTRIBUTE, EJCUtils.encodeAttributeName(clazz.getGenericType(), clazz.getGenericType().getName()));
            endStartElement(writer, false, true);
        }
        createEndElement(writer, BINDINGS_ELEMENT);

        if (clazz.getGenericType() != null) {
            parseClass((JClass)clazz.getGenericType(), writer);
        }

        for (JAttribute attribute : clazz.getAttributes()) {
            if (attribute.getType() instanceof JClass) {
                parseClass((JClass)attribute.getType(), writer);
            }
        }
    }

    private static String getBeanId(JType type) throws IllegalNameException {
        String beanId;
        if (type instanceof JClass && ((JClass)type).getGenericType() != null) {
            beanId = EJCUtils.encodeAttributeName(((JClass)type).getGenericType(), ((JClass)type).getGenericType().getName());
            if (type.equals(JJavaClass.ARRAY_LIST)) {
                beanId += "List";
            }
        } else {
            beanId = EJCUtils.encodeAttributeName(type, type.getName());
        }
        return beanId;
    }

    private static void createStartElement(Writer writer, String elementName) throws IOException {        
        writer.write(ELEMENT_START);
        writer.write(elementName);
    }

    private static void endStartElement(Writer writer, boolean hasChildren, boolean addNewLine) throws IOException {
        if (hasChildren) {
            writer.write(ELEMENT_END);
        } else {
            writer.write(LEAF_ELEMENT_END);
        }

        if (addNewLine) {
            writer.write(NEWLINE);
        }
    }

    private static void createEndElement(Writer writer, String elementName) throws IOException {
        writer.write(END_ELEMENT_START);
        writer.write(elementName);
        writer.write(ELEMENT_END);    
    }

    private static void addAttribute(Writer writer, String attributeName, String attributeValue) throws IOException {
        writer.write(" ");
        writer.write(attributeName);
        writer.write("=");
        writer.write("\"");
        writer.write(attributeValue);
        writer.write("\"");
    }

    private static void addValue(Writer writer, String value) throws IOException {
        writer.write(value);
    }
}
