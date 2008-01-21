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

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import junit.framework.TestCase;
import org.milyn.io.StreamUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksServletFilterTest extends TestCase {

    public void test_firefox() throws IOException, SAXException {
        WebConversation wc = new WebConversation();
        WebRequest req = new GetMethodWebRequest("http://localhost:8080/smooks-test/");

        req.setHeaderField("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
        WebResponse resp = wc.getResponse(req);

        InputStream expected = getClass().getResourceAsStream("test-exp-firefox.xml");
        InputStream actual = new ByteArrayInputStream(resp.getText().getBytes());
        assertTrue(StreamUtils.compareCharStreams(expected, actual));
    }

    public void test_msie() throws IOException, SAXException {
        WebConversation wc = new WebConversation();
        WebRequest req = new GetMethodWebRequest("http://localhost:8080/smooks-test/");

        req.setHeaderField("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
        WebResponse resp = wc.getResponse(req);

        InputStream expected = getClass().getResourceAsStream("test-exp-msie.xml");
        InputStream actual = new ByteArrayInputStream(resp.getText().getBytes());
        assertTrue(StreamUtils.compareCharStreams(expected, actual));
    }
}
