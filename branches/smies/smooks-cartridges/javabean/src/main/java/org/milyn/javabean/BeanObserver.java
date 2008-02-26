/**
 * 
 */
package org.milyn.javabean;

import org.milyn.container.ExecutionContext;

/**
 * @author maurice
 *
 */
public interface BeanObserver {
	
	String getBeanId();
	
	void beanRegistrationNotify(ExecutionContext executionContext, Object obj);
	
}
