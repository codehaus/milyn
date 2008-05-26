/**
 *
 */
package org.milyn.javabean.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.milyn.assertion.AssertArgument;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanIdList {

	private int index = 0;

	private final Map<String, BeanId> beanIdMap = new HashMap<String, BeanId>();

	/**
	 * registers a string based beanId and returns the {@link BeanId} object.
	 * if the beanId is already registered then that one is returned.
	 *
	 * This method is synchronized.
	 *
	 */
	public synchronized BeanId register(String beanIdName) {
		AssertArgument.isNotEmpty(beanIdName, "beanIdName");

		BeanId beanId = beanIdMap.get(beanIdName);
		if(beanId == null) {
			int id = index++;

			beanId = new BeanId(this, id, beanIdName);

			beanIdMap.put(beanIdName, beanId);
		}
		return beanId;
	}

	/**
	 * Returns the BeanId or <code>null</code> if it not registered;
	 *
	 * This method is not synchronized.
	 *
	 */
	public BeanId getBeanId(String beanId) {
		return beanIdMap.get(beanId);
	}

	/**
	 * Returns if the string based bean Id is already registered.
	 *
	 * This method is not synchronized.
	 *
	 */
	public boolean containsBeanId(String beanId) {
		return beanIdMap.containsKey(beanId);
	}

	/**
	 * Returns an unmodifiable map where the key is the
	 * string based beanId and the value is the BeanId.
	 *
	 * This method is not synchronized.
	 *
	 */
	public Map<String, BeanId> getBeanIdMap() {
		return Collections.unmodifiableMap(beanIdMap) ;
	}

	/**
	 * Returns the current size of the map.
	 *
	 */
	public int size() {
		return index;
	}
}
