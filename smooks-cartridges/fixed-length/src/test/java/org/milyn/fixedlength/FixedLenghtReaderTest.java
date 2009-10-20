package org.milyn.fixedlength;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.milyn.FilterSettings;
import org.milyn.Smooks;

import org.milyn.payload.JavaResult;


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
				"firstname[10].trim,lastname[10].trim.upper_case,$ignore$[2],gender[1],age[3],country[3]lower_case")
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
		assertEquals("ZEIJEN", person.get("lastname"));
		assertEquals("M", person.get("gender"));
		assertEquals("026", person.get("age"));
		assertEquals("nld", person.get("country"));

		person = people.get("Sanne");
		assertEquals("Sanne", person.get("firstname"));
		assertEquals("FRIES", person.get("lastname"));
		assertEquals("F", person.get("gender"));
		assertEquals("022", person.get("age"));
		assertEquals("nld", person.get("country"));
	}
}
