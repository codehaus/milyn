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

package org.milyn.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stream Utilities.
 * @author tfennelly
 */
public abstract class StreamUtils {

	/**
	 * Read the supplied InputStream and return as a byte array.
	 * @param stream The stream to read.
	 * @return byte array containing the Stream data.
	 * @throws IOException Exception reading from the stream.
	 */
	public static byte[] readStream(InputStream stream) throws IOException {
		if(stream == null) {
			throw new IllegalArgumentException("null 'stream' arg in method call.");
		}
		
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		byte[] byteBuf = new byte[1024];
		int readCount = 0;
		
		while((readCount = stream.read(byteBuf)) != -1) {
			bytesOut.write(byteBuf, 0, readCount);
		}
		
		return bytesOut.toByteArray();
	}
}
