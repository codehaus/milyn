package org.milyn.javabean.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.repository.BeanRepositoryId;
import org.milyn.javabean.repository.BeanRepositoryManager;

public class BeanLifecycleSubject {

    private final ArrayList<ObserverContext> observers = new ArrayList<ObserverContext>();
    
    private final BeanLifecycle beanLifecycle;

    private final BeanRepositoryId beanRepositoryId;

    private final ExecutionContext executionContext;

    public BeanLifecycleSubject(ExecutionContext executionContext, BeanLifecycle beanLifecycle, BeanRepositoryId beanRepositoryId) {
    	AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNull(beanLifecycle, "beanLifecycle");
    	AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
    	
    	this.beanLifecycle = beanLifecycle;
    	this.executionContext = executionContext;
		this.beanRepositoryId = beanRepositoryId;
	}
    
    /**
     * 
     * @param executionContext
     * @param beanLifecycle
     * @param beanId
     * @deprecated Use the {@link #BeanLifecycleSubject(ExecutionContext, BeanLifecycle, BeanRepositoryId)} constructor
     */
    @Deprecated
    public BeanLifecycleSubject(ExecutionContext executionContext, BeanLifecycle beanLifecycle, String beanId) {
    	this(executionContext, beanLifecycle, getBeanRepositoryId(executionContext, beanId));
	}

    /**
     * 
     * @param observerId
     * @param notifyOnce
     * @param observer
     * @deprecated Us the {@link #addObserver(String, boolean, RepositoryBeanLifecycleObserver)}
     */
    @Deprecated
    public void addObserver(String observerId, boolean notifyOnce, BeanLifecycleObserver observer) {
    	AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");
    	AssertArgument.isNotNull(observer, "observer");
    	
    	removeObserver(observerId);

    	ObserverContext observerContext = new ObserverContext();
    	observerContext.observerId = observerId;
    	observerContext.observer = observer;
    	observerContext.notifyOnce = notifyOnce;

    	observers.add(observerContext);
    	
    }
    
    public void addObserver(String observerId, boolean notifyOnce, RepositoryBeanLifecycleObserver observer) {
    	AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");
    	AssertArgument.isNotNull(observer, "observer");
    	
    	removeObserver(observerId);

    	ObserverContext observerContext = new ObserverContext();
    	observerContext.observerId = observerId;
    	observerContext.repositoryBeanLifecycleObserver = observer;
    	observerContext.notifyOnce = notifyOnce;

    	observers.add(observerContext);
    	
    }

    public void removeObserver(String observerId) {
    	AssertArgument.isNotNullAndNotEmpty(observerId, "observerId");
    	
    	boolean found = false;
    	for (int i = 0; !found && i < observers.size(); i++) {
    		ObserverContext observerContext = observers.get(i);

    		found = observerContext.observerId.equals(observerId);
    		if(found) {
    			observers.remove(i);
    		}
		}

    }

    @SuppressWarnings({ "unchecked", "deprecation" })
	public void notifyObservers(Object bean) {
    	if(observers.size() > 0) {

			List<ObserverContext> observersClone = (List<ObserverContext>) observers.clone();
			for(int i = 0; i < observersClone.size(); i++) {
				ObserverContext observerContext = observersClone.get(i);
				
				if(observerContext.repositoryBeanLifecycleObserver != null) {
					
					RepositoryBeanLifecycleEvent beanLifecycleEvent = new RepositoryBeanLifecycleEvent(executionContext, beanLifecycle, beanRepositoryId, bean);
					
					observerContext.repositoryBeanLifecycleObserver.onBeanLifecycleEvent(beanLifecycleEvent);
					
				} else {
					
					observerContext.observer.onBeanLifecycleEvent(executionContext, beanLifecycle, beanRepositoryId.getBeanId(), bean);
					
				}

				if(observerContext.notifyOnce) {
					removeObserver(observerContext.observerId);
				}

    		}
    	}

    }

	/**
	 * @return the beanLifecycle
	 */
	public BeanLifecycle getBeanLifecycle() {
		return beanLifecycle;
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
     * The context around on observer. The enabled property indicates
     * if this observer is enabled and can be notified.
     *
     * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
     */
    private class ObserverContext {

    	String observerId;

    	boolean notifyOnce = false;

    	@Deprecated
    	BeanLifecycleObserver observer;
    	
    	RepositoryBeanLifecycleObserver repositoryBeanLifecycleObserver;
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