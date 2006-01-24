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

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;

import org.milyn.cdr.CDRConfig;
import org.milyn.cdr.CDRDef;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * Unit tests forthe ArciveDef class.
 * @author tfennelly
 */
public class CDRConfigTest extends TestCase {

	private static final String DOC_HEADER = "<?xml version='1.0'?><!DOCTYPE cdres-list PUBLIC '-//MILYN//DTD SMOOKS 1.0//EN' 'http://www.milyn.org/dtd/cdres-list-1.0.dtd'>";

	public CDRConfigTest(String arg0) {
		super(arg0);
	}

	public void testConstructor_1() {
		try {
			// Valid doc
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget='xxx,yyy,zzz' path='/'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  
		try {
			// Invalid doc - no unit defs
			String streamData =	DOC_HEADER + "<cdres-list></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			fail("No exception on invalid archive definition file.");
		} catch (SAXException e1) {
			// OK
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
		try {
			// Valid doc - Defaulting type to "N/A"
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget='xxx,yyy,zzz' path='/'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
		try {
			// Invalid doc - missing the list attribute
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' path='/'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			fail("No exception on invalid archive definition file.");
		} catch (SAXException e1) {
			// OK
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
		try {
			// Valid doc - missing the path attribute
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget='xxx,yyy,zzz' /></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
		} catch (SAXException e1) {
			fail("Unexpected exception." + e1.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
	}


	public void testConstructor_2() {
		try {
			// Invalid doc - empty element attribute
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector=' ' uatarget='xxx,yyy,zzz' path='/'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			fail("No exception on invalid archive definition file.");
		} catch (SAXException e1) {
			// OK
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
		try {
			// Invalid doc - empty list attribute
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget=' ' path='/'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			fail("No exception on invalid archive definition file.");
		} catch (SAXException e1) {
			// OK
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
		try {
			// Invalid doc - empty list attribute
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget=',,,' path='/'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			fail("No exception on invalid archive definition file.");
		} catch (SAXException e1) {
			// OK
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
		try {
			// Valid doc - empty path attribute
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget='xxx,yyy,zzz' path='' /></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
		} catch (SAXException e1) {
			fail("Unexpected exception." + e1.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
	}
	
	public void testGetCDRDefinitions() {
		try {
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' namespace='http://xxxx' uatarget='xxx,yyy,zzz' path='/a/b/res.class'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			CDRDef unitDefs[] = archDef.getCDRDefs();
			
			assertEquals(1, unitDefs.length);
			assertEquals("a", unitDefs[0].getSelector());
			assertEquals("http://xxxx", unitDefs[0].getNamespaceURI());
			assertEquals("/a/b/res.class", unitDefs[0].getPath());
			assertEquals(3, unitDefs[0].getUaTargets().length);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			String streamData =	DOC_HEADER + "<cdres-list><cdres selector='a' uatarget='xxx,yyy,zzz' path='/a/b/resa.class'/><cdres selector='b' uatarget='xxx' path='/a/b/resb.class'/></cdres-list>";
			CDRConfig archDef = new CDRConfig("test", new ByteArrayInputStream(streamData.getBytes()));
			CDRDef unitDefs[] = archDef.getCDRDefs();
			
			assertEquals(2, unitDefs.length);

			assertEquals("a", unitDefs[0].getSelector());
			assertEquals("/a/b/resa.class", unitDefs[0].getPath());
			assertEquals(3, unitDefs[0].getUaTargets().length);

			assertEquals("b", unitDefs[1].getSelector());
			assertEquals("/a/b/resb.class", unitDefs[1].getPath());
			assertEquals(1, unitDefs[1].getUaTargets().length);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testCDRDefParams() {
		try {
			CDRConfig archDef = new CDRConfig("test", getClass().getResourceAsStream("cdrar.cdrl"));
			CDRDef unitDefs[] = archDef.getCDRDefs();
			LinkedHashMap params;
			
			assertEquals("a", unitDefs[0].getSelector());
			assertEquals("b", unitDefs[1].getSelector());
			assertEquals("c", unitDefs[2].getSelector());

			// a
			assertEquals(1, unitDefs[0].getParameterCount());
			assertEquals("value1", unitDefs[0].getParameter("param1").getValue());

			// b
			assertEquals(2, unitDefs[1].getParameterCount());
			assertEquals("value2", unitDefs[1].getParameter("param2").getValue());
			assertEquals("val‹e3 &", unitDefs[1].getParameter("param3").getValue());

			// c
			assertEquals(0, unitDefs[2].getParameterCount());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}  		
	}
}
