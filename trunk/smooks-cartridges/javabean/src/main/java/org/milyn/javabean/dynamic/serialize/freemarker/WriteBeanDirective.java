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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.javabean.dynamic.Model;
import org.milyn.javabean.dynamic.serialize.BeanWriter;
import org.milyn.xml.XmlUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class WriteBeanDirective implements TemplateDirectiveModel {

    private static Log logger = LogFactory.getLog(WriteBeanDirective.class);

    public void execute(Environment environment, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        SimpleScalar beanNameScalar = (SimpleScalar) params.get("name");
        SimpleScalar indentScalar = (SimpleScalar) params.get("indent");
        int indent = 0;

        if(beanNameScalar == null) {
            throw new TemplateException("Mandatory <@writeBean> directive parameter 'name' node defined e.g. <@writeBean name='customer' />.", environment);
        }
        if(indentScalar != null) {
            String indentParamVal = indentScalar.getAsString().trim();
            try {
                indent = Integer.parseInt(indentParamVal);
                indent = Math.min(indent, 100);
            } catch(NumberFormatException e) {
                logger.debug("Invalid <@writeBean> 'indent' parameter value '" + indentParamVal + "'.  Must be a valid integer (<= 100).");                
            }
        }

        String beanName = beanNameScalar.getAsString();
        TemplateModel beanTemplateModel = environment.getLocalVariable(beanName);

        if(beanTemplateModel == null) {
            beanTemplateModel = environment.getDataModel().get(beanName);
            if(beanTemplateModel == null) {
                return;
            }
        }

        if(beanTemplateModel instanceof BeanModel) {
            Object bean = ((BeanModel)beanTemplateModel).getWrappedObject();
            BeanModel modelBeanModel = (BeanModel) environment.getDataModel().get(FreeMarkerBeanWriter.MODEL_CTX_KEY);
            Model model = (Model) modelBeanModel.getWrappedObject();
            BeanWriter beanWriter = model.getBeanWriter(bean);

            if(indent > 0) {
                StringWriter beanWriteBuffer = new StringWriter();

                beanWriteBuffer.write('\n');
                beanWriter.write(bean, beanWriteBuffer, model);

                environment.getOut().write(XmlUtil.indent(beanWriteBuffer.toString(), indent));
            } else {
                beanWriter.write(bean, environment.getOut(), model);
            }
        }
    }
}
