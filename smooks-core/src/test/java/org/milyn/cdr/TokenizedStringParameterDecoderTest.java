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
import java.util.HashSet;
import java.util.List;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;

import junit.framework.TestCase;

public class TokenizedStringParameterDecoderTest extends TestCase {

	/*
	 * Class under test for Object decodeValue(String)
	 */
	public void testDecodeValue_string_list() {
		Collection collection = getParameter("string-list", "a,b,c,d ,");
		assertTrue("Expected to get back a java.util.List parameter", (collection instanceof List));
		List paramsList = (List)collection;
		assertTrue("Expected java.util.List to contain value.", paramsList.contains("a"));
		assertTrue("Expected java.util.List to contain value.", paramsList.contains("b"));
		assertTrue("Expected java.util.List to contain value.", paramsList.contains("c"));
		assertTrue("Expected java.util.List to contain value.", paramsList.contains("d"));
		assertFalse("Expected java.util.List to NOT contain value.", paramsList.contains("e"));
	}

	/*
	 * Class under test for Object decodeValue(String)
	 */
	public void testDecode_string_hashset() {
		Collection collection = getParameter("string-hashset", "a,b,c,d ,");
		assertTrue("Expected to get back a java.util.List parameter", (collection instanceof HashSet));
		HashSet paramsHashSet = (HashSet)collection;
		assertTrue("Expected java.util.HashSet to contain value.", paramsHashSet.contains("a"));
		assertTrue("Expected java.util.HashSet to contain value.", paramsHashSet.contains("b"));
		assertTrue("Expected java.util.HashSet to contain value.", paramsHashSet.contains("c"));
		assertTrue("Expected java.util.HashSet to contain value.", paramsHashSet.contains("d"));
		assertFalse("Expected java.util.HashSet to NOT contain value.", paramsHashSet.contains("e"));
	}
	
	public Collection getParameter(String type, String value) {
        SmooksResourceConfiguration decoderConfig;
        TokenizedStringParameterDecoder decoder;
		
        decoderConfig = new SmooksResourceConfiguration(Parameter.PARAM_TYPE_PREFIX + type, "org.milyn.cdr.TokenizedStringParameterDecoder");
        decoderConfig.setParameter(Parameter.PARAM_TYPE_PREFIX, type);
        decoder = Configurator.configure(new TokenizedStringParameterDecoder(), decoderConfig);
        
		return (Collection)decoder.decodeValue(value);
	}

}
