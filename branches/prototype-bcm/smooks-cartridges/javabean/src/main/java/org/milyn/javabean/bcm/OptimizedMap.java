/**
 * 
 */
package org.milyn.javabean.bcm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@SuppressWarnings("unchecked")
public abstract class OptimizedMap implements Map {

	protected static final Object NOT_SET = new Object();
	
	protected static final Object NOT_FOUND = new Object();
	
	private Map backingMap;
	
	private int size = 0;
	
	protected abstract boolean virtualClear();
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		virtualClear();
		size = 0;
		
		if(isBackingMapUsed()) {
			backingMap().clear();
		}
		
	}
	
	protected abstract boolean virtualContainsKey(Object key);

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		boolean result = false;
		
		if(key != null) {
			result = virtualContainsKey(key);
		}
		
		if(!result && isBackingMapUsed()) {
			result = backingMap().containsKey(key);
		}
		
		return result;
	}
	
	
	protected abstract boolean virtualContainsValue(Object value);

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		boolean result = virtualContainsValue(value);
		if(result == false && isBackingMapUsed()) {
			result = backingMap.containsValue(value);
		}
		return result;
	}

	/**
	 * 
	 * Returns a set view of the mappings contained in this map. 
	 * Each element in the returned set is a Map.Entry. 
	 * The set is <b>not</b> backed by the map, so changes to the map are 
	 * <b>not</b> reflected in the set, and vice-versa. 
	 * 
	 */
	public Set entrySet() {
		Map result =  virtualFieldsToMap();
		
		if(isBackingMapUsed()) {
			result.putAll(backingMap());
		}
		
		return result.entrySet();
	}
	
	protected abstract Object virtualGet(Object key);

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		
		Object result = NOT_FOUND;
		
		if(key != null) {
			result = virtualGet(key);
		}
		
		if(result == NOT_FOUND) {
			if(isBackingMapUsed()) {
				result = backingMap().get(key);
			} else {
				result = null;
			}
		} else {
			if(result == NOT_SET) {
				result = null;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return size == 0 && (!isBackingMapUsed() || backingMap().isEmpty() );
	}

	/**
	 * Returns a set view of the keys contained in this map. 
	 * The set is backed <b>not</b> by the map, so 
	 * changes to the map are <b>not</b> reflected in the set, and vice-versa.
	 *  
	 */
	public Set keySet() {
		Map result =  virtualFieldsToMap();
		
		if(isBackingMapUsed()) {
			result.putAll(backingMap());
		}
		return result.keySet();
	}

	protected abstract Object virtualPut(Object key, Object value);
	
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		Object result = NOT_FOUND;
		
		if(key != null) {
			result = virtualPut(key, value);
		}
		
		if(result == NOT_FOUND) {
			return backingMap().put(key, value);
		}
		
		if(result == NOT_SET) {
			size++;
		}
		return result;
	}
	
	//protected abstract boolean virtualPutAll(Map t);

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map t) {
		throw new UnsupportedOperationException();
	}
	
	protected abstract boolean virtualRemove(Object key);

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		Object result = NOT_FOUND;
		
		if(key != null) {
			result = virtualRemove(key);
		}
		
		if(result == NOT_FOUND) {
			if(isBackingMapUsed()) {
				result = backingMap.remove(key);
			} else {
				result = null;
			}
			
		} else {
			size--;
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		int totalSize = size;
		
		if(isBackingMapUsed()) {
			totalSize += backingMap().size(); 
		}
		
		return totalSize;
	}

	/**
	 * Returns a collection view of the values contained in this map. The collection 
	 * is <b>not</b> backed by the map, so changes to the map are <b>not</b> reflected in the collection, and vice-versa.
	 * 
	 * TODO: optimize
	 */
	public Collection values() {
		Map result =  virtualFieldsToMap();
		
		if(isBackingMapUsed()) {
			result.putAll(backingMap());
		}
		
		return backingMap().values();
	}

	protected abstract Map virtualFieldsToMap();
	
	/**
	 * @return the backMap
	 */
	protected Map backingMap() {
		if(backingMap == null) {
			backingMap = new HashMap();
		}
		return backingMap;
	}
	
	public boolean isBackingMapUsed() {
		return backingMap != null;
	}
}
