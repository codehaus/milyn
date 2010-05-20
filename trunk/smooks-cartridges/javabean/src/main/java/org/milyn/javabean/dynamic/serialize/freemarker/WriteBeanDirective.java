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
import freemarker.ext.beans.StringModel;
import freemarker.template.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.javabean.dynamic.BeanMetadata;
import org.milyn.javabean.dynamic.BeanRegistrationException;
import org.milyn.javabean.dynamic.Model;
import org.milyn.javabean.dynamic.serialize.BeanWriter;
import org.milyn.xml.XmlUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * Write bean directive.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class WriteBeanDirective implements TemplateDirectiveModel {

    private static Log logger = LogFactory.getLog(WriteBeanDirective.class);

    public void execute(Environment environment, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Object beanParam = params.get("bean");

        if(beanParam == null) {
            if(params.containsKey("bean")) {
                throw new TemplateException("Mandatory <@writeBean> directive parameter 'bean' is defined, but the bean is not visible in the model.  Should be a valid model object reference (no quotes) e.g. <@writeBean bean=customer.address />.", environment);
            } else {
                throw new TemplateException("Mandatory <@writeBean> directive parameter 'bean' is not defined.  Should be a valid model object reference (no quotes) e.g. <@writeBean bean=customer.address />.", environment);
            }
        }

        if(!(beanParam instanceof StringModel)) {
            throw new TemplateException("Mandatory <@writeBean> directive parameter 'bean' not defined properly.  Should be a valid model object reference (no quotes) e.g. <@writeBean bean=customer.address />.", environment);
        }

        StringModel beanModel = (StringModel) beanParam;
        SimpleScalar indentScalar = (SimpleScalar) params.get("indent");
        int indent = 0;

        if(indentScalar != null) {
            String indentParamVal = indentScalar.getAsString().trim();
            try {
                indent = Integer.parseInt(indentParamVal);
                indent = Math.min(indent, 100);
            } catch(NumberFormatException e) {
                logger.debug("Invalid <@writeBean> 'indent' parameter value '" + indentParamVal + "'.  Must be a valid integer (<= 100).");                
            }
        }

        Object bean = beanModel.getWrappedObject();
        BeanModel modelBeanModel = (BeanModel) environment.getDataModel().get(FreeMarkerBeanWriter.MODEL_CTX_KEY);
        Model model = (Model) modelBeanModel.getWrappedObject();
        BeanMetadata beanMetadata = model.getBeanMetadata(bean);

        if(beanMetadata == null) {
            BeanRegistrationException.throwUnregisteredBeanInstanceException(bean);
        }

        BeanWriter beanWriter = beanMetadata.getWriter();

        if(beanMetadata.getPreText() != null) {
            environment.getOut().write(beanMetadata.getPreText());
        }

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
