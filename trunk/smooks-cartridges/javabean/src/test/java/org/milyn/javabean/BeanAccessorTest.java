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

package org.milyn.javabean;

import junit.framework.TestCase;

import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.context.BeanIdIndex;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanId;

/**
 *
 * @author tfennelly
 */
@SuppressWarnings("deprecation")
public class BeanAccessorTest extends TestCase {

	private ExecutionContext executionContext;

	/**
	 * Tests adding a bean
	 */
	public void test_add_bean() {
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();

        getBeanIdIndex().register("bean1");
        getBeanIdIndex().register("bean2");

        assertNull(BeanAccessor.getBean(executionContext, "bean1"));
        assertNull(BeanAccessor.getBean(executionContext, "bean2"));

        BeanAccessor.addBean(executionContext, "bean1", bean1);
        BeanAccessor.addBean(executionContext, "bean2", bean2);

        assertEquals(bean1, BeanAccessor.getBean(executionContext, "bean1"));
        assertEquals(bean2, BeanAccessor.getBean(executionContext, "bean2"));

        assertEquals(2, BeanAccessor.getBeanMap(executionContext).size());
        assertEquals(bean1, BeanAccessor.getBeanMap(executionContext).get("bean1"));
        assertEquals(bean2, BeanAccessor.getBeanMap(executionContext).get("bean2"));
    }

	/**
	 * Test adding and replacing a bean
	 */
	public void test_add_and_replace_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        getBeanIdIndex().register("bean1");

        assertNull(BeanAccessor.getBean(executionContext, "bean1"));

        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertEquals(bean1, BeanAccessor.getBean(executionContext, "bean1"));

        BeanAccessor.addBean(executionContext, "bean1", newBean1);

