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
package org.milyn.ejc.classes;

import org.milyn.ejc.EJCUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * JClass represents a java class used for genarating implementation
 * and binding-file.
 * @author bardl
 */
public class JClass extends XmlElement implements JType {

    private JVisibility visibility;
    private JPackage classPackage;
    private Set<JImport> imports;
    private String name;
    private List<JAttribute> attributes;
    private List<JMethod> methods;

    private JType genericType;
    private JType interfaceOf;


    public JClass(JPackage classPackage, String name, JType genericType, JType interfaceOf) {
        this.visibility = JVisibility.PUBLIC;
        this.classPackage = classPackage;
        this.name = name;
        this.genericType = genericType;
        this.interfaceOf = interfaceOf;
    }

    public JClass(JPackage classPackage, String name) {
        this(classPackage, name, null, null);
    }

    public JType getGenericType() {
        return genericType;
    }

    public JType getInterfaceOf() {
        return interfaceOf;
    }

    public JPackage getPackage() {
        return classPackage;
    }

    public JVisibility getVisibility() {
        return visibility;
    }

    public void setPackage(JPackage classPackage) {
        this.classPackage = classPackage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return classPackage.getName() + "." + name;
    }

    public Set<JImport> getImports() {
        if (imports == null) {
            imports = new HashSet<JImport>();
        }
        return imports;
    }

    public JType getType() {
        return new JClass(classPackage, name);
    }

    public void setType(JType type) {
        setName(type.getName());
        setPackage(type.getPackage());
    }

    public List<JAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<JAttribute>();
        }
        return attributes;
    }

    public List<JMethod> getMethods() {
        if (methods == null) {
            methods = new ArrayList<JMethod>();
        }
        return methods;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    //This is not entirely correct since equality should check value for all attributes in the class.
    public boolean equals(Object obj) {
        if (obj instanceof JClass) {
            return obj.toString().equals(toString());
        }
        return false;
    }

    public String toString() {
        return classPackage.getName() + "." + name;
    }
    
}
