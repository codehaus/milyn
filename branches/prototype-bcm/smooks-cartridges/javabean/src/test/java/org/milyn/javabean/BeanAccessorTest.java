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

import java.util.List;

import junit.framework.TestCase;

import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleObserver;

/**
 *
 * @author tfennelly
 */
@SuppressWarnings("deprecation")
public class BeanAccessorTest extends TestCase {

	/**
	 * Tests adding a bean
	 */
	public void test_add_bean() {
		MockExecutionContext request = new MockExecutionContext();
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();

        assertNull(BeanAccessor.getBean(request, "bean1"));
        assertNull(BeanAccessor.getBean(request, "bean2"));

        BeanAccessor.addBean(request, "bean1", bean1);
        BeanAccessor.addBean(request, "bean2", bean2);

        assertEquals(bean1, BeanAccessor.getBean(request, "bean1"));
        assertEquals(bean2, BeanAccessor.getBean(request, "bean2"));

        assertEquals(2, BeanAccessor.getBeanMap(request).size());
        assertEquals(bean1, BeanAccessor.getBeanMap(request).get("bean1"));
        assertEquals(bean2, BeanAccessor.getBeanMap(request).get("bean2"));
    }

	/**
	 * Test adding and replacing a bean
	 */
	public void test_add_and_replace_bean() {
		MockExecutionContext request = new MockExecutionContext();
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        assertNull(BeanAccessor.getBean(request, "bean1"));

        BeanAccessor.addBean(request, "bean1", bean1);

        assertEquals(bean1, BeanAccessor.getBean(request, "bean1"));

        BeanAccessor.addBean(request, "bean1", newBean1);

        assertEquals(newBean1, BeanAccessor.getBean(request, "bean1"));
    }

	/**
	 * Test adding and replacing a bean
	 */
	public void test_change_bean() {
		MockExecutionContext request = new MockExecutionContext();
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanAccessor.addBean(request, "bean1", bean1);

        assertEquals(bean1, BeanAccessor.getBean(request, "bean1"));

        BeanAccessor.changeBean(request, "bean1", newBean1);

        assertEquals(newBean1, BeanAccessor.getBean(request, "bean1"));

        boolean fired = false;

        try {
        	BeanAccessor.changeBean(request, "notExisting", new Object());
        } catch (IllegalStateException e) {
        	fired = true;
		}
        assertTrue(fired);
    }


	/**
	 * Test adding and replacing a bean
	 */
	public void test_lifecycle_associates() {
		MockExecutionContext request = new MockExecutionContext();
        Object parent = new MyGoodBean();
        Object child = new MyGoodBean();
        Object child2 = new MyGoodBean();
        Object childChild = new MyGoodBean();

        // check single level association
        BeanAccessor.addBean(request, "parent", parent);
        BeanAccessor.addBean(request, "child", child);
        BeanAccessor.associateLifecycles(request, "parent", "child");

        assertEquals(parent, BeanAccessor.getBean(request, "parent"));
        assertEquals(child, BeanAccessor.getBean(request, "child"));

        BeanAccessor.addBean(request, "parent", parent);

        assertEquals(parent, BeanAccessor.getBean(request, "parent"));
        assertNull(BeanAccessor.getBean(request, "child"));

        BeanAccessor.addBean(request, "child", child);
        BeanAccessor.associateLifecycles(request, "parent", "child");

        BeanAccessor.addBean(request, "child2", child2);
        BeanAccessor.associateLifecycles(request, "parent", "child2");

        BeanAccessor.addBean(request, "parent", parent);

        assertEquals(parent, BeanAccessor.getBean(request, "parent"));
        assertNull(BeanAccessor.getBean(request, "child"));
        assertNull(BeanAccessor.getBean(request, "child2"));

        // check full tree association
        BeanAccessor.addBean(request, "child", child);
        BeanAccessor.addBean(request, "childChild", childChild);
        BeanAccessor.associateLifecycles(request, "parent", "child");
        BeanAccessor.associateLifecycles(request, "child", "childChild");

        assertEquals(parent, BeanAccessor.getBean(request, "parent"));
        assertEquals(child, BeanAccessor.getBean(request, "child"));
        assertEquals(childChild, BeanAccessor.getBean(request, "childChild"));

        BeanAccessor.addBean(request, "parent", parent);

        assertEquals(parent, BeanAccessor.getBean(request, "parent"));
        assertNull(BeanAccessor.getBean(request, "child"));
        assertNull(BeanAccessor.getBean(request, "childChild"));

        // check partially tree association
        BeanAccessor.addBean(request, "child", child);
        BeanAccessor.addBean(request, "childChild", childChild);
        BeanAccessor.associateLifecycles(request, "parent", "child");
        BeanAccessor.associateLifecycles(request, "child", "childChild");

        BeanAccessor.addBean(request, "child", child);

        assertEquals(parent, BeanAccessor.getBean(request, "parent"));
        assertEquals(child, BeanAccessor.getBean(request, "child"));
        assertNull(BeanAccessor.getBean(request, "childChild"));
	}

