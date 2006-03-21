/*
	Milyn - Copyright (C) 2003

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.milyn.delivery.ContentDeliveryConfig;

/**
 * Content Delivery Resource Definition.
 * <p/>
 * Represents an instance of the &lt;cdres&gt; element within a .cdrl file (Content Delivery Resource List file).
 * The .cdrl DTD can be seen at <a href="http://www.milyn.org/dtd/cdres-list-1.0.dtd">
 * http://www.milyn.org/dtd/cdres-list-1.0.dtd</a>
 * <p/> 
 * An instance of this class defines the following attributes of a Content Delivery Resource
 * from a .cdrl file:
 * <ul>
 * 		<li><b id="uatarget">uatarget</b>: A list of 1 or more browser/useragent target(s) to which this 
 * 			resource is to be applied.  Each entry ("uatarget expression") in this list is seperated
 * 			by a comma.  Uatarget expressions are represented by the {@link org.milyn.cdr.UATargetExpression}
 * 			class.  
 * 			<br/> 
 * 			Can be one of:
 * 			<ol>
 * 				<li>A browser "Common Name" as defined in the device recognition configuration.</li>
 * 				<li>A browser profile as defined in the device profiling configuration.</li>
 * 				<li>Astrix ("*") indicating a match for all browsers.</li>
 * 			</ol>
 * 			See <a href="#res-targeting">Resource Targeting</a>.
 * 			<p/>
 * 			<b>AND</b> and <b>NOT</b> expressions are supported on the uatarget attribute.
 * 			NOT expressions are specified in the "not:&lt;<i>profile-name</i>&gt;"
 * 			format. AND expressions are supported simply - by seperating the device/profile
 * 			names using "AND".  An example of the use of these expressions
 * 			in one uatarget attribute value could be <i>uatarget="html4 AND not:xforms"</i> -
 * 			target the resource at browsers/devices that have the "html4" profile but don't
 * 			have the "xforms" profile.
 * 			<p/>
 * 		</li>
 * 		<li><b id="selector">selector</b>: Selector string.  Used by Smooks to "lookup" a .cdrl resource.
 * 			<br/> 
 * 			Example values currently being used are:
 * 			<ol>
 * 				<li><u>Markup element names (e.g. table, tr, pre etc)</u>.  
 * 				</li>
 * 				<li><u>The requesting browser's markup definition i.e. DTD</u>.  Currently Smooks only support
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
 * 		<li><b>path</b>: The path to the resource within the classpath or one of the loaded .cdrar files.
 * 			<p/>
 * 		</li>
 * 		<li><b id="namespace">namespace</b>: The XML namespace of the target for this resource.  This is used
 * 			to target {@link org.milyn.delivery.ContentDeliveryUnit}s at XML elements from a
 * 			specific XML namespace e.g. "http://www.w3.org/2002/xforms".  If not defined, the resource
 * 			is targeted at all namespces. 
 * 		</li>
 * </ul>
 * The following is a sample showing the basic structure of the .cdrl file.
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE cdres-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://www.milyn.org/dtd/cdres-cdres-list-1.0.dtd> 
 * &lt;cdres-list&gt;
 * 	&lt;cdres uatarget="value" selector="value" path="value"/&gt;
 * &lt;/cdres-list&gt;</pre>
 * <p/>
 * All of the &lt;cdres&gt; attributes can be defaulted on the enclosing &lt;cdres-list&gt; element.
 * Just prefix the attribute name with "default-".  Example:
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE cdres-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://www.milyn.org/dtd/cdres-liscdres-list-1.0.dtd>
 * &lt;cdres-list default-uatarget="value" default-selector="value" default-namespace="http://www.w3.org/2002/xforms"&gt;
 * 	&lt;cdres path="value"/&gt;
 * &lt;/cdres-list&gt;</pre>
 * 
 * <h3 id="res-targeting">Resource Targeting</h3>
 * Content Delivery Resources ({@link org.milyn.delivery.trans.TransUnit} etc) are targeted
 * using a combination of the <a href="#uatarget">uatarget</a>, <a href="#selector">selector</a> 
 * and <a href="#namespace">namespace</a> attributes (see above).
 * <p/>
 * Smooks does this at runtime by building (and caching) a table of resources per requesting device/browser type.
 * When Smooks receives a request it uses the device recognition and profiling information provided by
 * Tinak to iterate over the .cdrl configurations and select the definitions that apply to that browser type.
 * It evaluates this based on the <a href="#uatarget">uatarget</a> attribute value.  Once the table 
 * is built it is cached so it doesn't need to be rebuilt for future requests from this browser type. 
 * <p/>
 * Once the resource table is built Smooks can "lookup" resources based on the <a href="#selector">selector</a> attribute value.
 * As you'll probably notice, the types of configurations that the .cdrl file permits can/will result in  
 * multiple resources being mapped to a browser under the same "selector" value i.e. if you request the resource
 * by selector "x", there may be 1+ matches.  Because of this Smooks sorts these matches based on what we call
 * the definitions "specificity" (a term stollen from the CSS spec :-).  
 * See {@link org.milyn.cdr.CDRDefSortComparator}.
 * 
 * <h3>&lt;param&gt; Elements</h3>
 * As can be seen from the <a href="http://www.milyn.org/dtd/cdres-list-1.0.dtd">DTD</a>, the &lt;cdres&gt; element can 
 * also define zero or more &lt;param&gt; elements. These elements allow runtime parameters to be passed to content delivery units.
 * This element defines a single mandatory attribute called "<b>name</b>".  The parameter value is inclosed in the
 * param element e.g.
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE cdres-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://www.milyn.org/dtd/cdres-list-1.0.cdres-list-1.0.dtd;cdres-list default-uatarget="value" default-selector="value" &gt;
 * 	&lt;cdres path="value"&gt;
 * 		&lt;param name="paramname"&gt;paramval&lt;/param&gt;
 * 	&lt;/cdres&gt;
 * &lt;/cdres-list&gt;</pre>
 * <p/>
 * Complex parameter values can be defined and decoded via configured 
 * {@link org.milyn.cdr.ParameterDecoder}s and the 
 * {@link #getParameter(String)}.{@link CDRDef.Parameter#getValue(ContentDeliveryConfig) getValue(ContentDeliveryConfig)} 
 * method (see {@link org.milyn.cdr.TokenizedStringParameterDecoder} as an example).
 *  
 * <h3 id="cdrs">Content Delivery Resources</h3>
 * &lt;cdres&gt;s can also be used to define <a href="../delivery/doc-files/unit-config.html#cdrs">Content 
 * Delivery Resources</a> other than {@link org.milyn.delivery.ContentDeliveryUnit}s
 * ({@link org.milyn.delivery.assemble.AssemblyUnit}s, 
 * {@link org.milyn.delivery.trans.TransUnit}s and
 * {@link org.milyn.delivery.serialize.SerializationUnit}s).  In this case the type attribute is
 * not required. Amoung other things, this "feature" is used by Smooks to target specific DTD and DOCTYPE header files at
 * specific browsers e.g.
 * <pre>
 * &lt;?xml version='1.0'?&gt;
 * &lt;!DOCTYPE cdres-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://www.milyn.org/dtd/cdres-list-1.0.dtdcdres-list-1.0.dtdres-list&gt;
 * 	&lt;!--	
 * 		Note: 
 * 		1. 	"wml11" is a browser profile.
 * 	--&gt;
 * 	&lt;cdres uatarget="wml11" selector="doctype" path="www.wapforum.org/DTD/wml_1_1.doctype" /&gt;
 * 	&lt;cdres uatarget="wml11" selector="dtd" path="www.wapforum.org/DTD/wml_1_1.dtd"" /&gt;
 * &lt;/cdres-list&gt;</pre>
 * This feature could be used by 
 * {@link org.milyn.delivery.assemble.AssemblyUnit}s, 
 * {@link org.milyn.delivery.trans.TransUnit}s and
 * {@link org.milyn.delivery.serialize.SerializationUnit}s to load other resources.  See
 * {@link org.milyn.delivery.ContentDeliveryConfig#getObjects(String)} and 
 * {@link org.milyn.delivery.ContentDeliveryConfig#getCDRDefs(String)}.
 *   
 * @see CDRDefSortComparator 
 * @author tfennelly
 */
