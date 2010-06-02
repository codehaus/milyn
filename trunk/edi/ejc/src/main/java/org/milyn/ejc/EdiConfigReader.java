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
package org.milyn.ejc;

import org.apache.commons.logging.Log;
import org.milyn.config.Configurable;
import org.milyn.edisax.model.internal.*;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DataEncoder;
import org.milyn.javabean.pojogen.JClass;
import org.milyn.javabean.pojogen.JMethod;
import org.milyn.javabean.pojogen.JNamedType;
import org.milyn.javabean.pojogen.JType;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * org.milyn.ejc.EdiConfigReader parses a Edimap and generates events while parsing.
 *
 * @author bardl
 */
public class EdiConfigReader {

    private static Log LOG = EJCLogFactory.getLog(EdiConfigReader.class);

    private Map<String, JClass> segRefClasses = new HashMap<String, JClass>();

    private ClassModel model;

    public ClassModel parse(Edimap edimap, String classPackage) throws IllegalNameException {
        model = new ClassModel();

        model.setEdimap(edimap);

        SegmentGroup segmentGroup = edimap.getSegments();
        JClass rootClass = new JClass(classPackage, EJCUtils.encodeClassName(segmentGroup.getXmltag()));

        //Insert root class into classModel and its' corresponding xmltag-value.
        model.addClass(rootClass);
        model.addClassValueNodeConfig(rootClass, new ValueNodeInfo(segmentGroup.getXmltag(), null));
        model.setRoot(rootClass);

        LOG.debug("Added root class [" + rootClass + "] to ClassModel.");

        parseSegmentGroups(segmentGroup.getSegments(), rootClass);

        LOG.debug("Finished parsing edi-configuration. All segments are added to ClassModel.");
        LOG.debug("ClassModel contains " + model.getCreatedClasses().size() + " classes.");

        return model;
    }

    /**********************************************************************************************************
     * Private Parser Methods
     **********************************************************************************************************/

    /**
     * Parse all SegmentGroups in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param segmentGroups the SegmentsGroups to parse.
     * @param parent the JClass 'owning' the SegmentGroups.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword. 
     */
    private void parseSegmentGroups(List<SegmentGroup> segmentGroups, JClass parent) throws IllegalNameException {
        WriteMethod writeMethod = getWriteMethod(parent);

        for (SegmentGroup segmentGroup : segmentGroups) {
            parseSegmentGroup(segmentGroup, parent);

            // Add Write Method details for the property just added...
            JNamedType property = getLastAddedProperty(parent);
            if(isCollection(property)) {
                writeMethod.writeSegmentCollection(property, segmentGroup.getSegcode());
            } else {
                if(segmentGroup instanceof Segment) {
                    writeMethod.appendToBody("\n        writer.write(\"" + segmentGroup.getSegcode() + "\");");
                    writeMethod.writeDelimiter(DelimiterType.FIELD);
                }
                writeMethod.writeObject(property);
            }
        }

        parent.getImplementTypes().add(new JType(EJCWritable.class));
        addWriteMethod(writeMethod, parent);
    }

