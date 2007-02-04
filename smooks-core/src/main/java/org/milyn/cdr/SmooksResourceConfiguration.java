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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.io.StreamUtils;
import org.milyn.resource.URIResourceLocator;
import org.milyn.util.ClassUtil;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Smooks Resource Definition.
 * <p/>
 * A <b>Content Deliver Resource</b> is anything that can be used by Smooks in the process of analysing or
 * manipulating/transforming a data stream e.g. a J2EE Servlet Response.  They could be pieces
 * of Java logic ({@link org.milyn.delivery.assemble.AssemblyUnit}, {@link org.milyn.delivery.process.ProcessingUnit}, 
 * {@link org.milyn.delivery.serialize.SerializationUnit}), some text or script resource, or perhaps
 * simply a configuration parameter (see {@link org.milyn.cdr.ParameterAccessor}).  One way Smooks allows 
 * definition of resource configurations is via <b>.cdrl</b> XML files.  An example of such a file is as 
 * follows (with an explanation below):
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE smooks-resource-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://milyn.codehaus.org/dtd/smooksres-list-1.0.dtd>
 * &lt;smooks-resource-list&gt;
 * 	&lt;!--	
 * 		Note: 
 * 		1. 	"wml11" is a useragent profile.
 * 	--&gt;
 * 	&lt;smooks-resource useragent="wml11" selector="dtd" path="www.wapforum.org/DTD/wml_1_1.dtd" /&gt;
 * 	&lt;smooks-resource useragent="wml11" selector="table" path="{@link org.milyn.delivery.process.ProcessingUnit com.acme.transform.TableWML11}" /&gt;
 * &lt;/smooks-resource-list&gt;</pre>
 * <p/>
 * The .cdrl DTD can be seen at <a href="http://milyn.codehaus.org/dtd/smooksres-list-1.0.dtd">
 * http://milyn.codehaus.org/dtd/smooksres-list-1.0.dtd</a>
 * 
 * <h3 id="attribdefs">Attribute Definitions</h3>
 * <ul>
 * 		<li><b id="useragent">useragent</b>: A list of 1 or more useragent targets to which this 
 * 			resource is to be applied.  Each entry ("useragent expression") in this list is seperated
 * 			by a comma.  Useragent expressions are represented by the {@link org.milyn.cdr.UseragentExpression}
 * 			class.  
 * 			<br/> 
 * 			Can be one of:
 * 			<ol>
 * 				<li>A useragent "Common Name" as defined in the device recognition configuration (see <a href="http://milyn.org/Tinak">Milyn Tinak</a>).</li>
 * 				<li>A useragent profile as defined in the device profiling configuration (see <a href="http://milyn.org/Tinak">Milyn Tinak</a>).</li>
 * 				<li>Astrix ("*") indicating a match for all useragents.  This is the default value if this
 *                  attribute is not specified.</li>
 * 			</ol>
 * 			See <a href="#res-targeting">Resource Targeting</a>.
 * 			<p/>
 * 			<b>AND</b> and <b>NOT</b> expressions are supported on the useragent attribute.
 * 			NOT expressions are specified in the "not:&lt;<i>profile-name</i>&gt;"
 * 			format. AND expressions are supported simply - by seperating the device/profile
 * 			names using "AND".  An example of the use of these expressions
 * 			in one useragent attribute value could be <i>useragent="html4 AND not:xforms"</i> -
 * 			target the resource at useragents/devices that have the "html4" profile but don't
 * 			have the "xforms" profile.
 * 			<p/>
 * 		</li>
 * 		<li><b id="selector">selector</b>: Selector string.  Used by Smooks to "lookup" a .cdrl resource.
 * 			<br/> 
 * 			Example values currently being used are:
 * 			<ol>
 * 				<li><u>Markup element names (e.g. table, tr, pre etc)</u>.  These selector types can be
 *              be contextual in a similar way to contextual selectors in CSS e.g. "td ol li" will target the
 *              resource (e.g. a {@link org.milyn.delivery.process.ProcessingUnit}) at all "li" elements nested
 *              inside an "ol" element, which is in turn nested inside a "td" element.
 * 				</li>
 * 				<li><u>The requesting useragent's markup definition i.e. DTD</u>.  Currently Smooks only support
 * 					"Element Content Spec" based selectors, identified by the "xmldef:elcspec:" prefix.  Supported
 * 					values are "xmldef:elcspec:<b>empty</b>", "xmldef:elcspec:<b>not-empty</b>", "xmldef:elcspec:<b>any</b>", 
 * 					"xmldef:elcspec:<b>not-any</b>", "xmldef:elcspec:<b>mixed</b>", "xmldef:elcspec:<b>not-mixed</b>", 
 * 					"xmldef:elcspec:<b>pcdata</b>", "xmldef:elcspec:<b>not-pcdata</b>".
 * 					We hope to be able expand this to support more DTD based selection criteria.  See {@link org.milyn.dtd.DTDStore}.
 * 				</li>
 * 				<li><u>Astrix ("*") indicating a match for all markup elements</u>.  Note this doesn't mean match anything.  It's only
 * 					relevant to, and used by, markup element based selection. I hope this makes sense!
 * 				</li>
 * 				<li><u>Arbitrary strings</u>.  Examples of where selector is currently used in this mode are how Smooks
 * 					<a href="../delivery/doc-files/doctype.cdrl">applies DOCTYPE headers</a> and 
 * 					<a href="../delivery/doc-files/dtds.cdrl">targets DTDs</a>.  Content Delivery Units
 * 				</li>
 * 			</ol>
 * 			The first 3 of these are used by Smooks to select {@link org.milyn.delivery.ContentDeliveryUnit}s.
 * 			<br/>
 * 			See <a href="#res-targeting">Resource Targeting</a>.
 * 			<p/>
 * 		</li>
 * 		<li><b>path</b>: The path to the resource file within the classpath.  The resource data can also be specified
 * 			on a resource parameter called "resdata".  XML based data (or data containing special XML characters)
 *  		should be enclosed in a CDATA section within the parameter.
 * 			<p/>
 * 		</li>
 * 		<li><b id="namespace">namespace</b>: The XML namespace of the target for this resource.  This is used
 * 			to target {@link org.milyn.delivery.ContentDeliveryUnit}s at XML elements from a
 * 			specific XML namespace e.g. "http://www.w3.org/2002/xforms".  If not defined, the resource
 * 			is targeted at all namespces. 
 * 		</li>
 * </ul>
 * All of the &lt;smooks-resource&gt; attributes can be defaulted on the enclosing &lt;smooks-resource-list&gt; element.
 * Just prefix the attribute name with "default-".  Example:
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE smooks-resource-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://milyn.codehaus.org/dtd/smooksres-list-1.0.dtd>
 * &lt;smooks-resource-list default-useragent="value" default-selector="value" default-namespace="http://www.w3.org/2002/xforms"&gt;
 * 	&lt;smooks-resource path="value"/&gt;
 * &lt;/smooks-resource-list&gt;</pre>
 * 
 * Also note that there is a resource "typing" mechanism in place.  See {@link #getType()}.
 * 
 * <h3 id="res-targeting">Resource Targeting</h3>
 * Content Delivery Resources ({@link org.milyn.delivery.process.ProcessingUnit} etc) are targeted
 * using a combination of the <a href="#useragent">useragent</a>, <a href="#selector">selector</a> 
 * and <a href="#namespace">namespace</a> attributes (see above).
 * <p/>
 * Smooks does this at runtime by building (and caching) a table of resources per useragent type (e.g. requesting useragent).
 * For example, when the <a href="http://milyn.codehaus.org/Tutorials">SmooksServletFilter</a> receives a request, it 
 * <ol>
 * 	<li>
 * 		Uses the device recognition and profiling information provided by
 * 		<a href="http://milyn.codehaus.org/Tinak">Milyn Tinak</a> to iterate over the .cdrl configurations 
 *      and select the definitions that apply to that useragent type.
 * 		It evaluates this based on the <a href="#useragent">useragent</a> attribute value.  Once the table 
 * 		is built it is cached so it doesn't need to be rebuilt for future requests from this useragent type. 
 * 	</li>
 * 	<li>
 * 		Smooks can then "lookup" resources based on the <a href="#selector">selector</a> attribute value.
 * 	</li>
 * </ol>
 * As you'll probably notice, the types of configurations that the .cdrl file permits can/will result in  
 * multiple resources being mapped to a useragent under the same "selector" value i.e. if you request the resource
 * by selector "x", there may be 1+ matches.  Because of this Smooks sorts these matches based on what we call
 * the definitions "specificity".  
 * See {@link org.milyn.cdr.SmooksResourceConfigurationSortComparator}.
 * 
 * <h3>&lt;param&gt; Elements</h3>
 * As can be seen from the <a href="http://milyn.codehaus.org/dtd/smooksres-list-1.0.dtd">DTD</a>, the &lt;smooks-resource&gt; element can 
 * also define zero or more &lt;param&gt; elements. These elements allow runtime parameters to be passed to content delivery units.
 * This element defines a single mandatory attribute called "<b>name</b>".  The parameter value is inclosed in the
 * param element e.g.
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE smooks-resource-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://milyn.codehaus.org/dtd/smooksres-list-1.0.dtd>
 * &lt;smooks-resource-list default-useragent="value" default-selector="value" &gt;
 * 	&lt;smooks-resource path="value"&gt;
 * 		&lt;param name="paramname"&gt;paramval&lt;/param&gt;
 * 	&lt;/smooks-resource&gt;
 * &lt;/smooks-resource-list&gt;</pre>
 * <p/>
 * Complex parameter values can be defined and decoded via configured 
 * {@link org.milyn.cdr.ParameterDecoder}s and the 
 * {@link #getParameter(String)}.{@link Parameter#getValue(ContentDeliveryConfig) getValue(ContentDeliveryConfig)} 
 * method (see {@link org.milyn.cdr.TokenizedStringParameterDecoder} as an example).
 *   
 * @see SmooksResourceConfigurationSortComparator 
 * @author tfennelly
 */
public class SmooksResourceConfiguration {

	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(SmooksResourceConfiguration.class);
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
	 * List of device/profile names on which the Content Delivery Resource is to be applied 
	 * for instances of selector.
	 */
	private String[] useragents;
	/**
	 * Useragent expresssions built from the useragents list.
	 */
	private UseragentExpression[] useragentExpressions;
	/**
	 * The path to the Content Delivery Resource within the cdrar.
	 */
	private String path;
	/**
	 * The resource type can be specified as a resource parameter.  This constant defines
	 * that parameter name.
	 */
	public static final String PARAM_RESTYPE = "restype";
	/**
	 * The resource data can be specified as a resource parameter.  This constant defines
	 * that parameter name.
	 */
	public static final String PARAM_RESDATA = "resdata";
	/**
	 * XML selector type definition prefix
	 */
	public static final String XML_DEF_PREFIX = "xmldef:".toLowerCase();
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
	 * URI resource locator.
	 */
	private static URIResourceLocator uriResourceLocator = new URIResourceLocator();

    
    /**
     * Public constructor.
     * @param selector The selector definition.
     * @param path The cdrar path of the Content Delivery Resource.
     */
    public SmooksResourceConfiguration(String selector, String path) {
        this(selector, "*", path);
    }
    
	/**
	 * Public constructor.
	 * @param selector The selector definition.
	 * @param useragents The device/profile useragents - comma separated useragents.
	 * @param path The cdrar path of the Content Delivery Resource.
	 */
	public SmooksResourceConfiguration(String selector, String useragents, String path) {
        if(selector == null || selector.trim().equals("")) {
            throw new IllegalArgumentException("null or empty 'selector' arg in constructor call.");
        }
		if(useragents == null || useragents.trim().equals("")) {
            // Default the useragent to everything if not specified.
            useragents = "*";
		}
        this.selector = selector.toLowerCase().intern();
        isXmlDef = selector.startsWith(XML_DEF_PREFIX);
        this.path = path;
		
        // Parse the selector in case it's a contextual selector of the CSS
        // form e.g. "TD UL LI"
        contextualSelector = this.selector.split(" +");
		parseUseragentExpressions(useragents);
	}
	
	/**
	 * Public constructor.
	 * @param selector The selector definition.
	 * @param namespaceURI The XML namespace URI of the element to which this config
	 * applies.
	 * @param useragents The device/profile useragents - comma separated useragents.
	 * @param path The cdrar path of the Content Delivery Resource.
	 */
	public SmooksResourceConfiguration(String selector, String namespaceURI, String useragents, String path) {
		this(selector, useragents, path);
		if(namespaceURI != null) {
            if(namespaceURI.equals("*")) {
                this.namespaceURI = null;
            } else {
                this.namespaceURI = namespaceURI.intern();
            }
		}
	}

	/**
     * Parse the useragent expressions for this configuration.
     * @param useragents The useragent expression from the resource configuration.
	 */
    private void parseUseragentExpressions(String useragents) {
        // Parse the device/profile useragents.  Seperation tokens: ',' '|' and ';'
        StringTokenizer tokenizer = new StringTokenizer(useragents.toLowerCase(), ",|;");
        if(tokenizer.countTokens() == 0) {
            throw new IllegalArgumentException("Empty device/profile useragents. [" + selector + "][" + path + "]");
        } else {
            this.useragents = new String[tokenizer.countTokens()];          
            useragentExpressions = new UseragentExpression[tokenizer.countTokens()];            
            for(int i = 0; tokenizer.hasMoreTokens(); i++) {
                String expression = tokenizer.nextToken();
                this.useragents[i] = expression;
                useragentExpressions[i] = new UseragentExpression(expression);
            }
        }
    }
    
	/**
	 * Get the selector definition for this SmooksResourceConfiguration.
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
     * @return The target XML element name.
     */
    public String getTargetElement() {
        if(contextualSelector != null) {
            return contextualSelector[contextualSelector.length - 1];
        } else {
            return null;
        }
    }
    
	/**
	 * The the XML namespace URI of the element to which this configuration
	 * applies.
	 * @return The XML namespace URI of the element to which this configuration
	 * applies, or null if not namespaced.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * Get the device/profile useragents for this SmooksResourceConfiguration.
	 * @return The device/profile useragents.
	 */
	public UseragentExpression[] getUseragentExpressions() {
		return useragentExpressions;
	}
	
	/**
	 * Get the cdrar path of the Content Delivery Resource for this SmooksResourceConfiguration.
	 * @return The cdrar path.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Get the resource "type" for this resource.
	 * <p/>
	 * Determines the type through the following checks (in order):
	 * <ol>
	 * 	<li>Is it a Java resource. See {@link #isJavaResource()}.  If it is, return "class".</li>
	 * 	<li>Is the "restype" resource paramater specified.  If it is, return it's value.</li>
	 * 	<li>Return the resource path file extension e.g. "xsl".</li>
	 * </ol>
	 * @return
	 */
	public String getType() {
        String restype;

        if(isJavaResource()) {
        	return "class";
        }
        
        restype = getStringParameter(PARAM_RESTYPE);
        if(restype != null &&  !restype.trim().equals("")) {
        	if(getParameter(PARAM_RESDATA) == null) {
        		logger.warn("Resource configuration defined with '" + PARAM_RESTYPE + "' parameter but no '" + PARAM_RESDATA + "' parameter.");
        	}
        } else {
        	restype = getExtension(getPath());
        }
        
        return restype;
	}

	/**
	 * Get the file extension from the resource path.
	 * @param path Resource path.
	 * @return File extension, or null if the resource path has no file extension.
	 */
	private String getExtension(String path) {
		if(path != null) {
			File resFile = new File(path);
			String resName = resFile.getName();
			
			if(resName != null && !resName.trim().equals("")) {
				int extensionIndex = resName.lastIndexOf('.');
				if(extensionIndex != -1 && (extensionIndex + 1 < resName.length())) {
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
	 * @param name Parameter name.
	 * @param value Parameter value.
	 */
	public void setParameter(String name, String value) {
		setParameter(new Parameter(name, value));
	}

	/**
	 * Set the named SmooksResourceConfiguration parameter value (with type).
	 * <p/>
	 * Overwrites previous value of the same name.
	 * @param name Parameter name.
	 * @param type Parameter type.
	 * @param value Parameter value.
	 */
	public void setParameter(String name, String type, String value) {
		setParameter(new Parameter(name, value, type));
	}

	public void setParameter(Parameter parameter) {
		if(parameters == null) {
			parameters = new LinkedHashMap<String, Object>();
		}
		Object exists = parameters.get(parameter.getName());
		
		if(exists == null) {
			parameters.put(parameter.getName(), parameter);
		} else if(exists instanceof Parameter) {
			Vector<Parameter> paramList = new Vector<Parameter>();			
			paramList.add((Parameter)exists);
			paramList.add(parameter);
			parameters.put(parameter.getName(), paramList);
		} else if(exists instanceof List) {
			((List)exists).add(parameter);
		}
		parameterCount++;
	}

	/**
	 * Get the named SmooksResourceConfiguration {@link Parameter parameter}.
	 * <p/>
	 * If there is more than one of the named parameters defined, the first
	 * defined value is returned.  
	 * @param name Name of parameter to get. 
	 * @return Parameter value, or null if not set.
	 */
	public Parameter getParameter(String name) {
		if(parameters == null) {
			return null;
		}
		Object parameter = parameters.get(name);
		
		if(parameter instanceof List) {
			return (Parameter)((List)parameter).get(0);
		} else if(parameter instanceof Parameter) {
			return (Parameter)parameter;
		}
		
		return null;
	}

	/**
	 * Get all {@link Parameter parameter} values set on this configuration.
	 * @return {@link Parameter} value {@link List}, or null if not set.
	 */
	public List getParameters() {
		if(parameters == null) {
			return null;
		}

		List list = new ArrayList();
		list.addAll(parameters.values());
		
		return list;
	}

	/**
	 * Get the named SmooksResourceConfiguration {@link Parameter parameter} List.
	 * @param name Name of parameter to get. 
	 * @return {@link Parameter} value {@link List}, or null if not set.
	 */
	public List getParameters(String name) {
		if(parameters == null) {
			return null;
		}
		Object parameter = parameters.get(name);
		
		if(parameter instanceof List) {
			return (List)parameter;
		} else if(parameter instanceof Parameter) {
			Vector paramList = new Vector();			
			paramList.add(parameter);
			parameters.put(name, paramList);
			return paramList;
		}
		
		return null;
	}

	/**
	 * Get the named SmooksResourceConfiguration parameter.
	 * @param name Name of parameter to get. 
	 * @return Parameter value, or null if not set.
	 */
	public String getStringParameter(String name) {
		Parameter parameter = getParameter(name);
        
		return (parameter != null?parameter.value:null);
	}

	/**
	 * Get the named SmooksResourceConfiguration parameter.
	 * @param name Name of parameter to get. 
	 * @param defaultVal The default value to be returned if there are no 
	 * parameters on the this SmooksResourceConfiguration instance, or the parameter is not defined.
	 * @return Parameter value, or defaultVal if not defined.
	 */
	public String getStringParameter(String name, String defaultVal) {
        Parameter parameter = getParameter(name);
        
		return (parameter != null?parameter.value:defaultVal);
	}

	/**
	 * Get the named SmooksResourceConfiguration parameter as a boolean.
	 * @param name Name of parameter to get. 
	 * @param defaultVal The default value to be returned if there are no 
	 * parameters on the this SmooksResourceConfiguration instance, or the parameter is not defined.
	 * @return true if the parameter is set to true, defaultVal if not defined, otherwise false.
	 */
	public boolean getBoolParameter(String name, boolean defaultVal) {
		String paramVal;

		if(parameters == null) {
			return defaultVal;
		}
		
		paramVal = getStringParameter(name);
		if(paramVal == null) {
			return defaultVal;
		}
		paramVal = paramVal.trim();
		if(paramVal.equals("true")) {
			return true;
		} else if(paramVal.equals("false")) {
			return false;
		} else {
			return defaultVal;
		}
	}

	/**
	 * Get the SmooksResourceConfiguration parameter count.
	 * @return Number of parameters defined on this SmooksResourceConfiguration.
	 */
	public int getParameterCount() {
		return parameterCount;
	}
    
    /**
     * Remove the named parameter.
     * @param name The name of the parameter to be removed.
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

	/**
	 * Is this selector defininition an XML based definition.
	 * <p/>
	 * I.e. is the selector attribute value prefixed with "xmldef:".
	 * @return True if this selector defininition is an XML based definition, otherwise false.
	 */
	public boolean isXmlDef() {
		return isXmlDef;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Useragent: [" + Arrays.asList(useragents) +"], Selector: [" + selector + "], Target Namespace URI: [" + namespaceURI + "], Resource Path: [" + path + "], Num Params: [" + getParameterCount() + "]";
	}
    
    /**
     * Get the resource as a byte array.
     * <p/>
     * If the resource data is not inlined in the configuration (in a "resdata" param), it will be 
     * resolved using the {@link URIResourceLocator} i.e. the path will be enterpretted as a {@link java.net.URI}.
     * 
     * @return The resource as a byte array, or null if resource path
     * is null or the resource doesn't exist.
     * @throws IOException Failed to read the resource bytes.
     */
    public byte[] getBytes() throws IOException {
        String paramBasedData = getStringParameter(PARAM_RESDATA);
        
        // If the resource data is specified as a parameter, return this.
        if(paramBasedData != null) {
        	return paramBasedData.getBytes();
        }
        
        // If the resource data is specified on the resource path attribute, return this.
        if(path != null) {
        	InputStream resStream = uriResourceLocator.getResource(path);
        	byte[] resourceBytes = null;
        	
            if(resStream == null) {
                throw new IOException("Resource [" + path + "] not found.");
            }
            
            try {
            	resourceBytes = StreamUtils.readStream(resStream);
            } finally {
            	resStream.close();
            }
        	
            return resourceBytes;
        }
        
        return null;
    }
    
    /**
     * Returns the resource as a Java Class instance.
     * @return The Java Class instance refered to be this resource configuration, or null
     * if the resource doesn't refer to a Java Class.
     */
    public Class toJavaResource() {
        String className;
        
        if(path == null) {
            return null;
        }
        
        className = ClasspathUtils.toClassName(path);
        try {
            return ClassUtil.forName(className, getClass());
        } catch (ClassNotFoundException e) {
            if(path.equals(className)) {
                logger.warn("Resource path [" + path + "] looks as though it may be a Java resource reference.  If so, this class is not available on the classpath.");
            }
            return null;
        }    	
    }

    /**
     * Does this resource configuration refer to a Java Class resource.
     * @return True if this resource configuration refers to a Java Class 
     * resource, otherwise false.
     */
    public boolean isJavaResource() {
    	return (toJavaResource() != null);
    }
    
    /**
     * Is this resource a Java {@link org.milyn.delivery.ContentDeliveryUnit} resource.
     * @return True if this resource refers to an instance of the
     * {@link org.milyn.delivery.ContentDeliveryUnit} class, otherwise false.
     */
    public boolean isJavaContentDeliveryUnit() {
        Class runtimeClass = toJavaResource();
        
        return (runtimeClass != null && ContentDeliveryUnit.class.isAssignableFrom(runtimeClass));
    }

    /**
     * Is this resource configuration targets at the same namespace as the
     * specified elemnt.
     * @param element The element to check against.
     * @return True if this resource config is targeted at the element namespace,
     * or if the resource is not targeted at any namespace (i.e. not specified), 
     * otherwise false.
     */
    public boolean isTargetedAtElementNamespace(Element element) {
        // Check the namespace (if specified) of the config against the 
        // supplied element namespace.
        if(namespaceURI != null && !namespaceURI.equals(element.getNamespaceURI())) {
            return false;
        }
        
        return true;
    }

    /**
     * Is the resource selector contextual.
     * <p/>
     * See details about the "selector" attribute in the 
     * <a href="#attribdefs">Attribute Definitions</a> section.
     * @return True if the selector is contextual, otherwise false.
     */
    public boolean isSelectorContextual() {
        return (contextualSelector.length > 1);
    }
    
    /**
     * Is this resource configuration targeted at the specified element
     * in context.
     * <p/>
     * See details about the "selector" attribute in the 
     * <a href="#attribdefs">Attribute Definitions</a> section.
     * <p/>
     * Note this doesn't perform any namespace checking.
     * @param element The element to check against.
     * @return True if this resource configuration is targeted at the specified
     * element in context, otherwise false.
     */
    public boolean isTargetedAtElementContext(Element element) {
        Node currentNode = element;
        
        // Check the element name(s).
        for(int i = contextualSelector.length - 1; i >= 0; i--) {
            if(currentNode.getNodeType() != Node.ELEMENT_NODE) {
                return false;
            }
            
            Element currentElement = (Element)currentNode;
            String elementName = DomUtils.getName(currentElement);
            
            if(contextualSelector[i].equals("*")) {
                // match
            } else if(!contextualSelector[i].equalsIgnoreCase(elementName)) {
                return false;
            }
            
            // Go the next parent node...
            if(i > 0) {
                currentNode = currentNode.getParentNode();
            }
        }
        
        return true;
    }
}
