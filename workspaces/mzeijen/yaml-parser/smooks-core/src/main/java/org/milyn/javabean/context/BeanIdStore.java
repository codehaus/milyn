package org.milyn.javabean.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.repository.BeanId;

/**
 * Bean Id Store
 * <p/>
 * Represents a map of BeanId's. Every BeanId has it own unique index. The index
 * is incremental. The index starts with zero.
 * <p/>
 * Once a BeanId is registered it can never be unregistered.
 *
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanIdStore {
	private int index = 0;

	private final Map<String, BeanId> beanIdMap = new HashMap<String, BeanId>();

	/**
	 * registers a beanId name and returns the {@link BeanId} object.
	 * If the beanId name is already registered then belonging BeanId
	 * is returned.
	 * <p>
	 * If you are sure that the BeanId is already registered
	 * then use the {@link #getBeanId(String)} method to retrieve it,
	 * because it is faster.
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
	 * @return The BeanId or <code>null</code> if it is not registered;
	 *
	 */
	public BeanId getBeanId(String beanId) {
		return beanIdMap.get(beanId);
	}

	/**
	 * @return if the bean Id name is already registered.
	 *
	 */
	public boolean containsBeanId(String beanId) {
		return beanIdMap.containsKey(beanId);
	}

	/**
	 * @return An unmodifiable map where the key is the
	 * string based beanId and the value is the BeanId.
	 *
	 */
	public Map<String, BeanId> getBeanIdMap() {
		return Collections.unmodifiableMap(beanIdMap) ;
	}

	/**
	 * @return the current size of the map.
	 *
	 */
	public int size() {
		return index;
	}
}
