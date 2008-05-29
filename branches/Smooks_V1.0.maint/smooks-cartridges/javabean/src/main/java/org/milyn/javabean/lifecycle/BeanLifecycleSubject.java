package org.milyn.javabean.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;

public class BeanLifecycleSubject {

    private final ArrayList<ObserverContext> observers = new ArrayList<ObserverContext>();

    private final BeanLifecycle beanLifecycle;

    private final String beanId;

    private final ExecutionContext executionContext;

    public BeanLifecycleSubject(ExecutionContext executionContext, BeanLifecycle beanLifecycle, String beanId) {
    	AssertArgument.isNotNull(executionContext, "executionContext");
    	AssertArgument.isNotNull(beanLifecycle, "beanLifecycle");
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	
    	this.beanLifecycle = beanLifecycle;
    	this.executionContext = executionContext;
		this.beanId = beanId;
	}

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

    public void removeObserver(String observerId) {
    	AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
    	
    	boolean found = false;
    	for (int i = 0; !found && i < observers.size(); i++) {
    		ObserverContext observerContext = observers.get(i);

    		found = observerContext.observerId.equals(observerId);
    		if(found) {
    			observers.remove(i);
    		}
		}

    }

    @SuppressWarnings("unchecked")
	public void notifyObservers(Object bean) {
    	if(observers.size() > 0) {

			List<ObserverContext> observersClone = (List<ObserverContext>) observers.clone();
			for(int i = 0; i < observersClone.size(); i++) {
				ObserverContext observerContext = observersClone.get(i);
				
				observerContext.observer.onBeanLifecycleEvent(executionContext, beanLifecycle, beanId, bean);

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

    /**
     * The context around on observer. The enabled property indicates
     * if this observer is enabled and can be notified.
     *
     * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
     */
    public class ObserverContext {

    	String observerId;

    	boolean notifyOnce = false;

    	BeanLifecycleObserver observer;

    }
	
}
