/**
 * 
 */
package org.milyn.javabean.repository;

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanRepositoryManager {
	
	private static final String CONTEXT_KEY = BeanRepositoryManager.class.getName() + "#CONTEXT_KEY";
	
	private static final String BEAN_REPOSITORY_CONTEXT_KEY = BeanRepository.class.getName() + "#CONTEXT_KEY";
	
	private final BeanRepositoryIdList beanRepositoryIdList = new BeanRepositoryIdList();	
	
	public static BeanRepositoryManager getInstance(ApplicationContext applicationContext) {
		BeanRepositoryManager beanRepositoryManager = (BeanRepositoryManager) applicationContext.getAttribute(CONTEXT_KEY);
		
		if(beanRepositoryManager == null) {
			
			beanRepositoryManager = new BeanRepositoryManager();
			
			applicationContext.setAttribute(CONTEXT_KEY, beanRepositoryManager);
			
		}
		
		return beanRepositoryManager;
		
	}
	
	private BeanRepositoryManager() {
	}
	
	/**
	 * @return the beanProviderManager
	 */
	public BeanRepositoryIdList getBeanRepositoryIdList() {
		return beanRepositoryIdList;
	}
	
	/**
	 * @return the beanProviderManager
	 */
	public BeanRepository getBeanRepository(ExecutionContext executionContext) {
		BeanRepository beanRepository = (BeanRepository) executionContext.getAttribute(BEAN_REPOSITORY_CONTEXT_KEY);
		
		if(beanRepository == null) {
			
			beanRepositoryIdList.freeze();
			
			beanRepository = new BeanRepository(executionContext, beanRepositoryIdList.size());
			
			executionContext.setAttribute(BEAN_REPOSITORY_CONTEXT_KEY, beanRepository);
		}
		
		
		return beanRepository;
	}

}
