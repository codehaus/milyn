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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.ExpandableContentDeliveryUnit;
import org.milyn.util.ClassUtil;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Abstract Javabean populator.
 * <h3>XML Configuration</h3>
 * <pre>
 * &lt;resource-config target-profile="<i>profile</i>" selector="<i>target-element</i>"&gt;
 *  &lt;resource&gt;<b>org.milyn.javabean.AssemblyPhaseBeanPopulator</b> OR <b>org.milyn.javabean.ProcessingPhaseBeanPopulator</b>&lt;/resource&gt;
 *
 *  &lt;!-- (Mandatory) The bean identifier. --&gt;
 *  &lt;param name="<b>beanId</b>"&gt;<i>bean class name</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) The runtime class name of the bean to be populated.
 *          This param is only required when creating a new bean instance.
 *          Used in conjunction with the 'addToList' param. --&gt;
 *  &lt;param name="<b>beanClass</b>"&gt;<i>bean class name</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) Are multiple bean instance to be accumulated into
 *          a list, or should each new instance replace the previous instance.
 *          Must be specified in conjunction with the 'beanClass' param.
 *          Default is "false". --&gt;
 *  &lt;param name="<b>addToList</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) The name of the bean setter method on which the data 
 *          item (text or attribute value) is to be set.  If 'attributeName' 
 *          is specified and this param is not specified, the setterName defaults to
 *          the Javabean setter name for the attribute e.g. an attributeName param
 *          value of 'phoneNumber' will default setterName to 'setPhoneNumber'. 
 *          If both 'attributeName' and 'setterName' are unspecified, the bean
 *          instance is simply created and bound to the request. 
 *          If 'attributeName' is not set and 'setterName' is
 *          set, the elements child text will be set on the bean. --&gt;
 *  &lt;param name="<b>setterName</b>"&gt;<i>the name of the bean setter method</i>&lt;/param&gt;
 * 
 *  &lt;!-- (Optional) If this param is set, get the attribute value for this
 *          attribute and set it on the bean based on the setter method
 *          specified by the setterName param. --&gt;
 *  &lt;param name="<b>attributeName</b>"&gt;<i>the name of the element attribute</i>&lt;/param&gt;
 * 
 * &lt;/resource-config&gt;
 * </pre>
 * 
 * @author tfennelly
 */
public abstract class AbstractBeanPopulator implements DOMElementVisitor, ExpandableContentDeliveryUnit {

    private static Log logger = LogFactory.getLog(AbstractBeanPopulator.class);
    private SmooksResourceConfiguration config;
    private String beanId;
    private Class beanClass;
    private boolean addToList = false;
    private String setterName;
    private Method beanSetterMethod;
    private boolean isAttribute = true;
    private String attributeName;

    /**
     * Set the resource configuration on the bean populator.
     */
	public void setConfiguration(SmooksResourceConfiguration config) {
        this.config = config;

        // Bean ID...
        beanId = config.getStringParameter("beanId");
        if(beanId == null || (beanId = beanId.trim()).equals("")) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'beanId' param not specified.");
        }
        
