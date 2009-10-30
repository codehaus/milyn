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

package org.milyn.javabean.context;

import junit.framework.TestCase;
import org.milyn.container.ExecutionContext;
import org.milyn.container.MockExecutionContext;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleEvent;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;

import java.util.HashMap;
import java.util.Map;

/**
 *	@author maurice_zeijen
 */
public class BeanContextTest extends TestCase {

	private ExecutionContext executionContext;

	/**
	 * Tests adding a bean
	 */
	public void test_add_bean() {
        Object bean1 = new MyGoodBean();
        Object bean2 = new MyGoodBean();

        BeanId beanId1 = getBeanIdIndex().register("bean1");
        BeanId beanId2 = getBeanIdIndex().register("bean2");

        BeanContext BeanContext = getBeanContext();

        assertEquals(2, BeanContext.getBeanMap().size());

        assertNull(BeanContext.getBean(beanId1));
        assertNull(BeanContext.getBean(beanId2));

        BeanContext.addBean(beanId1, bean1);
        BeanContext.addBean(beanId2, bean2);

        assertEquals(bean1, BeanContext.getBean(beanId1));
        assertEquals(bean2, BeanContext.getBean(beanId2));


        assertEquals(bean1, BeanContext.getBeanMap().get("bean1"));
        assertEquals(bean2, BeanContext.getBeanMap().get("bean2"));
    }




	/**
	 * Test adding and replacing a bean
	 */
	public void test_add_and_overwrite_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanId beanId1 = getBeanIdIndex().register("bean1");

        BeanContext BeanContext = getBeanContext();

        assertNull(BeanContext.getBean(beanId1));

        BeanContext.addBean( beanId1, bean1);

        assertEquals(bean1, BeanContext.getBean(beanId1));

        BeanContext.addBean( beanId1, newBean1);

