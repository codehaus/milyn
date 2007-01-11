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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.css.engine.StyleSheet;
import org.w3c.dom.DOMException;

import junit.framework.TestCase;

public class SimpleCssParserTest extends TestCase {

	public void test_parseElementStyle() {
		SimpleCssParser parser = new SimpleCssParser();
		try {
			StyleSheet ss = parser.parseStyleSheet("h {border: solid; border-width: 1; text-align: center}", new URL("http://www.milyn.org"), "media");
			
			System.out.println("Num rules: " + ss.getSize());
			System.out.println(ss.getRule(0).toString(parser));
		} catch (DOMException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}
}
