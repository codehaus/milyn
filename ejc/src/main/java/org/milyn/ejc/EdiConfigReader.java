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

import org.apache.commons.logging.Log;
import org.milyn.edisax.model.internal.*;
import org.milyn.javabean.pojogen.JClass;
import org.milyn.javabean.pojogen.JNamedType;
import org.milyn.javabean.pojogen.JType;

import java.util.List;

/**
 * org.milyn.ejc.EdiConfigReader parses a Edimap and generates events while parsing.
 *
 * @author bardl
 */
public class EdiConfigReader {

    private static Log LOG = EJCLogFactory.getLog(EdiConfigReader.class);

    private ClassModel model;

    public ClassModel parse(Edimap edimap, String classPackage) throws IllegalNameException {
        model = new ClassModel();

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
        for (SegmentGroup segmentGroup : segmentGroups) {
            parseSegmentGroup(segmentGroup, parent);
        }
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
            parseFields(((Segment)segmentGroup).getFields(), child);
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
        for (Field field : fields) {
            LOG.debug("Parsing field " + field.getXmltag());
            if (field.getComponent() != null && field.getComponent().size() > 0) {
                //Add class type.
                child = createChildAndConnectWithParent(parent, field, 1);
                parseComponents(field.getComponent(), child);
            } else {
                //Add primitive type.
                createAndAddSimpleType(field, parent);
            }
        }
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
        parent.addProperty(new JNamedType(jtype, propertyName));
        model.addPropertyValueNodeConfig(parent.getClassName(), propertyName, new ValueNodeInfo(valueNode.getXmltag(), valueNode.getParameters()));
    }

    /**
     * Parse all {@link org.milyn.edisax.model.internal.Component} in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param components the {@link org.milyn.edisax.model.internal.Component} to parse.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.Component}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void parseComponents(List<Component> components, JClass parent) throws IllegalNameException {
        JClass child;
        for (Component component : components) {
            if (component.getSubComponent() != null && component.getSubComponent().size() > 0) {
                //Add class type.
                child = createChildAndConnectWithParent(parent, component, 1);
                parseSubComponents(component.getSubComponent(), child);
            } else {
                //Add primitive type.
                createAndAddSimpleType(component, parent);
            }
        }
    }

    /**
     * Parse all {@link org.milyn.edisax.model.internal.SubComponent} in List and insert parsed info into the {@link org.milyn.ejc.ClassModel}.
     * @param subComponents the {@link org.milyn.edisax.model.internal.SubComponent} to parse.
     * @param parent the JClass 'owning' the {@link org.milyn.edisax.model.internal.SubComponent}.
     * @throws IllegalNameException when name found in a xmltag-attribute is a java keyword.
     */
    private void parseSubComponents(List<SubComponent> subComponents, JClass parent) throws IllegalNameException {
        for (SubComponent subComponent : subComponents) {
            //Add primitive type.
            createAndAddSimpleType(subComponent, parent);
        }
    }

    /**********************************************************************************************************
     * Private Helper Methods
     **********************************************************************************************************/

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
        JClass child = new JClass(parent.getPackageName(), EJCUtils.encodeClassName(mappingNode.getXmltag()));

        LOG.debug("Created class " + child.getClassName() + ".");

        JType jtype;
        if (maxOccurs > 1 || maxOccurs == -1) {
            jtype = new JType(List.class, child.getSkeletonClass());
        } else {
            jtype = new JType(child.getSkeletonClass());
        }
        String propertyName = EJCUtils.encodeAttributeName(jtype, mappingNode.getXmltag());
        parent.addProperty(new JNamedType(jtype, propertyName));
        model.addClass(child);
        model.addClassValueNodeConfig(child, new ValueNodeInfo(mappingNode.getXmltag(), null));
        model.addPropertyValueNodeConfig(child.getClassName(), propertyName, new ValueNodeInfo(mappingNode.getXmltag(), null));

        return child;
    }
}
