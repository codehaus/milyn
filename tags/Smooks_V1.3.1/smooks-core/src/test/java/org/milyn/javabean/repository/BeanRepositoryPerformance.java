package org.milyn.javabean.repository;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.context.BeanIdStore;

@SuppressWarnings("deprecation")
public class BeanRepositoryPerformance {

	private static final Log log = LogFactory.getLog(BeanRepositoryPerformance.class);

	private ExecutionContext executionContext;

	public static void main(String[] args) {


		//new BeanRepositoryPerformance().test_BeanRepository_performance();

		//new BeanRepositoryPerformance().test_BeanContext_performance();
		new BeanRepositoryPerformance().test_old_BeanAccessor_performance();
	}

	public void _test_dummy() {
	}

	public void test_old_BeanAccessor_performance() {

		test_old_BeanAccessor_performance(100, 100, true);
		test_old_BeanAccessor_performance(1, 100000, false);
		test_old_BeanAccessor_performance(10, 100000, false);
		test_old_BeanAccessor_performance(100, 100000, false);
	}

	public void test_BeanRepository_performance() {

		test_BeanRepository_performance(100, 100, true);
		test_BeanRepository_performance(1, 100000, false);
		test_BeanRepository_performance(10, 100000, false);
		test_BeanRepository_performance(100, 100000, false);

	}

	public void test_BeanContext_performance() {

		test_BeanContext_performance(100, 100, true);
		test_BeanContext_performance(1, 100000, false);
		test_BeanContext_performance(10, 100000, false);
		test_BeanContext_performance(100, 100000, false);

	}

	private void test_old_BeanAccessor_performance(int beans, int loops, boolean warmup) {
		if(!warmup) {
			sleep();
		}

		executionContext = new MockExecutionContext();

		ArrayList<String> beanIds = new ArrayList<String>();

		for(int i = 0; i < beans; i++) {
			beanIds.add(getBeanId(i));
		}

		Object bean = new Object();

		long begin = System.currentTimeMillis();
		for(int l = 0; l < loops; l++) {

			for(String id: beanIds) {
				OldBeanAccessor.addBean(executionContext, id, bean);
			}
			for(String id: beanIds) {
				OldBeanAccessor.getBean(executionContext, id);
			}
		}
		long end  = System.currentTimeMillis();

		if(!warmup) {
			log.error("Old BeanAccessor performance beans: " + beans + "; loops: " + loops + "; time: " + (end - begin) + "ms");
		}


	}

	public void test_BeanRepository_performance(int beans, int loops, boolean warmup) {
		sleep();

		executionContext = new MockExecutionContext();

		BeanIdRegister beanIdRegister = getBeanIdRegister();

		ArrayList<BeanId> beanIds = new ArrayList<BeanId>();

		for(int i = 0; i < beans; i++) {
			beanIds.add(beanIdRegister.register(getBeanId(i)));
		}

		Object bean = new Object();

		long begin = System.currentTimeMillis();
		for(int l = 0; l < loops; l++) {

			for(BeanId id: beanIds) {
				BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);
				beanRepository.addBean(id, bean);
			}
			for(BeanId id: beanIds) {
				BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);
				beanRepository.getBean(id);
			}
		}
		long end  = System.currentTimeMillis();

		if(!warmup) {
			log.error("BeanRepository performance beans: " + beans + "; loops: " + loops + "; time: " + (end - begin) + "ms");
		}
	}

	public void test_BeanContext_performance(int beans, int loops, boolean warmup) {
		sleep();

		executionContext = new MockExecutionContext();

		BeanIdStore beanIdStore = executionContext.getContext().getBeanIdStore();

		ArrayList<BeanId> beanIds = new ArrayList<BeanId>();

		for(int i = 0; i < beans; i++) {
			beanIds.add(beanIdStore.register(getBeanId(i)));
		}

		Object bean = new Object();

		long begin = System.currentTimeMillis();
		for(int l = 0; l < loops; l++) {

			for(BeanId id: beanIds) {
				BeanContext beanContext = executionContext.getBeanContext();
				beanContext.addBean(id, bean);
			}
			for(BeanId id: beanIds) {
				BeanContext beanContext = executionContext.getBeanContext();
				beanContext.getBean(id);
			}
		}
		long end  = System.currentTimeMillis();

		if(!warmup) {
			log.error("BeanRepository performance beans: " + beans + "; loops: " + loops + "; time: " + (end - begin) + "ms");
		}
	}

	private String getBeanId(int i) {
		return "bean" + i;
	}

	/**
	 *
	 */
	private BeanIdRegister getBeanIdRegister() {
		BeanRepositoryManager beanRepositoryManager = getRepositoryManager();

        return beanRepositoryManager.getBeanIdRegister();
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
