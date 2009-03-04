package org.milyn.javabean.lifecycle;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;

public class BeanLifecycleSubjectGroup {

	private final BeanLifecycleSubject beginLifecycleNotifier;

	private final BeanLifecycleSubject changeLifecycleNotifier;

	private final String beanId;

	private final ExecutionContext executionContext;

	/**
	 * @param beanId
	 */
	public BeanLifecycleSubjectGroup(ExecutionContext executionContext, String beanId) {
		AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	
		this.executionContext = executionContext;
		this.beanId = beanId;

		beginLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.BEGIN, beanId);
		changeLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.CHANGE, beanId);
	}

	public void addObserver(BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanLifecycleObserver observer) {
		getBeanObserverNotifier(lifecycle).addObserver(observerId, notifyOnce, observer);

    }

    public void removeObserver(BeanLifecycle lifecycle, String observerId) {
    	getBeanObserverNotifier(lifecycle).removeObserver(observerId);

    }

    public void notifyObservers(BeanLifecycle lifecycle, Object bean) {
    	getBeanObserverNotifier(lifecycle).notifyObservers(bean);

    }

    protected BeanLifecycleSubject getBeanObserverNotifier(BeanLifecycle lifecycle) {

    	switch (lifecycle) {
		case BEGIN:
			return beginLifecycleNotifier;
		case CHANGE:
			return changeLifecycleNotifier;

		default:
			throw new IllegalArgumentException("Unknown BeanLifecycle '" + lifecycle + "'");
		}

    }

	/**
	 * @return the beanId
	 */
	public String getBeanId() {
		return beanId;
	}

	/**
	 * @return the executionContext
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}



}