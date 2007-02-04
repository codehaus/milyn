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

package org.milyn.container;

import java.net.URI;
import java.util.Enumeration;

import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.ElementList;
import org.milyn.useragent.UAContext;
import org.milyn.useragent.request.HttpRequest;

/**
 * Smooks container request interface definition.
 * @author tfennelly
 */
public interface ContainerRequest extends HttpRequest, BoundAttributeStore {

	/**
	 * Returns the portion of the request URI that indicates the context of 
	 * the request. The context path always comes first in a request URI. 
	 * The path starts with a "/" character but does not end with a "/" 
	 * character.
	 * @return String specifying the portion of the request URI that indicates 
	 * the context of the request.
	 */
	public String getContextPath();
	
	/**
	 * Returns the full request URI made by the requesting device (including the
	 * query string).
	 * @return The full request URI made by the requesting device.
	 */
	public URI getRequestURI();
	
	/**
	 * Returns an Enumeration of String  objects containing the names of the 
	 * parameters contained in this request. If the request has no parameters, 
	 * the method returns an empty Enumeration.
	 * @return an Enumeration of String  objects, each String containing the 
	 * name of a request parameter; or an empty Enumeration if the request has 
	 * no parameters.
	 */
	public abstract Enumeration getParameterNames();
	
	/**
	 * Returns an array of String objects containing all of the values the given 
	 * request parameter has, or null if the parameter does not exist.
	 * <p/>
	 * If the parameter has a single value, the array has a length of 1.
	 * @param name String containing the name of the parameter whose value is 
	 * requested.
	 * @return an array of String objects containing the parameter's values.
	 */
	public abstract String[] getParameterValues(java.lang.String name);
	
	/**
	 * Get the container context within which this request "lives".
	 * @return The ContainerContext instance.
	 */
	public abstract ContainerContext getContext();
	
	/**
	 * Get the container session within which this request "lives".
	 * @return The ContainerSession instance.
	 */
	public abstract ContainerSession getSession();
	
	/**
	 * Get the UAContext instance for the useragent attached to this request.
	 * @return UAContext instance.
	 */
	public abstract UAContext getUseragentContext();
	
	/**
	 * Get the content delivery configuration for the useragent attached to
	 * this request.
	 * @return ContentDeliveryConfigImpl instance.
	 */
	public abstract ContentDeliveryConfig getDeliveryConfig();
	
	/**
	 * Request utility method for "hooking" elements of the same name
	 * together within the context of a request.
	 * <p/>
	 * Implementations must create the ElementList instance for the
	 * element if it doesn't exist.
	 * @param name The name of the element whose ElementList is being 
	 * requested. 
	 * @return ElementList for the element specified by the name param.
	 * @see #clearElementLists()
	 */
	public abstract ElementList getElementList(String name);
	
	/**
	 * Clear the list of {@link ElementList}s from this request.
	 * @see #getElementList(String)
	 */
	public abstract void clearElementLists();
}