public class CDRDef {
	/**
	 * Document target on which the resource is to be applied.
	 */
	private String selector;
	/**
	 * List of device/profile names on which the Content Delivery Resource is to be applied 
	 * for instances of selector.
	 */
	private String[] uaTargets;
	/**
	 * UATarget expresssions built from the uatargets list.
	 */
	private UATargetExpression[] uaTargetExpressions;
	/**
	 * The path to the Content Delivery Resource within the cdrar.
	 */
	private String path;
	/**
	 * XML selector type definition prefix
	 */
	public static final String XML_DEF_PREFIX = "xmldef:".toLowerCase();
	/**
	 * Is this selector defininition an XML based definition.
	 */
	private boolean isXmlDef;
	/**
	 * CDRDef parameters - String name and String value.
	 */
	private HashMap parameters;
	private int parameterCount;
	/**
	 * The XML namespace of the tag to which this config 
	 * should only be applied.
	 */
	private String namespaceURI;
	
	/**
	 * Public constructor.
	 * @param selector The selector definition.
	 * @param uatargets The device/profile uaTargets - comma separated uaTargets.
	 * @param path The cdrar path of the Content Delivery Resource.
	 */
	public CDRDef(String selector, String uatargets, String path) {
		if(selector == null || selector.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'selector' arg in constructor call.");
		}
		if(uatargets == null || uatargets.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'uatargets' arg in constructor call.");
		}
		this.selector = selector.toLowerCase();
		isXmlDef = selector.startsWith(XML_DEF_PREFIX);
		this.path = path;
		
		// Parse the device/profile uaTargets.  Seperation tokens: ',' '|' and ';'
		StringTokenizer tokenizer = new StringTokenizer(uatargets.toLowerCase(), ",|;");
		if(tokenizer.countTokens() == 0) {
			throw new IllegalArgumentException("Empty device/profile uaTargets. [" + selector + "][" + path + "]");
		} else {
			this.uaTargets = new String[tokenizer.countTokens()];			
			uaTargetExpressions = new UATargetExpression[tokenizer.countTokens()];			
			for(int i = 0; tokenizer.hasMoreTokens(); i++) {
				String expression = tokenizer.nextToken();
				this.uaTargets[i] = expression;
				uaTargetExpressions[i] = new UATargetExpression(expression);
			}
		}
	}
	
