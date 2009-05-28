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
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.SmooksUtil;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.xml.XmlUtil;
import org.xml.sax.SAXException;

/**
 * @author tfennelly
 */
public class CSVReaderTest extends TestCase {

	public void test_01_csv_reader() throws SmooksException, UnsupportedEncodingException {
		test_01(CSVReader.class);
	}

	@SuppressWarnings("deprecation")
	public void test_01_csv_parser() throws SmooksException, UnsupportedEncodingException {
		test_01(CSVParser.class);
	}

	public void test_01(Class<?> readerClass) throws SmooksException, UnsupportedEncodingException {
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration config;
        ExecutionContext context;

        config = new SmooksResourceConfiguration("org.xml.sax.driver", "type:Order-List and from:Acme", readerClass.getName());
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

    public void test_02() throws SmooksException, IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-config-01.xml"));

        ExecutionContext context = smooks.createExecutionContext();
        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-01.csv"), smooks);
        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);
    }

    public void test_03() throws SmooksException, IOException, SAXException {
        test_03("smooks-config-02.xml");
        test_03("smooks-config-03.xml");
    }

    public void test_03(String config) throws SmooksException, IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream(config));

        ExecutionContext context = smooks.createExecutionContext();
        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-02.csv"), smooks);
        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);
    }

    public void test_04() throws SmooksException, IOException, SAXException {
        Smooks smooks = new Smooks( getClass().getResourceAsStream("smooks-extended-config-04.xml"));

        ExecutionContext context = smooks.createExecutionContext();
        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-03.csv"), smooks);
        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);
    }

    public void test_05() throws SmooksException, IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("smooks-extended-config-05.xml"));

        ExecutionContext context = smooks.createExecutionContext("A");

        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-03.csv"), smooks);
        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);

        context = smooks.createExecutionContext("B");

        result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-04.csv"), smooks);
        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);
    }

}