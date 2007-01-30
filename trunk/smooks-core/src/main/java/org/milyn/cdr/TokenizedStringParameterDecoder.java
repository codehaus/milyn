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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import org.milyn.delivery.ContentDeliveryConfig;


/**
 * {@link ParameterDecoder} used to tokenize a parameter values into a {@link java.util.List}
 * or {@link java.util.HashSet}.
 * <p/>
 * Tokenizes the parameter value into a {@link java.util.List} (param-type="string-list")
 * or {@link java.util.HashSet} (param-type="string-hashset") using {@link java.util.StringTokenizer}.
 * <h3>.cdrl Configuration</h3>
 * The following configurations (installed by default, with defaults) show how this {@link ParameterDecoder} is configured into
 * Smooks such that it can be used by {@link org.milyn.delivery.ContentDeliveryUnit}s
 * for accessing tokenised param values.  See the <a href="#exampleusage">example usage</a>
 * below for an example on how this decoder can be used once configured.
 * <pre>
 * &lt;smooks-resource useragent="*" selector="param-type:string-collection-X" path="org.milyn.cdr.TokenizedStringParameterDecoder"&gt;
 * 
 * 	&lt;!-- (Optional) Tokenizer Delimiters. Default is "string-list". --&gt;
 * 	&lt;param name="<b>param-type</b>"&gt;<i>string-list/string-hashset</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Tokenizer Delimiters. Default is ",". --&gt;
 * 	&lt;param name="<b>delims</b>"&gt;<i>delim-chars</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Return Delimiters. Default false. --&gt;
 * 	&lt;param name="<b>returnDelims</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Trim token values. Default true. --&gt;
 * 	&lt;param name="<b>trimTokens</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/smooks-resource&gt;</pre>
 * 
 * <p/>
 * Two default configurations of this decoder are pre-installed for all useragents.  They're named
 * "string-list" and "string-hashset".
 * 
 * <h3 id="exampleusage">Example Usage</h3>
 * The following example illustrates use of the pre-installed "string-hashset" decoder:
 * <p/>
 * .cdrl param:
 * <pre>
 * &lt;smooks-resource useragent="html4" path="com.acme.XXXContentDeliveryUnit"&gt;
 * 	&lt;param name="blockLevelElements" type="<b>string-hashset</b>"&gt;
 * 		p,h1,h2,h3,h4,h5,h6,div,ul,ol,dl,menu,dir,pre,hr,blockquote,address,center,noframes,isindex,fieldset,table
 * 	&lt;/param&gt;
 * &lt;/smooks-resource&gt;</pre>
 * <p/>
 * ... and the "com.acme.XXXContentDeliveryUnit" {@link org.milyn.delivery.ContentDeliveryUnit} accessing this parameter value:
 * <pre>
 * {@link org.milyn.cdr.Parameter} param = {@link org.milyn.cdr.SmooksResourceConfiguration resourceConfig}.{@link org.milyn.cdr.SmooksResourceConfiguration#getParameter(String) getParameter("blockLevelElements")};
 * {@link java.util.HashSet} blockLevelElements = (HashSet)param.{@link org.milyn.cdr.Parameter#getValue(ContentDeliveryConfig) getValue(ContentDeliveryConfig)}; 
 * </pre>
 * <p/>
 * Note, we will make this process easier in the next release.  You'll be able to call a method such
 * as "getDecodedParameter" on the {@link SmooksResourceConfiguration}, returning a decoded parameter Object.
 * 
 * See {@link org.milyn.cdr.SmooksResourceConfiguration}.
 * @author tfennelly
 */
public class TokenizedStringParameterDecoder extends ParameterDecoder {

	Class returnType;
	String delims;
	boolean returnDelims;
	boolean trimTokens;
	
	/**
	 * Public constructor.
	 * @param resourceConfig Configuration.
	 */
	public TokenizedStringParameterDecoder(SmooksResourceConfiguration resourceConfig) {
		super(resourceConfig);
		delims = resourceConfig.getStringParameter("delims", ",");
		returnDelims = resourceConfig.getBoolParameter("returnDelims", false);
		trimTokens = resourceConfig.getBoolParameter("trimTokens", true);
		
		String paramType = resourceConfig.getStringParameter(Parameter.PARAM_TYPE_PREFIX, "string-list");
		if(paramType.equals("string-list")) {
			returnType = Vector.class;
		} else if(paramType.equals("string-hashset")) {
			returnType = LinkedHashSet.class;
		} else {
			throw new ParameterDecodeException("Unsupported decoded return type [" + paramType + "]");
		}
	}

	/**
	 * Decodes the value based on the smooks-resource configuration passed in the constructor.
	 */
	public Object decodeValue(String value) throws ParameterDecodeException {
		Collection returnVal = null;
		StringTokenizer tokenizer;
		
		// Create the desired Collection.
		try {
			returnVal = (Collection)returnType.newInstance();
		} catch (Exception e) {
			IllegalStateException state = new IllegalStateException("Unable to construct Collection instance.");
			state.initCause(e);
			throw state;
		}
		
		// Create the tokenizer.
		tokenizer = new StringTokenizer(value, delims, returnDelims);
		while(tokenizer.hasMoreTokens()) {
			if(trimTokens) {
				returnVal.add(tokenizer.nextToken().trim());
			} else {
				returnVal.add(tokenizer.nextToken());
			}
		}
				
		return returnVal;
	}
	
}
