package org.milyn.routing.file;

import java.io.StringWriter;

import javax.xml.transform.Result;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.container.plugin.PayloadProcessor;
import org.milyn.delivery.StringResult;

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
	protected Result prepareResult( Result result, ExecutionContext executionContext )
	{
		final String fileName =  FileListAccessor.getFileName( executionContext );
		final StringResult stringResult = new StringResult();
		StringWriter writer = new StringWriter();
		writer.write( fileName );
		
		stringResult.setWriter( writer );
		return stringResult;
	}

}
