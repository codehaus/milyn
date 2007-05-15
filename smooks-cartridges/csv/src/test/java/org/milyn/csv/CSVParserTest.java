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
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.xml.XmlUtil;

import junit.framework.TestCase;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMResult;

/**
 * @author tfennelly
 */
public class CSVParserTest extends TestCase {

	public void test() throws SmooksException, UnsupportedEncodingException {
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration config;
        StandaloneExecutionContext context;

        config = new SmooksResourceConfiguration("org.xml.sax.driver", "type:Order-List and from:Acme", CSVParser.class.getName());
		config.setParameter("fields", "string-list", "name,address");		
		SmooksUtil.registerResource(config, smooks);
		SmooksUtil.registerProfileSet(DefaultProfileSet.create("Order-List-Acme-AcmePartner1", new String[] {"type:Order-List", "from:Acme", "to:AcmePartner1"}), smooks);
		
		String csvMessage;
        DOMResult domResult = new DOMResult();

		csvMessage = "Tom Fennelly,Ireland";
        context = smooks.createExecutionContext("Order-List-Acme-AcmePartner1");
        smooks.filter(new StreamSource(new ByteArrayInputStream(csvMessage.getBytes("UTF-8"))), domResult, context);
        assertEquals("Tom Fennelly", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[1]/name/text()"));
		assertEquals("Ireland", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[1]/address/text()"));

		csvMessage = "Tom Fennelly,Ireland\nJoe Bloggs,England";
        context = smooks.createExecutionContext("Order-List-Acme-AcmePartner1");
        smooks.filter(new StreamSource(new ByteArrayInputStream(csvMessage.getBytes("UTF-8"))), domResult, context);
		assertEquals("Tom Fennelly", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[1]/name/text()"));
		assertEquals("Ireland", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[1]/address/text()"));
		assertEquals("Joe Bloggs", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[2]/name/text()"));
		assertEquals("England", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[2]/address/text()"));
		
		csvMessage = "Tom Fennelly\nJoe Bloggs,England";
        context = smooks.createExecutionContext("Order-List-Acme-AcmePartner1");
        smooks.filter(new StreamSource(new ByteArrayInputStream(csvMessage.getBytes("UTF-8"))), domResult, context);
		assertEquals("Joe Bloggs", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[1]/name/text()"));
		assertEquals("England", XmlUtil.getString(domResult.getNode(), "/csv-set/csv-record[1]/address/text()"));		
	}
}
