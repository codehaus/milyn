/*
 * Milyn - Copyright (C) 2006
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.routing.file.naming;

import org.milyn.util.FreeMarkerTemplate;

import freemarker.template.TemplateException;

/**
 * TemplatedNamingStrategy uses FreeMarker to generate a file name from
 * the passed in <code>templateString</code> and the <code>dataModel</code>
 * <p/>
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class TemplatedNamingStrategy implements NamingStrategy
{
	/**
	 * Generates a file name by delegating to {@link FreeMarkerTemplate }
	 * 
	 * @param <code>template</code>		- FreeMarker template
	 * @param <code>dataModel</code>	- FreeMarker data model
	 * @throws NamingStrategyException 
	 * @throws TemplateException 
	 */
	public String generateFileName( final String templateString, final Object dataModel ) throws NamingStrategyException 
	{
        FreeMarkerTemplate template = new FreeMarkerTemplate( templateString );
        return template.apply( dataModel );
	}

}
