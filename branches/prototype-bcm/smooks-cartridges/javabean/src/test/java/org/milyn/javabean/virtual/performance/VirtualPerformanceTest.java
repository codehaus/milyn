package org.milyn.javabean.virtual.performance;

import org.apache.commons.logging.Log;

public abstract class VirtualPerformanceTest {


	private long startTime;

	public abstract void put(int num, String[] keys);

	public abstract void get(int num, String[] keys);

	public abstract void directGet(int num, String[] keys);

	public abstract void directPut(int num, String[] keys);

	public void startTime() {
		startTime = System.currentTimeMillis();
	}

	public void publishTime() {
		long time = System.currentTimeMillis() - startTime;

		getLog().info("Time: " + time + "ms");
	}

	public abstract Log getLog();

}