        // Bean runtime class...
        String beanClassName = config.getStringParameter("beanClass");
        if(beanClassName != null && (beanClassName = beanClassName.trim()).equals("")) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'beanClass' param empty.");
        } else if(beanClassName != null) {
            beanClass = createBeanRuntime(beanClassName);
            addToList = config.getBoolParameter("addToList", false);
        }
        
        // What data is to be extracted from the visited element and set on the bean...
        attributeName = config.getStringParameter("attributeName");
        if(attributeName != null) {
            if((attributeName = attributeName.trim()).equals("")) {
                throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'attributeName' param specified but blank.");
            }
            isAttribute = true;
        } else {
            // Not an attribute - this means we'll be setting the element text on the bean.
            isAttribute = false;
        }

        // Setter Method object instance...
        setterName = config.getStringParameter("setterName");
        if(setterName == null || (setterName = setterName.trim()).equals("")) {
            if(isAttribute) {
                setterName = "set" + Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
            } else {
                // setterName and attributeName are not set - visitAfter method should only
                // create the bean!!
            }
        }

        logger.debug("Bean Populator created for [" + beanId + ":" + beanClassName + "].  Add to list=" + addToList + ", attributeName=" + attributeName + ", setterName" + setterName);
    }

    public List<SmooksResourceConfiguration> getExpansionConfigurations() {
        List<SmooksResourceConfiguration> resources = new ArrayList<SmooksResourceConfiguration>();
        String bindings = config.getStringParameter("bindings");

        if(bindings != null) {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(bindings));
            String bindingConfig;

            try {
                while((bindingConfig = bufferedReader.readLine()) != null) {
                    bindingConfig = bindingConfig.trim();
                    if(!bindingConfig.equals("")) {
                        resources.add(buildConfig(bindingConfig));
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to read binding configuration for " + config);
            }
        }

        return resources;
    }

    private SmooksResourceConfiguration buildConfig(String bindingConfig) throws IOException {
        SmooksResourceConfiguration resourceConfig = null;
        Properties bindingProperties = new Properties();

        // Read in the bindings as a set of properties...
        bindingConfig = bindingConfig.replace(',', '\n');
        bindingProperties.load(new ByteArrayInputStream(bindingConfig.getBytes("UTF-8")));

        // Make sure there's a selector...
        if(!bindingProperties.containsKey("selector")) {
            logger.error("Binding configuration must contain a 'selector' key: " + config);
        }

        // Construct the configuraton...
        resourceConfig = new SmooksResourceConfiguration(bindingProperties.getProperty("selector", config.getSelector()), getClass().getName());
        resourceConfig.setParameter("beanId", beanId);
        bindingProperties.remove("selector");
        Iterator bindingPropertyIterator = bindingProperties.entrySet().iterator();
        while(bindingPropertyIterator.hasNext()) {
            Map.Entry property = (Map.Entry) bindingPropertyIterator.next();
            resourceConfig.setParameter((String)property.getKey(), (String)property.getValue());
        }

        return resourceConfig;
    }

    /**
     * Visit the element and extract data and set on the bean.
     * @param element Element being visited.
     * @param executionContext Execution context.
     */
    public void visitBefore(Element element, ExecutionContext executionContext) {
        if(logger.isDebugEnabled()) {
        	logger.debug("Visiting bean populator for beanId [" + beanId + "] on element " + DomUtils.getXPath(element));
        }
    	    	
    	Object bean = getBean(executionContext);
        
        if(setterName == null && attributeName == null) {
            // setterName and attributeName are not set - visitAfter method should only
            // create the bean!!
            return;
        }

        // If we need to create the bean setter method instance...
        if(beanSetterMethod ==  null) {
            beanSetterMethod = createBeanSetterMethod(bean);
        }
        
        // Set the data on the bean...
        try {
            if(isAttribute) {
                beanSetterMethod.invoke(bean, new Object[] {element.getAttribute(attributeName)});
            } else {
                beanSetterMethod.invoke(bean, new Object[] {DomUtils.getAllText(element, false)});
            }
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + setterName + "] on bean instance class type [" + bean.getClass() + "].", e);
        } catch (InvocationTargetException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + setterName + "] on bean instance class type [" + bean.getClass() + "].", e);
        }
    }

    /**
     * Visit the element and extract data and set on the bean.
     * @param element Element being visited.
     * @param executionContext Container request.
     */
    public void visitAfter(Element element, ExecutionContext executionContext) {        
    }

    /**
     * Get the bean instance on which this populator instance is to set data.
     * @param request The request instance to which the bean instance is to be
     * be bound.
     * @return The bean instance.
     */
    private Object getBean(ExecutionContext request) {
        Object bean = null;

        // If the configuration associated with this populator instance has a beanClass
        // configured, create a new instance and set it on the request...
        if(beanClass != null) {
            bean = createBeanInstance();
            BeanAccessor.addBean(beanId, bean, request, addToList);
            if(logger.isDebugEnabled()) {
            	logger.debug("Bean [" + beanId + "] instance created.");
            }
        } else {
            // Get the bean instance from the request.  If there is non, it's a bad config!!
            bean = BeanAccessor.getBean(beanId, request);
            if(logger.isDebugEnabled()) {
            	logger.debug("Not creating a new bean instance for beanId [" + beanId + "].  Using [" + bean + "]");
            }
            if(bean == null) {
                throw new SmooksConfigurationException("Bean instance [id=" + beanId + "] not available and bean runtime class not set on configuration.");
            }
        }
        
        return bean;
    }

    /**
     * Create a new bean instance, generating relevant configuration exceptions.
     * @return A new bean instance.
     */
    private Object createBeanInstance() {
        Object bean;
        
        try {
            bean = beanClass.newInstance();
        } catch (InstantiationException e) {
            throw new SmooksConfigurationException("Unable to create bean instance [" + beanId + ":" + beanClass.getName() + "].", e);
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Unable to create bean instance [" + beanId + ":" + beanClass.getName() + "].", e);
        }
        
        return bean;
    }

    /**
     * Create the Javabean runtime class.
     * <p/>
     * Also performs some checks on the bean.
     * @param beanClass The beanClass name.
     * @return The bean runtime class instance.
     */
    private Class createBeanRuntime(String beanClass) {
        Class clazz;

        try {
            clazz = ClassUtil.forName(beanClass, getClass());
        } catch (ClassNotFoundException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " not in classpath.");
        }
        
        // check for a default constructor.
        try {
            clazz.getConstructor(null);
        } catch (NoSuchMethodException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " doesn't have a default constructor.");
        }
        
        return clazz;
    }

    /**
     * Create the bean setter method instance for this visitor.
     * @param bean The bean instance on which the setter method is to be
     * @return The bean setter method.
     */
    private synchronized Method createBeanSetterMethod(Object bean) {
        if(beanSetterMethod ==  null) {
            try {
                beanSetterMethod = bean.getClass().getMethod(setterName, new Class[] {String.class});
            } catch (NoSuchMethodException e) {
                throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "] not found.  Bean: [" + beanId + ":" + bean.getClass().getName() + "].", e);
            }
        }

        return beanSetterMethod;
    }
}
