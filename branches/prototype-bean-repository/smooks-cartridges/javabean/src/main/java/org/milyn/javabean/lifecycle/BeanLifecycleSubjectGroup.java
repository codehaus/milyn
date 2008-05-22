package org.milyn.javabean.lifecycle;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanRepositoryId;
import org.milyn.javabean.repository.BeanRepositoryManager;

public class BeanLifecycleSubjectGroup {

	private final BeanLifecycleSubject beginLifecycleNotifier;

	private final BeanLifecycleSubject changeLifecycleNotifier;

	private final BeanRepositoryId beanRepositoryId;

	private final ExecutionContext executionContext;

	/**
	 * @param beanId
	 */
	public BeanLifecycleSubjectGroup(ExecutionContext executionContext, BeanRepositoryId beanRepositoryId) {
		AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
    	
		this.executionContext = executionContext;
		this.beanRepositoryId = beanRepositoryId;

		beginLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.BEGIN, beanRepositoryId);
		changeLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.CHANGE, beanRepositoryId);
	}
	
	/**
	 * @param beanId
	 * @deprecated Use the constructor 
	 */
	@Deprecated
	public BeanLifecycleSubjectGroup(ExecutionContext executionContext, String beanId) {
		this(executionContext, getBeanRepositoryId(executionContext, beanId));
	}

	/**
	 * @deprecated Use the {@link #addObserver(BeanLifecycle, String, boolean, RepositoryBeanLifecycleObserver)}
	 */
	@Deprecated
	public void addObserver(BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanLifecycleObserver observer) {
		getBeanObserverNotifier(lifecycle).addObserver(observerId, notifyOnce, observer);

    }
	
	public void addObserver(BeanLifecycle lifecycle, String observerId, boolean notifyOnce, RepositoryBeanLifecycleObserver observer) {
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
	 * @deprecated Use the {@link #getBeanRepositoryId()} to retrieve the beanId
	 */
    @Deprecated
	public String getBeanId() {
		return beanRepositoryId.getBeanId();
	}
    
    /**
	 * @return the beanId
	 */
	public BeanRepositoryId getBeanRepositoryId() {
		return beanRepositoryId;
	}
    

	/**
	 * @return the executionContext
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}


	/**
	 * @param executionContext
	 * @param beanId
	 * @return
	 */
	private static BeanRepositoryId getBeanRepositoryId(ExecutionContext executionContext, String beanId) {
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		
		return BeanRepositoryManager.getInstance(executionContext.getContext()).getBeanRepositoryIdList().getRepositoryBeanId(beanId);
	}
}
