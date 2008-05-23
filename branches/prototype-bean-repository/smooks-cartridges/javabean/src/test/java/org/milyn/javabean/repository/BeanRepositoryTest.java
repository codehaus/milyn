/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/

package org.milyn.javabean.repository;

import junit.framework.TestCase;

import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.MyGoodBean;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.RepositoryBeanLifecycleEvent;
import org.milyn.javabean.lifecycle.RepositoryBeanLifecycleObserver;

/**
 *
 * @author tfennelly
 */
public class BeanRepositoryTest extends TestCase {

	private ExecutionContext executionContext;
	private ApplicationContext applicationContext;


	/**
	 * Tests adding a bean
	 */
	public void test_add_bean() {
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();

        BeanRepositoryId beanRepositoryId1 = getBeanRepositoryIdList().register("bean1");
        BeanRepositoryId beanRepositoryId2 = getBeanRepositoryIdList().register("bean2");

        BeanRepository beanRepository = getBeanRepository();

        assertEquals(2, beanRepository.getBeanMap().size());

        assertNull(beanRepository.getBean(beanRepositoryId1));
        assertNull(beanRepository.getBean(beanRepositoryId2));

        beanRepository.addBean(beanRepositoryId1, bean1);
        beanRepository.addBean(beanRepositoryId2, bean2);

        assertEquals(bean1, beanRepository.getBean(beanRepositoryId1));
        assertEquals(bean2, beanRepository.getBean(beanRepositoryId2));


        assertEquals(bean1, beanRepository.getBeanMap().get("bean1"));
        assertEquals(bean2, beanRepository.getBeanMap().get("bean2"));
    }




	/**
	 * Test adding and replacing a bean
	 */
	public void test_add_and_overwrite_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanRepositoryId beanRepositoryId1 = getBeanRepositoryIdList().register("bean1");

        BeanRepository beanRepository = getBeanRepository();

        assertNull(beanRepository.getBean(beanRepositoryId1));

        beanRepository.addBean( beanRepositoryId1, bean1);

        assertEquals(bean1, beanRepository.getBean(beanRepositoryId1));

        beanRepository.addBean( beanRepositoryId1, newBean1);

