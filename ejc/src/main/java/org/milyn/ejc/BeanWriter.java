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
import org.milyn.ejc.classes.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
     * @param bindingFile the name of the smooks configuration.
     * @throws IOException when error ocurrs while saving the implemented class to filesystem.
     * @throws IllegalNameException when class is a keyword in java.
     */
    public static void writeBeans(ClassModel model, String folder, String bindingFile) throws IOException, IllegalNameException {
        folder = new File(folder).getCanonicalPath();

        for ( JClass bean : model.getCreatedClasses().values() ) {
            if (bean instanceof JJavaClass) {
                continue;
            }
            writeToFile(folder, bean.getPackage().getName(), bean.getName(), writeBean(bean));
        }

        writeFactoryClass(folder, model.getRoot(), bindingFile);
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

    /**
     * Creates a Factory for root-class in ClassModel. The Factory wraps
     * the logic for filtering the edi-input in Smooks.
     * @param folder the folder to store the Factory.     
     * @param rootClass the root class in {@link org.milyn.ejc.ClassModel}.
     * @param bindingfile the name of the smooks configuration.
     * @throws IllegalNameException when class is a keyword in java.
     * @throws IOException when error occurs when saving to filesystem.
     */
    private static void writeFactoryClass(String folder, JClass rootClass, String bindingfile  ) throws IllegalNameException, IOException {
        String packageName = rootClass.getPackage().getName();
        String className = rootClass.getName();
        String classId = EJCUtils.encodeAttributeName(rootClass.getType(), rootClass.getName());
        String factoryClass = getFactorySkeleton();
        factoryClass = factoryClass.replaceAll("\\$\\{package\\}", packageName);
        factoryClass = factoryClass.replaceAll("\\$\\{className\\}", className);
        factoryClass = factoryClass.replaceAll("\\$\\{classId\\}", classId);
        factoryClass = factoryClass.replaceAll("\\$\\{bindingFile\\}", new File(bindingfile).getName());

        writeToFile(folder, packageName, className+"Factory", factoryClass);
    }

    /**
     * Saves beanAsString to filesystem.
     * @param folder the folder to store the Factory.
     * @param packageName the java package.
     * @param beanName the name of the Class.
     * @param beanAsString a String-representation of the Class.
     * @throws IOException when error occurs when saving to filesystem.
     */
    private static void writeToFile(String folder, String packageName, String beanName, String beanAsString) throws IOException {
        File file;
        file = new File(folder + "/" + packageName.replace(".", "/"));
        if (!file.exists()) {
            file.mkdirs();
        }

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream = new FileOutputStream(file.getCanonicalPath()+ "/" + beanName + ".java");
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write( beanAsString );
        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * Returns the code for the Factory with keywords to substitute
     * with specific information found in root-class in {@link org.milyn.ejc.ClassModel}.
     * @return the Factory class implementation.
     */
    private static String getFactorySkeleton() {
        return "package ${package};\n" +
                "\n" +
                "import org.milyn.Smooks;\n" +
                "import org.milyn.payload.JavaResult;\n" +
                "import org.xml.sax.SAXException;\n" +
                "\n" +
                "import javax.xml.transform.stream.StreamSource;\n" +
                "import java.io.Reader;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.io.InputStream;\n" +
                "import java.io.IOException;\n" +
                "import javax.xml.transform.Result;\n" +
                "\n" +
                "public class ${className}Factory {\n" +
                "\n" +
                "    private Smooks smooks;\n" +
                "\n" +
                "    public static ${className}Factory getInstance() throws IOException, SAXException {\n" +
                "        return new ${className}Factory();\n" +
                "    }\n" +
                "\n" +
                "    public void addConfigurations(InputStream resourceConfigStream) throws SAXException, IOException {\n" +
                "        smooks.addConfigurations(resourceConfigStream);\n" +
                "    }\n" +
                "\n" +
                "    public ${className} parse(InputStream ediStream, Result... additionalResults) {\n" +
                "        return parse(new InputStreamReader(ediStream), additionalResults);\n" +
                "    }\n" +
                "\n" +
                "    public ${className} parse(Reader ediStream) {\n" +
                "        JavaResult result = new JavaResult();\n" +
                "        smooks.filterSource(new StreamSource(ediStream), result);\n" +
                "        return (${className}) result.getBean(\"${classId}\");\n" +
                "    }\n" +
                "\n" +
                "    public ${className} parse(Reader ediStream, Result... additionalResults) {\n" +
                "        JavaResult javaResult = new JavaResult();\n" +
                "        int numAdditionalRes = (additionalResults != null? additionalResults.length : 0);\n" +
                "        Result[] results = new Result[numAdditionalRes + 1];\n" +
                "        \n" +
                "        results[0] = javaResult;\n" +
                "        if(additionalResults != null) {\n" +
                "            System.arraycopy(additionalResults, 0, results, 1, numAdditionalRes);\n" +
                "        }\n" +
                "        \n" +
                "        smooks.filterSource(new StreamSource(ediStream), results);\n" +
                "        return (${className}) javaResult.getBean(\"${classId}\");\n" +
                "    }\n" +
                "\n" +
                "    private ${className}Factory() throws IOException, SAXException {\n" +
                "        smooks = new Smooks(${className}Factory.class.getResourceAsStream(\"${bindingFile}\"));\n" +
                "    }\n" +
                "\n" +
                "}";
    }
}
