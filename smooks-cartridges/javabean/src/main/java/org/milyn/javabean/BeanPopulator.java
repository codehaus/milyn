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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.Parameter;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AnnotationConstants;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.ConfigurationExpander;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.VisitPhase;
import org.milyn.javabean.runtime.info.Classification;
import org.milyn.javabean.runtime.info.MapRuntimeInfo;
import org.milyn.javabean.runtime.info.ObjectRuntimeInfo;
import org.milyn.javabean.runtime.info.ObjectRuntimeInfoFactory;
import org.milyn.javabean.virtual.annotation.DirectSettableProperties;
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
 *     &lt;-- Create the Order bean instance when we encounter the "order" element
 *            and call it "order"... --&gt;
 *     &lt;resource-config selector="order"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanId"&gt;<b><u>order</u></b>&lt;/param&gt;
 *         &lt;param name="beanClass"&gt;<b>org.milyn.javabean.Order</b>&lt;/param&gt;
 *         &lt;param name="bindings"&gt;
 *             &lt;binding property="header" selector="${header}" /&gt; &lt;-- Wire the header bean to the header property. See header configuration below... --&gt;
 *             &lt;binding property="orderItems" selector="${orderItems}" /&gt; &lt;-- Wire the orderItems ArrayList to the orderItems property. See orderItems configuration below... --&gt;             
 *         &lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 *     &lt;-- Create a List for the OrderItem instances when we encounter the "order" element.
 *            Call it "orderItems" and set it on the "order" bean... --&gt;
 *     &lt;resource-config selector="order"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanId"&gt;<b><u>orderItems</u></b>&lt;/param&gt;
 *         &lt;param name="beanClass"&gt;<b>{@link ArrayList java.util.ArrayList}</b>&lt;/param&gt;
 *         &lt;param name="bindings"&gt;
 *             &lt;binding selector="${orderItem}" /&gt; &lt;-- Wire the orderItem to this ArrayList. See order-item configuration below... --&gt;
 *         &lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 *     &lt;-- Create the Header bean instance when we encounter the "header" element.
 *            Call it "header" --&gt;
 *     &lt;resource-config selector="header"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanId"&gt;<b><u>header</u></b>&lt;/param&gt;
 *         &lt;param name="beanClass"&gt;<b>org.milyn.javabean.Header</b>&lt;/param&gt;
 *         &lt;param name="bindings"&gt;
 *             &lt;-- Header bindings... --&gt;
 *             &lt;binding property="date" type="OrderDateLong" selector="header date" /&gt; &lt;-- See OrderDateLong decoder definition below... --&gt;
 *             &lt;binding property="customerNumber" type="{@link org.milyn.javabean.decoders.LongDecoder Long}" selector="header customer @number" /&gt;
 *             &lt;binding property="customerName" selector="header customer" /&gt; &lt;-- Type defaults to String --&gt;
 *         &lt;/param&gt;
 *     &lt;/resource-config&gt;
 *
 *     &lt;-- Create OrderItem instances when we encounter the "order-item" element.
 *            Set them on the "orderItems" bean (List)... --&gt;
 *     &lt;resource-config selector="order-item"&gt;
 *         &lt;resource&gt;org.milyn.javabean.BeanPopulator&lt;/resource&gt;
 *         &lt;param name="beanClass"&gt;<b>org.milyn.javabean.OrderItem</b>&lt;/param&gt;
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
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
@DirectSettableProperties( {"fewfe", "fwefew"})
public class BeanPopulator implements ConfigurationExpander {
	
    private static Log logger = LogFactory.getLog(BeanPopulator.class);

    @ConfigParam(defaultVal = AnnotationConstants.NULL_STRING)
    private String beanId;

    @ConfigParam(name="beanClass", defaultVal = AnnotationConstants.NULL_STRING)
    private String beanClassName;
    
    @Config
    private SmooksResourceConfiguration config;
    
    @AppContext
    private ApplicationContext appContext;

    private boolean optimized = false;
    
    private ObjectRuntimeInfo objectRuntimeInfo;
    
    /*******************************************************************************************************
     *  Common Methods.
     *******************************************************************************************************/

