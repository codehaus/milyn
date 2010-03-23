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

import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.pojogen.JClass;
import org.milyn.javabean.pojogen.JNamedType;
import org.milyn.util.FreeMarkerTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * BindingWriter generates a bindingfile based on classstructure found in ClassModel.
 * @author bardl
 */
public class BindingWriter {

    private ClassModel classModel;
    private List<String> packagesIncluded;
    private List<String> packagesExcluded;
    private Stack<JClass> classStack = new Stack<JClass>();
    private FreeMarkerTemplate template = new FreeMarkerTemplate("templates/bindingConfig.ftl.xml", BindingWriter.class);

    public BindingWriter(ClassModel classModel) throws ClassNotFoundException {
        AssertArgument.isNotNull(classModel, "classModel");

        packagesExcluded = null;
        packagesIncluded = parsePackages(classModel.getRoot().getPackageName());
        this.classModel = classModel;
    }


    public void generate(String bindingfile) throws IOException {
        Map<String, Object> templatingContextObject = new HashMap<String, Object>();
        List<ClassConfig> classConfigs = new ArrayList<ClassConfig>();

        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(bindingfile));

            addClassConfig(classConfigs, classModel.getRoot(), null);

            templatingContextObject.put("classConfigs", classConfigs);
            templatingContextObject.put("classPackage", classModel.getRoot().getPackageName().replace('.', '/'));
            writer.write(template.apply(templatingContextObject));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private ClassConfig addClassConfig(List<ClassConfig> classConfigs, JClass beanClass, String beanId) {
        if(classStack.contains(beanClass)) {
            // Don't go into an endless loop... stack overflow etc...
            return null;
        }

        classStack.push(beanClass);
        try {
            ClassConfig classConfig = new ClassConfig(beanClass.getSkeletonClass(), beanId, classModel.getClassXmlElementName(beanClass.getClassName()));

            List<JNamedType> fields = beanClass.getProperties();
            List<BindingConfig> bindings = classConfig.getBindings();
            String rootPackage = classModel.getRoot().getPackageName();

            classConfigs.add(classConfig);

            for(JNamedType field : fields) {
                Class<?> type = field.getType().getType();
                Class<? extends DataDecoder> decoder = DataDecoder.Factory.getInstance(type);

                if(decoder != null) {
                    String xmlElementName = classModel.getClassXmlElementName(beanClass.getClassName()) + " " + classModel.getPropertyXmlElementName(beanClass.getClassName(), field.getName());
                    List<Map.Entry<String, String>> decoderConfigs = classModel.getPropertyDecoderConfigs(beanClass.getClassName(), field.getName());
                    bindings.add(new BindingConfig(field, xmlElementName, decoderConfigs));
                } else {
                    if(Collection.class.isAssignableFrom(type)) {
                        addCollectionConfig(classConfigs, bindings, rootPackage, field, beanClass);
                    } else {
                        String typePackage = getPackageName(type.getName());

                        if(isExcluded(typePackage)) {
                            continue;
                        } else if(typePackage.startsWith(rootPackage) || isIncluded(typePackage)) {
                            String xmlElementName = classModel.getPropertyXmlElementName(getClassName(type.getName()), field.getName());
                            if (field.getName() == null) {
                                xmlElementName = classModel.getClassXmlElementName(getClassName(type.getName())) + xmlElementName;
                            }
                            bindings.add(new BindingConfig(field, field.getName(), xmlElementName));
                            String className = getClassName(field.getType().getType().getName());
                            addClassConfig(classConfigs, classModel.getCreatedClasses().get(className), field.getName());
                        }
                    }
                }
            }

            return classConfig;
        } finally {
            classStack.pop();
        }
    }

    private String getClassName(String name) {
        return name.substring(name.lastIndexOf('.')+1);
    }

    private String getPackageName(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }

    private void addCollectionConfig(List<ClassConfig> classConfigs, List<BindingConfig> bindings, String rootPackage, JNamedType field, JClass beanClass) {
        Class paramType = field.getType().getGenericType();

        if(paramType == null) {
            // No generics info.  Can't infer anything...
        } else {

            String wireBeanId = field.getName() + "_entry";
            String typePackage = getPackageName(paramType.getName());

            if(isExcluded(typePackage)) {
                return;
            } else if(typePackage.startsWith(rootPackage) || isIncluded(typePackage)) {
                ClassConfig listConfig = new ClassConfig(ArrayList.class, field.getName(), classModel.getClassXmlElementName(beanClass.getClassName()));
                String xmlElementName = classModel.getPropertyXmlElementName(getClassName(paramType.getName()), field.getName());
                if (field.getName() == null) {
                    xmlElementName = classModel.getClassXmlElementName(beanClass.getClassName()) + xmlElementName;
                }
                listConfig.getBindings().add(new BindingConfig(wireBeanId, xmlElementName));
                classConfigs.add(listConfig);

                bindings.add(new BindingConfig(field, field.getName(), xmlElementName));
                String className = getClassName(paramType.getName());
                addClassConfig(classConfigs, classModel.getCreatedClasses().get(className), wireBeanId);
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

    private List<String> parsePackages(String packagesString) {
        String[] packages = packagesString.split(";");
        List<String> packagesSet = new ArrayList<String>();

        for(String aPackage : packages) {
            packagesSet.add(aPackage.trim());
        }

        return packagesSet;
    }
}

