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

package org.milyn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.milyn.dom.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class IETest extends TestCase {

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
}
