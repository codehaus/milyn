/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.javabean.dynamic.serialize.freemarker;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.*;
import org.milyn.javabean.dynamic.Model;
import org.milyn.javabean.dynamic.serialize.BeanWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
class WriteNamespacesDirective implements TemplateDirectiveModel {

    public void execute(Environment environment, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Writer writer = environment.getOut();
        BeanModel modelBeanModel = (BeanModel) environment.getDataModel().get(FreeMarkerBeanWriter.MODEL_CTX_KEY);
        Model model = (Model) modelBeanModel.getWrappedObject();
        Map<String, String> namespaces = model.getNamespacePrefixMappings();
        Set<Map.Entry<String, String>> nsEntries = namespaces.entrySet();
        boolean addSpace = false;

        for(Map.Entry<String, String> nsEntry : nsEntries) {
            if(addSpace) {
                writer.write(' ');
            }

            String uri = nsEntry.getKey();
            String prefix = nsEntry.getValue();

            writer.write("xmlns:" + prefix + "=");
            writer.write('"');
            writer.write(uri);
            writer.write('"');
            
            addSpace = true;
        }
    }
}