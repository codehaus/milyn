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
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.dom.VisitPhase;
import org.milyn.delivery.ExpandableContentHandler;
import org.milyn.util.ClassUtil;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Javabean Populator.
 * <h2>Sample Configuration</h2>
 * Populate an <b><code>Order</code></b> bean with <b><code>Header</code></b> and <b><code>OrderItem</code></b>
 * beans (OrderItems added to a list).
 * 
 * <h4>Input XML</h4>
 * <pre>
 * &lt;order&gt;
 *     &lt;header&gt;
 *         &lt;date&gt;Wed Nov 15 13:45:28 EST 2006&lt;/date&gt;
 *         &lt;customer number="123123"&gt;Joe&lt;/customer&gt;
 *     &lt;/header&gt;
 *     &lt;order-items&gt;
 *         &lt;order-item&gt;
 *             &lt;product&gt;111&lt;/product&gt;
 *             &lt;quantity&gt;2&lt;/quantity&gt;
 *             &lt;price&gt;8.90&lt;/price&gt;
 *         &lt;/order-item&gt;
 *         &lt;order-item&gt;
 *             &lt;product&gt;222&lt;/product&gt;
 *             &lt;quantity&gt;7&lt;/quantity&gt;
 *             &lt;price&gt;5.20&lt;/price&gt;
 *         &lt;/order-item&gt;
 *     &lt;/order-items&gt;
 * &lt;/order&gt;
 * </pre>
 * <h4 id="targetbeans">Target Java Beans</h4>
 * (Not including getters and setters):
 * <pre>
 * public class Order {
 *     private Header header;
 *     private List&lt;OrderItem&gt; orderItems;
 * }
 * public class Header {
 *     private Date date;
 *     private Long customerNumber;
 *     private String customerName;
 * }
 * public class OrderItem {
 *     private long productId;
 *     private Integer quantity;
 *     private double price;
 * }
 * </pre>
 * <h4>Smooks Configuration</h4>
 * The following configuration, when applied to the input XML, will create (and populate) the object graph
 * defined by the <a href="#targetbeans">Target Java Beans</a> using the data in the input XML.
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;smooks-resource-list xmlns="http://www.milyn.org/xsd/smooks-1.0.xsd"&gt;
 *
 *     &lt;resource-config selector="order"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanId"&gt;<b><u>order</u></b>&lt;/param&gt;
 *         &lt;param name="beanClass"&gt;<b>org.milyn.javabean.Order</b>&lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 *     &lt;resource-config selector="header"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanClass"&gt;<b>org.milyn.javabean.Header</b>&lt;/param&gt;
 *         &lt;param name="setOn"&gt;<b><u>order</u></b>&lt;/param&gt; &lt;-- Set bean on Order --&gt;
 *         &lt;param name="bindings"&gt;
 *             &lt;-- Header bindings... --&gt;
 *             &lt;binding property="date" type="OrderDateLong" selector="header date" /&gt; &lt;-- See OrderDateLong decoder definition below... --&gt;
 *             &lt;binding property="customerNumber" type="{@link org.milyn.javabean.decoders.LongDecoder Long}" selector="header customer @number" /&gt;
 *             &lt;binding property="customerName" selector="header customer" /&gt; &lt;-- Type defaults to String --&gt;
 *         &lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 *     &lt;resource-config selector="order-item"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanClass"&gt;<b>org.milyn.javabean.OrderItem</b>&lt;/param&gt;
 *         &lt;param name="addToList"&gt;<b>true</b>&lt;/param&gt;  &lt;-- Create a list of these bean instances --&gt;
 *         &lt;param name="setOn"&gt;<b><u>order</u></b>&lt;/param&gt; &lt;-- Set bean on Order --&gt;
 *         &lt;param name="bindings"&gt;
 *             &lt;-- OrderItem bindings... --&gt;
 *             &lt;binding property="productId" type="{@link org.milyn.javabean.decoders.LongDecoder Long}" selector="order-item product" /&gt;
 *             &lt;binding property="quantity" type="{@link org.milyn.javabean.decoders.IntegerDecoder Integer}" selector="order-item quantity" /&gt;
 *             &lt;binding property="price" type="{@link org.milyn.javabean.decoders.DoubleDecoder Double}" selector="order-item price" /&gt;
 *         &lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 *     &lt;-- Explicitly defining a decoder for the date field on the order header so as to define the format... --&gt;
 *     &lt;resource-config selector="decoder:OrderDateLong"&gt;
 *         &lt;resource&gt;{@link org.milyn.javabean.decoders.DateDecoder org.milyn.javabean.decoders.DateDecoder}&lt;/resource&gt;
 *         &lt;param name="format"&gt;EEE MMM dd HH:mm:ss z yyyy&lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 * &lt;/smooks-resource-list&gt;
 * </pre>
 *
 * <h4>Smooks Configuration</h4>
 * To trigger this visitor during the Assembly Phase, simply set the "VisitPhase" param to "ASSEMBLY".
 *
 * @author tfennelly
 */
