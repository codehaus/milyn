/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (version 2.1) as published by the Free Software
 * Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.ejc;

import org.milyn.config.Configurable;
import org.milyn.edisax.model.internal.*;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DataEncoder;
import org.milyn.javabean.pojogen.JClass;
import org.milyn.javabean.pojogen.JMethod;
import org.milyn.javabean.pojogen.JNamedType;
import org.milyn.javabean.pojogen.JType;
import org.milyn.smooks.edi.EDIWritable;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * EDIWritable bean serialization class.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
class WriteMethod extends JMethod {

    private JClass jClass;

    WriteMethod(JClass jClass) {
        super("write");
        addParameter(new JType(Writer.class), "writer");
        addParameter(new JType(Delimiters.class), "delimiters");
        getExceptions().add(new JType(IOException.class));
        jClass.getImplementTypes().add(new JType(EDIWritable.class));
        jClass.getMethods().add(this);
        this.jClass = jClass;
    }

    public void writeObject(JNamedType property, DelimiterType delimiterType, BindingConfig bindingConfig, MappingNode mappingNode) {
        writeDelimiter(delimiterType);
        writeObject(property, bindingConfig, mappingNode);
    }

    public void writeObject(JNamedType property, BindingConfig bindingConfig, MappingNode mappingNode) {
        appendToBody("\n        if(" + property.getName() + " != null) {");
        if(mappingNode instanceof Segment) {
            if(bindingConfig.getParent() == null || getBody().length() > 0) {
                appendToBody("\n            writer.write(\"" + ((Segment)mappingNode).getSegcode() + "\");");
                appendToBody("\n            writer.write(delimiters.getField());");
            }
        }
        appendToBody("\n            " + property.getName() + ".write(writer, delimiters);");
        appendToBody("\n        }");
    }

    public void writeValue(JNamedType property, ValueNode modelNode, DelimiterType delimiterType) {
        writeDelimiter(delimiterType);
        writeValue(property, modelNode);
    }

    public void writeValue(JNamedType property, ValueNode modelNode) {
        appendToBody("\n        if(" + property.getName() + " != null) {");

        DataDecoder dataDecoder = modelNode.getDecoder();
        if(dataDecoder instanceof DataEncoder) {
            String encoderName = property.getName() + "Encoder";
            Class<? extends DataDecoder> decoderClass = dataDecoder.getClass();

            // Add the property for the encoder instance...
            jClass.getProperties().add(new JNamedType(new JType(decoderClass), encoderName));

            // Create the encoder in the constructor...
            JMethod defaultConstructor = jClass.getDefaultConstructor();
            defaultConstructor.appendToBody("\n        " + encoderName + " = new " + decoderClass.getSimpleName() + "();");

            // Configure the encoder in the constructor (if needed)....
            if(dataDecoder instanceof Configurable) {
                Properties configuration = ((Configurable) dataDecoder).getConfiguration();

                if(configuration != null) {
                    Set<Map.Entry<Object, Object>> encoderConfig = configuration.entrySet();
                    String encoderPropertiesName = encoderName + "Properties";

                    jClass.getRawImports().add(new JType(Properties.class));
                    defaultConstructor.appendToBody("\n        Properties " + encoderPropertiesName + " = new Properties();");
                    for(Map.Entry<Object, Object> entry : encoderConfig) {
                        defaultConstructor.appendToBody("\n        " + encoderPropertiesName + ".setProperty(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");");
                    }
                    defaultConstructor.appendToBody("\n        " + encoderName + ".setConfiguration(" + encoderPropertiesName + ");");
                }
            }

            // Add the encoder encode instruction to te write method...
            appendToBody("\n            writer.write(" + encoderName + ".encode(" + property.getName() + "));");
        } else {
            appendToBody("\n            writer.write(" + property.getName() + ".toString());");
        }

        appendToBody("\n        }");
    }

    public void writeSegmentCollection(JNamedType property, SegmentGroup segmentGroup) {
        appendToBody("\n        if(" + property.getName() + " != null && !" + property.getName() + ".isEmpty()) {");
        appendToBody("\n            for(" + property.getType().getGenericType().getSimpleName() + " " + property.getName() + "Inst : " + property.getName() + ") {");
        appendToBody("\n                writer.write(\"" + segmentGroup.getSegcode() + "\");");
        appendToBody("\n                writer.write(delimiters.getField());");
        appendToBody("\n                " + property.getName() + "Inst.write(writer, delimiters);");
        appendToBody("\n            }");
        appendToBody("\n        }");
    }

    public void writeDelimiter(DelimiterType delimiterType) {
        if(bodyLength() == 0) {
            return;
        }

        switch (delimiterType) {
            case SEGMENT:
                appendToBody("\n        writer.write(delimiters.getSegmentDelimiter());");
                break;
            case FIELD:
                appendToBody("\n        writer.write(delimiters.getField());");
                break;
            case FIELD_REPEAT:
                appendToBody("\n        writer.write(delimiters.getFieldRepeat());");
                break;
            case COMPONENT:
                appendToBody("\n        writer.write(delimiters.getComponent());");
                break;
            case SUB_COMPONENT:
                appendToBody("\n        writer.write(delimiters.getSubComponent());");
                break;
            default:
                throw new UnsupportedOperationException("Unsupported '" + DelimiterType.class.getName() + "' enum conversion.  Enum '" + delimiterType + "' not specified in switch statement.");
        }
    }

    public void addFlush() {
        appendToBody("\n        writer.flush();");
    }
}
