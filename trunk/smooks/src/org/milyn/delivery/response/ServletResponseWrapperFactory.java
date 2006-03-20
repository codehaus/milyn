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

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletResponse;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;

/**
 * ServletResponseWrapper Factory (GoF) class.
 * <p/>
 * Used by the {@link org.milyn.SmooksServletFilter} to allow browser aware 
 * manipulation of a Servlet response right at the response stream level via
 * {@link org.milyn.delivery.response.ServletResponseWrapper} implementations.
 * This mechanism allows different {@link javax.servlet.http.HttpServletResponseWrapper}
 * implementations to be applied to a Servlet response based on characteristics of
 * the requesting browser e.g. perform different image transformations.
 * <p/>
 * See {@link org.milyn.delivery.response.XMLServletResponseWrapper} and 
 * {@link org.milyn.delivery.response.PassThruServletResponseWrapper}.
 * @author tfennelly
 */
public abstract class ServletResponseWrapperFactory {

	/**
	 * ServletResponseWrapper factory method.
	 * @param cdrDef Content Delivery Resource definition for the ServletResponseWrapper.
	 * @param containerRequest Container request.
	 * @param originalResponse Original Servlet Response.
	 * @return ServletResponseWrapper instance.
	 */
	public static ServletResponseWrapper createServletResponseWrapper(CDRDef cdrDef, ContainerRequest containerRequest, HttpServletResponse originalResponse) {
		Constructor constructor;
		Class runtime = null;
		
		try {
			ClassLoader classloader = containerRequest.getContext().getCdrarStore().getCdrarClassLoader();
			runtime =  classloader.loadClass(cdrDef.getPath());
		} catch (ClassNotFoundException e) {
			IllegalStateException state = new IllegalStateException("Unable to load " + cdrDef.getPath());
			state.initCause(e);
			throw state;
		}
		try {
			constructor = runtime.getConstructor(new Class[] {ContainerRequest.class, HttpServletResponse.class});
		} catch (SecurityException e) {
			IllegalStateException state = new IllegalStateException("Container doesn't have permissions to load class " + runtime);
			state.initCause(e);
			throw state;
		} catch (NoSuchMethodException e) {
			IllegalStateException state = new IllegalStateException(runtime + " must contain a constructor with an arg signature of (" + ContainerRequest.class + ", " + HttpServletResponse.class + ")");
			state.initCause(e);
			throw state;
			}

		try {
			return (ServletResponseWrapper)constructor.newInstance(new Object[] {containerRequest, originalResponse});
		} catch (ClassCastException e) {
			IllegalStateException state = new IllegalStateException("Failed to construct " + cdrDef.getParameter("class") + ".  Must be an instance of " + ServletResponseWrapper.class);
			state.initCause(e);
			throw state;
		} catch (Exception e) {
			IllegalStateException state = new IllegalStateException("Failed to construct " + cdrDef.getParameter("class") + ".");
			state.initCause(e);
			throw state;
		}
	}
}
