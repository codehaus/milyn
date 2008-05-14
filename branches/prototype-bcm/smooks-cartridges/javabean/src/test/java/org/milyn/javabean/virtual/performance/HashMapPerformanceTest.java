package org.milyn.javabean.virtual.performance;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("unchecked")
public class HashMapPerformanceTest extends VirtualPerformanceTest {

	private static final Log log = LogFactory.getLog(HashMapPerformanceTest.class);

	@Override
	public void directGet(int num, String[] fields) {
		get(num, fields);
	}

	@Override
	public void directPut(int num, String[] fields) {
		put(num, fields);
	}

	@Override
	public void get(int num, String[] fields) {

		startTime();
		for (int i = 0; i < num; i++) {
			Map map = new HashMap();

			for (int j = 0; j < fields.length; j++) {
				map.get(fields[j]);
			}
		}
		publishTime();
	}


	@Override
	public void put(int num, String[] fields) {
		startTime();
		for (int i = 0; i < num; i++) {
			Map map = new HashMap();

			for (int j = 0; j < fields.length; j++) {
				map.put(fields[j], fields[j]);
			}
		}
		publishTime();
	}

	@Override
	public Log getLog() {
		return log;
	}
}
