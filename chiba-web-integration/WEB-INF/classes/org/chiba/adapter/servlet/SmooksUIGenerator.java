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

package org.chiba.adapter.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.chiba.tools.xslt.UIGenerator;
import org.chiba.xml.xforms.exception.XFormsException;
import org.milyn.delivery.response.HtmlServletResponseWrapper;
import org.w3c.dom.Node;

public class SmooksUIGenerator implements UIGenerator {

	private HttpServletRequest request;
	private Writer output;
	
	public SmooksUIGenerator(HttpServletRequest request) {
		this.request = request;
	}

	public void setInputNode(Node transNode) {
		request.setAttribute(HtmlServletResponseWrapper.SOURCE_DOCUMENT, transNode);
	}

	public void setOutput(Object output) {
		this.output = (Writer)output;
	}

	public void setParameter(String arg0, Object arg1) {
	}

	public void generate() throws XFormsException {
		try {
			output.write("Response DOM is bound to the request.");
		} catch (IOException e) {
            throw new XFormsException(e);
		}
	}
}
