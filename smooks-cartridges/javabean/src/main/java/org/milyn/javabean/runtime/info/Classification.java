/**
 * 
 */
package org.milyn.javabean.runtime.info;

/**
 * Bean type classification.
 * <p/>
 * We maintain this classification enum because it helps us avoid performing
 * instanceof checks, which are cheap when the instance being checked is
 * an instanceof, but expensive if it's not.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public enum Classification {
	NON_COLLECTION,
    ARRAY_COLLECTION,
    COLLECTION_COLLECTION,
    MAP_COLLECTION,
}
