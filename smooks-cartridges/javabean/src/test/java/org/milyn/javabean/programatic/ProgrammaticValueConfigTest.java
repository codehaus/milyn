package org.milyn.javabean.programatic;

import java.util.Date;

import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.javabean.Value;
import org.milyn.javabean.decoders.BooleanDecoder;
import org.milyn.javabean.decoders.DateDecoder;
import org.milyn.javabean.decoders.IntegerDecoder;
import org.milyn.payload.JavaResult;

import junit.framework.TestCase;

/**
 * Programmatic Binding config test for the Value class.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ProgrammaticValueConfigTest extends TestCase {

	public void test_01() {

		Smooks smooks = new Smooks();

		smooks.addVisitor(new Value("customerName", "customer"));
		smooks.addVisitor(new Value("customerNumber", "customer/@number")
								.setDecoder(new IntegerDecoder()));
		smooks.addVisitor(new Value("privatePerson", "privatePerson")
								.setDecoder(new BooleanDecoder())
								.setDefaultValue("true"));

		JavaResult result = new JavaResult();
        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("../order-01.xml")), result);

        assertEquals("Joe", result.getBean("customerName"));
		assertEquals(123123, result.getBean("customerNumber"));
		assertEquals(Boolean.TRUE, result.getBean("privatePerson"));
	}

}
