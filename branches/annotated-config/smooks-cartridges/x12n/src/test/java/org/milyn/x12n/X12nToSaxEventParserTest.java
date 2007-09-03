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

package org.milyn.x12n;

import java.io.IOException;
import java.io.InputStream;

import org.milyn.SmooksStandalone;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */
public class X12nToSaxEventParserTest extends TestCase {

    public void testTransform() throws SAXException, IOException {
        SmooksStandalone smooks = new SmooksStandalone("UTF-8");

        // Configure Smooks...
        smooks.registerUseragent("x12n-client-request-X", new String[] {"x12n-requester"});
        smooks.registerResources("x12n-config", getClass().getResourceAsStream("x12n-config.cdrl"));

        // Perform the transformation...
        InputStream requestStream = getClass().getResourceAsStream("x12n-sample2.txt");
        String requestResult = smooks.filterAndSerialize("x12n-client-request-X", requestStream);
        System.out.println(requestResult);
        
    }
}