        assertEquals(newBean1, BeanAccessor.getBean(executionContext, "bean1"));
    }

	/**
	 * Test adding and replacing a bean
	 */
	public void test_change_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        getBeanIdIndex().register("bean1");
        getBeanIdIndex().register("notExisting");

        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertEquals(bean1, BeanAccessor.getBean(executionContext, "bean1"));

        BeanAccessor.changeBean(executionContext, "bean1", newBean1);

        assertEquals(newBean1, BeanAccessor.getBean(executionContext, "bean1"));

        boolean fired = false;

        try {
        	BeanAccessor.changeBean(executionContext, "notExisting", new Object());
        } catch (IllegalStateException e) {
        	fired = true;
		}
        assertTrue(fired);
    }


	/**
	 * Test adding and replacing a bean
	 */
	public void test_lifecycle_associates() {
        Object parent = new MyGoodBean();
        Object child = new MyGoodBean();
        Object child2 = new MyGoodBean();
        Object childChild = new MyGoodBean();

        BeanId parentId = getBeanIdIndex().register("parent");
        BeanId child1Id = getBeanIdIndex().register("child");
        BeanId child2Id = getBeanIdIndex().register("child2");
        BeanId child3Id = getBeanIdIndex().register("childChild");

        // check single level association
        BeanAccessor.addBean(executionContext, "parent", parent);
        BeanAccessor.addBean(executionContext, "child", child);
        BeanAccessor.associateLifecycles(executionContext, "parent", "child");

        assertEquals(parent, BeanAccessor.getBean(executionContext, "parent"));
        assertEquals(child, BeanAccessor.getBean(executionContext, "child"));

        // Mark all beans as being "out of context"...
        markBeanContextEnd(parentId, child1Id, child2Id, child3Id);

        BeanAccessor.addBean(executionContext, "parent", parent);

        assertEquals(parent, BeanAccessor.getBean(executionContext, "parent"));
        assertNull(BeanAccessor.getBean(executionContext, "child"));

        BeanAccessor.addBean(executionContext, "child", child);
        BeanAccessor.associateLifecycles(executionContext, "parent", "child");

        BeanAccessor.addBean(executionContext, "child2", child2);
        BeanAccessor.associateLifecycles(executionContext, "parent", "child2");

        // Mark all beans as being "out of context"...
        markBeanContextEnd(parentId, child1Id, child2Id, child3Id);

        BeanAccessor.addBean(executionContext, "parent", parent);

        assertEquals(parent, BeanAccessor.getBean(executionContext, "parent"));
        assertNull(BeanAccessor.getBean(executionContext, "child"));
        assertNull(BeanAccessor.getBean(executionContext, "child2"));

        // check full tree association
        BeanAccessor.addBean(executionContext, "child", child);
        BeanAccessor.addBean(executionContext, "childChild", childChild);
        BeanAccessor.associateLifecycles(executionContext, "parent", "child");
        BeanAccessor.associateLifecycles(executionContext, "child", "childChild");

        assertEquals(parent, BeanAccessor.getBean(executionContext, "parent"));
        assertEquals(child, BeanAccessor.getBean(executionContext, "child"));
        assertEquals(childChild, BeanAccessor.getBean(executionContext, "childChild"));

        BeanAccessor.addBean(executionContext, "parent", parent);

        assertEquals(parent, BeanAccessor.getBean(executionContext, "parent"));
        assertNull(BeanAccessor.getBean(executionContext, "child"));
        assertNull(BeanAccessor.getBean(executionContext, "childChild"));

        // check partially tree association
        BeanAccessor.addBean(executionContext, "child", child);
        BeanAccessor.addBean(executionContext, "childChild", childChild);
        BeanAccessor.associateLifecycles(executionContext, "parent", "child");
        BeanAccessor.associateLifecycles(executionContext, "child", "childChild");

        BeanAccessor.addBean(executionContext, "child", child);

        assertEquals(parent, BeanAccessor.getBean(executionContext, "parent"));
        assertEquals(child, BeanAccessor.getBean(executionContext, "child"));
        assertNull(BeanAccessor.getBean(executionContext, "childChild"));
	}

    /**
	 * replace with easy mock framework for more control
	 *
	 * Test adding and replacing a bean
	 */
	public void test_bean_lifecycle_begin_observers_associates() {
        final Object bean1 = new MyGoodBean();
        final Object bean2 = new MyGoodBean();

        getBeanIdIndex().register("bean1");
        getBeanIdIndex().register("bean2");

        MockBeanLifecycleObserver observer = new MockBeanLifecycleObserver();
        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer1", false, observer);

        //Add first time
        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add second time
        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add another bean
        BeanAccessor.addBean(executionContext, "bean2", bean2);

        assertFalse(observer.isFired());

        // The following tests are generic for all types of Lifecycle events

        //register override
        MockBeanLifecycleObserver observer2 = new MockBeanLifecycleObserver();
        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer1", false, observer2);

        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertFalse(observer.isFired());
        assertTrue(observer2.isFired());

        observer2.reset();

        //multi observers
        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer1", false, observer);
        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer2", false, observer2);

        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertTrue(observer.isFired());
        assertTrue(observer2.isFired());

        observer.reset();
        observer2.reset();

        //unregister one
        BeanAccessor.removeBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer2");

        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertTrue(observer.isFired());
        assertFalse(observer2.isFired());

        observer.reset();

        //unregister last
        BeanAccessor.removeBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer1");

        BeanAccessor.addBean(executionContext, "bean1", bean1);

        assertFalse(observer.isFired());
        assertFalse(observer2.isFired());

        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean1", BeanLifecycle.BEGIN, "observer2", false, new BeanLifecycleObserver() {

        	public void onBeanLifecycleEvent(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, Object bean) {
        		assertEquals(executionContext, executionContext);
        		assertEquals(BeanLifecycle.BEGIN, lifecycle);
        		assertEquals("bean1", beanId);
        		assertEquals(bean1, bean);
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

        getBeanIdIndex().register("bean");

        MockBeanLifecycleObserver observerChange = new MockBeanLifecycleObserver();
        MockBeanLifecycleObserver observerBegin= new MockBeanLifecycleObserver();
        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean", BeanLifecycle.CHANGE, "observerChange", false, observerChange);

        //Add first time
        BeanAccessor.addBean(executionContext, "bean", bean);

        assertFalse(observerChange.isFired());

        BeanAccessor.addBeanLifecycleObserver(executionContext, "bean", BeanLifecycle.BEGIN, "observerBegin", false, observerBegin);

        //now do the change
        BeanAccessor.changeBean(executionContext, "bean", bean);

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
	private BeanIdIndex getBeanIdIndex() {
        return executionContext.getContext().getBeanIdIndex();
	}



    public class MockBeanLifecycleObserver implements BeanLifecycleObserver {

        private boolean fired = false;

        public void onBeanLifecycleEvent(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, Object bean) {
    		fired = true;
    	}

        public boolean isFired() {
    		return fired;
    	}

        public void reset() {
    		fired = false;
    	}

    }

    private void markBeanContextEnd(BeanId parentId, BeanId child1Id, BeanId child2Id, BeanId child3Id) {
        BeanContext beanContext = executionContext.getBeanContext();
        beanContext.setBeanInContext(parentId, false);
        beanContext.setBeanInContext(child1Id, false);
        beanContext.setBeanInContext(child2Id, false);
        beanContext.setBeanInContext(child3Id, false);
    }

}
