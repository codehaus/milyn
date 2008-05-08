/**
 * 
 */
package org.milyn.javabean.runtime.info;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class MapRuntimeInfo extends ObjectRuntimeInfo {

	private List<String> keys = new ArrayList<String>();

	private boolean optimize = false;
	
	/**
	 * @return the keys
	 */
	public List<String> getKeys() {
		return keys;
	}

	/**
	 * @param keys the keys to set
	 */
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	
	/**
	 * @param keys the keys to set
	 */
	public void addKey(String key) {
		keys.add(key);
	}

	/**
	 * @return the optimized
	 */
	public boolean isOptimize() {
		return optimize;
	}

	/**
	 * @param optimized the optimized to set
	 */
	public void setOptimize(boolean optimized) {
		this.optimize = optimized;
	}
	
}
