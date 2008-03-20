package org.milyn.routing.file;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.PayloadProcessor;

public class FileRouterContainerPlugin extends PayloadProcessor
{
	public FileRouterContainerPlugin(Smooks smooks)
	{
		super( smooks );
	}

	/**
	 * 	Will retrieve the file name from the exeuction context.
	 */
	@Override
	protected Object packagePayload( Object object, ExecutionContext execContext )
	{
		return FileListAccessor.getFileName( execContext );
	}
}
