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
package org.milyn.templating.freemarker;

import junit.framework.TestCase;
import org.xml.sax.SAXException;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.templating.TemplatingUtils;
import org.milyn.profile.DefaultProfileSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 *
 * @author tfennelly
 */
public class FreeMarkerContentDeliveryUnitCreatorTest extends TestCase {

    public void testFreeMarkerTrans_01() throws SAXException, IOException {
        Smooks smooks = new Smooks();

        // Configure Smooks
        SmooksUtil.registerProfileSet(DefaultProfileSet.create("useragent", new String[] {"profile1"}), smooks);
        smooks.addConfigurations("test-configs.cdrl", getClass().getResourceAsStream("test-configs-01.cdrl"));

        test_ftl(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_ftl(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    public void testFreeMarkerTrans_02() throws SAXException, IOException {
        Smooks smooks = new Smooks();

        // Configure Smooks
        SmooksUtil.registerProfileSet(DefaultProfileSet.create("useragent", new String[] {"profile1"}), smooks);
        smooks.addConfigurations("test-configs.cdrl", getClass().getResourceAsStream("test-configs-02.cdrl"));

        test_ftl(smooks, "<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>", "<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>");
        // Test transformation via the <context-object /> by transforming the root element using StringTemplate.
        test_ftl(smooks, "<c x='xvalueonc1' />", "<mybean>xvalueonc1</mybean>");
    }

    private void test_ftl(Smooks smooks, String input, String expected) {
        InputStream stream = new ByteArrayInputStream(input.getBytes());
        StandaloneExecutionContext context = smooks.createExecutionContext("useragent");
        String result = SmooksUtil.filterAndSerialize(context, stream, smooks);

        assertEquals(expected, result);
    }
}
