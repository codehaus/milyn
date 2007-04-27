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
import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.milyn.Smooks;
import org.xml.sax.SAXException;

public class StandaloneContainerUtil {

	public static StandaloneExecutionContext getRequest(String requestURI, String useragent) {
		StandaloneExecutionContext request = null;
		try {
			Smooks smooksSA = new PreconfiguredSmooks();
			request = new StandaloneExecutionContext(useragent, new LinkedHashMap(), smooksSA.getApplicationContext());
		} catch (SAXException e) {
            TestCase.fail(e.getMessage());
        } catch (IOException e) {
            TestCase.fail(e.getMessage());
        }
		
		return request;
	}
}
