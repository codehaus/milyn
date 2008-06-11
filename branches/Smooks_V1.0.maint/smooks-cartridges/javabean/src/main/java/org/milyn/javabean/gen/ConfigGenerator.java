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
package org.milyn.javabean.gen;

import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.gen.model.BindingConfig;
import org.milyn.javabean.gen.model.ClassConfig;
import org.milyn.util.FreeMarkerTemplate;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Java binding configuration template generator.
 * <p/>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ConfigGenerator {

    public static final String ROOT_BEAN_CLASS = "root.beanClass";
    public static final String PACKAGES_INCLUDED = "packages.included";
    public static final String PACKAGES_EXCLUDED = "packages.excluded";

    private Writer outputWriter;
    private Class<?> rootBeanClass;
    private List<String> packagesIncluded;
    private List<String> packagesExcluded;
    private Stack classStack = new Stack();

    public void main(String args[]) throws IOException, ClassNotFoundException {
        if(args.length != 2) {
            throw new IllegalArgumentException("Expecting 2 arguments specifying:\n\t1. The path to the binding configuration properties file.\n\t2. The output Smooks configuration template.  See class Javadoc.");
        }

        Properties properties = loadProperties(args[0]);
        File outputFile = new File(args[1]);

        outputFile.getParentFile().mkdirs();
        Writer outputWriter = new FileWriter(outputFile);

        try {
            ConfigGenerator generator = new ConfigGenerator(properties, outputWriter);
            generator.generate();
        } finally {
            try {
                outputWriter.flush();
            } finally {
                outputWriter.close();
            }
        }
    }

    public ConfigGenerator(Properties bindingProperties, Writer outputWriter) throws ClassNotFoundException {
        AssertArgument.isNotNull(bindingProperties, "bindingProperties");
        AssertArgument.isNotNull(outputWriter, "outputWriter");
        this.outputWriter = outputWriter;

        configure(bindingProperties);
    }

    public void generate() throws IOException {
        Map templatingContextObject = new HashMap();
        List<ClassConfig> classConfigs = new ArrayList<ClassConfig>();
        FreeMarkerTemplate template;

        addClassConfig(classConfigs, rootBeanClass, null);
        template = new FreeMarkerTemplate("templates/bindingConfig.ftl.xml", getClass());

        templatingContextObject.put("classConfigs", classConfigs);
        outputWriter.write(template.apply(templatingContextObject));
    }

    private ClassConfig addClassConfig(List<ClassConfig> classConfigs, Class beanClass, String beanId) {
        if(classStack.contains(beanClass)) {
            // Don't go into an endless loop... stack overflow etc...
            return null;
        }

        classStack.push(beanClass);
        try {
            ClassConfig classConfig = new ClassConfig(beanClass, beanId);
            Field[] fields = beanClass.getDeclaredFields();
            List<BindingConfig> bindings = classConfig.getBindings();
            String rootPackage = rootBeanClass.getPackage().getName();

            classConfigs.add(classConfig);

            for(Field field : fields) {
                Class type = field.getType();
                Class<? extends DataDecoder> decoder = DataDecoder.Factory.getInstance(type);

                if(decoder != null) {
                    bindings.add(new BindingConfig(field));
                } else {
                    if(type.isArray()) {
                        addArrayConfig(classConfigs, bindings, rootPackage, field);
                    } else if(List.class.isAssignableFrom(type)) {
                        addListConfig(classConfigs, bindings, rootPackage, field);
                    } else {
                        String typePackage = type.getPackage().getName();

                        if(isExcluded(typePackage)) {
                            continue;
                        } else if(typePackage.startsWith(rootPackage) || isIncluded(typePackage)) {
                            bindings.add(new BindingConfig(field, field.getName()));
                            addClassConfig(classConfigs, type, field.getName());
                        }
                    }
                }
            }

            return classConfig;
        } finally {
            classStack.pop();
        }
    }

    private void addArrayConfig(List<ClassConfig> classConfigs, List<BindingConfig> bindings, String rootPackage, Field field) {
        Class type = field.getType();
        Class arrayType = type.getComponentType();
        String wireBeanId = field.getName() + "_entry";
        String typePackage = arrayType.getPackage().getName();

        if(isExcluded(typePackage)) {
            return;
        } else if(typePackage.startsWith(rootPackage) || isIncluded(typePackage)) {
            ClassConfig arrayConfig = new ClassConfig(arrayType, field.getName());

            arrayConfig.getBindings().add(new BindingConfig(wireBeanId));
            arrayConfig.setArray(true);
            classConfigs.add(arrayConfig);

            bindings.add(new BindingConfig(field, field.getName()));
            addClassConfig(classConfigs, arrayType, wireBeanId);
        }
    }

    private void addListConfig(List<ClassConfig> classConfigs, List<BindingConfig> bindings, String rootPackage, Field field) {
        ParameterizedType paramType = (ParameterizedType) field.getGenericType();
        Type[] types = paramType.getActualTypeArguments();

        if(types.length == 0) {
            // No generics info.  Can't infer anything...
        } else {
            Class type = (Class) types[0];
            String wireBeanId = field.getName() + "_entry";
            String typePackage = type.getPackage().getName();

            if(isExcluded(typePackage)) {
                return;
            } else if(typePackage.startsWith(rootPackage) || isIncluded(typePackage)) {
                ClassConfig listConfig = new ClassConfig(ArrayList.class, field.getName());

                listConfig.getBindings().add(new BindingConfig(wireBeanId));
                classConfigs.add(listConfig);

                bindings.add(new BindingConfig(field, field.getName()));
                addClassConfig(classConfigs, type, wireBeanId);
            }
        }
    }

    private boolean isIncluded(String packageName) {
        if(packagesIncluded != null) {
            if(isInPackageList(packagesIncluded, packageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcluded(String packageName) {
        if(packagesExcluded != null) {
            if(isInPackageList(packagesExcluded, packageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInPackageList(List<String> packages, String typePackage) {
        for (String packageName : packages) {
            if(typePackage.startsWith(packageName)) {
                return true;
            }
        }

        return false;
    }

    private void configure(Properties bindingProperties) throws ClassNotFoundException {
        String rootBeanClassConfig = bindingProperties.getProperty(ConfigGenerator.ROOT_BEAN_CLASS);
        String packagesIncludedConfig = bindingProperties.getProperty(ConfigGenerator.PACKAGES_INCLUDED);
        String packagesExcludedConfig = bindingProperties.getProperty(ConfigGenerator.PACKAGES_EXCLUDED);

        if(rootBeanClassConfig == null) {
            throw new IllegalArgumentException("Binding configuration property '" + ConfigGenerator.ROOT_BEAN_CLASS + "' not defined.");
        }
        rootBeanClass = Class.forName(rootBeanClassConfig);

        if(packagesIncludedConfig != null) {
            packagesIncluded = parsePackages(packagesIncludedConfig);
        }
        if(packagesExcludedConfig != null) {
            packagesExcluded = parsePackages(packagesExcludedConfig);
        }
    }

    private List<String> parsePackages(String packagesString) {
        String[] packages = packagesString.split(";");
        List<String> packagesSet = new ArrayList<String>();

        for(String aPackage : packages) {
            packagesSet.add(aPackage.trim());
        }

        return packagesSet;
    }

    private Properties loadProperties(String fileName) throws IOException {
        File propertiesFile = new File(fileName);

        if(!propertiesFile.exists()) {
            throw new IllegalArgumentException("Binding configuration properties file '" + propertiesFile.getAbsolutePath() + "' doesn't exist.  See class Javadoc.");
        }

        Properties properties = new Properties();
        InputStream stream = new FileInputStream(propertiesFile);

        try {
            properties.load(stream);
        } finally {
            stream.close();
        }

        return properties;
    }
}
