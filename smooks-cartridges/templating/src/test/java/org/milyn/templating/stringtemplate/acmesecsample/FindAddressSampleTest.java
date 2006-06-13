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

package org.milyn.templating.stringtemplate.acmesecsample;

import java.io.IOException;
import java.io.InputStream;

import org.milyn.SmooksStandalone;
import org.milyn.templating.TemplatingUtils;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class FindAddressSampleTest extends TestCase {

    public void testTransform() throws SAXException, IOException {
        SmooksStandalone smooks = new SmooksStandalone("UTF-8");

        // Configure Smooks...
        smooks.registerUseragent("acme-findAddresses-request", new String[] {"acme-request"});
        TemplatingUtils.registerCDUCreators(smooks.getContext());
        smooks.registerResources("acme-creds", getClass().getResourceAsStream("acme-creds.cdrl"));

        // Perform the transformation...
        InputStream requestStream = getClass().getResourceAsStream("AcmeFindaddressRequest.xml");
        String requestResult = smooks.filterAndSerialize("acme-findAddresses-request", requestStream);
        System.out.println(requestResult);
        
        // Node requestTrans = smooks.filter("shipping-request", requestStream);
        // smooks.serialize("shipping-request", requestTrans, outputWriter);
      
        // get assertions on this - need something for doing the compare that can handle the \r and \n chars
    }
}
