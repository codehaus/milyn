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

package org.milyn.container.plugin;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * SourceResult is data holder for a Source and a Result.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 * @since 1.0
 */
public class SourceResult
{
	private Source source; 
	private Result result;
	
	public SourceResult() { }
	
	public SourceResult( final Source source, final Result result ) 
	{
		this.source = source;
		this.result = result; 
	}
	
	public Source getSource()
	{
		return source;
	}
	
	public void setSource( final Source source )
	{
		this.source = source;
	}
	
	public Result getResult()
	{
		return result;
	}
	
	public void setResult( final Result result )
	{
		this.result = result;
	}

}
