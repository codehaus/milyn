package org.milyn.javabean.lifecycle;

import org.milyn.container.ExecutionContext;

public class BeanLifecycleSubjectGroup {

	private final BeanLifecycleSubject beginLifecycleNotifier;

	private final BeanLifecycleSubject endLifecycleNotifier;

	private final BeanLifecycleSubject changeLifecycleNotifier;

	private final String beanId;

	private final ExecutionContext executionContext;

	/**
	 * @param beanId
	 */
	public BeanLifecycleSubjectGroup(ExecutionContext executionContext, String beanId) {
		super();
		this.executionContext = executionContext;
		this.beanId = beanId;

		beginLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.BEGIN, beanId);
		endLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.END, beanId);
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
		case END:
			return endLifecycleNotifier;
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
