package org.milyn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;

import junit.framework.TestCase;

public class MultiLineToStringBuilderTest extends TestCase {

	private static final String NL = System.getProperty("line.separator");

	public void test() {

		ExecutionContext context = new MockExecutionContext();

		context.getBeanContext().addBean("string", "blaat");
		context.getBeanContext().addBean("emptyMap", Collections.emptyMap());
		context.getBeanContext().addBean("emptyList", Collections.emptyList());
		context.getBeanContext().addBean("emptyArray", new String[0]);

		Map<String, String> stringMap = new HashMap<String, String>();
		stringMap.put("v1", "some text");
		stringMap.put("v2", "other text");
		stringMap.put("v3", null);

		context.getBeanContext().addBean("stringMap", stringMap);

		List<Integer> integerList = new ArrayList<Integer>();
		integerList.add(1);
		integerList.add(2);
		integerList.add(null);

		context.getBeanContext().addBean("integerList", integerList);

		context.getBeanContext().addBean("stringArray", new String[] {"a1", "a2", "a3", null});

		Map<String, Object> objectMap = new HashMap<String, Object>();
		objectMap.put("self", objectMap);

		Map<String, Object> object2Map = new HashMap<String, Object>();
		object2Map.put("parent", objectMap);

		objectMap.put("map", object2Map);

		List<Object> list = new ArrayList<Object>();

		list.add(list);
		list.add(objectMap);

		object2Map.put("list", list);

		context.getBeanContext().addBean("objectMap", objectMap);

		context.getAttributes().put("multiline", "hello\nworld");

		String actual = MultiLineToStringBuilder.toString(context);

		String expected =
			"BeanContext : {" + NL +
			"   \"stringMap\" : {" + NL +
			"      \"v1\" : \"some text\"," + NL +
			"      \"v3\" : NULL," + NL +
			"      \"v2\" : \"other text\"" + NL +
			"   }," + NL +
			"   \"string\" : \"blaat\"," + NL +
			"   \"stringArray\" : [" + NL +
			"      \"a1\"," + NL +
			"      \"a2\"," + NL +
			"      \"a3\"," + NL +
			"      NULL" + NL +
			"   ]," + NL +
			"   \"emptyArray\" : []," + NL +
			"   \"integerList\" : [" + NL +
			"      1," + NL +
			"      2," + NL +
			"      NULL" + NL +
			"   ]," + NL +
			"   \"emptyMap\" : {}," + NL +
			"   \"objectMap\" : {" + NL +
			"      \"map\" : {" + NL +
			"         \"parent\" : PARENT-1," + NL +
			"         \"list\" : [" + NL +
			"            THIS," + NL +
			"            PARENT-2" + NL +
			"         ]" + NL +
			"      }," + NL +
			"      \"self\" : THIS" + NL +
			"   }," + NL +
			"   \"emptyList\" : []" + NL +
			"}" + NL +
			NL +
			"Attributes : {" + NL +
			"   \"multiline\" : \"hello" + NL +
			"               world\"" + NL +
			"}";


		assertEquals(expected, actual);
	}

}