    /**
     * Set the resource configuration on the bean populator.
     * @throws SmooksConfigurationException Incorrectly configured resource.
     */
    @Initialize
    public void initialize() throws SmooksConfigurationException {
        if (beanClassName == null || beanClassName.trim().equals("")) {
            beanClassName = HashMap.class.getName();
            optimized = true;
        }

        // May need to default the "beanId"...
        if (beanId == null || beanId.trim().equals("")) {
            beanId = toBeanId(beanClassName);
            logger.debug("No 'beanId' specified for beanClass '" + beanClassName + "'.  Defaulting beanId to '" + beanId + "'.");
        }

        if (config.getStringParameter("attributeName") != null) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'attributeName' param config no longer supported.  Please use the <bindings> config style.");
        }

        if (config.getStringParameter("setterName") != null) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  'setterName' param config no longer supported.  Please use the <bindings> config style.");
        }
        
        objectRuntimeInfo = ObjectRuntimeInfoFactory.createBeanRuntime(beanClassName);
        if(objectRuntimeInfo.getClassification() == Classification.MAP_COLLECTION) {
        	((MapRuntimeInfo)objectRuntimeInfo).setVirtual(optimized);
        }
        
        ObjectRuntimeInfo.recordRuntimeInfo(beanId, objectRuntimeInfo, appContext);
        
        logger.debug("Bean Populator created for [" + beanId + ":" + beanClassName + "].");
    }

    public List<SmooksResourceConfiguration> expandConfigurations() throws SmooksConfigurationException {
        List<SmooksResourceConfiguration> resources = new ArrayList<SmooksResourceConfiguration>();

        buildInstanceCreatorConfig(resources);
        buildBindingConfigs(resources);
        
        return resources;
    }

    private void buildInstanceCreatorConfig(List<SmooksResourceConfiguration> resources) {
        SmooksResourceConfiguration resource = (SmooksResourceConfiguration) config.clone();

        // Reset the beanId and beanClass parameters
        resource.removeParameter("beanId");
        resource.setParameter("beanId", beanId);
        resource.removeParameter("beanClass");
        resource.setParameter("beanClass", beanClassName);

        // Remove the bindings param...
        resource.removeParameter("bindings");

        // Reset the resource...
        resource.setResource(BeanInstanceCreator.class.getName());

        resources.add(resource);
    }



    private void buildBindingConfigs(List<SmooksResourceConfiguration> resources) {
        Parameter bindingsParam = config.getParameter("bindings");

        if (bindingsParam != null) {
            Element bindingsParamElement = bindingsParam.getXml();

            if(bindingsParamElement != null) {
                NodeList bindings = bindingsParamElement.getElementsByTagName("binding");

                try {
                    for (int i = 0; bindings != null && i < bindings.getLength(); i++) {
                    	Element node = (Element)bindings.item(i);

                    	resources.add(buildInstancePopulatorConfig(node));

                    }
                } catch (IOException e) {
                    throw new SmooksConfigurationException("Failed to read binding configuration for " + config, e);
                }
            } else {
                logger.error("Sorry, the Javabean populator bindings must be available as XML DOM.  Please configure using XML.");
            }
        }
    }

    private SmooksResourceConfiguration buildInstancePopulatorConfig(Element bindingConfig) throws IOException, SmooksConfigurationException {
        SmooksResourceConfiguration resourceConfig;
        String selector;
        String selectorNamespace;
        String property;
        String setterMethod;
        String type;
        String defaultVal;
        String wireBeanId = null;

        // Make sure there's both 'selector' and 'property' attributes...
        selector = getSelectorAttr(bindingConfig);

        //Check if we get a bean binding, if so then we need to change the selector to selector of current config so that the
        //BeanInstanceCreator is called on that node instead of one off the child nodes.
        //The targetBeanId indicates the beanId that should be selected
        if(selector.startsWith("${") && selector.endsWith("}")) {
        	wireBeanId = selector.substring(2, selector.length() - 1);
        	selector = config.getSelector();
        }


        setterMethod = DomUtils.getAttributeValue(bindingConfig, "setterMethod");
        property = DomUtils.getAttributeValue(bindingConfig, "property");

        // Extract the binding config properties from the selector and property values...
        String attributeNameProperty = getAttributeNameProperty(selector);
        String selectorProperty = getSelectorProperty(selector);

        // Construct the configuraton...
        resourceConfig = new SmooksResourceConfiguration(selectorProperty, BeanInstancePopulator.class.getName());
        resourceConfig.setParameter(VisitPhase.class.getSimpleName(), config.getStringParameter(VisitPhase.class.getSimpleName(), VisitPhase.PROCESSING.toString()));
        resourceConfig.setParameter("beanId", beanId);

        if(wireBeanId != null) {
            resourceConfig.setParameter("wireBeanId", wireBeanId);
        }

        
        if(setterMethod != null) {
        	if(objectRuntimeInfo.getClassification() != Classification.NON_COLLECTION) {
        		throw new SmooksConfigurationException("The 'setterMethod' attribute isn't allowed with a Collection or Map bean class: " + bindingConfig);
        	}
        	
        	setterMethod = setterMethod.trim();
        	
            resourceConfig.setParameter("setterMethod", setterMethod);
        }
        
        if (setterMethod == null && property == null ) {
        	if(wireBeanId != null && (objectRuntimeInfo.getClassification() == Classification.NON_COLLECTION || objectRuntimeInfo.getClassification() == Classification.MAP_COLLECTION)) {
        		property = wireBeanId;
        	} else if(objectRuntimeInfo.getClassification() == Classification.NON_COLLECTION){
        		throw new SmooksConfigurationException("Binding configuration for beanId='" + beanId + "' must contain " +
                    "either a 'property' or 'setterMethod' attribute definition, unless the target bean is a Collection/Array." +
                    "  Bean is type '" + objectRuntimeInfo.getPopulateType().getName() + "'.");
        	}
        }
        
        if(property != null) {
        	property = property.trim();
        	
            resourceConfig.setParameter("property", property);
            
            if(objectRuntimeInfo.getClassification() == Classification.MAP_COLLECTION) {
            	if(property.charAt(0) != '@') {
            		((MapRuntimeInfo)objectRuntimeInfo).addKey(property);
            	}
            }
        }

        if (attributeNameProperty != null && !attributeNameProperty.trim().equals("")) {
            // The value is comming out of a property on the target element.
            // If this attribute is not defined, the value will be taken from the element text...
            resourceConfig.setParameter("valueAttributeName", attributeNameProperty);
        }

        type = DomUtils.getAttributeValue(bindingConfig, "type");
        defaultVal = DomUtils.getAttributeValue(bindingConfig, "default");
        if(wireBeanId == null ) {
        	if(type == null) {
        		type = "String";
        	}
        	
        	// Set the data type...
        	resourceConfig.setParameter("type", type);

            if(defaultVal != null) {
                resourceConfig.setParameter("default", defaultVal);
            }
        } else {
        	if(type != null) {
        		throw new SmooksConfigurationException("The 'type' attribute isn't allowed when binding a bean: " + bindingConfig);
        	}
        	if(defaultVal != null) {
        		throw new SmooksConfigurationException("The 'default' attribute isn't allowed when binding a bean: " + bindingConfig);
        	}
        }

        resourceConfig.setTargetProfile(config.getTargetProfile());

        // Set the selector namespace...
        selectorNamespace = DomUtils.getAttributeValue(bindingConfig, "selector-namespace");
        if(selectorNamespace == null) {
            selectorNamespace = config.getSelectorNamespaceURI();
        }
        resourceConfig.setSelectorNamespaceURI(selectorNamespace);

        return resourceConfig;
    }

    private String getSelectorProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = SmooksResourceConfiguration.parseSelector(selector);

        for (String selectorToken : selectorTokens) {
            if (!selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken).append(" ");
            }
        }

        return selectorProp.toString().trim();
    }

    private String getAttributeNameProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = SmooksResourceConfiguration.parseSelector(selector);

        for (String selectorToken : selectorTokens) {
            if (selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken.substring(1));
            }
        }

        return selectorProp.toString();
    }

    private String toBeanId(String beanClassName) {
        String[] beanClassNameTokens = beanClassName.split("\\.");
        StringBuffer simpleClassName = new StringBuffer(beanClassNameTokens[beanClassNameTokens.length - 1]);

        // Lowercase the first char...
        simpleClassName.setCharAt(0, Character.toLowerCase(simpleClassName.charAt(0)));

        return simpleClassName.toString();
    }

    private String getSelectorAttr(Element bindingConfig) {
    	String selector = DomUtils.getAttributeValue(bindingConfig, "selector");
        if (selector == null) {
            throw new SmooksConfigurationException("Binding configuration must contain a 'selector' key: " + bindingConfig);
        }
        return selector;
    }
}
