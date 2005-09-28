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

package org.milyn.dom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.milyn.dom.Parser;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	public void testParseNode() {
		// Can the parser parse badly formed markup
		parseCheck("Text value parse failure.", "<x>Some Text",
				"x/text()", "Some Text");
	}
	
	/*
	public void testParseText() {
		parseCheck("Text value parse failure.", "<x>Some Text</x>",
				"x/text()", "Some Text");
		parseCheck("Text value parse failure.", "<x>Some \"Text\"</x>",
				"x/text()", "Some \"Text\"");
		parseCheck("Text value parse failure.", "<x>Some 'Text'</x>",
				"x/text()", "Some 'Text'");
		parseCheck("Text value parse failure.", "<x>Some &amp; Text</x>",
				"x/text()", "Some &#38; Text");
		parseCheck("Text value parse failure.", "<x>Some + - ( ) : ; . , : # £ $ % ^ @ Text</x>",
				"x/text()", "Some + - ( ) : ; . , : # £ $ % ^ @ Text");
	}
	
	public void testParseAttribute() {
		parseCheck("Attribute value parse failure.", "<x attrib=\"value\" />",
				"x/@attrib", "value");
		parseCheck("Attribute value parse failure.", "<x attrib=\"va'l'ue\" />",
				"x/@attrib", "va'l'ue");
		parseCheck("Attribute value parse failure.", "<x attrib=\"val'ue\" />",
				"x/@attrib", "val'ue");
		parseCheck("Attribute value parse failure.", "<x attrib=\"'value\" />",
				"x/@attrib", "'value");
		parseCheck("Attribute value parse failure.", "<x attrib=\"value'\" />",
				"x/@attrib", "value'");
		parseCheck("Attribute value parse failure.", "<x attrib=\"val&amp;ue\" />",
				"x/@attrib", "val&#38;ue");
		parseCheck("Attribute value parse failure.", "<x attrib=\"val=ue\" />",
				"x/@attrib", "val=ue");
		parseCheck("Attribute value parse failure.", "<x attrib=\"val>ue\" />",
				"x/@attrib", "val>ue");
		
		parseCheck("Attribute value parse failure.", "<x attrib='value' />",
				"x/@attrib", "value");
		parseCheck("Attribute value parse failure.", "<x attrib='va\"l\"ue' />",
				"x/@attrib", "va\"l\"ue");
		parseCheck("Attribute value parse failure.", "<x attrib='val\"ue' />",
				"x/@attrib", "val\"ue");
		parseCheck("Attribute value parse failure.", "<x attrib='\"value' />",
				"x/@attrib", "\"value");
		parseCheck("Attribute value parse failure.", "<x attrib='value\"' />",
				"x/@attrib", "value\"");
		parseCheck("Attribute value parse failure.", "<x attrib='val&amp;ue' />",
				"x/@attrib", "val&#38;ue");

		parseCheck("Attribute value parse failure.", "<x attrib='value + - ( ) : ; . , : # £ $ % ^ @ value' />",
				"x/@attrib", "value + - ( ) : ; . , : # £ $ % ^ @ value");
	}
	*/

	public void testParseComment() {
		try {
			Parser parser = new Parser();
			Document doc = parser.parse(getReader("<x><!-- Comment --></x>"));
			Node node = XmlUtil.getNode(doc, "x").getFirstChild();;
			
			if(node.getNodeType() != Node.COMMENT_NODE) {
				fail("Comment node parse failure.");
			}
			assertEquals("Comment node parse failure.", " Comment ", node.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testParseCdata() {
		try {
			Parser parser = new Parser();
			Document doc = parser.parse(getReader("<x><![CDATA[CDATA Section]]></x>"));
			Node node = XmlUtil.getNode(doc, "x").getFirstChild();;
			
			if(node.getNodeType() != Node.CDATA_SECTION_NODE) {
				fail("CDATA Section node parse failure.");
			}
			assertEquals("CDATA Section node parse failure.", "CDATA Section", ((CDATASection)node).getData());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testDoctype() {
		InputStream stream = getClass().getResourceAsStream("IE6_bug_with_overflow_and_positionrelative.html");
		Parser parser = new Parser();
		try {
			Document doc = parser.parse(new InputStreamReader(stream));
			DocumentType doctype = doc.getDoctype();
			assertNotNull(doctype);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public void parseCheck(String message, String input, String xpath, String expected) {
		try {
			Parser parser = new Parser();
			Document doc = parser.parse(getReader(input));
			Node node = XmlUtil.getNode(doc, xpath);
			
			assertEquals(message, expected, node.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}

	private Reader getReader(String string) {
		ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
		return new InputStreamReader(stream);
	}
}
