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
package org.milyn.fixedlength;

import junit.framework.TestCase;
import org.milyn.FilterSettings;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;

import javax.xml.transform.stream.StreamSource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public class FixedLenghtReaderTest extends TestCase {
	public void test_01_xml_dom() throws Exception {
		test_01_xml(FilterSettings.DEFAULT_DOM);
	}

	public void test_01_xml_sax() throws Exception {
		test_01_xml(FilterSettings.DEFAULT_SAX);
	}

	public void test_01_programmatic_dom() throws Exception {
		test_01_programmatic(FilterSettings.DEFAULT_DOM);
	}

	public void test_01_programmatic_sax() throws Exception {
		test_01_programmatic(FilterSettings.DEFAULT_SAX);
	}

	public void test_01_xml(FilterSettings filterSettings) throws Exception{
		Smooks smooks = new Smooks(getClass().getResourceAsStream("/smooks-config-01.xml"));
		smooks.setFilterSettings(filterSettings);
		test_01(smooks);
	}

	public void test_01_programmatic(FilterSettings filterSettings) throws Exception {
		Smooks smooks = new Smooks();

		smooks.setReaderConfig(new FixedLengthReaderConfigurator(
				"firstname[10].rtrim,lastname[10].trim.capitalize,$ignore$[2],gender[1],age[3],country[3]lower_case")
				.setBinding(
						new FixedLengthBinding("people", HashMap.class, FixedLengthBindingType.MAP)
								.setKeyField("firstname")));

		smooks.setFilterSettings(filterSettings);

		test_01(smooks);
	}


	private void test_01(Smooks smooks) {
		JavaResult result = new JavaResult();
		smooks.filterSource(new StreamSource(getClass().getResourceAsStream("/input-message-01.txt")), result);

		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> people = (Map<String, Map<String, String>>) result.getBean("people");
		Map<String, String> person;

		person = people.get("Maurice");
		assertEquals("Maurice", person.get("firstname"));
		assertEquals("Zeijen", person.get("lastname"));
		assertEquals("M", person.get("gender"));
		assertEquals("026", person.get("age"));
		assertEquals("nld", person.get("country"));

		person = people.get("Sanne");
		assertEquals("Sanne", person.get("firstname"));
		assertEquals("Fries", person.get("lastname"));
		assertEquals("F", person.get("gender"));
		assertEquals("022", person.get("age"));
		assertEquals("nld", person.get("country"));
	}
}
