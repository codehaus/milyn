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
import org.milyn.templating.TemplatingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */
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
        System.out.println(requestResult);
        
        // Node requestTrans = smooks.filter("shipping-request", requestStream);
        // smooks.serialize("shipping-request", requestTrans, outputWriter);

        InputStream responseStream = getClass().getResourceAsStream("response.xml");
        String responseResult = smooks.filterAndSerialize("shipping-response", responseStream);
        System.out.println(responseResult);

        // Node responseTrans = smooks.filter("shipping-response", responseStream);
        // smooks.serialize("shipping-response", responseTrans, responseOutputWriter);
        
        // get assertions on this - need something for doing the compare that can handle the \r and \n chars
    }
}
