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
import org.milyn.delivery.VisitorConfigMap;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.javabean.ext.SelectorPropertyResolver;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Programmatic Bean Configurator.
 * <p/>
 * This class can be used to programmatically configure a Smooks instance for performing
 * a Java Bindings on a specific class.  To populate a graph, you simply create a graph of
 * Bean instances by binding Beans onto Beans.
 * <p/>
 * This class uses a Fluid API (all methods return the Bean instance), making it easy to
 * string configurations together to build up a graph of Bean configuration.
 * <p/>
 * <h3>Example</h3>
 * Taking the "classic" Order message as an example and binding it into a corresponding
 * Java Object model.
 * <h4>The Message</h4>
 * <pre>
 * &lt;order xmlns="http://x"&gt;
 *     &lt;header&gt;
 *         &lt;y:date xmlns:y="http://y"&gt;Wed Nov 15 13:45:28 EST 2006&lt;/y:date&gt;
 *         &lt;customer number="123123"&gt;Joe&lt;/customer&gt;
 *         &lt;privatePerson&gt;&lt;/privatePerson&gt;
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
 * <p/>
 * <h4>The Java Model</h4>
 * (Not including getters and setters):
 * <pre>
 * public class Order {
 *     private Header header;
 *     private List&lt;OrderItem&gt; orderItems;
 * }
 * public class Header {
 *     private Long customerNumber;
 *     private String customerName;
 * }
 * public class OrderItem {
 *     private long productId;
 *     private Integer quantity;
 *     private double price;
 * }
 * </pre>
 * <p/>
 * <h4>The Binding Configuration and Execution Code</h4>
 * The configuration code (Note: Smooks instance defined and instantiated globally):
 * <pre>
 * Bean orderBean = new Bean(Order.class, "order", "/order", smooks);
 * orderBean.bindTo("header",
 *     orderBean.newBean(Header.class, "/order")
 *         .bindTo("customerNumber", "header/customer/@number")
 *         .bindTo("customerName", "header/customer")
 *     ).bindTo("orderItems",
 *     orderBean.newBean(ArrayList.class, "/order")
 *         .bindTo(orderBean.newBean(OrderItem.class, "order-item")
 *             .bindTo("productId", "order-item/product")
 *             .bindTo("quantity", "order-item/quantity")
 *             .bindTo("price", "order-item/price"))
 *     );
 * </pre>
 * <p/>
 * And the execution code:
 * <pre>
 * JavaResult result = new JavaResult();
 * <p/>
 * smooks.filter(new StreamSource(orderMessageStream), result);
 * Order order = (Order) result.getBean("order");
 * </pre>
 *
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class Bean {

    private BeanInstanceCreator beanInstanceCreator;
    private Class beanClass;
    private String createOnElement;
    private VisitorConfigMap visitorConfigMap;
    private String targetNamespace;

    /**
     * Create a Bean binding configuration.
     *
     * @param beanClass       The bean runtime class.
     * @param beanId          The bean ID.
     * @param createOnElement The element selector used to create the bean instance.
     * @param smooks          The {@link Smooks} instance.
     */
    public Bean(Class beanClass, String beanId, String createOnElement, Smooks smooks) {
        this(beanClass, beanId, createOnElement, null, smooks.getVisitorConfigMap());
    }

    /**
     * Create a Bean binding configuration.
     *
     * @param beanClass        The bean runtime class.
     * @param beanId           The bean ID.
     * @param createOnElement  The element selector used to create the bean instance.
     * @param visitorConfigMap The {@link VisitorConfigMap} to which this bean is to be added.
     */
    public Bean(Class beanClass, String beanId, String createOnElement, VisitorConfigMap visitorConfigMap) {
        this(beanClass, beanId, createOnElement, null, visitorConfigMap);
    }

    /**
     * Create a Bean binding configuration.
     *
     * @param beanClass         The bean runtime class.
     * @param beanId            The bean ID.
     * @param createOnElement   The element selector used to create the bean instance.
     * @param createOnElementNS The namespace for the element selector used to create the bean instance.
     * @param smooks            The {@link Smooks} instance.
     */
    public Bean(Class beanClass, String beanId, String createOnElement, String createOnElementNS, Smooks smooks) {
        this(beanClass, beanId, createOnElement, createOnElementNS, smooks.getVisitorConfigMap());
    }

    /**
     * Create a Bean binding configuration.
     *
     * @param beanClass         The bean runtime class.
     * @param beanId            The bean ID.
     * @param createOnElement   The element selector used to create the bean instance.
     * @param createOnElementNS The namespace for the element selector used to create the bean instance.
     * @param visitorConfigMap  The {@link VisitorConfigMap} to which this bean is to be added.
     */
    public Bean(Class beanClass, String beanId, String createOnElement, String createOnElementNS, VisitorConfigMap visitorConfigMap) {
        AssertArgument.isNotNull(beanClass, "beanClass");
        AssertArgument.isNotNull(beanId, "beanId");
        AssertArgument.isNotNull(createOnElement, "createOnElement");
        AssertArgument.isNotNull(visitorConfigMap, "visitorConfigMap");

        this.beanClass = beanClass;
        this.createOnElement = createOnElement;
        this.visitorConfigMap = visitorConfigMap;
        this.targetNamespace = createOnElementNS;

        beanInstanceCreator = new BeanInstanceCreator(beanId, beanClass);

        // Add the visitor
        SmooksResourceConfiguration resourceConfig = visitorConfigMap.addVisitor(beanInstanceCreator, createOnElement, createOnElementNS, true);
        resourceConfig.setParameter("beanId", beanId);
        resourceConfig.setParameter("beanClass", beanClass.getName());
    }

    /**
     * Get the beanId of this Bean configuration.
     *
     * @return The beanId of this Bean configuration.
     */
    public String getBeanId() {
        return beanInstanceCreator.getBeanId();
    }

    /**
     * Create a Bean binding configuration.
     *
     * @param beanClass         The bean runtime class.
     * @param beanId            The bean ID.
     * @param createOnElement   The element selector used to create the bean instance.
     * @param createOnElementNS The namespace for the element selector used to create the bean instance.
     * @param smooks            The {@link Smooks} instance.
     */
    public static Bean newBean(Class beanClass, String beanId, String createOnElement, String createOnElementNS, Smooks smooks) {
        return new Bean(beanClass, beanId, createOnElement, createOnElementNS, smooks);
    }

    /**
     * Create a Bean binding configuration.
     *
     * @param beanClass         The bean runtime class.
     * @param beanId            The bean ID.
     * @param createOnElement   The element selector used to create the bean instance.
     * @param createOnElementNS The namespace for the element selector used to create the bean instance.
     * @param visitorConfigMap  The {@link VisitorConfigMap} to which this bean is to be added.
     */
    public static Bean newBean(Class beanClass, String beanId, String createOnElement, String createOnElementNS, VisitorConfigMap visitorConfigMap) {
        return new Bean(beanClass, beanId, createOnElement, createOnElementNS, visitorConfigMap);
    }

    /**
     * Create a Bean binding configuration.
     * <p/>
     * This method binds the configuration to the same {@link Smooks} instance
     * supplied in the constructor.  The beanId is generated.
     *
     * @param beanClass       The bean runtime class.
     * @param createOnElement The element selector used to create the bean instance.
     * @return <code>this</code> Bean configuration instance.
     */
    public Bean newBean(Class beanClass, String createOnElement) {
        String randomBeanId = UUID.randomUUID().toString();
        return new Bean(beanClass, randomBeanId, createOnElement, visitorConfigMap);
    }

    /**
     * Create a Bean binding configuration.
     * <p/>
     * This method binds the configuration to the same {@link Smooks} instance
     * supplied in the constructor.
     *
     * @param beanClass       The bean runtime class.
     * @param beanId          The beanId.
     * @param createOnElement The element selector used to create the bean instance.
     * @return <code>this</code> Bean configuration instance.
     */
    public Bean newBean(Class beanClass, String beanId, String createOnElement) {
        return new Bean(beanClass, beanId, createOnElement, visitorConfigMap);
    }

    /**
     * Create a binding configuration to bind the data, selected from the message by the
     * dataSelector, to the specified bindingMember (field/method).
     * <p/>
     * Discovers the {@link org.milyn.javabean.DataDecoder} through the specified
     * bindingMember.
     *
     * @param bindingMember The name of the binding member.  This is a bean property (field)
     *                      or method name.
     * @param dataSelector  The data selector for the data value to be bound.
     * @return The Bean configuration instance.
     */
    public Bean bindTo(String bindingMember, String dataSelector) {
        return bindTo(bindingMember, dataSelector, null);
    }

    /**
     * Create a binding configuration to bind the data, selected from the message by the
     * dataSelector, to the target Bean member specified by the bindingMember param.
     *
     * @param bindingMember The name of the binding member.  This is a bean property (field)
     *                      or method name.
     * @param dataSelector  The data selector for the data value to be bound.
     * @param dataDecoder   The {@link org.milyn.javabean.DataDecoder} to be used for decoding
     *                      the data value.
     * @return <code>this</code> Bean configuration instance.
     */
    public Bean bindTo(String bindingMember, String dataSelector, DataDecoder dataDecoder) {
        AssertArgument.isNotNull(bindingMember, "bindingMember");
        AssertArgument.isNotNull(dataSelector, "dataSelector");
        // dataDecoder can be null

        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();
        SmooksResourceConfiguration populatorConfig = new SmooksResourceConfiguration(dataSelector);

        SelectorPropertyResolver.resolveSelectorTokens(populatorConfig);

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setValueAttributeName(populatorConfig.getStringParameter(BeanInstancePopulator.VALUE_ATTRIBUTE_NAME));

        Method bindingMethod = getBindingMethod(bindingMember);
        if (bindingMethod != null) {
            if (dataDecoder == null) {
                Class dataType = bindingMethod.getParameterTypes()[0];                
                dataDecoder = DataDecoder.Factory.create(dataType);
            }

            if (bindingMethod.getName().equals(bindingMember)) {
                beanInstancePopulator.setSetterMethod(bindingMethod.getName());
            } else {
                beanInstancePopulator.setProperty(bindingMember);
            }
        } else {
            beanInstancePopulator.setProperty(bindingMember);
        }
        beanInstancePopulator.setDecoder(dataDecoder);

        visitorConfigMap.addVisitor(beanInstancePopulator, populatorConfig.getSelector(), targetNamespace, true);

        return this;
    }

    /**
     * Add a bean binding configuration for the specified bindingMember (field/method) to
     * this bean binding config.
     * <p/>
     * This method is used to build a binding configuration graph, which in turn configures
     * Smooks to build a Java Object Graph (ala &lt;jb:wiring&gt; configurations).
     *
     * @param bindingMember The name of the binding member.  This is a bean property (field)
     *                      or method name.  The bean runtime class should match the
     * @param bean          The Bean instance to be bound
     * @return <code>this</code> Bean configuration instance.
     */
    public Bean bindTo(String bindingMember, Bean bean) {
        AssertArgument.isNotNull(bindingMember, "bindingMember");
        AssertArgument.isNotNull(bean, "bean");

        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setWireBeanId(bean.getBeanId());
        Method bindingMethod = getBindingMethod(bindingMember);

        if (bindingMethod != null) {
            if (bindingMethod.getName().equals(bindingMember)) {
                beanInstancePopulator.setSetterMethod(bindingMethod.getName());
            } else {
                beanInstancePopulator.setProperty(bindingMember);
            }
        } else {
            beanInstancePopulator.setProperty(bindingMember);
        }

        visitorConfigMap.addVisitor(beanInstancePopulator, createOnElement, targetNamespace, true);

        return this;
    }

    /**
     * Add a bean binding configuration to this Collection/array bean binding config.
     * <p/>
     * This method checks that this bean's beanClass is a Collection/array, generating an
     * {@link IllegalArgumentException} if the check fails.
     *
     * @param bean The Bean instance to be bound
     * @return <code>this</code> Bean configuration instance.
     * @throws IllegalArgumentException <u><code>this</code></u> Bean's beanClass (not the supplied bean!) is
     *                                  not a Collection/array.  You cannot call this method on Bean configurations whose beanClass is not a
     *                                  Collection/array.  For non Collection/array types, you must use one of the bindTo meths that specify a
     *                                  'bindingMember'.
     */
    public Bean bindTo(Bean bean) throws IllegalArgumentException {
        AssertArgument.isNotNull(bean, "bean");
        assertBeanClassIsCollection();

        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setWireBeanId(bean.getBeanId());

        visitorConfigMap.addVisitor(beanInstancePopulator, createOnElement, targetNamespace, true);

        return this;
    }

    /**
     * Create a binding configuration to bind the data, selected from the message by the
     * dataSelector, to the target Collection/array Bean beanclass instance.
     *
     * @param dataSelector The data selector for the data value to be bound.
     * @return <code>this</code> Bean configuration instance.
     */
    public Bean bindTo(String dataSelector) {
        return bindTo(dataSelector, (DataDecoder) null);
    }


    /**
     * Create a binding configuration to bind the data, selected from the message by the
     * dataSelector, to the target Collection/array Bean beanclass instance.
     *
     * @param dataSelector The data selector for the data value to be bound.
     * @param dataDecoder  The {@link org.milyn.javabean.DataDecoder} to be used for decoding
     *                     the data value.
     * @return <code>this</code> Bean configuration instance.
     */
    public Bean bindTo(String dataSelector, DataDecoder dataDecoder) {
        AssertArgument.isNotNull(dataSelector, "dataSelector");
        // dataDecoder can be null
        assertBeanClassIsCollection();

        BeanInstancePopulator beanInstancePopulator = new BeanInstancePopulator();
        SmooksResourceConfiguration populatorConfig = new SmooksResourceConfiguration(dataSelector);

        SelectorPropertyResolver.resolveSelectorTokens(populatorConfig);

        // Configure the populator visitor...
        beanInstancePopulator.setBeanId(getBeanId());
        beanInstancePopulator.setValueAttributeName(populatorConfig.getStringParameter(BeanInstancePopulator.VALUE_ATTRIBUTE_NAME));
        beanInstancePopulator.setDecoder(dataDecoder);

        visitorConfigMap.addVisitor(beanInstancePopulator, populatorConfig.getSelector(), targetNamespace, true);

        return this;
    }

    /**
     * Get the bean binding class Member (field/method).
     *
     * @param bindingMember Binding member name.
     * @return The binding member, or null if not found.
     */
    private Method getBindingMethod(String bindingMember) {
        Method[] methods = beanClass.getMethods();

        // Check is the bindingMember an actual fully qualified method name...
        for (Method method : methods) {
            if (method.getName().equals(bindingMember) && method.getParameterTypes().length == 1) {
                return method;
            }
        }

        // Check is the bindingMember defined by a property name.  If so, there should be a
        // bean setter method for that property...
        String asPropertySetterMethod = BeanUtils.toSetterName(bindingMember);
        for (Method method : methods) {
            if (method.getName().equals(asPropertySetterMethod) && method.getParameterTypes().length == 1) {
                return method;
            }
        }

        // Can't resolve it...
        return null;
    }

    /**
     * Assert that the beanClass associated with this configuration is an array or Collection.
     */
    private void assertBeanClassIsCollection() {
        BeanRuntimeInfo beanRuntimeInfo = beanInstanceCreator.getBeanRuntimeInfo();

        if (beanRuntimeInfo.getClassification() != BeanRuntimeInfo.Classification.COLLECTION_COLLECTION && beanRuntimeInfo.getClassification() != BeanRuntimeInfo.Classification.ARRAY_COLLECTION) {
            throw new IllegalArgumentException("Invalid call to a Collection/array Bean.bindTo method.  Runtime class of bean being bound to is not a Collection/array.  Use one of the Bean.bindTo methods that specify a 'bindingMember' argument.");
        }
    }
}