	/**
	 * replace with easy mock framework for more control
	 *
	 * Test adding and replacing a bean
	 */
	public void test_bean_lifecycle_begin_observers_associates() {
		final MockExecutionContext request = new MockExecutionContext();
        final Object bean1 = new MyGoodBean();
        final Object bean2 = new MyGoodBean();

        MockBeanLifecycleObserver observer = new MockBeanLifecycleObserver();
        BeanAccessor.addBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer1", false, observer);

        //Add first time
        BeanAccessor.addBean(request, "bean1", bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add second time
        BeanAccessor.addBean(request, "bean1", bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add another bean
        BeanAccessor.addBean(request, "bean2", bean2);

        assertFalse(observer.isFired());

        // The following tests are generic for all types of Lifecycle events

        //register override
        MockBeanLifecycleObserver observer2 = new MockBeanLifecycleObserver();
        BeanAccessor.addBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer1", false, observer2);

        BeanAccessor.addBean(request, "bean1", bean1);

        assertFalse(observer.isFired());
        assertTrue(observer2.isFired());

        observer2.reset();

        //multi observers
        BeanAccessor.addBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer1", false, observer);
        BeanAccessor.addBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer2", false, observer2);

        BeanAccessor.addBean(request, "bean1", bean1);

        assertTrue(observer.isFired());
        assertTrue(observer2.isFired());

        observer.reset();
        observer2.reset();

        //unregister one
        BeanAccessor.removeBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer2");

        BeanAccessor.addBean(request, "bean1", bean1);

        assertTrue(observer.isFired());
        assertFalse(observer2.isFired());

        observer.reset();

        //unregister last
        BeanAccessor.removeBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer1");

        BeanAccessor.addBean(request, "bean1", bean1);

        assertFalse(observer.isFired());
        assertFalse(observer2.isFired());

        BeanAccessor.addBeanLifecycleObserver(request, "bean1", BeanLifecycle.BEGIN, "observer2", false, new BeanLifecycleObserver() {

        	public void onBeanLifecycleEvent(ExecutionContext executionContext, BeanLifecycle lifecycle, String beanId, Object bean) {
        		assertEquals(request, executionContext);
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
		MockExecutionContext request = new MockExecutionContext();
        Object bean = new MyGoodBean();

        MockBeanLifecycleObserver observerChange = new MockBeanLifecycleObserver();
        MockBeanLifecycleObserver observerBegin= new MockBeanLifecycleObserver();
        BeanAccessor.addBeanLifecycleObserver(request, "bean", BeanLifecycle.CHANGE, "observerChange", false, observerChange);

        //Add first time
        BeanAccessor.addBean(request, "bean", bean);

        assertFalse(observerChange.isFired());

        BeanAccessor.addBeanLifecycleObserver(request, "bean", BeanLifecycle.BEGIN, "observerBegin", false, observerBegin);

        //now do the change
        BeanAccessor.changeBean(request, "bean", bean);

        assertTrue(observerChange.isFired());
        assertFalse(observerBegin.isFired());

	}

    /**
     * Tests deprecated methods with the addToList parameter
     */
    @SuppressWarnings("deprecation")
	public void test_addToList_backward_compatible() {
        MockExecutionContext request = new MockExecutionContext();
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();

        assertNull(BeanAccessor.getBean("bean1", request));

        // Test that we get an error if calling addBean twice with different 'addToList' flags...
        BeanAccessor.addBean("blah", bean1, request, false);
        try {
            BeanAccessor.addBean("blah", bean1, request, true);
        } catch(IllegalArgumentException e) {
            assertEquals("bean [blah] already exists on request and is not a List.  Arg 'addToList' set to true - this is inconsistent!!", e.getMessage());
        }
        BeanAccessor.addBean("blahx", bean1, request, true);


        // Add a non-List bean...
        BeanAccessor.addBean("a", bean1, request, false);
        assertEquals(bean1, BeanAccessor.getBean("a", request));
        BeanAccessor.addBean("a", bean2, request, false);
        assertEquals(bean2, BeanAccessor.getBean("a", request));
        assertEquals(bean2, BeanAccessor.getBeanMap(request).get("a"));

        // Add a bean to a bean list...
        BeanAccessor.addBean("b", bean1, request, true);
        assertEquals(bean1, BeanAccessor.getBean("b", request));
        BeanAccessor.addBean("b", bean2, request, true);
        assertEquals(bean2, BeanAccessor.getBean("b", request));
        List<?> list = (List<?>)BeanAccessor.getBeanMap(request).get("bList");
        assertEquals(2, list.size());
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

}
