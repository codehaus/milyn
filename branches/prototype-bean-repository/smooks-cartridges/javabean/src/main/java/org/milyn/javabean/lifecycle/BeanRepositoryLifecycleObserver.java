/**
 * 
 */
package org.milyn.javabean.lifecycle;


/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public interface BeanRepositoryLifecycleObserver {

	void onBeanLifecycleEvent(BeanRepositoryLifecycleEvent event);
	
}
