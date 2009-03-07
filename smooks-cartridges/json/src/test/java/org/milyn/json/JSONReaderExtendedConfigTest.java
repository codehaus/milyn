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
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;

/**
 * @author <a href="mailto:maurice@zeijen.net">maurice@zeijen.net</a>
 */
public class JSONReaderExtendedConfigTest extends TestCase {

	private static final Log logger = LogFactory.getLog(JSONReaderExtendedConfigTest.class);


    public void test_simple_smooks_config() throws Exception {
    	test_config_file("simple_smooks_config");

        // Programmatic config....
        Smooks smooks = new Smooks();
        smooks.setReaderConfig(new JSONReaderConfigurator());
        test_config_file("simple_smooks_config", smooks);
    }

    public void test_key_replacement() throws Exception {
    	test_config_file("key_replacement");

        // Programmatic config....
        Smooks smooks = new Smooks();

        JSONReaderConfigurator config = new JSONReaderConfigurator();
        Map<String, String> keyMap = new HashMap<String, String>();

        keyMap.put("some key", "someKey");
        keyMap.put("some&key", "someAndKey");
        config.setKeyMap(keyMap);

        smooks.setReaderConfig(config);
        test_config_file("key_replacement", smooks);
    }

    public void test_several_replacements() throws Exception {
    	test_config_file("several_replacements");

        // Programmatic config....
        Smooks smooks = new Smooks();

        JSONReaderConfigurator config = new JSONReaderConfigurator();

        config.setKeyWhitspaceReplacement("_");
        config.setKeyPrefixOnNumeric("n");
        config.setIllegalElementNameCharReplacement(".");
        config.setNullValueReplacement("##NULL##");

        smooks.setReaderConfig(config);
        test_config_file("several_replacements", smooks);
    }

    public void test_configured_different_node_names() throws Exception {
    	test_config_file("configured_different_node_names");

        // Programmatic config....
        Smooks smooks = new Smooks();

        JSONReaderConfigurator config = new JSONReaderConfigurator();

        config.setRootName("root");
        config.setArrayElementName("e");

        smooks.setReaderConfig(config);
        test_config_file("configured_different_node_names", smooks);
    }

    private void test_config_file(String testName) throws Exception {
        Smooks smooks = new Smooks("/test/" + testName + "/smooks-extended-config.xml");

        test_config_file(testName, smooks);
    }

    private void test_config_file(String testName, Smooks smooks) throws IOException {
        ExecutionContext context = smooks.createExecutionContext();
        String result = SmooksUtil.filterAndSerialize(context, getClass().getResourceAsStream("/test/" + testName + "/input-message.jsn"), smooks);

        if(logger.isDebugEnabled()) {
        	logger.debug("Result: " + result);
        }

        assertEquals("/test/" + testName + "/expected.xml", result.getBytes());
    }

    private void assertEquals(String fileExpected, byte[] actual) throws IOException {

		byte[] expected = StreamUtils.readStream(getClass().getResourceAsStream(fileExpected));

        assertTrue("Expected XML and result XML are not the same!", StreamUtils.compareCharStreams(new ByteArrayInputStream(actual), new ByteArrayInputStream(expected)));

	}
}
