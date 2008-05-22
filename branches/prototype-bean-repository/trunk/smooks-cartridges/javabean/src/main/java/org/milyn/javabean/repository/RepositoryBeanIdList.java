/**
 * 
 */
package org.milyn.javabean.repository;

import java.util.HashMap;
import java.util.Map;

import org.milyn.assertion.AssertArgument;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class RepositoryBeanIdList {

	private boolean frozen = false;
	
	private int index = 0;
	
	private final Map<String, RepositoryBeanId> repositoryBeanIdMap = new HashMap<String, RepositoryBeanId>();
	
	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#register(java.lang.String)
	 */
	public RepositoryBeanId registerRepositoryBeanId(String beanId) {
		AssertArgument.isNotEmpty(beanId, "beanId");
		
		if(frozen) {
			throw new IllegalStateException("The BeanProviderManager is frozen. No new providers can be registered");
		}
		
		if(repositoryBeanIdMap.containsKey(beanId)) {
			throw new IllegalArgumentException("Member with beanId '" + beanId + "' is already registered");
		}
		
		int id = index++;
		
		RepositoryBeanId repositoryBeanId = new RepositoryBeanId(this, id, beanId);
		
		repositoryBeanIdMap.put(beanId, repositoryBeanId);

		return repositoryBeanId;
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#getMember(java.lang.String)
	 */
	public RepositoryBeanId getRepositoryBeanId(String beanId) {
		return repositoryBeanIdMap.get(beanId);
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
