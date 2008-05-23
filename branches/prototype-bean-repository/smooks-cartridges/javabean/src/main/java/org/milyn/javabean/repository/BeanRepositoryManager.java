/**
 *
 */
package org.milyn.javabean.repository;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.FilterResult;
import org.milyn.payload.FilterSource;
import org.milyn.payload.JavaResult;
import org.milyn.payload.JavaSource;

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
	public static BeanRepository getBeanRepository(ExecutionContext executionContext) {
		BeanRepository beanRepository = (BeanRepository) executionContext.getAttribute(BEAN_REPOSITORY_CONTEXT_KEY);

		if(beanRepository == null) {

			beanRepository = getInstance(executionContext.getContext()).createBeanRepository(executionContext);

			executionContext.setAttribute(BEAN_REPOSITORY_CONTEXT_KEY, beanRepository);
		}

		return beanRepository;
	}

	/**
	 * @param executionContext
	 * @return
	 */
	private BeanRepository createBeanRepository(ExecutionContext executionContext) {
		BeanRepository beanRepository;

		Map<String, Object> beanMap = createBeanMap(executionContext);

		beanRepositoryIdList.freeze();

		beanRepository = new BeanRepository(executionContext, beanRepositoryIdList, beanMap);


		return beanRepository;
	}


	/**
	 * @param executionContext
	 * @return
	 */
	private Map<String, Object> createBeanMap(ExecutionContext executionContext) {
		Result result = FilterResult.getResult(executionContext);
		Source source = FilterSource.getSource(executionContext);
		Map<String, Object> beanMap = null;

		if(result instanceof JavaResult) {
		    JavaResult javaResult = (JavaResult) result;
		    beanMap = javaResult.getResultMap();
		}

		if(source instanceof JavaSource) {
		    JavaSource javaSource = (JavaSource) source;
		    Map<String, Object> sourceBeans = javaSource.getBeans();

		    if(sourceBeans != null) {
		        if(beanMap != null) {
		            beanMap.putAll(sourceBeans);
		        } else {
		            beanMap = sourceBeans;
		        }
		    }
		}

		if(beanMap == null) {
			beanMap = new HashMap<String, Object>();
		} else {

			for(String beanId : beanMap.keySet()) {

				if(!beanRepositoryIdList.containsRepositoryBeanId(beanId)) {
					beanRepositoryIdList.register(beanId);
				}

	        }

		}
		return beanMap;
	}

}
