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
package org.milyn.javabean;

import org.milyn.Smooks;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.javabean.ext.SelectorPropertyResolver;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class Bean {

    private BeanInstanceCreator beanInstanceCreator;
    private Class beanClass;
    private String createOnElement;
    private Smooks smooks;

    public Bean(Class beanClass, String beanId, String createOnElement, Smooks smooks) {
        AssertArgument.isNotNull(beanClass, "beanClass");
        AssertArgument.isNotNull(createOnElement, "createOnElement");
        AssertArgument.isNotNull(smooks, "smooks");

        this.beanClass = beanClass;
        this.createOnElement = createOnElement;
        this.smooks = smooks;

        beanInstanceCreator = new BeanInstanceCreator(beanId, beanClass);
        smooks.addVisitor(createOnElement, new BeanInstanceCreator(beanId, beanClass));
    }

    public String getBeanId() {
        return beanInstanceCreator.getBeanId();
    }

    public Bean newBean(Class beanClass, String createOnElement) {
        String randomBeanId = UUID.randomUUID().toString();
        return new Bean(beanClass, randomBeanId, createOnElement, smooks);
    }

    public Bean bindTo(String bindingMember, String dataSelector) {
        return bindTo(bindingMember, dataSelector, null);
    }

    public Bean bindTo(String bindingMember, String dataSelector, DataDecoder dataDecoder) {
        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();
        SmooksResourceConfiguration populatorConfig = new SmooksResourceConfiguration(dataSelector);

        SelectorPropertyResolver.resolveSelectorTokens(populatorConfig);

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setValueAttributeName(populatorConfig.getStringParameter(BeanInstancePopulator.VALUE_ATTRIBUTE_NAME));
        Method bindingMethod = getBindingMethod(bindingMember);
        if(bindingMethod != null) {
            if(dataDecoder == null) {
                Class dataType = bindingMethod.getParameterTypes()[0];
                dataDecoder = DataDecoder.Factory.create(dataType);
            }

            if(bindingMethod.getName().equals(bindingMember)) {
                beanInstancePopulator.setSetterMethod(bindingMethod.getName());
            } else {
                beanInstancePopulator.setProperty(bindingMember);
            }
        } else {
            beanInstancePopulator.setProperty(bindingMember);
        }
        beanInstancePopulator.setDecoder(dataDecoder);

        smooks.addVisitor(populatorConfig.getSelector(), beanInstancePopulator);

        return this;
    }

    public Bean bindTo(String bindingMember, Bean bean) {
        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setWireBeanId(bean.getBeanId());
        Method bindingMethod = getBindingMethod(bindingMember);
        if(bindingMethod != null) {
            if(bindingMethod.getName().equals(bindingMember)) {
                beanInstancePopulator.setSetterMethod(bindingMethod.getName());
            } else {
                beanInstancePopulator.setProperty(bindingMember);
            }
        } else {
            beanInstancePopulator.setProperty(bindingMember);
        }

        smooks.addVisitor(createOnElement, beanInstancePopulator);

        return this;
    }

    public Bean bind(Bean bean) {
        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setWireBeanId(bean.getBeanId());

        smooks.addVisitor(createOnElement, beanInstancePopulator);

        return this;
    }

    public Bean bind(String dataSelector) {
        return bind(dataSelector, null);
    }

    public Bean bind(String dataSelector, DataDecoder dataDecoder) {
        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();
        SmooksResourceConfiguration populatorConfig = new SmooksResourceConfiguration(dataSelector);

        SelectorPropertyResolver.resolveSelectorTokens(populatorConfig);

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setValueAttributeName(populatorConfig.getStringParameter(BeanInstancePopulator.VALUE_ATTRIBUTE_NAME));
        beanInstancePopulator.setDecoder(dataDecoder);

        smooks.addVisitor(populatorConfig.getSelector(), beanInstancePopulator);

        return this;
    }

    private Method getBindingMethod(String bindingMember) {
        Method[] methods = beanClass.getMethods();

        // Check is the bindingMember an actual fully qualified method name...
        for(Method method : methods) {
            if(method.getName().equals(bindingMember) && method.getParameterTypes().length == 1) {
                return method;
            }
        }

        // Check is the bindingMember defined by a property name.  If so, there should be a
        // bean setter method for that property...
        String asPropertySetterMethod = BeanUtils.toSetterName(bindingMember);
        for(Method method : methods) {
            if(method.getName().equals(asPropertySetterMethod) && method.getParameterTypes().length == 1) {
                return method;
            }
        }

        // Can't resolve it...
        return null;
    }
}
