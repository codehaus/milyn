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

package org.milyn.delivery.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.milyn.container.ContainerRequest;

/**
 * Pass-through ServletResponseWrapper.
 * <p/>
 * Pass content through without performing any manipulation/transformation.  Triggered
 * by supplying "<b>smooksrw</b>=<b>passthru-smooksrw</b>" as a HTTP request parameter.  
 * <p/>
 * This response wrapper is configured to be applicable to all devices through the 
 * following content delivery resource configuration:  
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;!DOCTYPE cdres-list PUBLIC "-//MILYN//DTD SMOOKS 1.0//EN" 
 * 			"http://www.milyn.org/dtd/cdres-list-1.0.dtd"&gt;
 * 
 * &lt;!--
 * 	This CDRL defines common delivery units shared across all browsers.
 * 
 * 	NB: See device-profile.xml
 * --&gt;
 * 
 * &lt;cdres-list default-uatarget="*"&gt; 
 * 	&lt;!--
 * 		Pass-Thru response filter configuration - this basically turns of Smooks transformation.
 * 	--&gt;
 * 	&lt;cdres selector="<b>passthru-smooksrw</b>" path="<b>org.milyn.delivery.response.PassThruServletResponseWrapper</b>" /&gt;
 * &lt;/cdres-list&gt;</pre>
 * 
 * @author tfennelly
 */
public class PassThruServletResponseWrapper extends ServletResponseWrapper {

	public PassThruServletResponseWrapper(ContainerRequest containerRequest, HttpServletResponse originalResponse) {
		super(containerRequest, originalResponse);
	}

	public void deliverResponse() throws IOException {
		// Leave to the HttpServletResponseWrapper
	}

	public void close() {
	}

	public String getShortDescription() {
		return "Pass-through ServletResponseWrapper";
	}

	public String getDetailDescription() {
		return "Passes content through without performing any manipulation/transformation.";
	}

}
