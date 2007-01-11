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

package org.milyn.cdres.css;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleRule;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.sac.AbstractElementSelector;
import org.apache.batik.css.engine.sac.CSSConditionalSelector;
import org.apache.batik.css.engine.value.Value;
import org.milyn.cdres.css.SimpleCssParser;
import org.milyn.xml.XmlUtil;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Document;

public abstract class CssTestUtil {

	public static StyleSheet parseCSS(String classpath, CSSEngine cssParser) {
		org.w3c.css.sac.InputSource inputSrc = new InputSource(new InputStreamReader(CssTestUtil.class.getResourceAsStream(classpath)));
		URL baseURL = null;
		try {
			baseURL = new URL("http://www.milyn.org");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		}
		StyleSheet styleSheet = cssParser.parseStyleSheet(inputSrc, baseURL, "media");
		return styleSheet;
	}

	public static StyleSheet parseCSS(String classpath) {
		return parseCSS(classpath, new SimpleCssParser());
	}

	public static void printCSS(StyleSheet styleSheet) {
		SimpleCssParser cssParser = new SimpleCssParser();
		System.out.println("Num Rules: " + styleSheet.getSize());
		System.out.println("=============================\n");
		
		for(int i = 0; i < styleSheet.getSize(); i++) {
			Rule rule = styleSheet.getRule(i);
			
			if(rule instanceof StyleRule) {
				StyleRule styleRule = (StyleRule)rule; 
				StyleDeclaration decl = styleRule.getStyleDeclaration();
				SelectorList selectors = styleRule.getSelectorList();

				Value val = decl.getValue(1);
				
				System.out.println(i + ":");
				System.out.println("\t" + rule.toString(cssParser));
				for(int iInner = 0;  iInner < selectors.getLength(); iInner++) {
					Selector selector = selectors.item(iInner);
					if(selector instanceof ElementSelector) {
						AbstractElementSelector cssElementSelector = (AbstractElementSelector)selector;
						System.out.print(cssElementSelector.getLocalName() + ":" + cssElementSelector.getSpecificity() + ", ");
					} else if(selector instanceof CSSConditionalSelector) {
						CSSConditionalSelector cssElementSelector = (CSSConditionalSelector)selector;
						System.out.print(cssElementSelector + ":" + cssElementSelector.getSpecificity() + ", ");
					}
				}
				System.out.println();
			} else {
				System.out.println(i + ": " + rule);
			}
		}
	}

	public static Document parseXMLString(String xmlString) {
		try {
			return XmlUtil.parseStream(new ByteArrayInputStream(xmlString.getBytes()), false);
		} catch (Exception e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		}
		return null;
	}

	public static Document parseCPResource(String classpath) {
		try {
			return XmlUtil.parseStream(CssTestUtil.class.getResourceAsStream(classpath), false);
		} catch (Exception e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		}
		return null;
	}
}