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
public class BeanRepositoryIdList {

	private boolean frozen = false;

	private int index = 0;

	private final Map<String, BeanRepositoryId> repositoryBeanIdMap = new HashMap<String, BeanRepositoryId>();

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#register(java.lang.String)
	 */
	public BeanRepositoryId register(String beanId) {
		AssertArgument.isNotEmpty(beanId, "beanId");

		if(frozen) {
			throw new IllegalStateException("The BeanProviderManager is frozen. No new bean id's can be registered");
		}

		if(repositoryBeanIdMap.containsKey(beanId)) {
			throw new IllegalArgumentException("Member with beanId '" + beanId + "' is already registered");
		}

		int id = index++;

		BeanRepositoryId beanRepositoryId = new BeanRepositoryId(this, id, beanId);

		repositoryBeanIdMap.put(beanId, beanRepositoryId);

		return beanRepositoryId;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#getMember(java.lang.String)
	 */
	public BeanRepositoryId getRepositoryBeanId(String beanId) {
		return repositoryBeanIdMap.get(beanId);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#getMember(java.lang.String)
	 */
	public boolean containsRepositoryBeanId(String beanId) {
		return repositoryBeanIdMap.containsKey(beanId);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#getMember(java.lang.String)
	 */
	public Map<String, BeanRepositoryId> getRepositoryBeanIdMap() {
		return Collections.unmodifiableMap(repositoryBeanIdMap) ;
	}

	public int size() {
		return index;
	}

	public void freeze() {
		frozen = true;
	}

	public boolean isFrozen() {
		return frozen;
	}
}
