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
import org.milyn.util.FreeMarkerTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * BeanWriter writes all classes found in ClassModel to filesystem.
 * @author bardl
 */
public class BeanWriter {

    private static Log LOG = EJCLogFactory.getLog(EdiConfigReader.class);

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
            writeToFile(folder, bean);
        }

        writeFactoryClass(folder, model.getRoot(), bindingFile);
    }

    /**
     * Creates the factory class for wrapping the filtering logic in Smooks.
     * @param folder the folder where the factory-class should be created.
     * @param rootClass the root class giving name to the factory-class.
     * @param bindingFile the bindingfile created by the EJC.
     * @throws IllegalNameException when class name violates keywords in java.
     * @throws IOException when error ocurrrs while writing factory to file.
     */
    private static void writeFactoryClass(String folder, JClass rootClass, String bindingFile) throws IllegalNameException, IOException {
        String packageName = rootClass.getPackageName();
        String className = rootClass.getClassName();
        String classId = EJCUtils.encodeAttributeName(null, rootClass.getClassName());

        Map<String, String> configs = new HashMap<String,String>();
        configs.put("package", packageName);
        configs.put("className", className);
        configs.put("classId", classId);
        configs.put("bindingFile", new File(bindingFile).getName());

        FreeMarkerTemplate template;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter writer = null;
        try {
            File file = new File(folder + "/" + packageName.replace(".", "/"));
            fileOutputStream = new FileOutputStream(file.getCanonicalPath()+ "/" + className + "Factory.java");
            writer = new OutputStreamWriter(fileOutputStream);
            template = new FreeMarkerTemplate("templates/factoryClass.ftl.xml", BeanWriter.class);

            writer.write(template.apply(configs));

        } finally {
            if (writer != null) {
                writer.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * Writes a JClass to file.
     * @param folder the file-path.
     * @param bean the ${@link org.milyn.javabean.pojogen.JClass] to save.
     * @throws IOException when error occurs while saving file.
     * @throws java.io.IOException when error occurs while saving class to file.
     */
    private static void writeToFile(String folder, JClass bean) throws IOException {
        File file = new File(folder + "/" + bean.getPackageName().replace(".", "/"));
        if (!file.exists()) {
            file.mkdirs();
        }

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream = new FileOutputStream(file.getCanonicalPath()+ "/" + bean.getClassName() + ".java");
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            bean.writeClass(outputStreamWriter);
        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
}
