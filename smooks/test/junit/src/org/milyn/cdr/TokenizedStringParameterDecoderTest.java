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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.CDRDef.Parameter;
import org.milyn.container.ContainerRequest;
import org.milyn.util.SmooksUtil;

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
		SmooksUtil smooksUtil = new SmooksUtil();
		CDRDef cdrDef;
		ContainerRequest request;
		
		smooksUtil.addCDRDef(Parameter.PARAM_TYPE_PREFIX + type, "device", "org.milyn.cdr.TokenizedStringParameterDecoder", null);
		cdrDef = smooksUtil.addCDRDef("config", "device", null, null);
		cdrDef.setParameter("param1", type, value);
		request = smooksUtil.getRequest("device");
		
		List config = request.getDeliveryConfig().getCDRDefs("config");
		CDRDef paramx = (CDRDef)config.get(0);
		
		Parameter parameter = paramx.getParameter("param1");
		Object obj = parameter.getValue(request.getDeliveryConfig());
		assertNotNull("Expected to get back a parameter value for " + parameter, obj);
		return (Collection)obj;
	}

}
