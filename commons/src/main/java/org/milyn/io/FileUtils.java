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

import org.milyn.assertion.AssertArgument;

import java.io.*;

/**
 * File utilities.
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public abstract class FileUtils {
    
    public static void copyFile(String from, String to) throws IOException {
        File fromFile = new File(from);
        File toFile = new File(to);

        writeFile(readFile(fromFile), toFile);
    }

    /**
     * Read the contents of the specified file.
     * @param file The file to read.
     * @return The file contents.
     * @throws IOException Error readiong file.
     */
    public static byte[] readFile(File file) throws IOException {
        AssertArgument.isNotNull(file, "file");

        if(!file.exists()) {
            throw new IllegalArgumentException("No such file '" + file.getAbsoluteFile() + "'.");
        } else if(file.isDirectory()) {
            throw new IllegalArgumentException("File '" + file.getAbsoluteFile() + "' is a directory.  Cannot read.");
        }

        InputStream stream = new FileInputStream(file);
        try {
            return StreamUtils.readStream(stream);
        } finally {
            stream.close();
        }
    }

    public static void writeFile(byte[] bytes, File file) throws IOException {
        if(file.isDirectory()) {
            throw new IllegalArgumentException("File '" + file.getAbsoluteFile() + "' is an existing directory.  Cannot write.");
        }

        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(bytes);
            stream.flush();
        } finally {
            stream.close();
        }
    }
}
