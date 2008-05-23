package org.milyn.javabean.repository;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.BeanAccessor;

public class BeanRepositoryPerformance  extends TestCase {

	private static final Log log = LogFactory.getLog(BeanRepositoryPerformance.class);

	private ExecutionContext executionContext;

	public void test_dummy() {
	}

	public void _test_BeanAccessor_performance() {

		test_BeanAccessor_performance(100, 100, true);
		test_BeanAccessor_performance(1, 100000, false);
		test_BeanAccessor_performance(10, 100000, false);
		test_BeanAccessor_performance(100, 100000, false);
	}

	public void _test_BeanRepository_performance() {

		test_BeanRepository_performance(100, 100, true);
		test_BeanRepository_performance(1, 100000, false);
		test_BeanRepository_performance(10, 100000, false);
		test_BeanRepository_performance(100, 100000, false);
		test_BeanRepository_performance(1000, 100000, false);
	}


	@SuppressWarnings("deprecation")
	private void test_BeanAccessor_performance(int beans, int loops, boolean warmup) {
		if(!warmup) {
			sleep();
		}

		executionContext = new MockExecutionContext();

		ArrayList<String> beanRepositoryIds = new ArrayList<String>();

		for(int i = 0; i < beans; i++) {
			beanRepositoryIds.add(getBeanId(i));
		}

		Object bean = new Object();

		long begin = System.currentTimeMillis();
		for(int l = 0; l < loops; l++) {

			for(String id: beanRepositoryIds) {
				BeanAccessor.addBean(executionContext, id, bean);
			}
			for(String id: beanRepositoryIds) {
				BeanAccessor.getBean(executionContext, id);
			}
		}
		long end  = System.currentTimeMillis();

		if(!warmup) {
			log.info("BeanAccessor performance beans: " + beans + "; loops: " + loops + "; time: " + (end - begin) + "ms");
		}


	}

	public void test_BeanRepository_performance(int beans, int loops, boolean warmup) {
		sleep();

		executionContext = new MockExecutionContext();

		BeanRepositoryIdList beanRepositoryIdList = getBeanRepositoryIdList();

		ArrayList<BeanRepositoryId> beanRepositoryIds = new ArrayList<BeanRepositoryId>();

		for(int i = 0; i < beans; i++) {
			beanRepositoryIds.add(beanRepositoryIdList.register(getBeanId(i)));
		}

		BeanRepositoryManager beanRepositoryManager =  getRepositoryManager();

		Object bean = new Object();

		long begin = System.currentTimeMillis();
		for(int l = 0; l < loops; l++) {

			for(BeanRepositoryId id: beanRepositoryIds) {
				BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);
				beanRepository.addBean(id, bean);
			}
			for(BeanRepositoryId id: beanRepositoryIds) {
				BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);
				beanRepository.getBean(id);
			}
		}
		long end  = System.currentTimeMillis();

		if(!warmup) {
			log.info("BeanRepository performance beans: " + beans + "; loops: " + loops + "; time: " + (end - begin) + "ms");
		}
	}


	private String getBeanId(int i) {
		return "bean" + i;
	}

	/**
	 *
	 */
	private BeanRepositoryIdList getBeanRepositoryIdList() {
		BeanRepositoryManager beanRepositoryManager = getRepositoryManager();

        return beanRepositoryManager.getBeanRepositoryIdList();
	}

	/**
	 *
	 */
	private BeanRepository getBeanRepository() {
		BeanRepositoryManager beanRepositoryManager = getRepositoryManager();

        return BeanRepositoryManager.getBeanRepository(executionContext);
	}

	/**
	 * @return
	 */
	private BeanRepositoryManager getRepositoryManager() {
		return BeanRepositoryManager.getInstance(executionContext.getContext());
	}

	private void sleep() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}

}
