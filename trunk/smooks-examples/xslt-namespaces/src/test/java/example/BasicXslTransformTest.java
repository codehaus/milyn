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
package example;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.milyn.io.StreamUtils;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BasicXslTransformTest extends TestCase {
	private Logger log = Logger.getLogger( BasicXslTransformTest.class );

    public void test() throws IOException, SAXException {
        byte[] expected = StreamUtils.readStream(getClass().getResourceAsStream("expected.xml"));
        String exp = getFileContent( "src/test/java/example/expected.xml" );
        String result = Main.runSmooksTransform();
        log.debug(  result );
        log.debug(  exp );
        assertTrue(StreamUtils.compareCharStreams(new ByteArrayInputStream(expected), new ByteArrayInputStream(result.getBytes())));
    }
    
    public static String getFileContent(String file)
	{
		if (file == null)
			throw new IllegalArgumentException( "file cannot be null" );
		BufferedReader bufr = null;
		StringBuilder sb = new StringBuilder();
		try
		{
			bufr = new BufferedReader( new FileReader( file ) );

			String tmp = null;
			while ((tmp = bufr.readLine()) != null)
				sb.append( tmp ).append( File.separator );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (bufr != null)
				try
				{
					bufr.close();
				}
				catch (IOException e)
				{ /* ignore */
				}
		}
		return sb.toString();
	}
    
}
