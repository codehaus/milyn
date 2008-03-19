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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class TemplatedNamingStrategy implements NamingStrategy
{
	private Log log = LogFactory.getLog( TemplatedNamingStrategy.class );
	
	/**
	 * 
	 * @param template	- FreeMarker template
	 * @param dataModel	- FreeMarker data model
	 * @throws NamingStrategyException 
	 * @throws TemplateException 
	 */
	public String generateFileName( final String templateString, final Object dataModel ) throws NamingStrategyException 
	{
		try
		{
			log.info( "template : " + templateString + ", dataModel(" + dataModel.getClass().getName() + ") : " + dataModel );
			Template template = new Template("free-marker-template", new StringReader( templateString ), new Configuration());
			StringWriter writer = new StringWriter();
			template.process( dataModel, writer );
			return writer.toString();
		} 
		catch (IOException e)
		{
			final String errorMsg = "IOException while trying to create a new FreeMarker Template";
			throw new NamingStrategyException( errorMsg, e );
		} 
		catch (TemplateException e)
		{
			final String errorMsg = "TempalateException while trying to process the FreeMarker template";
			throw new NamingStrategyException( errorMsg, e );
		}
	}

}
