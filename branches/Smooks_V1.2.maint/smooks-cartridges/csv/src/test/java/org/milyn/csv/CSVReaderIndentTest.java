/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2005-2006, JBoss Inc.
 */
package org.milyn.csv;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class CSVReaderIndentTest extends TestCase {

    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-extended-config-12.xml"));

        try {
            StringResult result = new StringResult();
            smooks.filterSource(new StreamSource(getClass().getResourceAsStream("input-message-01.csv")), result);
            assertEquals("<csv-set>\n" +
                    "\t<csv-record>\n" +
                    "\t\t<firstname>Tom</firstname>\n" +
                    "\t\t<lastname>Fennelly</lastname>\n" +
                    "\t\t<gender>Male</gender>\n" +
                    "\t\t<age>4</age>\n" +
                    "\t\t<country>Ireland</country>\n" +
                    "\t</csv-record>\n" +
                    "\t<csv-record>\n" +
                    "\t\t<firstname>Mike</firstname>\n" +
                    "\t\t<lastname>Fennelly</lastname>\n" +
                    "\t\t<gender>Male</gender>\n" +
                    "\t\t<age>2</age>\n" +
                    "\t\t<country>Ireland</country>\n" +
                    "\t</csv-record>\n" +
                    "</csv-set>", result.getResult());
        } finally {
            smooks.close();
        }
    }
}
