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

package org.milyn.templating.stringtemplate.sample;

import java.io.IOException;
import java.io.InputStream;

import org.milyn.SmooksStandalone;
import org.milyn.templating.CharUtils;
import org.milyn.templating.TemplatingUtils;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class ShippingIntegSampleTest extends TestCase {

    public void testTransform() throws SAXException, IOException {
        SmooksStandalone smooks = new SmooksStandalone("UTF-8");

        // Configure Smooks
        smooks.registerUseragent("shipping-request");
        smooks.registerUseragent("shipping-response");
        TemplatingUtils.registerCDUCreators(smooks.getContext());
        smooks.registerResources("trans-request", getClass().getResourceAsStream("trans-request.cdrl"));
        smooks.registerResources("trans-response", getClass().getResourceAsStream("trans-response.cdrl"));
                
        InputStream requestStream = getClass().getResourceAsStream("request.xml");
        String requestResult = smooks.filterAndSerialize("shipping-request", requestStream);
		CharUtils.assertEquals("StringTemplate test failed.", "/org/milyn/templating/stringtemplate/sample/request.xml.tran.expected", requestResult);

        InputStream responseStream = getClass().getResourceAsStream("response.xml");
        String responseResult = smooks.filterAndSerialize("shipping-response", responseStream);
		CharUtils.assertEquals("StringTemplate test failed.", "/org/milyn/templating/stringtemplate/sample/response.xml.tran.expected", responseResult);
    }
}