	/**
	 * Public constructor.
	 * @param selector The selector definition.
	 * @param namespaceURI The XML namespace URI of the element to which this config
	 * applies.
	 * @param uatargets The device/profile uaTargets - comma separated uaTargets.
	 * @param path The cdrar path of the Content Delivery Resource.
	 */
	public CDRDef(String selector, String namespaceURI, String uatargets, String path) {
		this(selector, uatargets, path);
		if(namespaceURI != null) {
			this.namespaceURI = namespaceURI.intern();
		}
	}

	/**
	 * Get the selector definition for this CDRDef.
	 * @return The selector definition.
	 */
	public String getSelector() {
		return selector;
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
	 * Get the device/profile uaTargets for this CDRDef.
	 * @return The device/profile uaTargets.
	 */
	public UATargetExpression[] getUaTargetExpressions() {
		return uaTargetExpressions;
	}
	
	/**
	 * Get the cdrar path of the Content Delivery Resource for this CDRDef.
	 * @return The cdrar path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the named CDRDef parameter value (default type - String).
	 * <p/>
	 * Overwrites previous value of the same name.
	 * @param name Parameter name.
	 * @param value Parameter value.
	 */
	public void setParameter(String name, String value) {
		setParmeter(name, new Parameter(null, value));
	}

	/**
	 * Set the named CDRDef parameter value (with type).
	 * <p/>
	 * Overwrites previous value of the same name.
	 * @param name Parameter name.
	 * @param type Parameter type.
	 * @param value Parameter value.
	 */
	public void setParameter(String name, String type, String value) {
		setParmeter(name, new Parameter(type, value));
	}

	private void setParmeter(String name, Parameter parameter) {
		if(parameters == null) {
			parameters = new LinkedHashMap();
		}
		Object exists = parameters.get(name);
		
		if(exists == null) {
			parameters.put(name, parameter);
		} else if(exists instanceof Parameter) {
			Vector paramList = new Vector();			
			paramList.add(exists);
			paramList.add(parameter);
			parameters.put(name, paramList);
		} else if(exists instanceof List) {
			((List)exists).add(parameter);
		}
		parameterCount++;
	}

	/**
	 * Get the named CDRDef {@link CDRDef.Parameter parameter}.
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
	 * Get the named CDRDef {@link CDRDef.Parameter parameter} List.
	 * @param name Name of parameter to get. 
	 * @return {@link CDRDef.Parameter} value {@link List}, or null if not set.
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
	 * Get the named CDRDef parameter.
	 * @param name Name of parameter to get. 
	 * @return Parameter value, or null if not set.
	 */
	public String getStringParameter(String name) {
		Parameter parameter;
		if(parameters == null) {
			return null;
		}
		parameter = (Parameter)parameters.get(name);
		return (parameter != null?parameter.value:null);
	}

	/**
	 * Get the named CDRDef parameter.
	 * @param name Name of parameter to get. 
	 * @param defaultVal The default value to be returned if there are no 
	 * parameters on the this CDRDef instance, or the parameter is not defined.
	 * @return Parameter value, or defaultVal if not defined.
	 */
	public String getStringParameter(String name, String defaultVal) {
		Parameter parameter;
		if(parameters == null) {
			return defaultVal;
		}
		parameter = getParameter(name);
		return (parameter != null?parameter.value:defaultVal);
	}

	/**
	 * Get the named CDRDef parameter as a boolean.
	 * @param name Name of parameter to get. 
	 * @param defaultVal The default value to be returned if there are no 
	 * parameters on the this CDRDef instance, or the parameter is not defined.
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
	 * Get the CDRDef parameter count.
	 * @return Number of parameters defined on this CDRDef.
	 */
	public int getParameterCount() {
		return parameterCount;
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
		return "[" + Arrays.asList(uaTargets) +"][" + selector + "][" + path + "]";
	}
	
	/**
	 * CDRDef Parameter.
	 * <p/>
	 * Wrapper for a param.  Handles decoding.
	 * @author tfennelly
	 */
	public static class Parameter {
		public static final String PARAM_TYPE_PREFIX = "param-type:";
		private String type;
		private String value;
		private Object objValue;

		private Parameter(String type, String value) {
			this.type = type;
			this.value = value;
		}

		/**
		 * Get the parameter value "undecoded".
		 * @return Parameter value.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Get the parameter value "decoded" into an Object.
		 * <p/>
		 * Uses the supplied <code>deliveryConfig</code> to get the {@link ParameterDecoder}
		 * implementation to be used to decode the parameter value.  Looks up the
		 * {@link ParameterDecoder} using the parameter type - selector="decoder-<i>&lt;type&gt;</i>".
		 * @param deliveryConfig Requesting device {@link ContentDeliveryConfig}.
		 * @return Decoded value.
		 * @throws ParameterDecodeException Unable to decode parameter value. 
		 */
		public Object getValue(ContentDeliveryConfig deliveryConfig) throws ParameterDecodeException {
			if(objValue == null) {
				synchronized (value) {
					if(objValue == null) {
						if(type == null) {
							objValue = value;
						} else {
							List decoders = deliveryConfig.getObjects(PARAM_TYPE_PREFIX + type);
							if(!decoders.isEmpty()) {
								try {
									ParameterDecoder paramDecoder = (ParameterDecoder)decoders.get(0);
									objValue = paramDecoder.decodeValue(value);
								} catch(ClassCastException cast) {
									throw new ParameterDecodeException("Configured ParameterDecoder '" + PARAM_TYPE_PREFIX + type + "' for device must be of type " + ParameterDecoder.class);
								}
							} else {
								throw new ParameterDecodeException("ParameterDecoder '" + PARAM_TYPE_PREFIX + type + "' not defined for requesting device.");
							}
						}
					}
				}
			}
			
			return objValue;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return value;
		}
	}

}