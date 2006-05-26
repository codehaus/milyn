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

package org.milyn.delivery.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.milyn.container.ContainerRequest;
import org.milyn.delivery.ContentDeliveryUnit;

/**
 * Abstract Servlet Response Wrapper for the Smooks framework.
 * <p/>
 * Implementations are hooked into the {@link org.milyn.SmooksServletFilter} by the
 * {@link org.milyn.delivery.response.ServletResponseWrapperFactory} to allow browser aware 
 * manipulation of a Servlet response right at the response stream level. 
 * <p/>
 * See {@link org.milyn.delivery.response.XMLServletResponseWrapper} and 
 * {@link org.milyn.delivery.response.PassThruServletResponseWrapper}.
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