public class BeanPopulator implements DOMElementVisitor, ExpandableContentHandler {

    private static Log logger = LogFactory.getLog(BeanPopulator.class);

    @ConfigParam(defaultVal ="", use=ConfigParam.Use.OPTIONAL)
    private String beanId;

    @ConfigParam(name="beanClass", defaultVal ="", use=ConfigParam.Use.OPTIONAL)
    private String beanClassName;

    @ConfigParam(defaultVal ="false", use=ConfigParam.Use.OPTIONAL)
    private boolean addToList;

    @ConfigParam(use=ConfigParam.Use.OPTIONAL)
    private String setOn; // The name of the bean on which to set this bean

    @ConfigParam(name="type", defaultVal ="String", use=ConfigParam.Use.OPTIONAL)
    private String typeAlias;

    @Config
    private SmooksResourceConfiguration config;

    private String attributeName;
    private Class beanClass;
    private String property;
    private Method beanSetterMethod;
    private String setOnMethod;
    private Method setOnBeanSetterMethod;
    private boolean isAttribute = true;
    private DataDecoder decoder;

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        beanId = beanId.trim();
        beanClassName = beanClassName.trim();

        // One of "beanId" or "beanClass" must be specified...
        if (beanId.equals("") && beanClassName.equals("")) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Both 'beanId' and 'beanClass' params are unspecified.");
        }

        // Bean class...
        if (!beanClassName.equals("")) {
            beanClass = createBeanRuntime(beanClassName);
        }

        // May need to default the "beanId"...
        if (beanId.equals("")) {
            beanId = toBeanId(beanClass);
            logger.debug("No 'beanId' specified for beanClass '" + beanClassName + "'.  Defaulting beanId to '" + beanId + "'.");
        }

        // What data is to be extracted from the visited element and set on the bean...
        attributeName = config.getStringParameter("attributeName");
        if (attributeName != null) {
            if ((attributeName = attributeName.trim()).equals("")) {
                throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'attributeName' param specified but blank.");
            }
            isAttribute = true;
        } else {
            // Not an attribute - this means we'll be setting the element text on the bean.
            isAttribute = false;
        }

        // Support legacy "setterName" attribute - map it to a "property"...
        property = config.getStringParameter("setterName");
        if (property != null) {
            property = toPropertyName(property);
        } else {
            property = config.getStringParameter("property");
        }
        if (property == null || (property = property.trim()).equals("")) {
            if (isAttribute) {
                property = attributeName;
            } else {
                // setterName and attributeName are not set - visitAfter method should only
                // create the bean!!
            }
        }

        // Get the details of the bean on which instances of beans created by this class are to be set on.
        if (setOn != null) {
            setOnMethod = config.getStringParameter("setOnMethod");
            if(setOnMethod == null) {
                String setOnProperty = config.getStringParameter("setOnProperty");
                if(setOnProperty == null) {
                    // If 'setOnProperty' is not defined, default to the name of this bean...
                    setOnProperty = toBeanId(beanClass);
                    if(addToList && !setOnProperty.endsWith("s")) {
                        setOnProperty += "s";
                    }
                }
                setOnMethod = toSetterName(setOnProperty);
            }
        }

        logger.debug("Bean Populator created for [" + beanId + ":" + beanClassName + "].  Add to list=" + addToList + ", attributeName=" + attributeName + ", property=" + property);
    }

    public List<SmooksResourceConfiguration> getExpansionConfigurations() throws SmooksConfigurationException {
        List<SmooksResourceConfiguration> resources = new ArrayList<SmooksResourceConfiguration>();
        Parameter bindingsParam = config.getParameter("bindings");

        if (bindingsParam != null) {
            Element bindingsParamElement = bindingsParam.getXml();

            if(bindingsParamElement != null) {
                NodeList bindings = bindingsParamElement.getElementsByTagName("binding");

                try {
                    for (int i = 0; bindings != null && i < bindings.getLength(); i++) {
                        resources.add(buildConfig((Element)bindings.item(i)));
                    }
                } catch (IOException e) {
                    throw new SmooksConfigurationException("Failed to read binding configuration for " + config, e);
                }
            } else {
                logger.error("Sorry, the Javabean populator bindings must be available as XML DOM.  Please configure using XML.");
            }
        }

        return resources;
    }

    private SmooksResourceConfiguration buildConfig(Element bindingConfig) throws IOException, SmooksConfigurationException {
        SmooksResourceConfiguration resourceConfig = null;
        String selector;
        String property;
        String type;

        // Make sure there's both 'selector' and 'property' attributes...
        selector = DomUtils.getAttributeValue(bindingConfig, "selector");
        if (selector == null) {
            throw new SmooksConfigurationException("Binding configuration must contain a 'selector' key: " + bindingConfig);
        }
        property = DomUtils.getAttributeValue(bindingConfig, "property");
        if (property == null) {
            throw new SmooksConfigurationException("Binding configuration must contain a 'property' key: " + bindingConfig);
        }

        // Extract the binding config properties from the selector and property values...
        String attributeNameProperty = getAttributeNameProperty(selector);
        String selectorProperty = getSelectorProperty(selector);

        // Construct the configuraton...
        resourceConfig = new SmooksResourceConfiguration(selectorProperty, getClass().getName());
        resourceConfig.setParameter(VisitPhase.class.getSimpleName(), VisitPhase.PROCESSING.name());
        resourceConfig.setParameter("beanId", beanId);
        resourceConfig.setParameter("property", property);
        if (attributeNameProperty != null && !attributeNameProperty.trim().equals("")) {
            // The value is comming out of a property on the target element.
            // If this attribute is not defined, the value will be taken from the element text...
            resourceConfig.setParameter("attributeName", attributeNameProperty);
        }
        type = DomUtils.getAttributeValue(bindingConfig, "type");
        resourceConfig.setParameter("type", (type != null?type:"String"));

        return resourceConfig;
    }

    private String getSelectorProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = selector.split(" ");

        for (String selectorToken : selectorTokens) {
            if (!selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken).append(" ");
            }
        }

        return selectorProp.toString();
    }

    private String getAttributeNameProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = selector.split(" ");

        for (String selectorToken : selectorTokens) {
            if (selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken.substring(1));
            }
        }

        return selectorProp.toString();
    }

    private String toSetterName(String property) {
        StringBuffer setterName = new StringBuffer();

        // Add the property string to the buffer...
        setterName.append(property);
        // Uppercase the first character...
        setterName.setCharAt(0, Character.toUpperCase(property.charAt(0)));
        // Prefix with "set"...
        setterName.insert(0, "set");

        return setterName.toString();
    }

    private String toPropertyName(String setterName) {
        StringBuffer propertyName = new StringBuffer(setterName);

        if (setterName.startsWith("set")) {
            propertyName.delete(0, 3);
            propertyName.setCharAt(0, Character.toLowerCase(propertyName.charAt(0)));
        }

        return propertyName.toString();
    }

    private String toBeanId(Class beanClass) {
        StringBuffer simpleClassName = new StringBuffer(beanClass.getSimpleName());
        simpleClassName.setCharAt(0, Character.toLowerCase(simpleClassName.charAt(0)));
        return simpleClassName.toString();
    }

    /**
     * Visit the element and extract data and set on the bean.
     *
     * @param element          Element being visited.
     * @param executionContext Execution context.
     */
    public void visitBefore(Element element, ExecutionContext executionContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("Visiting bean populator for beanId [" + beanId + "] on element " + DomUtils.getXPath(element));
        }

        Object bean = getBean(executionContext);
        Object dataObject = getDataObject(element, executionContext);

        if (property == null && attributeName == null) {
            // property and attributeName are not set - visitAfter method should only
            // create the bean!!
            return;
        }

        // If we need to create the bean setter method instance...
        if (beanSetterMethod == null) {
            beanSetterMethod = createBeanSetterMethod(bean, toSetterName(property), dataObject.getClass());
        }

        // Set the data on the bean...
        try {
            beanSetterMethod.invoke(bean, dataObject);
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + toSetterName(property) + "] on bean instance class type [" + bean.getClass() + "].", e);
        } catch (InvocationTargetException e) {
            throw new SmooksConfigurationException("Error invoking bean setter method [" + toSetterName(property) + "] on bean instance class type [" + bean.getClass() + "].", e);
        }
    }

    private Object getDataObject(Element element, ExecutionContext executionContext) throws DataDecodeException {
        String dataString;

        if (isAttribute) {
            dataString = element.getAttribute(attributeName);
        } else {
            dataString = DomUtils.getAllText(element, false);
        }

        if (decoder == null) {
            decoder = getDecoder(executionContext);
        }

        return decoder.decode(dataString);
    }

    private DataDecoder getDecoder(ExecutionContext executionContext) throws DataDecodeException {
        List decoders = executionContext.getDeliveryConfig().getObjects("decoder:" + typeAlias);

        if (decoders == null || decoders.isEmpty()) {
            decoder = DataDecoder.Factory.create(typeAlias);
        } else if (!(decoders.get(0) instanceof DataDecoder)) {
            throw new DataDecodeException("Configured decoder '" + typeAlias + ":" + decoders.get(0).getClass().getName() + "' is not an instance of " + DataDecoder.class.getName());
        } else {
            decoder = (DataDecoder) decoders.get(0);
        }

        return decoder;
    }

    /**
     * Visit the element and extract data and set on the bean.
     *
     * @param element          Element being visited.
     * @param executionContext Container request.
     */
    public void visitAfter(Element element, ExecutionContext executionContext) {
    }

    /**
     * Get the bean instance on which this populator instance is to set data.
     *
     * @param execContext The execution context.
     * @return The bean instance.
     */
    private Object getBean(ExecutionContext execContext) {
        Object bean = null;

        // If the configuration associated with this populator instance has a beanClass
        // configured, create a new instance and set it on the request...
        if (beanClass != null) {
            bean = createBeanInstance();

            if (setOn != null) {
                // Need to associate the 2 bean lifecycles...
                BeanAccessor.associateLifecycles(execContext, setOn, beanId, addToList);
            }

            BeanAccessor.addBean(beanId, bean, execContext, addToList);
            if (logger.isDebugEnabled()) {
                logger.debug("Bean [" + beanId + "] instance created.");
            }

            // If the new bean is to be set on a parent bean... set it...
            if (setOn != null) {
                // Set the bean instance on another bean. Supports creating an object graph
                Object setOnBean = BeanAccessor.getBean(setOn, execContext);
                if (setOnBean != null) {
                    try {
                        if (setOnBeanSetterMethod == null) {
                            if (!addToList) {
                                setOnBeanSetterMethod = createBeanSetterMethod(setOnBean, setOnMethod, bean.getClass());
                            } else {
                                setOnBeanSetterMethod = createBeanSetterMethod(setOnBean, setOnMethod, List.class);
                            }
                        }
                        if(logger.isDebugEnabled()) {
                            logger.debug("Setting bean '" + beanId + "' on parent bean '" + setOn + "'.");
                        }
                        if (!addToList) {
                            setOnBeanSetterMethod.invoke(setOnBean, bean);
                        } else {
                            Object beanList = BeanAccessor.getBean(beanId + "List", execContext);
                            setOnBeanSetterMethod.invoke(setOnBean, beanList);
                        }
                    } catch (IllegalAccessException e) {
                        throw new SmooksConfigurationException("Error invoking bean setter method [" + setOnMethod + "] on bean instance class type [" + setOnBean.getClass() + "].", e);
                    } catch (InvocationTargetException e) {
                        throw new SmooksConfigurationException("Error invoking bean setter method [" + setOnMethod + "] on bean instance class type [" + setOnBean.getClass() + "].", e);
                    }
                } else {
                    logger.error("Failed to set bean '" + beanId + "' on parent bean '" + setOn + "'.  Failed to find bean '" + setOn + "'.");
                }
            }
        } else {
            // Get the bean instance from the request.  If there is non, it's a bad config!!
            bean = BeanAccessor.getBean(beanId, execContext);
            if (logger.isDebugEnabled()) {
                logger.debug("Not creating a new bean instance for beanId [" + beanId + "].  Using [" + bean + "]");
            }
            if (bean == null) {
                throw new SmooksConfigurationException("Bean instance [" + beanId + "] not available and bean runtime class not set on configuration.");
            }
        }

        return bean;
    }

    /**
     * Create a new bean instance, generating relevant configuration exceptions.
     *
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
     *
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
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " doesn't have a public default constructor.");
        }

        return clazz;
    }

    /**
     * Create the bean setter method instance for this visitor.
     *
     * @param setterName The setter method name.
     * @return The bean setter method.
     */
    private synchronized Method createBeanSetterMethod(Object bean, String setterName, Class type) {
        if (beanSetterMethod == null) {
            beanSetterMethod = getMethod(type, bean, setterName);
            
            // Try it as a list...
            if (beanSetterMethod == null && List.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(type, bean, setterName + "s");
            }

            // Try it as a primitive...
            if(beanSetterMethod == null && Integer.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Integer.TYPE, bean, setterName);
            }
            if(beanSetterMethod == null && Long.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Long.TYPE, bean, setterName);
            }
            if(beanSetterMethod == null && Float.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Float.TYPE, bean, setterName);
            }
            if(beanSetterMethod == null && Double.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Double.TYPE, bean, setterName);
            }
            if(beanSetterMethod == null && Character.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Character.TYPE, bean, setterName);
            }
            if(beanSetterMethod == null && Byte.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Byte.TYPE, bean, setterName);
            }
            if(beanSetterMethod == null && Boolean.class.isAssignableFrom(type)) {
                beanSetterMethod = getMethod(Boolean.TYPE, bean, setterName);
            }

            if(beanSetterMethod == null) {
                throw new SmooksConfigurationException("Bean [" + beanId + "] configuration invalid.  Bean setter method [" + setterName + "(" + type.getName() + ")] not found on type [" + bean.getClass().getName() + "].  You may need to set a 'decoder' on the binding config.");
            }
        }

        return beanSetterMethod;
    }

    private Method getMethod(Class type, Object bean, String setterName) {
        Method[] methods = bean.getClass().getMethods();

        for(Method method : methods) {
            if(method.getName().equals(setterName)) {
                Class[] params = method.getParameterTypes();
                if(params != null && params.length == 1 && params[0].isAssignableFrom(type)) {
                    beanSetterMethod = method;
                    break;
                }
            }
        }

        return beanSetterMethod;
    }
}
