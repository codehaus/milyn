package org.milyn.javabean.lifecycle;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.RepositoryBeanId;
import org.milyn.javabean.repository.BeanRepositoryManager;

public class BeanLifecycleSubjectGroup {

	private final BeanLifecycleSubject beginLifecycleNotifier;

	private final BeanLifecycleSubject changeLifecycleNotifier;

	private final RepositoryBeanId repositoryBeanId;

	private final ExecutionContext executionContext;

	/**
	 * @param beanId
	 */
	public BeanLifecycleSubjectGroup(ExecutionContext executionContext, RepositoryBeanId repositoryBeanId) {
		AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNull(repositoryBeanId, "repositoryBeanId");
    	
		this.executionContext = executionContext;
		this.repositoryBeanId = repositoryBeanId;

		beginLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.BEGIN, repositoryBeanId);
		changeLifecycleNotifier = new BeanLifecycleSubject(executionContext, BeanLifecycle.CHANGE, repositoryBeanId);
	}
	
	/**
	 * @param beanId
	 * @deprecated Use the constructor 
	 */
	@Deprecated
	public BeanLifecycleSubjectGroup(ExecutionContext executionContext, String beanId) {
		this(executionContext, getBeanProvider(executionContext, beanId));
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
	 * @deprecated Use the {@link #getBeanProvider()} to retrieve the beanId
	 */
    @Deprecated
	public String getBeanId() {
		return repositoryBeanId.getBeanId();
	}
    
    /**
	 * @return the beanId
	 */
	public RepositoryBeanId getBeanProvider() {
		return repositoryBeanId;
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
	private static RepositoryBeanId getBeanProvider(ExecutionContext executionContext, String beanId) {
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		
		return BeanRepositoryManager.getInstance(executionContext.getContext()).getRepositoryBeanIdList().getRepositoryBeanId(beanId);
	}
}
