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
 * @author <a href="daniel.bevenius@redpill.se">Daniel Bevenius</a>
 */
public class BasicXslTransformTest extends TestCase {
	private Logger log = Logger.getLogger( BasicXslTransformTest.class );

    public void test() throws IOException, SAXException {
        byte[] expected = StreamUtils.readStream(getClass().getResourceAsStream("expected.xml"));
        String result = Main.runSmooksTransform();

        log.debug(  result );
        log.debug(  new String(expected) );
        assertTrue(StreamUtils.compareCharStreams(new ByteArrayInputStream(expected), new ByteArrayInputStream(result.getBytes())));
    }
}
