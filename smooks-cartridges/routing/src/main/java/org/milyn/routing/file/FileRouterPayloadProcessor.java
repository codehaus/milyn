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

package org.milyn.routing.file;

import javax.xml.transform.Result;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.PayloadProcessor;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>			
 *
 */
public class FileRouterPayloadProcessor extends PayloadProcessor
{
	public FileRouterPayloadProcessor(Smooks smooks)
	{
		super( smooks );
	}
	
	/**
	 * Will retrieve the file name from the exeuction context.
	 */
	@Override
	protected Object prepareResult( Result result, ExecutionContext executionContext )
	{
		return FileListAccessor.getListFileNames( executionContext );
	}

}
