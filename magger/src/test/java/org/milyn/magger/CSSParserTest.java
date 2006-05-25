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

package org.milyn.magger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.List;

import org.milyn.resource.ExternalResourceLocator;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;

import junit.framework.TestCase;

public class CSSParserTest extends TestCase {
	
	public void test_parse() {
		MyExternalResourceLocator resLocator = new MyExternalResourceLocator();
		
		resLocator.streams.put("http://www.x.com/test1.css", getClass().getResourceAsStream("test1.css"));
		resLocator.streams.put("http://www.x.com/b/test2.css", getClass().getResourceAsStream("test2.css"));
		
		CSSParser parser = new CSSParser(resLocator);
		
		try {
			CSSStylesheet styleSheet = parser.parse(URI.create("http://www.x.com/test1.css"), null);
			List rules = styleSheet.getRules();
			
			System.out.println("Num rules: " + rules.size());
			assertEquals("Wrong number of rules", 57, rules.size());
			CSSRule rule;
			
			rule = (CSSRule)rules.get(0);
			assertEquals("margin", rule.getProperty().getName());
			assertEquals("Wrong property value.", 3, rule.getProperty().getValue().getIntegerValue());
			assertEquals("h4", rule.getSelector().toString());

			rule = (CSSRule)rules.get(11);
			assertEquals("border-bottom", rule.getProperty().getName());
			assertEquals("Wrong property value.", LexicalUnit.SAC_PIXEL, rule.getProperty().getValue().getLexicalUnitType());
			assertEquals("Wrong property value.", "3px attr(solid) color(255 , 255 , 255)", rule.getProperty().getValue().toString());
			assertEquals("h1", rule.getSelector().toString());
			
			rule = (CSSRule)rules.get(56);
			assertEquals("background", rule.getProperty().getName());
			assertEquals("Wrong property value.", LexicalUnit.SAC_RGBCOLOR, rule.getProperty().getValue().getLexicalUnitType());
			assertEquals("Wrong property value.", "color(251 , 218 , 187)", rule.getProperty().getValue().toString());
			assertEquals("*.secondfloat", rule.getSelector().toString());
		} catch (CSSException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class MyExternalResourceLocator implements ExternalResourceLocator {
		private Hashtable streams = new Hashtable();

		public InputStream getResource(String res) throws IllegalArgumentException, IOException {
			return (InputStream) streams.get(res);
		}
	}
}
