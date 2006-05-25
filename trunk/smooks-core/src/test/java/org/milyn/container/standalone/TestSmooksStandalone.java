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

package org.milyn.container.standalone;


import java.io.IOException;

import org.milyn.SmooksStandalone;
import org.milyn.device.ident.UnknownDeviceException;
import org.xml.sax.SAXException;

public class TestSmooksStandalone extends SmooksStandalone {

	/**
	 * Public Constructor.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws UnknownDeviceException Unknown browser.
	 */
	public TestSmooksStandalone() throws SAXException, IOException {
		super("ISO-8859-1");
        registerUseragent("msie6w", new String[] {"msie6", "html4", "html"});
        registerUseragent("msie6m", new String[] {"msie6", "html4", "html"});
        registerUseragent("msie6", new String[] {"html4", "html"});
        registerUseragent("firefox", new String[] {"html4", "html"});
        
        registerResources("parameters", getClass().getResourceAsStream("/cdr/parameters.cdrl"));
        registerResources("parameters", getClass().getResourceAsStream("/cdr/test.cdrl"));
	}

}
