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
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.javabean.dynamic.BeanMetadata;
import org.milyn.javabean.dynamic.BeanRegistrationException;
import org.milyn.javabean.dynamic.Model;
import org.milyn.javabean.dynamic.serialize.BeanWriter;
import org.milyn.xml.XmlUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Write bean pretext directive.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class WriteBeanPreTextDirective extends AbstractBeanDirective {

    public void execute(Environment environment, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Object bean = getBeanObject(environment, params, "writePreText");

        BeanModel modelBeanModel = (BeanModel) environment.getDataModel().get(FreeMarkerBeanWriter.MODEL_CTX_KEY);
        Model model = (Model) modelBeanModel.getWrappedObject();
        BeanMetadata beanMetadata = model.getBeanMetadata(bean);

        if(beanMetadata != null && beanMetadata.getPreText() != null) {
            String preText = beanMetadata.getPreText().trim();
            if(preText.length() > 0) {
                environment.getOut().write(preText);
            }
        }
    }

}