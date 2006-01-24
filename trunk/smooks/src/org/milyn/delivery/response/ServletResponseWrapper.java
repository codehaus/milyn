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
import javax.servlet.http.HttpServletResponseWrapper;

import org.milyn.container.ContainerRequest;
import org.milyn.delivery.ContentDeliveryUnit;
import org.w3c.dom.Node;

/**
 * Abstract Servlet Response Wrapper for the Smooks framework.
 * <p/>
 * Allows browser aware manipulation of a Servlet response right at the
 * response stream level. 
 * <p/>
 * ServletResponseWrapper implementations are triggered
 * by supplying "<b>smooksrw</b>=<i>cdrl-selector</i>" as a HTTP request parameter.  
 * See {@link org.milyn.delivery.response.PassThruServletResponseWrapper}.
 * @author tfennelly
 */
public abstract class ServletResponseWrapper extends HttpServletResponseWrapper implements ContentDeliveryUnit {
	
	/**
	 * Container request instance.
	 */
	private ContainerRequest containerRequest;
	
	/**
	 * Constructor.
	 * @param containerRequest Container request.
	 * @param originalResponse Original servlet response.
	 */
	public ServletResponseWrapper(ContainerRequest containerRequest, HttpServletResponse originalResponse) {
		super(originalResponse);
		this.containerRequest = containerRequest;
	}
	
	/**
	 * Is the supplied node in the namespace of this response wrapper.
	 * <p/>
	 * This doesn't really make sense on this ContentDeliveryUnit, so it
	 * always returns true.
	 * @param node Node to be checked.
	 * @return True if the node is in the same namespace, otherwise false.
	 */
	public boolean isInNamespace(Node node) {
		return true;
	}
	
	/**
	 * Get the {@link ContainerRequest} instance associated with this response wrapper. 
	 * @return The {@link ContainerRequest}.
	 */
	public ContainerRequest getContainerRequest() {
		return containerRequest;
	}

	/**
	 * Transform and serialise the supplied content to the target OutputStream.
	 * @throws IOException Probable cause: Unable to get target ServletOutputStream.
	 */
	public abstract void deliverResponse() throws IOException;

	/**
	 * Close response resources.
	 * <p/>
	 * Ensure all resources etc are closed
	 */
	public abstract void close();
}