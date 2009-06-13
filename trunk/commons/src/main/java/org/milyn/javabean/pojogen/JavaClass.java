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
package org.milyn.javabean.pojogen;

import org.milyn.assertion.AssertArgument;
import org.milyn.util.FreeMarkerTemplate;
import org.milyn.io.StreamUtils;

import java.util.*;
import java.io.Writer;
import java.io.IOException;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CannotCompileException;

/**
 * Java POJO model.
 * @author bardl
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class JavaClass {

    private String packageName;
    private String className;
    private Class skeletonClass;
    private List<JavaNamedType> properties = new ArrayList<JavaNamedType>();
    private List<JavaMethod> methods = new ArrayList<JavaMethod>();

    private static FreeMarkerTemplate template;

    static {
        try {
            template = new FreeMarkerTemplate(StreamUtils.readStreamAsString(JavaClass.class.getResourceAsStream("JavaClass.ftl")));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load JavaClass.ftl FreeMarker template.", e);
        }
    }

    public JavaClass(String packageName, String className) {
        AssertArgument.isNotNull(packageName, "packageName");
        AssertArgument.isNotNull(className, "className");
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public Class getSkeletonClass() {
        if(skeletonClass == null) {
            String skeletonClassName = packageName + "." + className;
            ClassPool pool = ClassPool.getDefault();

            CtClass cc = pool.makeClass(skeletonClassName);
            try {
                skeletonClass = cc.toClass();
            } catch (CannotCompileException e) {
                throw new IllegalStateException("Unable to create runtime skeleton class for class '" + skeletonClassName + "'.", e);
            }
        }
        return skeletonClass;
    }

    public JavaClass addProperty(JavaNamedType property) {
        AssertArgument.isNotNull(property, "property");
        assertPropertyUndefined(property);

        properties.add(property);

        String propertyName = property.getName();
        String capitalizedPropertyName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

        // Add property getter method...
        JavaMethod getterMethod = new JavaMethod(property.getType(), "get" + capitalizedPropertyName);
        getterMethod.setBody("return " + property.getName() + ";");
        methods.add(getterMethod);

        // Add property setter method...
        JavaMethod setterMethod = new JavaMethod("set" + capitalizedPropertyName);
        setterMethod.addParameter(property);
        setterMethod.setBody("return " + property.getName() + ";");
        setterMethod.setBody("this." + property.getName() + " = " + property.getName() + ";");
        methods.add(setterMethod);

        return this;
    }

    public List<JavaNamedType> getProperties() {
        return properties;
    }

    public List<JavaMethod> getMethods() {
        return methods;
    }

    public Set<Class> getImports() {
        Set<Class> importSet = new LinkedHashSet<Class>();

        for(JavaNamedType property : properties) {
            if(!property.getType().getType().isPrimitive()) {
                property.getType().addImports(importSet);
            }
        }

        return importSet;
    }

    public void writeClass(Writer writer) throws IOException {
        Map contextObj = new HashMap();

        contextObj.put("class", this);
        writer.write(template.apply(contextObj));
    }

    private void assertPropertyUndefined(JavaNamedType property) {
        for(JavaNamedType definedProperty : properties) {
            if(property.getName().equals(definedProperty.getName())) {
                throw new IllegalArgumentException("Property '" + property.getName() + "' already defined.");
            }
        }
    }
}