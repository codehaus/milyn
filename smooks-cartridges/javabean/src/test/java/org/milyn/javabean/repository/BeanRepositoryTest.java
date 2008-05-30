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

import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.MyGoodBean;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;

/**
 *
 * @author tfennelly
 */
public class BeanRepositoryTest extends TestCase {

	private ExecutionContext executionContext;


	/**
	 * Tests adding a bean
	 */
	public void test_add_bean() {
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();

        BeanId beanId1 = getBeanIdList().register("bean1");
        BeanId beanId2 = getBeanIdList().register("bean2");

        BeanRepository beanRepository = getBeanRepository();

        assertEquals(2, beanRepository.getBeanMap().size());

        assertNull(beanRepository.getBean(beanId1));
        assertNull(beanRepository.getBean(beanId2));

        beanRepository.addBean(beanId1, bean1);
        beanRepository.addBean(beanId2, bean2);

        assertEquals(bean1, beanRepository.getBean(beanId1));
        assertEquals(bean2, beanRepository.getBean(beanId2));


        assertEquals(bean1, beanRepository.getBeanMap().get("bean1"));
        assertEquals(bean2, beanRepository.getBeanMap().get("bean2"));
    }




	/**
	 * Test adding and replacing a bean
	 */
	public void test_add_and_overwrite_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanId beanId1 = getBeanIdList().register("bean1");

        BeanRepository beanRepository = getBeanRepository();

        assertNull(beanRepository.getBean(beanId1));

        beanRepository.addBean( beanId1, bean1);

        assertEquals(bean1, beanRepository.getBean(beanId1));

        beanRepository.addBean( beanId1, newBean1);

        assertEquals(newBean1, beanRepository.getBean(beanId1));
    }

	/**
	 * Test adding and changing a bean
	 */
	public void test_change_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanId beanId1 = getBeanIdList().register("bean1");
        BeanId beanIdNE = getBeanIdList().register("notExisting");


        BeanRepository beanRepository = getBeanRepository();

        beanRepository.addBean(beanId1, bean1);

        assertEquals(bean1, beanRepository.getBean(beanId1));

        beanRepository.changeBean(beanId1, newBean1);

        assertEquals(newBean1, beanRepository.getBean(beanId1));

        boolean fired = false;

        try {
        	beanRepository.changeBean(beanIdNE, new Object());
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

        BeanId brIdParent = getBeanIdList().register("parent");
        BeanId brIdChild = getBeanIdList().register("child");
        BeanId brIdChild2 = getBeanIdList().register("child2");
        BeanId brIdChildChild = getBeanIdList().register("childChild");

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

        final BeanId beanId1 = getBeanIdList().register("bean1");
        final BeanId beanId2 = getBeanIdList().register("bean2");

        BeanRepository beanRepository = getBeanRepository();


        MockRepositoryBeanLifecycleObserver observer = new MockRepositoryBeanLifecycleObserver();
        beanRepository.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1", false, observer);

        //Add first time
        beanRepository.addBean(beanId1, bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add second time
        beanRepository.addBean(beanId1, bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add another bean
        beanRepository.addBean(beanId2, bean2);

        assertFalse(observer.isFired());

        // The following tests are generic for all types of Lifecycle events

        //register override
        MockRepositoryBeanLifecycleObserver observer2 = new MockRepositoryBeanLifecycleObserver();
        beanRepository.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1", false, observer2);

        beanRepository.addBean(beanId1, bean1);

        assertFalse(observer.isFired());
        assertTrue(observer2.isFired());

        observer2.reset();

        //multi observers
        beanRepository.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1", false, observer);
        beanRepository.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer2", false, observer2);

        beanRepository.addBean(beanId1, bean1);

        assertTrue(observer.isFired());
        assertTrue(observer2.isFired());

        observer.reset();
        observer2.reset();

        //unregister one
        beanRepository.removeBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer2");

        beanRepository.addBean(beanId1, bean1);

        assertTrue(observer.isFired());
        assertFalse(observer2.isFired());

        observer.reset();

        //unregister last
        beanRepository.removeBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1");

        beanRepository.addBean(beanId1, bean1);

        assertFalse(observer.isFired());
        assertFalse(observer2.isFired());

        beanRepository.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer2", false, new MockRepositoryBeanLifecycleObserver() {

        	@Override
			public void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event) {
        		assertEquals(executionContext, event.getExecutionContext());
        		assertEquals(BeanLifecycle.BEGIN, event.getLifecycle());
        		assertEquals(beanId1, event.getBeanId());
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

        BeanId beanId = getBeanIdList().register("bean");

        BeanRepository beanRepository = getBeanRepository();

        MockRepositoryBeanLifecycleObserver observerChange = new MockRepositoryBeanLifecycleObserver();
        MockRepositoryBeanLifecycleObserver observerBegin = new MockRepositoryBeanLifecycleObserver();
        beanRepository.addBeanLifecycleObserver(beanId, BeanLifecycle.CHANGE, "observerChange", false, observerChange);

        //Add first time
        beanRepository.addBean(beanId, bean);

        assertFalse(observerChange.isFired());

        beanRepository.addBeanLifecycleObserver(beanId, BeanLifecycle.BEGIN, "observerBegin", false, observerBegin);

        //now do the change
        beanRepository.changeBean(beanId, bean);

        assertTrue(observerChange.isFired());
        assertFalse(observerBegin.isFired());

	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		executionContext = new MockExecutionContext();
	}


	/**
	 *
	 */
	private BeanIdList getBeanIdList() {
		BeanRepositoryManager beanRepositoryManager = getRepositoryManager();

        return beanRepositoryManager.getBeanIdList();
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

    public class MockRepositoryBeanLifecycleObserver implements BeanRepositoryLifecycleObserver {

    	private boolean fired = false;

    	public boolean isFired() {
    		return fired;
    	}

    	public void reset() {
    		fired = false;
    	}

		public void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event) {
			fired = true;
		}
    }

}