        assertEquals(newBean1, beanRepository.getBean(beanRepositoryId1));
    }

	/**
	 * Test adding and changing a bean
	 */
	public void test_change_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanRepositoryId beanRepositoryId1 = getBeanRepositoryIdList().register("bean1");
        BeanRepositoryId beanRepositoryIdNE = getBeanRepositoryIdList().register("notExisting");


        BeanRepository beanRepository = getBeanRepository();

        beanRepository.addBean(beanRepositoryId1, bean1);

        assertEquals(bean1, beanRepository.getBean(beanRepositoryId1));

        beanRepository.changeBean(beanRepositoryId1, newBean1);

        assertEquals(newBean1, beanRepository.getBean(beanRepositoryId1));

        boolean fired = false;

        try {
        	beanRepository.changeBean(beanRepositoryIdNE, new Object());
        } catch (IllegalStateException e) {
        	fired = true;
		}
        assertTrue("The exception did not fire", fired);
    }


	/**
	 * Test adding and replacing a bean
	 */
	public void test_lifecycle_associates() {
        Object parent = new MyGoodBean();
        Object child = new MyGoodBean();
        Object child2 = new MyGoodBean();
        Object childChild = new MyGoodBean();

        BeanRepositoryId brIdParent = getBeanRepositoryIdList().register("parent");
        BeanRepositoryId brIdChild = getBeanRepositoryIdList().register("child");
        BeanRepositoryId brIdChild2 = getBeanRepositoryIdList().register("child2");
        BeanRepositoryId brIdChildChild = getBeanRepositoryIdList().register("childChild");

        BeanRepository beanRepository = getBeanRepository();

        // check single level association
        beanRepository.addBean(brIdParent, parent);
        beanRepository.addBean(brIdChild, child);
        beanRepository.associateLifecycles(brIdParent, brIdChild);

        assertEquals(parent, beanRepository.getBean(brIdParent));
        assertEquals(child, beanRepository.getBean(brIdChild));

        beanRepository.addBean(brIdParent, parent);

        assertEquals(parent, beanRepository.getBean(brIdParent));
        assertNull(beanRepository.getBean(brIdChild));

        beanRepository.addBean(brIdChild, child);
        beanRepository.associateLifecycles(brIdParent, brIdChild);

        beanRepository.addBean(brIdChild2, child2);
        beanRepository.associateLifecycles(brIdParent, brIdChild2);

        beanRepository.addBean(brIdParent, parent);

        assertEquals(parent, beanRepository.getBean(brIdParent));
        assertNull(beanRepository.getBean(brIdChild));
        assertNull(beanRepository.getBean(brIdChild2));

        // check full tree association
        beanRepository.addBean(brIdChild, child);
        beanRepository.addBean(brIdChildChild, childChild);
        beanRepository.associateLifecycles(brIdParent, brIdChild);
        beanRepository.associateLifecycles(brIdChild, brIdChildChild);

        assertEquals(parent, beanRepository.getBean(brIdParent));
        assertEquals(child, beanRepository.getBean(brIdChild));
        assertEquals(childChild, beanRepository.getBean(brIdChildChild));

        beanRepository.addBean(brIdParent, parent);

        assertEquals(parent, beanRepository.getBean(brIdParent));
        assertNull(beanRepository.getBean(brIdChild));
        assertNull(beanRepository.getBean(brIdChildChild));

        // check partially tree association
        beanRepository.addBean(brIdChild, child);
        beanRepository.addBean(brIdChildChild, childChild);
        beanRepository.associateLifecycles(brIdParent, brIdChild);
        beanRepository.associateLifecycles(brIdChild, brIdChildChild);

        beanRepository.addBean(brIdChild, child);

        assertEquals(parent, beanRepository.getBean(brIdParent));
        assertEquals(child, beanRepository.getBean(brIdChild));
        assertNull(beanRepository.getBean(brIdChildChild));
	}

	/**
	 * replace with easy mock framework for more control
	 *
	 * Test adding and replacing a bean
	 */
	public void _test_bean_lifecycle_begin_observers_associates() {
		final MockExecutionContext request = new MockExecutionContext();
        final Object bean1 = new MyGoodBean();
        final Object bean2 = new MyGoodBean();

        final BeanRepositoryId beanRepositoryId1 = getBeanRepositoryIdList().register("bean1");
        final BeanRepositoryId beanRepositoryId2 = getBeanRepositoryIdList().register("bean2");

        BeanRepository beanRepository = getBeanRepository();


        MockRepositoryBeanLifecycleObserver observer = new MockRepositoryBeanLifecycleObserver();
        beanRepository.addBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer1", false, observer);

        //Add first time
        beanRepository.addBean(beanRepositoryId1, bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add second time
        beanRepository.addBean(beanRepositoryId1, bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add another bean
        beanRepository.addBean(beanRepositoryId2, bean2);

        assertFalse(observer.isFired());

        // The following tests are generic for all types of Lifecycle events

        //register override
        MockRepositoryBeanLifecycleObserver observer2 = new MockRepositoryBeanLifecycleObserver();
        beanRepository.addBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer1", false, observer2);

        beanRepository.addBean(beanRepositoryId1, bean1);

        assertFalse(observer.isFired());
        assertTrue(observer2.isFired());

        observer2.reset();

        //multi observers
        beanRepository.addBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer1", false, observer);
        beanRepository.addBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer2", false, observer2);

        beanRepository.addBean(beanRepositoryId1, bean1);

        assertTrue(observer.isFired());
        assertTrue(observer2.isFired());

        observer.reset();
        observer2.reset();

        //unregister one
        beanRepository.removeBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer2");

        beanRepository.addBean(beanRepositoryId1, bean1);

        assertTrue(observer.isFired());
        assertFalse(observer2.isFired());

        observer.reset();

        //unregister last
        beanRepository.removeBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer1");

        beanRepository.addBean(beanRepositoryId1, bean1);

        assertFalse(observer.isFired());
        assertFalse(observer2.isFired());

        beanRepository.addBeanLifecycleObserver(beanRepositoryId1, BeanLifecycle.BEGIN, "observer2", false, new MockRepositoryBeanLifecycleObserver() {

        	@Override
			public void onBeanLifecycleEvent(RepositoryBeanLifecycleEvent event) {
        		assertEquals(executionContext, event.getExecutionContext());
        		assertEquals(BeanLifecycle.BEGIN, event.getLifecycle());
        		assertEquals(beanRepositoryId1, event.getBeanRepositoryId());
        		assertEquals(bean1, event.getBean());
        	}

        });

	}


	/**
	 * replace with easy mock framework for more control
	 *
	 * Test adding and replacing a bean
	 */
	public void test_bean_lifecycle_change_observers_associates() {
        Object bean = new MyGoodBean();

        BeanRepositoryId beanRepositoryId = getBeanRepositoryIdList().register("bean");

        BeanRepository beanRepository = getBeanRepository();

        MockRepositoryBeanLifecycleObserver observerChange = new MockRepositoryBeanLifecycleObserver();
        MockRepositoryBeanLifecycleObserver observerBegin = new MockRepositoryBeanLifecycleObserver();
        beanRepository.addBeanLifecycleObserver(beanRepositoryId, BeanLifecycle.CHANGE, "observerChange", false, observerChange);

        //Add first time
        beanRepository.addBean(beanRepositoryId, bean);

        assertFalse(observerChange.isFired());

        beanRepository.addBeanLifecycleObserver(beanRepositoryId, BeanLifecycle.BEGIN, "observerBegin", false, observerBegin);

        //now do the change
        beanRepository.changeBean(beanRepositoryId, bean);

        assertTrue(observerChange.isFired());
        assertFalse(observerBegin.isFired());

	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		executionContext = new MockExecutionContext();
		applicationContext = executionContext.getContext();
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

        return beanRepositoryManager.getBeanRepository(executionContext);
	}

	/**
	 * @return
	 */
	private BeanRepositoryManager getRepositoryManager() {
		return BeanRepositoryManager.getInstance(applicationContext);
	}

    public class MockRepositoryBeanLifecycleObserver implements RepositoryBeanLifecycleObserver {

    	private boolean fired = false;

    	public boolean isFired() {
    		return fired;
    	}

    	public void reset() {
    		fired = false;
    	}

		public void onBeanLifecycleEvent(RepositoryBeanLifecycleEvent event) {
			fired = true;
		}
    }

}
