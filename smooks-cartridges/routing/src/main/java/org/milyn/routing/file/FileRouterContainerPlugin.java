package org.milyn.routing.file;

import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.AbstractContainerPlugin;

public class FileRouterContainerPlugin extends AbstractContainerPlugin
{
	/**
	 * 	Will retrieve the file name from the exeuction context.
	 */
	@Override
	protected Object packagePayload( Object object, ExecutionContext execContext )
	{
		return FileListAccessor.getFileName( execContext );
	}
}
