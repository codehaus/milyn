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
import org.milyn.smooks.edi.EDIMessage;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Compiles a {@link ClassModel} from an {@link Edimap}.
 *
 * @author bardl
 */
public class ClassModelCompiler {

    private static Log LOG = EJCLogFactory.getLog(ClassModelCompiler.class);

    private Map<MappingNode, JClass> injectedCommonTypes = new HashMap<MappingNode, JClass>();
    private Map<MappingNode, JClass> createdClassesByNode = new HashMap<MappingNode, JClass>();

    private ClassModel model;
    private Stack<String> nodeStack = new Stack<String>();
    private boolean addEDIMessageAnnotation;

    public ClassModelCompiler(Map<MappingNode, JClass> commonTypes, boolean addEDIMessageAnnotation) {
        if(commonTypes != null) {
            injectedCommonTypes.putAll(commonTypes);
        }
        this.addEDIMessageAnnotation = addEDIMessageAnnotation;
    }

    public ClassModel compile(Edimap edimap, String classPackage) throws IllegalNameException {
        model = new ClassModel();

        model.setEdimap(edimap);

        SegmentGroup segmentGroup = edimap.getSegments();

        pushNode(segmentGroup);
        
        JClass rootClass = new JClass(classPackage, EJCUtils.encodeClassName(segmentGroup.getXmltag()), getCurrentClassId());
        BindingConfig rootBeanConfig = new BindingConfig(getCurrentClassId(), getCurrentNodePath(), rootClass, null);

        //Insert root class into classModel and its' corresponding xmltag-value.
        model.addCreatedClass(rootClass);
        model.setRootBeanConfig(rootBeanConfig);

        LOG.debug("Added root class [" + rootClass + "] to ClassModel.");

        processSegmentGroups(segmentGroup.getSegments(), rootBeanConfig);

        LOG.debug("Finished parsing edi-configuration. All segments are added to ClassModel.");
        LOG.debug("ClassModel contains " + model.getCreatedClasses().size() + " classes.");

        // Attach the createdClassesByNode map... so we can use them if they
        // are common classes in a model set...
        model.setClassesByNode(createdClassesByNode);
        model.setReferencedClasses(injectedCommonTypes.values());
        
        popNode();

        if(addEDIMessageAnnotation) {
            model.getRootBeanConfig().getBeanClass().getAnnotationTypes().add(new JType(EDIMessage.class));
        }

        return model;
    }

    /**********************************************************************************************************
     * Private Methods
     **********************************************************************************************************/

