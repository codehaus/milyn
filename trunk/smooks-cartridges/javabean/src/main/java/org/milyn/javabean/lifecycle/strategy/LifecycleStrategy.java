/**
 * 
 */
package org.milyn.javabean.lifecycle.strategy;


/**
 * Ugly idea of some kind of LifecycleStrategy interface...
 * 
 * 
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public interface LifecycleStrategy {
	
	void onBeanAddedToMap();
	
	void onBeanRemovedFromMap();
	
	void onBeanChangedInMap();
	
	void onElementEnd();
	
	void onDocumentEnd();
}
