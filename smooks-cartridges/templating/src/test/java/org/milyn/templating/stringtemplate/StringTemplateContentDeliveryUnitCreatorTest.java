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

package org.milyn.templating.stringtemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.milyn.Smooks;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.templating.TemplatingUtils;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 *
 * @author tfennelly
 */;
public class StringTemplateContentDeliveryUnitCreatorTest extends TestCase {

    public void testStringTemplateTrans() throws SAXException, IOException {
        Smooks smooks = new Smooks();

        // Configure Smooks
        smooks.registerProfileSet(DefaultProfileSet.create("useragent", new String[] {"profile1"}));
        TemplatingUtils.registerCDUCreators(smooks.getApplicationContext());
        smooks.registerResources("test-configs", getClass().getResourceAsStream("test-configs.cdrl"));

        InputStream stream = 
            new ByteArrayInputStream("<a><b><c x='xvalueonc1' /><c x='xvalueonc2' /></b></a>".getBytes());
        StandaloneExecutionContext context = smooks.createExecutionContext("useragent");
        String result = smooks.filterAndSerialize(context, stream);

        assertEquals("<a><b><mybean>xvalueonc1</mybean><mybean>xvalueonc2</mybean></b></a>", result);
    }
}