    /**
     *
     * Parse the {@link org.milyn.edisax.model.internal.SegmentGroup} in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param segmentGroup the {@link org.milyn.edisax.model.internal.SegmentGroup} to parse.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.SegmentGroup}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void parseSegmentGroup(SegmentGroup segmentGroup, JClass parent) throws IllegalNameException {
        LOG.debug("Parsing SegmentGroup " + segmentGroup.getXmltag());
        JClass child = createChildAndConnectWithParent(parent, segmentGroup, segmentGroup.getMaxOccurs());

        if (segmentGroup instanceof Segment) {
            Segment segment = (Segment) segmentGroup;
            String segRef = segment.getSegref();

            if(segRef == null || !segRefClasses.containsKey(segRef)) {
                parseFields(segment.getFields(), child);
                if(segRef != null) {
                    segRefClasses.put(segRef, child);
                }
            }
        }

        parseSegmentGroups(segmentGroup.getSegments(), child);
    }

    /**
     * Parse all {@link org.milyn.edisax.model.internal.Field} in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param fields the {@link org.milyn.edisax.model.internal.Field} to parse.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.Field}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void parseFields(List<Field> fields, JClass parent) throws IllegalNameException {
        JClass child;
        WriteMethod writeMethod = getWriteMethod(parent);

        for (Field field : fields) {
            LOG.debug("Parsing field " + field.getXmltag());
            if (field.getComponents() != null && field.getComponents().size() > 0) {
                //Add class type.
                child = createChildAndConnectWithParent(parent, field, 1);

                // Add Write Method details for the property just added...
                writeMethod.writeObject(getLastAddedProperty(parent), DelimiterType.FIELD);

                // Now add the components to the field...
                parseComponents(field.getComponents(), child);
            } else {
                // Add primitive type.
                createAndAddSimpleType(field, parent);

                // Add Write Method details for the property just added...
                writeMethod.writeValue(getLastAddedProperty(parent), field, DelimiterType.FIELD);
            }
        }

        writeMethod.writeDelimiter(DelimiterType.SEGMENT);
        writeMethod.addFlush();
        addWriteMethod(writeMethod, parent);
    }

    private JNamedType getLastAddedProperty(JClass jClass) {
        List<JNamedType> properties = jClass.getProperties();

        if(properties.isEmpty()) {
            throw new EJCException("Class '" + jClass.getClassName() + "' has no properties.");
        }

        return properties.get(properties.size() - 1);
    }

    /**
     * Creates a {@link org.milyn.javabean.pojogen.JNamedType} given a {@link org.milyn.edisax.model.internal.ValueNode}.
     * When {@link org.milyn.edisax.model.internal.ValueNode} contains no type information String-type is used as default.
     * The new {@link org.milyn.javabean.pojogen.JNamedType} is inserted into parent and the xmltag- and
     * typeParameters-value is inserted into classModel.
     * @param valueNode the {@link org.milyn.edisax.model.internal.ValueNode} to parse.
     * @param parent the {@link org.milyn.javabean.pojogen.JClass} 'owning' the valueNode.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void createAndAddSimpleType(ValueNode valueNode, JClass parent) throws IllegalNameException {
        JType jtype;
        if (valueNode.getType() != null && !valueNode.getType().equals("")) {
            jtype = new JType(valueNode.getTypeClass());
        } else {
            // Default type when no specific type is given.
            jtype = new JType(String.class);
        }
        String propertyName = EJCUtils.encodeAttributeName(jtype, valueNode.getXmltag());
        parent.addBeanProperty(new JNamedType(jtype, propertyName));
        model.addPropertyValueNodeConfig(parent.getClassName(), propertyName, new ValueNodeInfo(valueNode.getXmltag(), valueNode.getTypeParameters()));
    }

    /**
     * Parse all {@link org.milyn.edisax.model.internal.Component} in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param components the {@link org.milyn.edisax.model.internal.Component} to parse.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.Component}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void parseComponents(List<Component> components, JClass parent) throws IllegalNameException {
        JClass child;
        WriteMethod writeMethod = getWriteMethod(parent);

        for (Component component : components) {
            if (component.getSubComponents() != null && component.getSubComponents().size() > 0) {
                //Add class type.
                child = createChildAndConnectWithParent(parent, component, 1);

                // Add Write Method details for the property just added...
                writeMethod.writeObject(getLastAddedProperty(parent), DelimiterType.COMPONENT);

                parseSubComponents(component.getSubComponents(), child);
            } else {
                //Add primitive type.
                createAndAddSimpleType(component, parent);

                // Add Write Method details for the property just added...
                writeMethod.writeValue(getLastAddedProperty(parent), component, DelimiterType.COMPONENT);
            }
        }

        addWriteMethod(writeMethod, parent);
    }

    /**
     * Parse all {@link org.milyn.edisax.model.internal.SubComponent} in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param subComponents the {@link org.milyn.edisax.model.internal.SubComponent} to parse.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.SubComponent}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void parseSubComponents(List<SubComponent> subComponents, JClass parent) throws IllegalNameException {
        WriteMethod writeMethod = getWriteMethod(parent);

        for (SubComponent subComponent : subComponents) {
            //Add primitive type.
            createAndAddSimpleType(subComponent, parent);

            // Add Write Method details for the property just added...
            writeMethod.writeValue(getLastAddedProperty(parent), subComponent, DelimiterType.SUB_COMPONENT);
        }

        addWriteMethod(writeMethod, parent);
    }

    /**
     * Creates a new {@link org.milyn.javabean.pojogen.JClass} C and inserts the class as a property in parent.
     * If C occurs several times, i.e. maxOccurs > 1, then C exists in a {@link java.util.List} in parent.
     * The new {@link org.milyn.javabean.pojogen.JClass} is inserted into classModel along with xmltag-value
     * found in the {@link org.milyn.edisax.model.internal.MappingNode}.
     * @param parent the {@link org.milyn.javabean.pojogen.JClass} 'owning' the {@link org.milyn.edisax.model.internal.MappingNode}.
     * @param mappingNode the {@link org.milyn.edisax.model.internal.MappingNode} to parse.
     * @param maxOccurs the number of times {@link org.milyn.edisax.model.internal.MappingNode} can occur.
     * @return the created {@link org.milyn.javabean.pojogen.JClass}
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private JClass createChildAndConnectWithParent(JClass parent, MappingNode mappingNode, int maxOccurs) throws IllegalNameException {
        JClass child = getPreBuilt(mappingNode);

        if(child == null) {
            child = new JClass(parent.getPackageName(), EJCUtils.encodeClassName(mappingNode.getJavaName()));
            child.getImplementTypes().add(new JType(EJCWritable.class));
            LOG.debug("Created class " + child.getClassName() + ".");
        }

        JType jtype;
        if (maxOccurs > 1 || maxOccurs == -1) {
            jtype = new JType(List.class, child.getSkeletonClass());
        } else {
            jtype = new JType(child.getSkeletonClass());
        }

        String propertyName = EJCUtils.encodeAttributeName(jtype, mappingNode.getXmltag());

        parent.addBeanProperty(new JNamedType(jtype, propertyName));
        model.addClass(child);
        model.addClassValueNodeConfig(child, new ValueNodeInfo(mappingNode.getXmltag(), null));
        model.addPropertyValueNodeConfig(child.getClassName(), propertyName, new ValueNodeInfo(mappingNode.getXmltag(), null));

        return child;
    }

    private JClass getPreBuilt(MappingNode mappingNode) {
        if(mappingNode instanceof Segment) {
            String segRef = ((Segment)mappingNode).getSegref();
            if(segRef != null) {
                return segRefClasses.get(segRef);
            }
        }

        return null;
    }

    private static class WriteMethod extends JMethod {

        private JClass jClass;

        private WriteMethod(JClass jClass) {
            super("write");
            addParameter(new JType(Writer.class), "writer");
            addParameter(new JType(Delimiters.class), "delimiters");
            getExceptions().add(new JType(IOException.class));
            this.jClass = jClass;
        }

        public void writeObject(JNamedType property, DelimiterType delimiterType) {
            writeDelimiter(delimiterType);
            writeObject(property);
        }

        public void writeObject(JNamedType property) {
            appendToBody("\n        if(" + property.getName() + " != null) {");
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
                    Set<Map.Entry<Object, Object>> encoderConfig = ((Configurable) dataDecoder).getConfiguration().entrySet();
                    String encoderPropertiesName = encoderName + "Properties";

                    jClass.getRawImports().add(new JType(Properties.class));
                    defaultConstructor.appendToBody("\n        Properties " + encoderPropertiesName + " = new Properties();");
                    for(Map.Entry<Object, Object> entry : encoderConfig) {
                        defaultConstructor.appendToBody("\n        " + encoderPropertiesName + ".setProperty(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");");
                    }
                    defaultConstructor.appendToBody("\n        " + encoderName + ".setConfiguration(" + encoderPropertiesName + ");");
                }

                // Add the encoder encode instruction to te write method...
                appendToBody("\n            writer.write(" + encoderName + ".encode(" + property.getName() + "));");
            } else {
                appendToBody("\n            writer.write(" + property.getName() + ".toString());");
            }

            appendToBody("\n        }");
        }

        public void writeSegmentCollection(JNamedType property, String segcode) {
            appendToBody("\n        if(" + property.getName() + " != null && !" + property.getName() + ".isEmpty()) {");
            appendToBody("\n            for(" + property.getType().getGenericType().getSimpleName() + " " + property.getName() + "Inst : " + property.getName() + ") {");
            appendToBody("\n                writer.write(\"" + segcode + "\");");
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

    /**********************************************************************************************************
     * Private Helper Methods
     **********************************************************************************************************/

    private WriteMethod getWriteMethod(JClass jClass) {
        for(JMethod method : jClass.getMethods()) {
            if(method instanceof WriteMethod) {
                return (WriteMethod) method;
            }
        }

        return new WriteMethod(jClass);
    }

    private void addWriteMethod(WriteMethod writeMethod, JClass jClass) {
        for(JMethod method : jClass.getMethods()) {
            if(method == writeMethod) {
                return;
            }
        }

        jClass.getMethods().add(writeMethod);
    }

    private static boolean isCollection(JNamedType property) {
        if(Collection.class.isAssignableFrom(property.getType().getType())) {
            return true;
        }

        return false;
    }
}
