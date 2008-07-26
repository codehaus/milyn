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

package org.milyn.json;

import java.io.ByteArrayInputStream;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.xml.XmlUtil;

/**
 * @author tfennelly
 */
public class JSONReaderTest extends TestCase {

	private static final Log logger = LogFactory.getLog(JSONReaderTest.class);

	public void test_01() throws Exception {
		Smooks smooks = new Smooks();
		SmooksResourceConfiguration config;

        config = new SmooksResourceConfiguration("org.xml.sax.driver", "type:Order-List and from:Acme", JSONReader.class.getName());
		SmooksUtil.registerResource(config, smooks);
		SmooksUtil.registerProfileSet(DefaultProfileSet.create("Order-List-Acme-AcmePartner1", new String[] {"type:Order-List", "from:Acme", "to:AcmePartner1"}), smooks);

        test_01(smooks, "test_01_message_01");
        test_01(smooks, "test_01_message_02");
        test_01(smooks, "test_01_message_03");
        test_01(smooks, "test_01_message_04");
	}

	private void test_01(Smooks smooks, String filename) throws Exception{
		DOMResult domResult = new DOMResult();

		ExecutionContext context = smooks.createExecutionContext("Order-List-Acme-AcmePartner1");
        smooks.filter(new StreamSource(getClass().getResourceAsStream(filename + ".jsn")), domResult, context);

        String resultXML = XmlUtil.serialize(domResult.getNode().getChildNodes());
        logger.info(resultXML);

        byte[] result = resultXML.getBytes();
        byte[] expected = StreamUtils.readStream(getClass().getResourceAsStream(filename + ".xml"));

        assertTrue(StreamUtils.compareCharStreams(new ByteArrayInputStream(result), new ByteArrayInputStream(expected)));
	}

//    public void test_02() throws SmooksException, IOException, SAXException {
//        Smooks smooks = new Smooks();
//        ExecutionContext context;
//
//        smooks.addConfigurations("config", getClass().getResourceAsStream("smooks-config-01.xml"));
//        context = smooks.createExecutionContext();
//        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-01.csv"), smooks);
//        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);
//    }
//
//    public void test_03() throws SmooksException, IOException, SAXException {
//        Smooks smooks = new Smooks();
//        ExecutionContext context;
//
//        smooks.addConfigurations("config", getClass().getResourceAsStream("smooks-config-02.xml"));
//        context = smooks.createExecutionContext();
//        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("input-message-02.csv"), smooks);
//        assertEquals("<csv-set><csv-record><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></csv-record><csv-record><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></csv-record></csv-set>", result);
//    }
}
