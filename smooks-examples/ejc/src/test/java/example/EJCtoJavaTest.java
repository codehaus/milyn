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
package example;

import junit.framework.*;
import org.xml.sax.*;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.ejc.IllegalNameException;

import java.io.*;

/**
 * Test that example produces expected result.
 *  
 * @author bardl
 */
public class EJCtoJavaTest extends TestCase {

    public void test() throws EDIConfigurationException, IOException, IllegalNameException, InterruptedException, SAXException {
        String expected = org.milyn.io.StreamUtils.readStreamAsString(getClass().getResourceAsStream("expected.xml"));

        Main main = new Main();
        String orderXml = main.runEJCTest();
        orderXml = orderXml.replaceFirst("<date>.*</date>", "<date/>");

        boolean matchesExpected = org.milyn.io.StreamUtils.compareCharStreams(new java.io.StringReader(expected), new java.io.StringReader(orderXml));
        if(!matchesExpected) {
            assertEquals("Actual does not match expected.", expected, orderXml);
        }
    }
}