        assertEquals(newBean1, BeanContext.getBean(beanId1));
    }

	/**
	 * Test adding and changing a bean
	 */
	public void test_change_bean() {
        Object bean1 = new MyGoodBean();
        Object newBean1 = new MyGoodBean();

        BeanId beanId1 = getBeanIdIndex().register("bean1");
        BeanId beanIdNE = getBeanIdIndex().register("notExisting");


        BeanContext BeanContext = getBeanContext();

        BeanContext.addBean(beanId1, bean1);

        assertEquals(bean1, BeanContext.getBean(beanId1));

        BeanContext.changeBean(beanId1, newBean1);

        assertEquals(newBean1, BeanContext.getBean(beanId1));

        boolean fired = false;

        try {
        	BeanContext.changeBean(beanIdNE, new Object());
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

        BeanId brIdParent = getBeanIdIndex().register("parent");
        BeanId brIdChild = getBeanIdIndex().register("child");
        BeanId brIdChild2 = getBeanIdIndex().register("child2");
        BeanId brIdChildChild = getBeanIdIndex().register("childChild");

        BeanContext BeanContext = getBeanContext();

        // check single level association
        BeanContext.addBean(brIdParent, parent);
        BeanContext.addBean(brIdChild, child);
        BeanContext.associateLifecycles(brIdParent, brIdChild);

        assertEquals(parent, BeanContext.getBean(brIdParent));
        assertEquals(child, BeanContext.getBean(brIdChild));

        // Mark all beans as being "out of context"...
        BeanContext.setBeanInContext(brIdParent, false);
        BeanContext.setBeanInContext(brIdChild, false);

        // When we add a new parent bean old instances should be cleaned along with all associated
        // beans that are no longer in context (that's why we mark them as being out of context above)...
        BeanContext.addBean(brIdParent, parent);

        assertEquals(parent, BeanContext.getBean(brIdParent));
        assertNull(BeanContext.getBean(brIdChild));

        BeanContext.addBean(brIdChild, child);
        BeanContext.associateLifecycles(brIdParent, brIdChild);

        BeanContext.addBean(brIdChild2, child2);
        BeanContext.associateLifecycles(brIdParent, brIdChild2);

        // Mark all beans as being "out of context"...
        BeanContext.setBeanInContext(brIdParent, false);
        BeanContext.setBeanInContext(brIdChild, false);
        BeanContext.setBeanInContext(brIdChild2, false);

        BeanContext.addBean(brIdParent, parent);

        assertEquals(parent, BeanContext.getBean(brIdParent));
        assertNull(BeanContext.getBean(brIdChild));
        assertNull(BeanContext.getBean(brIdChild2));

        // check full tree association
        BeanContext.addBean(brIdChild, child);
        BeanContext.addBean(brIdChildChild, childChild);
        BeanContext.associateLifecycles(brIdParent, brIdChild);
        BeanContext.associateLifecycles(brIdChild, brIdChildChild);

        assertEquals(parent, BeanContext.getBean(brIdParent));
        assertEquals(child, BeanContext.getBean(brIdChild));
        assertEquals(childChild, BeanContext.getBean(brIdChildChild));

        // Mark all beans as being "out of context"...
        BeanContext.setBeanInContext(brIdParent, false);
        BeanContext.setBeanInContext(brIdChild, false);
        BeanContext.setBeanInContext(brIdChild2, false);
        BeanContext.setBeanInContext(brIdChildChild, false);

        BeanContext.addBean(brIdParent, parent);

        assertEquals(parent, BeanContext.getBean(brIdParent));
        assertNull(BeanContext.getBean(brIdChild));
        assertNull(BeanContext.getBean(brIdChildChild));

        // check partially tree association
        BeanContext.addBean(brIdChild, child);
        BeanContext.addBean(brIdChildChild, childChild);
        BeanContext.associateLifecycles(brIdParent, brIdChild);
        BeanContext.associateLifecycles(brIdChild, brIdChildChild);

        BeanContext.addBean(brIdChild, child);

        assertEquals(parent, BeanContext.getBean(brIdParent));
        assertEquals(child, BeanContext.getBean(brIdChild));
        assertNull(BeanContext.getBean(brIdChildChild));
	}

	/**
	 * Test adding and replacing a bean
	 */
	public void _test_bean_lifecycle_begin_observers_associates() {
        final Object bean1 = new MyGoodBean();
        final Object bean2 = new MyGoodBean();

        final BeanId beanId1 = getBeanIdIndex().register("bean1");
        final BeanId beanId2 = getBeanIdIndex().register("bean2");

        BeanContext BeanContext = getBeanContext();


        MockRepositoryBeanLifecycleObserver observer = new MockRepositoryBeanLifecycleObserver();
        BeanContext.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1", false, observer);

        //Add first time
        BeanContext.addBean(beanId1, bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add second time
        BeanContext.addBean(beanId1, bean1);

        assertTrue(observer.isFired());

        observer.reset();

        //Add another bean
        BeanContext.addBean(beanId2, bean2);

        assertFalse(observer.isFired());

        // The following tests are generic for all types of Lifecycle events

        //register override
        MockRepositoryBeanLifecycleObserver observer2 = new MockRepositoryBeanLifecycleObserver();
        BeanContext.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1", false, observer2);

        BeanContext.addBean(beanId1, bean1);

        assertFalse(observer.isFired());
        assertTrue(observer2.isFired());

        observer2.reset();

        //multi observers
        BeanContext.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1", false, observer);
        BeanContext.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer2", false, observer2);

        BeanContext.addBean(beanId1, bean1);

        assertTrue(observer.isFired());
        assertTrue(observer2.isFired());

        observer.reset();
        observer2.reset();

        //unregister one
        BeanContext.removeBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer2");

        BeanContext.addBean(beanId1, bean1);

        assertTrue(observer.isFired());
        assertFalse(observer2.isFired());

        observer.reset();

        //unregister last
        BeanContext.removeBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer1");

        BeanContext.addBean(beanId1, bean1);

        assertFalse(observer.isFired());
        assertFalse(observer2.isFired());

        BeanContext.addBeanLifecycleObserver(beanId1, BeanLifecycle.BEGIN, "observer2", false, new MockRepositoryBeanLifecycleObserver() {

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
	 * Test adding and replacing a bean
	 */
	public void test_bean_lifecycle_change_observers_associates() {
        Object bean = new MyGoodBean();

        BeanId beanId = getBeanIdIndex().register("bean");

        BeanContext BeanContext = getBeanContext();

        MockRepositoryBeanLifecycleObserver observerChange = new MockRepositoryBeanLifecycleObserver();
        MockRepositoryBeanLifecycleObserver observerBegin = new MockRepositoryBeanLifecycleObserver();
        BeanContext.addBeanLifecycleObserver(beanId, BeanLifecycle.CHANGE, "observerChange", false, observerChange);

        //Add first time
        BeanContext.addBean(beanId, bean);

        assertFalse(observerChange.isFired());

        BeanContext.addBeanLifecycleObserver(beanId, BeanLifecycle.BEGIN, "observerBegin", false, observerBegin);

        //now do the change
        BeanContext.changeBean(beanId, bean);

        assertTrue(observerChange.isFired());
        assertFalse(observerBegin.isFired());

	}

	/**
	 * Test adding and replacing a bean
	 */
	public void test_bean_map() {
		Object bean1 = new Object();
		Object bean2 = new Object();
		Object bean3 = new Object();
		Object bean4 = new Object();

		BeanId beanId1 = getBeanIdIndex().register("bean1");

		BeanContext BeanContext = getBeanContext();
		Map<String, Object> beanMap = BeanContext.getBeanMap();

		BeanContext.addBean(beanId1, bean1);

		assertEquals(1, beanMap.size());
		assertEquals(bean1, beanMap.get(beanId1.getName()));

		beanMap.put("bean2", bean2);

		BeanId beanId2 = BeanContext.getBeanId("bean2");

		assertEquals(bean2, BeanContext.getBean(beanId2));
		assertEquals(bean2, beanMap.get(beanId2.getName()));

		assertTrue(beanMap.containsKey("bean2"));
		assertFalse(beanMap.containsKey("x"));

		assertTrue(beanMap.containsValue(bean1));
		assertFalse(beanMap.containsValue(new Object()));

		assertFalse(beanMap.isEmpty());

        // Mark bean as being "out of context" so we can remove it...
        BeanContext.setBeanInContext(beanId1, false);

		beanMap.remove("bean1");

		assertNull(beanMap.get("bean1"));
		assertNull(BeanContext.getBean("bean1"));

		assertEquals(2, beanMap.entrySet().size());
		assertEquals(2, beanMap.keySet().size());
		assertEquals(2, beanMap.values().size());

		Map<String, Object> toPut = new HashMap<String, Object>();
		toPut.put("bean3", bean3);
		toPut.put("bean4", bean4);

		beanMap.putAll(toPut);

		assertEquals(4, beanMap.size());
		assertEquals(bean3, BeanContext.getBean("bean3"));
		assertEquals(bean4, BeanContext.getBean("bean4"));

		beanMap.clear();

		assertNull(BeanContext.getBean("bean1"));
		assertNull(BeanContext.getBean("bean2"));
		assertNull(BeanContext.getBean("bean3"));
		assertNull(BeanContext.getBean("bean4"));
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

	/**
	 *
	 */
	private BeanContext getBeanContext() {
        return executionContext.getBeanContext();
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
