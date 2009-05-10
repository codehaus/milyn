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
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * BeanWriter writes all classes found in ClassModel to filesystem.
 * @author bardl
 */
public class BeanWriter {

    private static Log LOG = EJCLogFactory.getLog(EdiConfigReader.class);

    private static final String NEW_LINE = "\n";
    private static final String KEYWORD_CLASS = " class ";
    private static final String TAB = "\t";
    private static final String END_BLOCK = "}";
    private static final String START_BLOCK = "{";
    private static final String GENERIC_START = "<";
    private static final String GENERIC_END = ">";

    /**
     * Iterates through all classes defined in ClassModel. For each class it generates the class
     * implementation and saves the new class to filesystem.
     * @param model the {@link org.milyn.ejc.ClassModel}.
     * @param folder the output folder for generated classes.
     * @throws IOException when error ocurrs while saving the implemented class to filesystem.   
     */
    public static void writeBeans(ClassModel model, String folder) throws IOException {
        folder = new File(folder).getCanonicalPath();
        File file;
        FileOutputStream fileOutputStream;
        OutputStreamWriter outputStreamWriter;
        for ( JClass bean : model.getCreatedClasses().values() ) {
            if (bean instanceof JJavaClass) {
                continue;
            }
            file = new File(folder + "/" + bean.getPackage().getName().replace(".", "/"));
            if (!file.exists()) {
                file.mkdirs();
            }

            fileOutputStream = new FileOutputStream(file.getCanonicalPath()+ "/" + bean.getName() + ".java");
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write( writeBean(bean) );
            outputStreamWriter.close();
            fileOutputStream.close();
        }

    }

    /**
     * Creates implementation for a JClass found in ClassModel.
     * @param jClass the class to generate implementation for.
     * @return the implementation as a String.
     */
    public static String writeBean(JClass jClass) {
        LOG.debug("Creating implementation class " + jClass.getFullName() + ".");

        StringBuilder result = new StringBuilder();

        // Implement packagename
        if (jClass.getPackage() != null) {
            result.append(jClass.getPackage());
            result.append(JStatement.STATEMENT_SUFFIX);
            result.append(NEW_LINE);
            result.append(NEW_LINE);
        }

        // Implement imports
        for (JImport i : jClass.getImports()) {
            result.append(i);
            result.append(JStatement.STATEMENT_SUFFIX);
            result.append(NEW_LINE);
        }
        result.append(NEW_LINE);

        // Implement class definition
        result.append(jClass.getVisibility());
        result.append(KEYWORD_CLASS);
        result.append(jClass.getName());
        result.append(START_BLOCK);
        result.append(NEW_LINE);

        // Implement attributes
        for (JAttribute attribute : jClass.getAttributes()) {
            result.append(TAB);
            result.append(writeAttribute(attribute));
            result.append(NEW_LINE);
        }
        result.append(NEW_LINE);

        // Implement methods
        for (JMethod method : jClass.getMethods()) {
            result.append(TAB);
            result.append(writeMethod(method));
            result.append(START_BLOCK);
            result.append(NEW_LINE);
            for (JStatement statement : method.getStatements()) {
                result.append(TAB);
                result.append(TAB);
                result.append(statement);
                result.append(NEW_LINE);
            }
            result.append(TAB);
            result.append(END_BLOCK);
            result.append(NEW_LINE);
            result.append(NEW_LINE);
        }

        result.append(END_BLOCK);

        return result.toString();
    }

    /**
     * Creates implementation for a JAttribute found in JClass.
     * @param attribute the attribute to implment.
     * @return the implemented attribute as a String.
     */
    private static String writeAttribute(JAttribute attribute) {
        StringBuilder result = new StringBuilder();
        if (attribute.getType() instanceof JClass) {
            JClass clazz = (JClass)attribute.getType();
            if (clazz.getInterfaceOf() != null) {
                result.append(clazz.getInterfaceOf().getName());
            } else {
                result.append(clazz.getName());
            }
            if (clazz.getGenericType() != null) {
                result.append(GENERIC_START);
                result.append(clazz.getGenericType().getName());
                result.append(GENERIC_END);
            }

        } else {
            result.append( attribute.getType().getName());
        }
        result.append(" ");
        result.append(attribute.getName());
        result.append(JStatement.STATEMENT_SUFFIX);
        return result.toString();
    }

    /**
     * Creates implementation for a JMethod found in JClass.
     * @param method the method to implement.
     * @return the implemented method as a String.
     */
    private static String writeMethod(JMethod method) {
        StringBuilder result = new StringBuilder();
        result.append(method.getVisibility());
        result.append(" ");
        result.append(writeType(method));
        result.append(" ");
        result.append(method.getName());
        result.append("(");

        for (JParameter p : method.getParameters()) {
            result.append(writeType(p));
            result.append(" ");
            result.append(p.getName());
            result.append(", ");
        }
        if (method.getParameters().size() > 0) {
            result.replace(result.length()-2, result.length(), ")");
        } else {
            result.append(")");
        }
        return result.toString();
    }

    /**
     * Returns the returntype of a JMethod.  
     * @param method the JMethod.
     * @return the returntype of JMethod.
     */
    private static Object writeType(JMethod method) {
        StringBuilder result = new StringBuilder();
        if (method.getReturnType() instanceof JClass) {
            JClass jClass = (JClass)method.getReturnType();
            if (jClass.getInterfaceOf() != null) {
                result.append(jClass.getInterfaceOf().getName());
            } else {
                result.append(jClass.getName());
            }
            if (jClass.getGenericType() != null) {
                result.append(GENERIC_START);
                result.append(jClass.getGenericType().getName());
                result.append(GENERIC_END);
            }

        } else {
            result.append(method.getReturnType().getName());
        }
        return result.toString();
    }

    /**
     * Returns the type of a JParameter.
     * @param parameter the JParameter.
     * @return the type of JParameter.
     */
    private static Object writeType(JParameter parameter) {
        StringBuilder result = new StringBuilder();
        if (parameter.getType() instanceof JClass) {
            JClass jClass = (JClass)parameter.getType();
            if (jClass.getInterfaceOf() != null) {
                result.append(jClass.getInterfaceOf().getName());
            } else {
                result.append(jClass.getName());
            }
            if (jClass.getGenericType() != null) {
                result.append(GENERIC_START);
                result.append(jClass.getGenericType().getName());
                result.append(GENERIC_END);
            }

        } else {
            result.append(parameter.getType().getName());
        }
        return result.toString();
    }
}
