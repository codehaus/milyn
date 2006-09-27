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

package org.milyn.csv;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.milyn.SmooksException;
import org.milyn.SmooksStandalone;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * @author tfennelly
 */
public class CSVParserTest extends TestCase {

	public void test() throws SmooksException, UnsupportedEncodingException {
		SmooksStandalone smooks = new SmooksStandalone("UTF-8");
		SmooksResourceConfiguration config;
		
		config = new SmooksResourceConfiguration("org.xml.sax.driver", "type:Order-List and from:Acme", CSVParser.class.getName());
		config.setParameter("fields", "string-list", "name,address");		
		smooks.registerResource(config);
		smooks.registerUseragent("Order-List-Acme-AcmePartner1", new String[] {"type:Order-List", "from:Acme", "to:AcmePartner1"});
		
		String csvMessage;
		Node result;
		
		csvMessage = "Tom Fennelly,Ireland";
		result = smooks.filter("Order-List-Acme-AcmePartner1", new ByteArrayInputStream(csvMessage.getBytes("UTF-8")));
		assertEquals("Tom Fennelly", XmlUtil.getString(result, "/csv-set/csv-record[1]/name/text()"));
		assertEquals("Ireland", XmlUtil.getString(result, "/csv-set/csv-record[1]/address/text()"));

		csvMessage = "Tom Fennelly,Ireland\nJoe Bloggs,England";
		result = smooks.filter("Order-List-Acme-AcmePartner1", new ByteArrayInputStream(csvMessage.getBytes("UTF-8")));
		assertEquals("Tom Fennelly", XmlUtil.getString(result, "/csv-set/csv-record[1]/name/text()"));
		assertEquals("Ireland", XmlUtil.getString(result, "/csv-set/csv-record[1]/address/text()"));
		assertEquals("Joe Bloggs", XmlUtil.getString(result, "/csv-set/csv-record[2]/name/text()"));
		assertEquals("England", XmlUtil.getString(result, "/csv-set/csv-record[2]/address/text()"));
		
		csvMessage = "Tom Fennelly\nJoe Bloggs,England";
		result = smooks.filter("Order-List-Acme-AcmePartner1", new ByteArrayInputStream(csvMessage.getBytes("UTF-8")));
		assertEquals("Joe Bloggs", XmlUtil.getString(result, "/csv-set/csv-record[1]/name/text()"));
		assertEquals("England", XmlUtil.getString(result, "/csv-set/csv-record[1]/address/text()"));		
	}
}
