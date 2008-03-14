package org.milyn.routing.file.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectOutputStrategy implements OutputStrategy
{
	private ObjectOutputStream out;
	
	public ObjectOutputStream getOutputStream()
	{
		return out;
	}

	public ObjectOutputStrategy( final String fileName ) throws FileNotFoundException, IOException
	{
		out = new ObjectOutputStream( new FileOutputStream( fileName, true) );
	}
	
	public void write( final Object object, final String encoding ) throws IOException
	{
		out.writeObject( object );
	}

	public void close()
	{
		IOUtil.closeOutputStream( out );
	}

	public void flush() throws IOException
	{
		out.flush();
	}

}