    /**
     * Process all SegmentGroups in List and insert info into the {@link org.milyn.ejc.ClassModel}.
     * @param segmentGroups the SegmentsGroups to process.
     * @param parent the JClass 'owning' the SegmentGroups.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void processSegmentGroups(List<SegmentGroup> segmentGroups, BindingConfig parent) throws IllegalNameException {
        WriteMethod writeMethod = null;

        if(model.isClassCreator(parent.getBeanClass())) {
            writeMethod = getWriteMethod(parent.getBeanClass());
        }

        for (SegmentGroup segmentGroup : segmentGroups) {
            BindingConfig childBeanConfig = processSegmentGroup(segmentGroup, parent);

            // Add Write Method details for the property just added...
            if(writeMethod != null) {
                if(isCollection(childBeanConfig.getPropertyOnParent())) {
                    writeMethod.writeSegmentCollection(childBeanConfig.getPropertyOnParent(), segmentGroup.getSegcode());
                } else {
                    if(segmentGroup instanceof Segment) {
                        writeMethod.appendToBody("\n        writer.write(\"" + segmentGroup.getSegcode() + "\");");
                        writeMethod.writeDelimiter(DelimiterType.FIELD);
                    }
                    writeMethod.writeObject(childBeanConfig.getPropertyOnParent());
                }
            }

            if(isCollection(childBeanConfig.getPropertyOnParent())) {
                BindingConfig collectionBinding = new BindingConfig(childBeanConfig.getBeanId() + "_List", parent.getCreateOnElement(), ArrayList.class, childBeanConfig.getPropertyOnParent());

                // Wire the List binding into the parent binding and wire the child binding into the list binding...
                parent.getWireBindings().add(collectionBinding);
                collectionBinding.getWireBindings().add(childBeanConfig);

                // And zap the propertyOnParent config because you don't wire onto a property on a collection...
                childBeanConfig.setPropertyOnParent(null);
            } else {
                parent.getWireBindings().add(childBeanConfig);
            }
        }

        if(writeMethod != null) {
            parent.getBeanClass().getImplementTypes().add(new JType(EJCWritable.class));
            addWriteMethod(writeMethod, parent.getBeanClass());
        }
    }

    /**
     *
     * Process the {@link org.milyn.edisax.model.internal.SegmentGroup} in List and insert info into the {@link org.milyn.ejc.ClassModel}.
     * @param segmentGroup the {@link org.milyn.edisax.model.internal.SegmentGroup} to process.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.SegmentGroup}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private BindingConfig processSegmentGroup(SegmentGroup segmentGroup, BindingConfig parent) throws IllegalNameException {
        LOG.debug("Parsing SegmentGroup " + segmentGroup.getXmltag());

        if(segmentGroup.getJavaName() == null) {
            throw new EJCException("The <segmentGroup> element can optionally omit the 'xmltag' attribute.  However, this attribute must be present for EJC to work properly.  It is omitted from one of the <segmentGroup> elements in this configuration.");
        }

        pushNode(segmentGroup);

        BindingConfig childBinding = createChildAndConnectWithParent(parent, segmentGroup, segmentGroup.getMaxOccurs());

        if (segmentGroup instanceof Segment) {
            Segment segment = (Segment) segmentGroup;
            processFields(segment.getFields(), childBinding);
        }

        processSegmentGroups(segmentGroup.getSegments(), childBinding);

        popNode();

        return childBinding;
    }

    /**
     * Process all {@link org.milyn.edisax.model.internal.Field} in List and insert info into the {@link org.milyn.ejc.ClassModel}.
     * @param fields the {@link org.milyn.edisax.model.internal.Field} to process.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.Field}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void processFields(List<Field> fields, BindingConfig parent) throws IllegalNameException {
        WriteMethod writeMethod = null;

        if(model.isClassCreator(parent.getBeanClass())) {
            writeMethod = getWriteMethod(parent.getBeanClass());
        }

        for (Field field : fields) {
            LOG.debug("Parsing field " + field.getXmltag());

            pushNode(field);

            if (field.getComponents() != null && field.getComponents().size() > 0) {
                //Add class type.
                BindingConfig childBinding = createChildAndConnectWithParent(parent, field, 1);

                parent.getWireBindings().add(childBinding);

                if(writeMethod != null) {
                    // Add Write Method details for the property just added...
                    writeMethod.writeObject(childBinding.getPropertyOnParent(), DelimiterType.FIELD);
                }

                // Now add the components to the field...
                processComponents(field.getComponents(), childBinding);
            } else {
                // Add primitive type.
                JNamedType childToParentProperty = createAndAddSimpleType(field, parent);

                if(writeMethod != null) {
                    // Add Write Method details for the property just added...
                    writeMethod.writeValue(childToParentProperty, field, DelimiterType.FIELD);
                }
            }

            popNode();
        }

        if(writeMethod != null) {
            writeMethod.writeDelimiter(DelimiterType.SEGMENT);
            writeMethod.addFlush();
            addWriteMethod(writeMethod, parent.getBeanClass());
        }
    }

    /**
     * Creates a {@link org.milyn.javabean.pojogen.JNamedType} given a {@link org.milyn.edisax.model.internal.ValueNode}.
     * When {@link org.milyn.edisax.model.internal.ValueNode} contains no type information String-type is used as default.
     * The new {@link org.milyn.javabean.pojogen.JNamedType} is inserted into parent and the xmltag- and
     * typeParameters-value is inserted into classModel.
     * @param valueNode the {@link org.milyn.edisax.model.internal.ValueNode} to process.
     * @param parent the {@link org.milyn.javabean.pojogen.JClass} 'owning' the valueNode.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private JNamedType createAndAddSimpleType(ValueNode valueNode, BindingConfig parent) throws IllegalNameException {
        JType jtype;
        JNamedType childToParentProperty;

        if (valueNode.getType() != null && !valueNode.getType().equals("")) {
            jtype = new JType(valueNode.getTypeClass());
        } else {
            // Default type when no specific type is given.
            jtype = new JType(String.class);
        }

        String propertyName = EJCUtils.encodeAttributeName(jtype, valueNode.getXmltag());
        childToParentProperty = new JNamedType(jtype, propertyName);

        if(model.isClassCreator(parent.getBeanClass())) {
            parent.getBeanClass().addBeanProperty(childToParentProperty);
        }

        parent.getValueBindings().add(new ValueNodeInfo(childToParentProperty, getCurrentNodePath(), valueNode.getTypeParameters()));

        return childToParentProperty;
    }

    /**
     * Process all {@link org.milyn.edisax.model.internal.Component} in List and insert info into the {@link org.milyn.ejc.ClassModel}.
     * @param components the {@link org.milyn.edisax.model.internal.Component} to process.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.Component}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void processComponents(List<Component> components, BindingConfig parent) throws IllegalNameException {
        WriteMethod writeMethod = null;

        if(model.isClassCreator(parent.getBeanClass())) {
            writeMethod = getWriteMethod(parent.getBeanClass());
        }

        for (Component component : components) {

            pushNode(component);

            if (component.getSubComponents() != null && component.getSubComponents().size() > 0) {
                //Add class type.
                BindingConfig childBeanConfig = createChildAndConnectWithParent(parent, component, 1);

                parent.getWireBindings().add(childBeanConfig);

                if(writeMethod != null) {
                    // Add Write Method details for the property just added...
                    writeMethod.writeObject(childBeanConfig.getPropertyOnParent(), DelimiterType.COMPONENT);
                }

                processSubComponents(component.getSubComponents(), childBeanConfig);
            } else {
                //Add primitive type.
                JNamedType childToParentProperty = createAndAddSimpleType(component, parent);

                if(writeMethod != null && childToParentProperty != null) {
                    // Add Write Method details for the property just added...
                    writeMethod.writeValue(childToParentProperty, component, DelimiterType.COMPONENT);
                }
            }

            popNode();
        }

        if(writeMethod != null) {
            addWriteMethod(writeMethod, parent.getBeanClass());
        }
    }

    /**
     * Process all {@link org.milyn.edisax.model.internal.SubComponent} in List and insert info into the {@link org.milyn.ejc.ClassModel}.
     * @param subComponents the {@link org.milyn.edisax.model.internal.SubComponent} to process.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.SubComponent}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void processSubComponents(List<SubComponent> subComponents, BindingConfig parent) throws IllegalNameException {
        WriteMethod writeMethod = null;

        if(model.isClassCreator(parent.getBeanClass())) {
            writeMethod = getWriteMethod(parent.getBeanClass());
        }

        for (SubComponent subComponent : subComponents) {

            pushNode(subComponent);

            //Add primitive type.
            JNamedType childToParentProperty = createAndAddSimpleType(subComponent, parent);

            if(writeMethod != null && childToParentProperty != null) {
                // Add Write Method details for the property just added...
                writeMethod.writeValue(childToParentProperty, subComponent, DelimiterType.SUB_COMPONENT);
            }

            popNode();
        }

        if(writeMethod != null) {
            addWriteMethod(writeMethod, parent.getBeanClass());
        }
    }

    private void pushNode(MappingNode node) {
        nodeStack.push(node.getXmltag());
    }

    private void popNode() {
        nodeStack.pop();
    }

    /**
     * Creates a new {@link org.milyn.javabean.pojogen.JClass} C and inserts the class as a property in parent.
     * If C occurs several times, i.e. maxOccurs > 1, then C exists in a {@link java.util.List} in parent.
     * The new {@link org.milyn.javabean.pojogen.JClass} is inserted into classModel along with xmltag-value
     * found in the {@link org.milyn.edisax.model.internal.MappingNode}.
     * @param parent The parent BindingConfig.
     * @param mappingNode the {@link org.milyn.edisax.model.internal.MappingNode} to process.
     * @param maxOccurs the number of times {@link org.milyn.edisax.model.internal.MappingNode} can occur.
     * @return the created {@link org.milyn.javabean.pojogen.JClass}
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private BindingConfig createChildAndConnectWithParent(BindingConfig parent, MappingNode mappingNode, int maxOccurs) throws IllegalNameException {
        JClass child = getCommonType(mappingNode);
        boolean addClassToModel = false;

        if(child == null) {
            child = new JClass(parent.getBeanClass().getPackageName(), EJCUtils.encodeClassName(mappingNode.getJavaName()), getCurrentClassId());
            child.getImplementTypes().add(new JType(EJCWritable.class));
            addClassToModel = true;
            LOG.debug("Created class " + child.getClassName() + ".");
        }

        JType jtype;
        if (maxOccurs > 1 || maxOccurs == -1) {
            jtype = new JType(List.class, child.getSkeletonClass());
        } else {
            jtype = new JType(child.getSkeletonClass());
        }

        String propertyName = EJCUtils.encodeAttributeName(jtype, mappingNode.getXmltag());
        JNamedType childProperty = new JNamedType(jtype, propertyName);

        if(model.isClassCreator(parent.getBeanClass())) {
            parent.getBeanClass().addBeanProperty(childProperty);
        }
        if(addClassToModel) {
            model.addCreatedClass(child);
            createdClassesByNode.put(mappingNode, child);            
        }

        return new BindingConfig(getCurrentClassId(), getCurrentNodePath(), child, childProperty);
    }

    private String getCurrentClassId() {
        return getCurrentNodePath().replace('/', '.');
    }

    private String getCurrentNodePath() {
        StringBuilder builder = new StringBuilder();

        for(String nodePathElement : nodeStack) {
            if(builder.length() > 0) {
                builder.append('/');
            }
            builder.append(nodePathElement);
        }

        return builder.toString();
    }

    private JClass getCommonType(MappingNode mappingNode) {
        if(mappingNode instanceof Segment) {
            return getCommonTypeBySegCode(((Segment)mappingNode).getSegref());
        } else {
            return injectedCommonTypes.get(mappingNode);
        }
    }

    private JClass getCommonTypeBySegCode(String segCode) {
        if(segCode != null) {
            int colonIndex = segCode.indexOf(':');

            if(colonIndex != -1) {
                segCode = segCode.substring(colonIndex + 1);
            }

            Set<Map.Entry<MappingNode, JClass>> commonTypes = injectedCommonTypes.entrySet();
            for(Map.Entry<MappingNode, JClass> typeEntry : commonTypes) {
                MappingNode mappingNode = typeEntry.getKey();

                if(mappingNode instanceof Segment) {
                    if(segCode.equals(((Segment)mappingNode).getSegcode())) {
                        return typeEntry.getValue();
                    }
                }
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
        if(property != null && Collection.class.isAssignableFrom(property.getType().getType())) {
            return true;
        }

        return false;
    }
}
