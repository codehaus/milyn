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

package org.milyn.cdr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.classpath.ClasspathUtils;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.Filter;
import org.milyn.expression.ExpressionEvaluator;
import org.milyn.expression.ExecutionContextExpressionEvaluator;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.io.StreamUtils;
import org.milyn.resource.URIResourceLocator;
import org.milyn.util.ClassUtil;
import org.milyn.xml.DomUtils;
import org.milyn.profile.Profile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Smooks Resource Targeting Configuration.
 * <p/>
 * A <b>Content Delivery Resource</b> is anything that can be used by Smooks in the process of analysing or
 * transforming a data stream.  They could be pieces
 * of Java logic ({@link org.milyn.delivery.dom.DOMElementVisitor},
 * {@link org.milyn.delivery.dom.serialize.SerializationUnit}), some text or script resource, or perhaps
 * simply a configuration parameter (see {@link org.milyn.cdr.ParameterAccessor}).
 * <p/>
 * <h2 id="restargeting">What is Resource Targeting?</h2>
 * Smooks works by "targeting" resources at message transformation/analysis processes.
 * It targets resources at <b>message profiles</b>, and then <b>message fragments</b>
 * (or other aspects of the transformation/analysis process) within that message profile.
 * This typically means targeting a piece of tranformation logic (XSLT, Java, Groovy etc) at a specific
 * type of message ("base profile"), and then at a specific fragment of that message.  The fragment may
 * include as much or as little of the document as required.  Smooks also allows you to target multilpe
 * resources at the same fragment (see {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}).
 * <p/>
 * Note you don't have to use message profiling.  You can simply create a set of configurations
 * that are only targeted at message fragments (no profiling info), supply them to a
 * {@link org.milyn.Smooks} instance and then use an {@link org.milyn.container.ExecutionContext}
 * instance that's not based on a profile (see {@link org.milyn.Smooks#createExecutionContext()}).  This is
 * definitely the easiest way to start using Smooks.
 * <p/>
 * <h2 id="restargeting">Resource Targeting Configurations</h2>
 * Smooks can be manually configured (through code), but the easiest way of working is through XML.  The follwoing
 * are a few sample configurations.  Explanations follow the samples.
 * <p/>
 * <b>A basic sample</b>.  Note that it is not using any profiling.  The <b>resource-config</b> element maps directly to an instance of this class.
 * <pre>
 * <i>&lt;?xml version='1.0'?&gt;
 * &lt;smooks-resource-list xmlns="http://www.milyn.org/xsd/smooks-1.0.xsd"&gt;
 *      <b>&lt;resource-config <a href="#selector">selector</a>="order order-header"&gt;
 *          &lt;resource type="xsl"&gt;<a target="new" href="http://milyn.codehaus.org/Smooks#Smooks-smookscartridges">/com/acme/transform/OrderHeaderTransformer.xsl</a>&lt;/resource&gt;
 *      &lt;/resource-config&gt;</b>
 *      <b>&lt;resource-config <a href="#selector">selector</a>="order-items order-item"&gt;
 *          &lt;resource&gt;{@link org.milyn.delivery.dom.DOMElementVisitor com.acme.transform.MyJavaOrderItemTransformer}&lt;/resource&gt;
 *      &lt;/resource-config&gt;</b>
 * &lt;/smooks-resource-list&gt;</i></pre>
 * <p/>
 * <b>A more complex sample</b>, using profiling.  So resource 1 is targeted at both "message-exchange-1" and "message-exchange-2",
 * whereas resource 2 is only targeted at "message-exchange-1" and resource 3 at "message-exchange-2" (see {@link org.milyn.Smooks#createExecutionContext(String)}).
 * <pre>
 * <i>&lt;?xml version='1.0'?&gt;
 * &lt;smooks-resource-list xmlns="http://www.milyn.org/xsd/smooks-1.0.xsd"&gt;
 *      <b>&lt;profiles&gt;
 *          &lt;profile base-profile="message-exchange-1" sub-profiles="message-producer-A, message-consumer-B" /&gt;
 *          &lt;profile base-profile="message-exchange-2" sub-profiles="message-producer-A, message-consumer-C" /&gt;
 *      &lt;/profiles&gt;</b>
 * (1)  &lt;resource-config selector="order order-header" <b>target-profile="message-producer-A"</b>&gt;
 *          &lt;resource&gt;com.acme.transform.AddIdentityInfo&lt;/resource&gt;
 *      &lt;/resource-config&gt;
 * (2)  &lt;resource-config selector="order-items order-item" <b>target-profile="message-consumer-B"</b>&gt;
 *          &lt;resource&gt;com.acme.transform.MyJavaOrderItemTransformer&lt;/resource&gt;
 *          &lt;param name="execution-param-X"&gt;param-value-forB&lt;/param&gt;
 *      &lt;/resource-config&gt;
 * (3)  &lt;resource-config selector="order-items order-item" <b>target-profile="message-consumer-C"</b>&gt;
 *          &lt;resource&gt;com.acme.transform.MyJavaOrderItemTransformer&lt;/resource&gt;
 *          &lt;param name="execution-param-X"&gt;param-value-forC&lt;/param&gt;
 *      &lt;/resource-config&gt;
 * &lt;/smooks-resource-list&gt;</i></pre>
 * <p/>
 * <h3 id="attribdefs">Attribute Definitions</h3>
 * <ul>
 * <li><b id="useragent">target-profile</b>: A list of 1 or more {@link ProfileTargetingExpression profile targeting expressions}.
 * (supports wildcards "*").
 * </ol>
 * <p/>
 * </li>
 * <li><b id="selector">selector</b>: Selector string.  Used by Smooks to "lookup" a resource configuration.
 * This is typically the message fragment name, but as mentioned above, not all resources are
 * transformation/analysis resources targeted at a message fragment - this is why we didn't call this attribute
 * "target-fragment".
 * <br/>
 * Example selectors:
 * <ol>
 * <li><u>The target fragment name (e.g. for HTML - table, tr, pre etc)</u>.  This type of selector can
 * be contextual in a similar way to contextual selectors in CSS e.g. "td ol li" will target the
 * resource at all "li" elements nested inside an "ol" element, which is in turn nested inside
 * a "td" element.  See sample configurations above. Also supports wildcard based fragment selection ("*").
 * </li>
 * <li>"$document" is a special selector that targets a resource at the "document" fragment i.e. the whole document,
 * or document root node fragment.</li>
 * <li>Targeting a specific {@link org.milyn.xml.SmooksXMLReader} at a specific profile.</li>
 * </ol>
 * <p/>
 * </li>
 * <li><b id="namespace">selector-namespace</b>: The XML namespace of the selector target for this resource.  This is used
 * to target {@link org.milyn.delivery.ContentHandler}s at XML elements from a
 * specific XML namespace e.g. "http://www.w3.org/2002/xforms".  If not defined, the resource
 * is targeted at all namespces.
 * </li>
 * </ul>
 * <p/>
 * <h2 id="conditions">Resource Targeting Configurations</h2>
 * 
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @see SmooksResourceConfigurationSortComparator
 */
public class SmooksResourceConfiguration {

    /**
     * Logger.
     */
    private static Log logger = LogFactory.getLog(SmooksResourceConfiguration.class);
    /**
     * The resource type can be specified as a resource parameter.  This constant defines
     * that parameter name.
     *
     * @deprecated Resource type now specified on "type" attribute of &lt;resource&gt; element.
     *             Since <a href="http://milyn.codehaus.org/dtd/smooksres-list-2.0.dtd">Configuration DTD v2.0</a>.
     */
    public static final String PARAM_RESTYPE = "restype";
    /**
     * The resource data can be specified as a resource parameter.  This constant defines
     * that parameter name.
     *
     * @deprecated Resource now specified on &lt;resource&gt; element.
     *             Since <a href="http://milyn.codehaus.org/dtd/smooksres-list-2.0.dtd">Configuration DTD v2.0</a>.
     */
    public static final String PARAM_RESDATA = "resdata";
    /**
     * XML selector type definition prefix
     */
    public static final String XML_DEF_PREFIX = "xmldef:".toLowerCase();
    /**
     * URI resource locator.
     */
    private static URIResourceLocator uriResourceLocator = new URIResourceLocator();
    /**
     * A special selector for resource targeted at the document as a whole (the roor element).
     */
    public static final String DOCUMENT_FRAGMENT_SELECTOR = "$document";

    /**
     * Document target on which the resource is to be applied.
     */
    private String selector;
    /**
     * Element based selectors can be contextual ala CSS contextual selectors.
     * The are of the CSS contextual selector form i.e. "UL UL LI".  This String
     * array contains a parsed contextual selector.
     */
    private String[] contextualSelector;
    /**
     * Target profile.
     */
    private String targetProfile;
    /**
     * List of device/profile names on which the Content Delivery Resource is to be applied
     * for instances of selector.
     */
    private String[] profileTargetingExpressionStrings;
    /**
     * Targeting expresssions built from the target-profile list.
     */
    private ProfileTargetingExpression[] profileTargetingExpressions;
    /**
     * The resource.
     */
    private String resource;
    /**
     * Condition evaluator used in resource targeting.
     */
    private ExpressionEvaluator expressionEvaluator;
    /**
     * The type of the resource.  "class", "groovy", "xsl" etc....
     */
    private String resourceType;
    /**
     * Is this selector defininition an XML based definition.
     */
    private boolean isXmlDef;
    /**
     * SmooksResourceConfiguration parameters - String name and String value.
     */
    private HashMap<String, Object> parameters;
    private int parameterCount;
    /**
     * The XML namespace of the tag to which this config
     * should only be applied.
     */
    private String namespaceURI;
    /**
     * Flag indicating whether or not the resource is a default applied resource
     * e.g. {@link org.milyn.delivery.dom.serialize.DefaultSerializationUnit} or
     * {@link org.milyn.delivery.sax.DefaultSAXElementVisitor}.
     */
    private boolean defaultResource = false;

    /**
     * Public default constructor.
     *
     * @see #setSelector(String)
     * @see #setSelectorNamespaceURI(String)
     * @see #setTargetProfile(String)
     * @see #setResource(String)
     * @see #setResourceType(String)
     * @see #setParameter(String, String)
     */
    public SmooksResourceConfiguration() {
        setSelector("none");
        setTargetProfile(Profile.DEFAULT_PROFILE);
    }

    /**
     * Public constructor.
     *
     * @param selector The selector definition.
     *
     * @see #setSelectorNamespaceURI(String)
     * @see #setTargetProfile(String)
     * @see #setResource(String)
     * @see #setResourceType(String)
     * @see #setParameter(String, String)
     */
    public SmooksResourceConfiguration(String selector) {
        setSelector(selector);
        setTargetProfile(Profile.DEFAULT_PROFILE);
    }

    /**
     * Public constructor.
     *
     * @param selector The selector definition.
     * @param resource The resource.
     *
     * @see #setSelectorNamespaceURI(String)
     * @see #setTargetProfile(String)
     * @see #setResourceType(String)
     * @see #setParameter(String, String)
     */
    public SmooksResourceConfiguration(String selector, String resource) {
        this(selector, Profile.DEFAULT_PROFILE, resource);
    }

    /**
     * Public constructor.
     *
     * @param selector      The selector definition.
     * @param targetProfile Target Profile(s).  Comma separated list of
     *                      {@link ProfileTargetingExpression ProfileTargetingExpressions}.
     * @param resource      The resource.
     *
     * @see #setSelectorNamespaceURI(String)
     * @see #setResourceType(String)
     * @see #setParameter(String, String)
     */
    public SmooksResourceConfiguration(String selector, String targetProfile, String resource) {
        this(selector);

        setTargetProfile(targetProfile);
        setResource(resource);
    }

    /**
     * Perform a shallow clone of this configuration.
     * @return Configuration clone.
     */
    public Object clone() {
        SmooksResourceConfiguration clone = new SmooksResourceConfiguration();

        clone.selector = selector;
        clone.contextualSelector = contextualSelector;
        clone.targetProfile = targetProfile;
        clone.defaultResource = defaultResource;
        clone.profileTargetingExpressionStrings = profileTargetingExpressionStrings;
        clone.profileTargetingExpressions = profileTargetingExpressions;
        clone.resource = resource;
        clone.resourceType = resourceType;
        clone.isXmlDef = isXmlDef;
        clone.parameters = parameters;
        clone.parameterCount = parameterCount;
        clone.namespaceURI = namespaceURI;
        clone.expressionEvaluator = expressionEvaluator;

        return clone;
    }

    /**
     * Public constructor.
     *
     * @param selector      The selector definition.
     * @param selectorNamespaceURI  The selector namespace URI.
     * @param targetProfile Target Profile(s).  Comma separated list of
     *                      {@link ProfileTargetingExpression ProfileTargetingExpressions}.
     * @param resource      The resource.
     *
     * @see #setResourceType(String)
     * @see #setParameter(String, String)
     */
    public SmooksResourceConfiguration(String selector, String selectorNamespaceURI, String targetProfile, String resource) {
        this(selector, targetProfile, resource);
        setSelectorNamespaceURI(selectorNamespaceURI);
    }

    /**
     * Set the config selector.
     *
     * @param selector The selector definition.
     */
    public void setSelector(String selector) {
        if (selector == null || selector.trim().equals("")) {
            throw new IllegalArgumentException("null or empty 'selector' arg in constructor call.");
        }
        this.selector = selector.toLowerCase().intern();
        isXmlDef = selector.startsWith(XML_DEF_PREFIX);

        // Parse the selector in case it's a contextual selector of the CSS
        // form e.g. "TD UL LI"
        contextualSelector = this.selector.split(" +");
    }

    /**
     * Set the namespace URI to which the selector is associated.
     * 
     * @param namespaceURI Selector namespace.
     */
    public void setSelectorNamespaceURI(String namespaceURI) {
        if (namespaceURI != null) {
            if (namespaceURI.equals("*")) {
                this.namespaceURI = null;
            } else {
                this.namespaceURI = namespaceURI.intern();
            }
        }
    }

    /**
     * Set the configs "resource".
     *
     * @param resource The resource.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Get the target profile string as set in the configuration.
     * @return The target profile.
     */
    public String getTargetProfile() {
        return targetProfile;
    }

    /**
     * Set the configs "target profile".
     *
     * @param targetProfile Target Profile(s).  Comma separated list of
     *                      {@link ProfileTargetingExpression ProfileTargetingExpressions}.
     */
    public void setTargetProfile(String targetProfile) {
        if (targetProfile == null || targetProfile.trim().equals("")) {
            // Default the target profile to everything if not specified.
            targetProfile = Profile.DEFAULT_PROFILE;
        }
        this.targetProfile = targetProfile;
        parseTargetingExpressions(targetProfile);
    }

    /**
     * Explicitly set the resource type.
     * <p/>
     * E.g. "class", "xsl", "groovy" etc.
     *
     * @param resourceType The resource type.
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Get the selector definition for this SmooksResourceConfiguration.
     *
     * @return The selector definition.
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Get the contextual selector definition for this SmooksResourceConfiguration.
     * <p/>
     * See details about the "selector" attribute in the
     * <a href="#attribdefs">Attribute Definitions</a> section.
     *
     * @return The contxtual selector definition.
     */
    public String[] getContextualSelector() {
        return contextualSelector;
    }

    /**
     * Get the name of the target element where the {@link #getSelector() selector}
     * is targeting the resource at an XML element.
     * <p/>
     * Accomodates the fact that element based selectors can be contextual. This method
     * is not relevant where the selector is not targeting an XML element.
     * <p/>
     * See details about the "selector" attribute in the
     * <a href="#attribdefs">Attribute Definitions</a> section.
     *
     * @return The target XML element name.
     */
    public String getTargetElement() {
        if (contextualSelector != null) {
            return contextualSelector[contextualSelector.length - 1];
        } else {
            return null;
        }
    }

    /**
     * The the selector namespace URI.
     *
     * @return The XML namespace URI of the element to which this configuration
     *         applies, or null if not namespaced.
     */
    public String getSelectorNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Get the profile targeting expressions for this SmooksResourceConfiguration.
     *
     * @return The profile targeting expressions.
     */
    public ProfileTargetingExpression[] getProfileTargetingExpressions() {
        return profileTargetingExpressions;
    }

    /**
     * Get the resource for this SmooksResourceConfiguration.
     *
     * @return The cdrar path.
     */
    public String getResource() {
        return resource;
    }

    /**
     * Set the condition evaluator to be used in targeting of this resource.
     * @param expressionEvaluator The {@link org.milyn.expression.ExpressionEvaluator}, or null if no condition is to be used.
     */
    public void setConditionEvaluator(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    /**
     * Get the condition evaluator used in targeting of this resource.
     * @return The {@link org.milyn.expression.ExpressionEvaluator}, or null if no condition is specified.
     */
    public ExpressionEvaluator getConditionEvaluator() {
        return expressionEvaluator;
    }

    /**
     * Is this resource config a default applied resource.
     * <p/>
     * Some resources (e.g. {@link org.milyn.delivery.dom.serialize.DefaultSerializationUnit} or
     * {@link org.milyn.delivery.sax.DefaultSAXElementVisitor}) are applied by default when no other
     * resources are targeted at an element.
     * 
     * @return True if this is a default applied resource, otherwise false.
     */
    public boolean isDefaultResource() {
        return defaultResource;
    }

    /**
     * Set this resource config as a default applied resource.
     * <p/>
     * Some resources (e.g. {@link org.milyn.delivery.dom.serialize.DefaultSerializationUnit} or
     * {@link org.milyn.delivery.sax.DefaultSAXElementVisitor}) are applied by default when no other
     * resources are targeted at an element.
     *
     * @param defaultResource True if this is a default applied resource, otherwise false.
     */
    public void setDefaultResource(boolean defaultResource) {
        this.defaultResource = defaultResource;
    }

    /**
     * Get the resource "type" for this resource.
     * <p/>
     * Determines the type through the following checks (in order):
     * <ol>
     * <li>Is it a Java resource. See {@link #isJavaResource()}.  If it is, return "class".</li>
     * <li>Is the "restype" resource paramater specified.  If it is, return it's value. Ala DTD v1.0</li>
     * <li>Is the resource type explicitly set on this configuration.  If it is, return it's
     * value. Ala the "type" attribute on the resource element on DTD v2.0</li>
     * <li>Return the resource path file extension e.g. "xsl".</li>
     * </ol>
     *
     * @return
     */
    public String getResourceType() {
        String restype;

        if (isJavaResource()) {
            return "class";
        }

        restype = getStringParameter(PARAM_RESTYPE);
        if (restype != null && !restype.trim().equals("")) {
            // Ala DTD v1.0, where we weren't able to specify the type in any other way.
            if (getParameter(PARAM_RESDATA) == null) {
                logger.warn("Resource configuration defined with '" + PARAM_RESTYPE + "' parameter but no '" + PARAM_RESDATA + "' parameter.");
            }
        } else if (resourceType != null) {
            // Ala DTD v2.0, where the type is set through the "type" attribute on the <resource> element.
            restype = resourceType;
        } else {
            restype = getExtension(getResource());
        }

        return restype;
    }

    /**
     * Parse the targeting expressions for this configuration.
     *
     * @param targetProfiles The <b>target-profile</b> expression from the resource configuration.
     */
    private void parseTargetingExpressions(String targetProfiles) {
        // Parse the profiles.  Seperation tokens: ',' '|' and ';'
        StringTokenizer tokenizer = new StringTokenizer(targetProfiles.toLowerCase(), ",|;");
        if (tokenizer.countTokens() == 0) {
            throw new IllegalArgumentException("Empty 'target-profile'. [" + selector + "][" + resource + "]");
        } else {
            this.profileTargetingExpressionStrings = new String[tokenizer.countTokens()];
            profileTargetingExpressions = new ProfileTargetingExpression[tokenizer.countTokens()];
            for (int i = 0; tokenizer.hasMoreTokens(); i++) {
                String expression = tokenizer.nextToken();
                this.profileTargetingExpressionStrings[i] = expression;
                profileTargetingExpressions[i] = new ProfileTargetingExpression(expression);
            }
        }
    }

    /**
     * Get the file extension from the resource path.
     *
     * @param path Resource path.
     * @return File extension, or null if the resource path has no file extension.
     */
    private String getExtension(String path) {
        if (path != null) {
            File resFile = new File(path);
            String resName = resFile.getName();

            if (resName != null && !resName.trim().equals("")) {
                int extensionIndex = resName.lastIndexOf('.');
                if (extensionIndex != -1 && (extensionIndex + 1 < resName.length())) {
                    return resName.substring(extensionIndex + 1);
                }
            }
        }

        return null;
    }

    /**
     * Set the named SmooksResourceConfiguration parameter value (default type - String).
     * <p/>
     * Overwrites previous value of the same name.
     *
     * @param name  Parameter name.
     * @param value Parameter value.
     * @return The parameter instance.
     */
    public Parameter setParameter(String name, String value) {
        Parameter param = new Parameter(name, value);
        setParameter(param);
        return param;
    }

    /**
     * Set the named SmooksResourceConfiguration parameter value (with type).
     * <p/>
     * Overwrites previous value of the same name.
     *
     * @param name  Parameter name.
     * @param type  Parameter type.
     * @param value Parameter value.
     * @return The parameter instance.
     */
    public Parameter setParameter(String name, String type, String value) {
        Parameter param = new Parameter(name, value, type);
        setParameter(param);
        return param;
    }

    public void setParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new LinkedHashMap<String, Object>();
        }
        Object exists = parameters.get(parameter.getName());

        if (exists == null) {
            parameters.put(parameter.getName(), parameter);
        } else if (exists instanceof Parameter) {
            Vector<Parameter> paramList = new Vector<Parameter>();
            paramList.add((Parameter) exists);
            paramList.add(parameter);
            parameters.put(parameter.getName(), paramList);
        } else if (exists instanceof List) {
            ((List) exists).add(parameter);
        }
        parameterCount++;
    }

    /**
     * Get the named SmooksResourceConfiguration {@link Parameter parameter}.
     * <p/>
     * If there is more than one of the named parameters defined, the first
     * defined value is returned.
     *
     * @param name Name of parameter to get.
     * @return Parameter value, or null if not set.
     */
    public Parameter getParameter(String name) {
        if (parameters == null) {
            return null;
        }
        Object parameter = parameters.get(name);

        if (parameter instanceof List) {
            return (Parameter) ((List) parameter).get(0);
        } else if (parameter instanceof Parameter) {
            return (Parameter) parameter;
        }

        return null;
    }

    /**
     * Get all {@link Parameter parameter} values set on this configuration.
     *
     * @return {@link Parameter} value {@link List}, or null if not set.
     */
    public List getParameters() {
        if (parameters == null) {
            return null;
        }

        List list = new ArrayList();
        list.addAll(parameters.values());

        return list;
    }

    /**
     * Get the named SmooksResourceConfiguration {@link Parameter parameter} List.
     *
     * @param name Name of parameter to get.
     * @return {@link Parameter} value {@link List}, or null if not set.
     */
    public List getParameters(String name) {
        if (parameters == null) {
            return null;
        }
        Object parameter = parameters.get(name);

        if (parameter instanceof List) {
            return (List) parameter;
        } else if (parameter instanceof Parameter) {
            Vector paramList = new Vector();
            paramList.add(parameter);
            parameters.put(name, paramList);
            return paramList;
        }

        return null;
    }

    /**
     * Get the named SmooksResourceConfiguration parameter.
     *
     * @param name Name of parameter to get.
     * @return Parameter value, or null if not set.
     */
    public String getStringParameter(String name) {
        Parameter parameter = getParameter(name);

        return (parameter != null ? parameter.value : null);
    }

    /**
     * Get the named SmooksResourceConfiguration parameter.
     *
     * @param name       Name of parameter to get.
     * @param defaultVal The default value to be returned if there are no
     *                   parameters on the this SmooksResourceConfiguration instance, or the parameter is not defined.
     * @return Parameter value, or defaultVal if not defined.
     */
    public String getStringParameter(String name, String defaultVal) {
        Parameter parameter = getParameter(name);

        return (parameter != null ? parameter.value : defaultVal);
    }

    /**
     * Get the named SmooksResourceConfiguration parameter as a boolean.
     *
     * @param name       Name of parameter to get.
     * @param defaultVal The default value to be returned if there are no
     *                   parameters on the this SmooksResourceConfiguration instance, or the parameter is not defined.
     * @return true if the parameter is set to true, defaultVal if not defined, otherwise false.
     */
    public boolean getBoolParameter(String name, boolean defaultVal) {
        String paramVal;

        if (parameters == null) {
            return defaultVal;
        }

        paramVal = getStringParameter(name);
        if (paramVal == null) {
            return defaultVal;
        }
        paramVal = paramVal.trim();
        if (paramVal.equals("true")) {
            return true;
        } else if (paramVal.equals("false")) {
            return false;
        } else {
            return defaultVal;
        }
    }

    /**
     * Get the SmooksResourceConfiguration parameter count.
     *
     * @return Number of parameters defined on this SmooksResourceConfiguration.
     */
    public int getParameterCount() {
        return parameterCount;
    }

    /**
     * Remove the named parameter.
     *
     * @param name The name of the parameter to be removed.
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * Is this selector defininition an XML based definition.
     * <p/>
     * I.e. is the selector attribute value prefixed with "xmldef:".
     *
     * @return True if this selector defininition is an XML based definition, otherwise false.
     */
    public boolean isXmlDef() {
        return isXmlDef;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    public String toString() {
        return "Target Profile: [" + Arrays.asList(profileTargetingExpressionStrings) + "], Selector: [" + selector + "], Selector Namespace URI: [" + namespaceURI + "], Resource: [" + resource + "], Num Params: [" + getParameterCount() + "]";
    }

    /**
     * Get the resource as a byte array.
     * <p/>
     * If the resource data is not inlined in the configuration (in a "resdata" param), it will be
     * resolved using the {@link URIResourceLocator} i.e. the path will be enterpretted as a {@link java.net.URI}.
     * If the resource doesn't resolve to a stream producing URI, the resource string will be converted to
     * bytes and returned.
     *
     * @return The resource as a byte array, or null if resource path
     *         is null or the resource doesn't exist.
     */
    public byte[] getBytes() {

        // This method supports 2 forms of resource config ala DTD 1.0 and DTD 2.0.
        // * 1.0 defined the resource on a "path" attribute as well as via a "resdata" resource
        //   parameter.
        // * 2.0 defines the resource in a resource element, so it can be used to specify a path
        //   or inlined resourcec data ala the "resdata" parameter in the 1.0 DTD.

        String paramBasedData = getStringParameter(PARAM_RESDATA);

        // If the resource data is specified as a parameter, return this.
        if (paramBasedData != null) {
            // Ala DTD v1.0, where we don't have the <resource> element.
            return paramBasedData.getBytes();
        }
        if (resource != null) {
            InputStream resStream = null;
            try {
                resStream = uriResourceLocator.getResource(resource);
            } catch(Exception e) {
                return getInlineResourceBytes();
            }

            try {
                byte[] resourceBytes = null;

                if (resStream == null) {
                    throw new IOException("Resource [" + resource + "] not found.");
                }

                try {
                    resourceBytes = StreamUtils.readStream(resStream);
                } finally {
                    resStream.close();
                }

                return resourceBytes;
            } catch (IOException e) {
                return getInlineResourceBytes();
            }
        }

        return null;
    }

    private byte[] getInlineResourceBytes() {
        try {
            // Ala DTD v2.0, where the <resource> element can carry the inlined resource data.
            return resource.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return resource.getBytes();
        }
    }

    /**
     * Returns the resource as a Java Class instance.
     *
     * @return The Java Class instance refered to be this resource configuration, or null
     *         if the resource doesn't refer to a Java Class.
     */
    public Class toJavaResource() {
        String className;

        if (resource == null) {
            return null;
        }

        className = ClasspathUtils.toClassName(resource);
        try {
            return ClassUtil.forName(className, getClass());
        } catch (ClassNotFoundException e) {
            if (resource.equals(className)) {
                logger.debug("Resource path [" + resource + "] looks as though it may be a Java resource reference.  If so, this class is not available on the classpath.");
            }

            return null;
        }
    }

    /**
     * Does this resource configuration refer to a Java Class resource.
     *
     * @return True if this resource configuration refers to a Java Class
     *         resource, otherwise false.
     */
    public boolean isJavaResource() {
        return (toJavaResource() != null);
    }

    /**
     * Is this resource a Java {@link org.milyn.delivery.ContentHandler} resource.
     *
     * @return True if this resource refers to an instance of the
     *         {@link org.milyn.delivery.ContentHandler} class, otherwise false.
     */
    public boolean isJavaContentHandler() {
        Class runtimeClass = toJavaResource();

        return (runtimeClass != null && ContentHandler.class.isAssignableFrom(runtimeClass));
    }

    /**
     * Is this resource configuration targets at the same namespace as the
     * specified elemnt.
     *
     * @param namespace The element to check against.
     * @return True if this resource config is targeted at the element namespace,
     *         or if the resource is not targeted at any namespace (i.e. not specified),
     *         otherwise false.
     */
    public boolean isTargetedAtNamespace(String namespace) {
        if (namespaceURI != null && !namespaceURI.equals(namespace)) {
            return false;
        }

        return true;
    }

    /**
     * Is the resource selector contextual.
     * <p/>
     * See details about the "selector" attribute in the
     * <a href="#attribdefs">Attribute Definitions</a> section.
     *
     * @return True if the selector is contextual, otherwise false.
     */
    public boolean isSelectorContextual() {
        return (contextualSelector.length > 1);
    }

    /**
     * Is this resource configuration targeted at the specified DOM element
     * in context.
     * <p/>
     * See details about the "selector" attribute in the
     * <a href="#attribdefs">Attribute Definitions</a> section.
     * <p/>
     * Note this doesn't perform any namespace checking.
     *
     * @param element The element to check against.
     * @return True if this resource configuration is targeted at the specified
     *         element in context, otherwise false.
     */
    public boolean isTargetedAtElementContext(Element element) {
        Node currentNode = element;

        // Check the element name(s).
        for (int i = contextualSelector.length - 1; i >= 0; i--) {
            if (currentNode == null || currentNode.getNodeType() != Node.ELEMENT_NODE) {
                return false;
            }

            Element currentElement = (Element) currentNode;
            String elementName = DomUtils.getName(currentElement);

            if (contextualSelector[i].equals("*")) {
                // match
            } else if (!contextualSelector[i].equalsIgnoreCase(elementName)) {
                return false;
            }

            // Go the next parent node...
            if (i > 0) {
                currentNode = currentNode.getParentNode();
            }
        }

        return true;
    }

    /**
     * Is this resource configuration targeted at the specified SAX element
     * in context.
     * <p/>
     * See details about the "selector" attribute in the
     * <a href="#attribdefs">Attribute Definitions</a> section.
     * <p/>
     * Note this doesn't perform any namespace checking.
     *
     * @param element The element to check against.
     * @return True if this resource configuration is targeted at the specified
     *         element in context, otherwise false.
     */
    public boolean isTargetedAtElementContext(SAXElement element) {
        SAXElement currentElement = element;

        // Check the element name(s).
        for (int i = contextualSelector.length - 1; i >= 0; i--) {
            if (currentElement == null) {
                return false;
            }

            String elementName = currentElement.getName().getLocalPart();

            if (contextualSelector[i].equals("*")) {
                // match
            } else if (!contextualSelector[i].equalsIgnoreCase(elementName)) {
                return false;
            }

            // Go the next parent node...
            if (i > 0) {
                currentElement = currentElement.getParent();
            }
        }

        return true;
    }

    /**
     * Is this configuration targeted at the supplied DOM element.
     * <p/>
     * Checks that the element is in the correct namespace and is a contextual
     * match for the configuration.
     *
     * @param element The element to be checked.
     * @return True if this configuration is targeted at the supplied element, otherwise false.
     */
    public boolean isTargetedAtElement(Element element) {
        if(!assertConditionTrue()) {
            return false;
        }

        if (!isTargetedAtNamespace(element.getNamespaceURI())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not applying resource [" + this + "] to element [" + DomUtils.getXPath(element) + "].  Element not in namespace [" + getSelectorNamespaceURI() + "].");
            }
            return false;
        } else if (isSelectorContextual() && !isTargetedAtElementContext(element)) {
            // Note: If the selector is not contextual, there's no need to perform the
            // isTargetedAtElementContext check because we already know the unit is targeted at the
            // element by name - because we looked it up by name in the 1st place (at least that's the assumption).
            if (logger.isDebugEnabled()) {
                logger.debug("Not applying resource [" + this + "] to element [" + DomUtils.getXPath(element) + "].  This resource is only targeted at '" + DomUtils.getName(element) + "' when in the following context '" + getSelector() + "'.");
            }
            return false;
        }

        return true;
    }

    /**
     * Is this configuration targeted at the supplied SAX element.
     * <p/>
     * Checks that the element is in the correct namespace and is a contextual
     * match for the configuration.
     *
     * @param element The element to be checked.
     * @return True if this configuration is targeted at the supplied element, otherwise false.
     */
    public boolean isTargetedAtElement(SAXElement element) {
        if(!assertConditionTrue()) {
            return false;
        }

        if (!isTargetedAtNamespace(element.getName().getNamespaceURI())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not applying resource [" + this + "] to element [" + element.getName() + "].  Element not in namespace [" + getSelectorNamespaceURI() + "].");
            }
            return false;
        } else if (isSelectorContextual() && !isTargetedAtElementContext(element)) {
            // Note: If the selector is not contextual, there's no need to perform the
            // isTargetedAtElementContext check because we already know the unit is targeted at the
            // element by name - because we looked it up by name in the 1st place (at least that's the assumption).
            if (logger.isDebugEnabled()) {
                logger.debug("Not applying resource [" + this + "] to element [" + element.getName() + "].  This resource is only targeted at '" + element.getName().getLocalPart() + "' when in the following context '" + getSelector() + "'.");
            }
            return false;
        }

        return true;
    }

    private boolean assertConditionTrue() {
        if(expressionEvaluator == null) {
            return true;
        }
        
        if(expressionEvaluator instanceof ExecutionContextExpressionEvaluator) {
            ExecutionContextExpressionEvaluator evaluator = (ExecutionContextExpressionEvaluator) expressionEvaluator;
            ExecutionContext execContext = Filter.getCurrentExecutionContext();

            return evaluator.eval(execContext);
        }

        throw new UnsupportedOperationException("Unsupported ExpressionEvaluator type '" + expressionEvaluator.getClass().getName() + "'.  Currently only support '" + ExecutionContextExpressionEvaluator.class.getName() + "' implementations.");
    }
}
