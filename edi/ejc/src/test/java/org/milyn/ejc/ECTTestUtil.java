/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.ejc;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.milyn.io.StreamUtils;

/**
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ECTTestUtil {

	public static void assertModelOK(ClassModel model, InputStream expectedModel) throws IOException, IllegalNameException, ClassNotFoundException {
        StringWriter writer = new StringWriter();

        BeanWriter.writeBeans(model, writer);
        BindingWriter.writeBindingConfig(model, writer);
        
        String expected = StreamUtils.readStreamAsString(expectedModel);
        String actual = writer.toString();
		
        TestCase.assertEquals("Expected mapping model not the same as actual.", StreamUtils.normalizeLines(expected), StreamUtils.normalizeLines(actual));
	}
}
