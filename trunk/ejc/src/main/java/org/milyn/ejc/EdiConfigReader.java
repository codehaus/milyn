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

import org.milyn.edisax.model.internal.*;
import org.milyn.edisax.EDITypeEnum;

import java.util.List;
import java.util.ArrayList;

import org.milyn.ejc.classes.*;

/**
 * org.milyn.ejc.EdiConfigReader parses a Edimap and generates events while parsing.
 *
 * @author bardl
 */
public class EdiConfigReader {

    private ClassModel model;

    public ClassModel parse(Edimap edimap, String classPackage) throws IllegalNameException {
        model = new ClassModel();

        SegmentGroup segmentGroup = edimap.getSegments();
        JClass rootClass = new JClass(new JPackage(classPackage), EJCUtils.encodeClassName(segmentGroup.getXmltag()));
        rootClass.setXmlElementName(segmentGroup.getXmltag());
        model.addClass(rootClass);
        model.setRoot(rootClass);

        parseSegmentGroups(segmentGroup.getSegments(), rootClass);

        return model;
    }

    /**********************************************************************************************************
     * Private Parser Methods
     **********************************************************************************************************/
    
    private void parseSegmentGroups(List<SegmentGroup> segmentGroups, JClass parent) throws IllegalNameException {
        for (SegmentGroup segmentGroup : segmentGroups) {
            parseSegmentGroup(segmentGroup, parent);
        }
    }

    private void parseSegmentGroup(SegmentGroup segmentGroup, JClass parent) throws IllegalNameException {
        JClass child = createChildAndConnectWithParent(parent, segmentGroup, segmentGroup.getMaxOccurs());

        if (segmentGroup instanceof Segment) {
            parseFields(((Segment)segmentGroup).getFields(), child);
        }
        parseSegmentGroups(segmentGroup.getSegments(), child);
    }

    private JClass wrapIntoGenericCollection(JClass clazz, int maxOccurs, String createOnElementName) {
        JClass result = clazz;
        if (maxOccurs > 1 || maxOccurs == -1) {
            result = new JJavaClass(JJavaClass.ARRAY_LIST.getPackage(), JJavaClass.ARRAY_LIST.getName(), clazz, JJavaClass.LIST);
            result.setXmlElementName(createOnElementName);
        }
        return result;
    }

    private void parseFields(List<Field> fields, JClass parent) throws IllegalNameException {
        JClass child;
        for (Field field : fields) {
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

    private void createAndAddSimpleType(ValueNode valueNode, JClass parent) throws IllegalNameException {
        JSimpleType type;
        if (valueNode.getType() != null && !valueNode.getType().equals("")) {
            if (valueNode.getType().equals(EDITypeEnum.CUSTOM_NAME)) {
                type = new JSimpleType(valueNode.getParameters().get(0).getValue(), valueNode.getParameters(), null);
            } else {
                type = new JSimpleType(valueNode.getType(), valueNode.getParameters(),JJavaClass.getPackageForClass(EDITypeEnum.valueOf(valueNode.getType()).getJavaClass()));
            }
        } else {
            type = new JSimpleType(JSimpleType.DEFAULT_TYPE, null, null);
        }
        JAttribute attribute = new JAttribute(type, EJCUtils.encodeAttributeName(type, valueNode.getXmltag()));
        attribute.setXmlElementName(valueNode.getXmltag());
        addAttributeAndCreateMethods( parent, attribute );
    }

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

    private void parseSubComponents(List<SubComponent> subComponents, JClass parent) throws IllegalNameException {
        for (SubComponent subComponent : subComponents) {
            //Add primitive type.
            createAndAddSimpleType(subComponent, parent);
        }
    }

    /**********************************************************************************************************
     * Private Helper Methods
     **********************************************************************************************************/
      
    private JClass createChildAndConnectWithParent(JClass parent, MappingNode mappingNode, int maxOccurs) throws IllegalNameException {
        JClass child = new JClass(parent.getPackage(), EJCUtils.encodeClassName(mappingNode.getXmltag()));
        child.setXmlElementName(mappingNode.getXmltag());
        child = wrapIntoGenericCollection(child, maxOccurs, parent.getXmlElementName());
        JAttribute attribute = new JAttribute(child, EJCUtils.encodeAttributeName(child, mappingNode.getXmltag()));
        attribute.setXmlElementName(mappingNode.getXmltag());
        addAttributeAndCreateMethods(parent, attribute);
        if (child.getGenericType() != null) {
            model.addClass((JClass)child.getGenericType());
        } else {
            model.addClass(child);
        }
        return child;
    }

    private void addAttributeAndCreateMethods(JClass parent, JAttribute attribute) {
        if (parent.getGenericType() != null && parent.getGenericType() instanceof JClass) {
            addAttributeAndCreateMethods(((JClass)parent.getGenericType()), attribute);
        } else {
            parent.getAttributes().add(attribute);
            applyImport(parent, attribute);
            addMethods(parent, attribute);
        }
    }

    private void addMethods(JClass parent, JAttribute attribute) {
        String methodSuffix = attribute.getName().substring(0,1).toUpperCase() + attribute.getName().substring(1, attribute.getName().length());
        List<JStatement> statements = new ArrayList<JStatement>();
        addInstantiationOfCollection(statements, attribute);
        statements.add(new JStatement("return this." + attribute.getName() + JStatement.STATEMENT_SUFFIX));
        JMethod method = new JMethod(JVisibility.PUBLIC, attribute.getType(), JMethod.GET_PREFIX + methodSuffix, null, statements);
        parent.getMethods().add(method);

        List<JParameter> parameters = new ArrayList<JParameter>();
        parameters.add(new JParameter(attribute.getType(), attribute.getName()));
        statements = new ArrayList<JStatement>();
        statements.add(new JStatement("this." + attribute.getName() + " = " + attribute.getName() + JStatement.STATEMENT_SUFFIX));
        parent.getMethods().add(new JMethod(JVisibility.PUBLIC, JSimpleType.VOID, JMethod.SET_PREFIX + methodSuffix, parameters, statements));
    }

    private void addInstantiationOfCollection(List<JStatement> statements, JAttribute attribute) {
        if (attribute.getType() instanceof JClass) {
            JClass clazz = (JClass)attribute.getType();
            if (clazz.getInterfaceOf() != null && clazz.getInterfaceOf().getName().equals("List")) {
                statements.add(new JStatement("if (" + attribute.getName() + " == null) {"));
                statements.add(new JStatement("\tthis." + attribute.getName() + " = new " + clazz.getName() + (clazz.getGenericType() != null ? "<" + clazz.getGenericType().getName() + ">" : "") + "()" + JStatement.STATEMENT_SUFFIX));
                statements.add(new JStatement("}"));
            }
        }
    }

    private void applyImport(JClass parent, JAttribute attribute) {
        JImport newImport = new JImport(attribute.getType());
        addImport(parent, newImport);
        if (attribute.getType() instanceof JClass) {
            JClass clazz = (JClass)attribute.getType();
            if (clazz.getInterfaceOf() != null ) {
                newImport = new JImport(clazz.getInterfaceOf());
                addImport(parent, newImport);
            }
            if (clazz.getGenericType() != null ) {
                newImport = new JImport(clazz.getGenericType());
                addImport(parent, newImport);
            }
        }
    }

    private void addImport(JClass parent, JImport newImport) {
        if ( newImport.getType().getPackage() != null && !newImport.getType().getPackage().equals(parent.getPackage())) {
            parent.getImports().add(newImport);
        }
    }
}
