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
package org.milyn.cdr.xsd11.extensiontests;

import junit.framework.TestCase;
import org.milyn.xml.XmlUtil;
import org.milyn.xml.XsdDOMValidator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class XSD11ExtendTest extends TestCase {

    public void test_validation() throws IOException, SAXException, ParserConfigurationException {
        Document configDoc = XmlUtil.parseStream(getClass().getResourceAsStream("config_01.xml"));
        XsdDOMValidator validator = new XsdDOMValidator(configDoc);

        assertEquals("http://www.milyn.org/xsd/smooks-1.1.xsd", validator.getDefaultNamespace().toString());
        assertEquals("[http://www.milyn.org/xsd/smooks-1.1.xsd, http://www.milyn.org/xsd/smooks/test-xsd-01.xsd]", validator.getNamespaces().toString());

        validator.validate();
    }
}
