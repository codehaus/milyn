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
import org.milyn.javabean.pojogen.JClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

/**
 * ClassModel contains a Map of {@link org.milyn.javabean.pojogen.JClass} for easy lookup when
 * {@link org.milyn.ejc.BeanWriter} and {@link org.milyn.ejc.BindingWriter} needs to access the
 * classes.
 *
 * Holds information about the xmltag and typeParameters associated with a given {@link org.milyn.javabean.pojogen.JClass}
 * or {@link org.milyn.javabean.pojogen.JNamedType}. These values are held in the {@link org.milyn.ejc.ValueNodeInfo}.
 *
 * @see org.milyn.ejc.BeanWriter
 * @see org.milyn.ejc.BindingWriter
 * @see org.milyn.ejc.ValueNodeInfo
 * @author bardl
 */
public class ClassModel {

    private static Log LOG = EJCLogFactory.getLog(ClassModel.class);

    private JClass root;
    private Map<String, JClass> createdClasses;
    private Map<String, ValueNodeInfo> valueNodeInfos;

    /**
     * Returns the root {@link org.milyn.javabean.pojogen.JClass}.
     * @return athe root {@link org.milyn.javabean.pojogen.JClass}.
     */
    public JClass getRoot() {
        return root;
    }

    /**
     * Sets the root {@link org.milyn.javabean.pojogen.JClass}.
     * @param root the {@link org.milyn.javabean.pojogen.JClass}.
     */
    public void setRoot(JClass root) {
        this.root = root;
    }

    /**
     * Returns a Map of all generated {@link org.milyn.javabean.pojogen.JClass}.
     * @return a {@link java.util.List} of {@link org.milyn.javabean.pojogen.JClass}.
     */
    public Map<String, JClass> getCreatedClasses() {
        if ( createdClasses == null ) {
            this.createdClasses = new LinkedHashMap<String, JClass>();
        }
        return createdClasses;
    }

    /**
     * Adds a {@link org.milyn.javabean.pojogen.JClass} to the ClassModel.
     * @param jclass the {@link org.milyn.javabean.pojogen.JClass} to add.
     */
    public void addClass(JClass jclass) {
        getCreatedClasses().put(jclass.getClassName(), jclass);
        LOG.info("Added class " + jclass.getPackageName() + "." + jclass.getClassName() + " to model.");
    }

    /**
     * Adds a {@link org.milyn.ejc.ValueNodeInfo} belonging a specific {@link org.milyn.javabean.pojogen.JClass} to ClassModel.
     * @param jclass the {@link org.milyn.javabean.pojogen.JClass} to which the {@link org.milyn.ejc.ValueNodeInfo} exists.
     * @param valueNodeInfo the {@link org.milyn.ejc.ValueNodeInfo} to add.
     */
    public void addClassValueNodeConfig(JClass jclass, ValueNodeInfo valueNodeInfo) {
        getValueNodeInfos().put(jclass.getClassName(), valueNodeInfo);
    }

    /**
     * Adds a {@link org.milyn.ejc.ValueNodeInfo} belonging a specific {@link org.milyn.javabean.pojogen.JClass} and '
     * {@link org.milyn.javabean.pojogen.JNamedType} to ClassModel. Both classname and namedType is given as a String-value.
     * @param className the 
     * @param propertyName
     * @param valueNodeInfo
     */
    public void addPropertyValueNodeConfig(String className, String propertyName, ValueNodeInfo valueNodeInfo) {
        getValueNodeInfos().put(className + "." + propertyName, valueNodeInfo);
    }

    public String getClassXmlElementName(String className) {
        return getValueNodeInfos().get(className).getXmlElementName();
    }

    public String getPropertyXmlElementName(String className, String propertyName) {
        return getValueNodeInfos().get(className + "." + propertyName).getXmlElementName();
    }

    public List<Map.Entry<String, String>> getPropertyDecoderConfigs(String className, String propertyName) {
        return getValueNodeInfos().get(className + "." + propertyName).getDecoderConfigs();
    }

    private Map<String, ValueNodeInfo> getValueNodeInfos() {
        if ( valueNodeInfos == null) {
            this.valueNodeInfos = new LinkedHashMap<String, ValueNodeInfo>();
        }
        return valueNodeInfos;
    }
}
