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

package org.milyn.container.standalone;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.milyn.SmooksStandalone;
import org.milyn.device.ident.UnknownDeviceException;
import org.xml.sax.SAXException;

public class StandaloneContainerUtil {

	public static StandaloneContainerRequest getRequest(String requestURI, String browserName) {
		StandaloneContainerRequest request = null;
		try {
			SmooksStandalone smooksSA = new TestSmooksStandalone();
			request = new StandaloneContainerRequest(new URI(requestURI), new LinkedHashMap(), smooksSA.getSession(browserName));
		} catch (UnknownDeviceException e) {
			TestCase.fail(e.getMessage());
		} catch (URISyntaxException e) {
			TestCase.fail(e.getMessage());
		} catch (SAXException e) {
            TestCase.fail(e.getMessage());
        } catch (IOException e) {
            TestCase.fail(e.getMessage());
        }
		
		return request;
	}
}
