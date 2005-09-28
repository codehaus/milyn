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

package org.milyn.param;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.ParameterDecodeException;
import org.milyn.cdr.ParameterDecoder;
import org.milyn.cdr.CDRDef.Parameter;

/**
 * {@link ParameterDecoder} used to tokenize a parameter value.
 * <p/>
 * Tokenizes the parameter value into a {@link java.util.List} (selector="param-type:string-list")
 * or {@link java.util.HashSet} (selector="param-type:string-hashset") using {@link java.util.StringTokenizer}.
 * <h3>.cdrl Configuration</h3>
 * <pre>
 * &lt;!-- Tokenize value into a {@link java.util.List} i.e. 
 * 		{@link org.milyn.cdr.CDRDef#getParameter(java.lang.String)}.getValue({@link org.milyn.delivery.ContentDeliveryConfig}) --&gt;
 * &lt;cdres	uatarget="*" selector="param-type:string-list" 
 * 	path="org.milyn.param.TokenizedStringParameterDecoder"&gt;
 * 
 * 	&lt;!-- (Optional) Tokenizer Delimiters. Default is ",". --&gt;
 * 	&lt;param name="<b>delims</b>"&gt;<i>delim-chars</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Return Delimiters. Default false. --&gt;
 * 	&lt;param name="<b>returnDelims</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Trim token values. Default true. --&gt;
 * 	&lt;param name="<b>trimTokens</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/cdres&gt;
 * 
 * &lt;!-- Tokenize value into a {@link java.util.HashSet} i.e. 
 * 		{@link org.milyn.cdr.CDRDef#getParameter(java.lang.String)}.getValue({@link org.milyn.delivery.ContentDeliveryConfig}) --&gt;
 * &lt;cdres	uatarget="*" selector="param-type:string-hashset"
 * 	path="org.milyn.param.TokenizedStringParameterDecoder"&gt;
 * 
 * 	&lt;!-- (Optional) Tokenizer Delimiters. Default is ",". --&gt;
 * 	&lt;param name="<b>delims</b>"&gt;<i>delim-chars</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Return Delimiters. Default false. --&gt;
 * 	&lt;param name="<b>returnDelims</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * 
 * 	&lt;!-- (Optional) Trim token values. Default true. --&gt;
 * 	&lt;param name="<b>trimTokens</b>"&gt;<i>true/false</i>&lt;/param&gt;
 * &lt;/cdres&gt;</pre>
 * See {@link org.milyn.cdr.CDRDef}.
 * @author tfennelly
 */
public class TokenizedStringParameterDecoder extends ParameterDecoder {

	Class returnType;
	String delims;
	boolean returnDelims;
	boolean trimTokens;
	
	/**
	 * Public constructor.
	 * @param cdrDef Configuration.
	 */
	public TokenizedStringParameterDecoder(CDRDef cdrDef) {
		super(cdrDef);
		delims = cdrDef.getStringParameter("delims", ",");
		returnDelims = cdrDef.getBoolParameter("returnDelims", false);
		trimTokens = cdrDef.getBoolParameter("trimTokens", true);
		if(cdrDef.getSelector().equals(Parameter.PARAM_TYPE_PREFIX + "string-list")) {
			returnType = Vector.class;
		} else if(cdrDef.getSelector().equals(Parameter.PARAM_TYPE_PREFIX + "string-hashset")) {
			returnType = LinkedHashSet.class;
		} else {
			throw new ParameterDecodeException("Unsupported return type [" + cdrDef.getSelector() + "]");
		}
	}

	/**
	 * Decodes the value based on the cdres configuration passed in the constructor.
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
