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

import java.io.*;

/**
 * Stream Utilities.
 * 
 * @author tfennelly
 */
public abstract class StreamUtils {

	/**
	 * Read the supplied InputStream and return as a byte array.
	 * 
	 * @param stream
	 *            The stream to read.
	 * @return byte array containing the Stream data.
	 * @throws IOException
	 *             Exception reading from the stream.
	 */
	public static byte[] readStream(InputStream stream) throws IOException {
		if (stream == null) {
			throw new IllegalArgumentException(
					"null 'stream' arg in method call.");
		}

		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		byte[] byteBuf = new byte[1024];
		int readCount = 0;

		while ((readCount = stream.read(byteBuf)) != -1) {
			bytesOut.write(byteBuf, 0, readCount);
		}

		return bytesOut.toByteArray();
	}

    /**
     * Compares the 2 streams.
     * <p/>
     * Calls {@link #trimLines(InputStream)} on each stream before comparing.
     * @param s1 Stream 1.
     * @param s2 Stream 2.
     * @return True if the streams are equal not including leading and trailing
     * whitespace on each line and blank lines, otherwise returns false.
     */
    public static boolean compareCharStreams(InputStream s1, InputStream s2) {
        StringBuffer s1Buf, s2Buf;

        try {
            s1Buf = trimLines(s1);
            s2Buf = trimLines(s2);

            return s1Buf.toString().equals(s2Buf.toString());
        } catch (IOException e) {
            // fail the comparison
        }

        return false;
    }

    /**
     * Read the lines lines of characters from the stream and trim each line
     * i.e. remove all leading and trailing whitespace.
     * @param charStream Character stream.
     * @return StringBuffer containing the line trimmed stream.
     * @throws IOException
     */
    public static StringBuffer trimLines(InputStream charStream) throws IOException {
        StringBuffer stringBuf = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(charStream));
        String line;

        while((line = reader.readLine()) != null) {
            stringBuf.append(line.trim());
        }

        return stringBuf;
    }
}
