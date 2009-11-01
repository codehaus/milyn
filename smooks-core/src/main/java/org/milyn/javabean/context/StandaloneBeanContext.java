package org.milyn.javabean.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.lifecycle.BeanContextLifecycleObserver;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleSubjectGroup;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdRegister;
import org.milyn.util.MultiLineToStringBuilder;

public class StandaloneBeanContext implements BeanContext {
	private final ExecutionContext executionContext;

	private final Map<String, Object> beanMap;

	private final ArrayList<ContextEntry> entries;

	private final BeanIdStore beanIdStore;

	private final BeanContextMapAdapter repositoryBeanMapAdapter = new BeanContextMapAdapter();

	/**
	 * Create the StandAloneBeanContext
	 *
	 * @param executionContext The {@link ExecutionContext} to which this object is bound to.
	 * @param beanIdList The {@link BeanIdRegister} to which this object is bound to.
	 * @param beanMap The {@link Map} in which the bean's will be set. It is important not to modify this map outside of
	 * the BeanRepository! It is only provided as constructor parameter because in some situations we need to control
	 * which {@link Map} is used.
	 */
	public StandaloneBeanContext(ExecutionContext executionContext, BeanIdStore beanIdList, Map<String, Object> beanMap) {
		this.executionContext = executionContext;
		this.beanIdStore = beanIdList;
		this.beanMap = beanMap;

		entries = new ArrayList<ContextEntry>(beanIdList.size());

		updateBeanMap();
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#addBean(org.milyn.javabean.repository.BeanId, java.lang.Object)
	 */
	public void addBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		// Check if the BeanIdList has new BeanIds and if so then
		// add those new entries to the Map. This ensures we always
		// have an up to date Map.
		checkUpdatedBeanIdList();

		int index = beanId.getIndex();
        ContextEntry repoEntry = entries.get(index);

        clean(index);
		repoEntry.setValue(bean);
		notifyObservers(beanId, BeanLifecycle.BEGIN, bean);
	}

    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#addBean(java.lang.String, java.lang.Object)
	 */
    public void addBean(String beanId, Object bean) {
        AssertArgument.isNotNull(beanId, "beanId");

        addBean(getBeanId(beanId), bean);
    }

    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#getBeanId(java.lang.String)
	 */
    public BeanId getBeanId(String beanId) {
        AssertArgument.isNotNull(beanId, "beanId");
        BeanId beanIdObj = beanIdStore.getBeanId(beanId);

        if(beanIdObj == null) {
            beanIdObj = beanIdStore.register(beanId);
        }

        return beanIdObj;
    }

    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#containsBean(org.milyn.javabean.repository.BeanId)
	 */
	public boolean containsBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		return entries.size() > index && entries.get(index).getValue() != null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#getBean(org.milyn.javabean.repository.BeanId)
	 */
	public Object getBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		if(entries.size() <= index) {
			return null;
		}

		return entries.get(index).getValue();
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#changeBean(org.milyn.javabean.repository.BeanId, java.lang.Object)
	 */
	public void changeBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		int index = beanId.getIndex();

		if(entries.size() > index && entries.get(index).getValue() != null) {
			entries.get(index).setValue(bean);

			notifyObservers(beanId, BeanLifecycle.CHANGE, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + beanId + "' can't be changed because it isn't in the repository.");
    	}
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#removeBean(org.milyn.javabean.repository.BeanId)
	 */
	public Object removeBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

        ContextEntry repositoryEntry = entries.get(beanId.getIndex());
		Object old = repositoryEntry.getValue();

        repositoryEntry.clean();
        repositoryEntry.setValue(null);

		return old;
	}

    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#removeBean(java.lang.String)
	 */
    public Object removeBean(String beanId) {
        BeanId beanIDObj = getBeanId(beanId);

        if(beanIDObj != null) {
            return removeBean(beanIDObj);
        }

        return null;
    }

    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#clear()
	 */
    public void clear() {

		for(ContextEntry entry : entries) {
			entry.setValue(null);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#associateLifecycles(org.milyn.javabean.repository.BeanId, org.milyn.javabean.repository.BeanId)
	 */
	public void associateLifecycles(BeanId parentBeanId, BeanId childBeanId) {
    	AssertArgument.isNotNull(parentBeanId, "parentBeanId");
    	AssertArgument.isNotNull(childBeanId, "childBeanId");

    	checkUpdatedBeanIdList();

    	int parentId = parentBeanId.getIndex();
    	int childId = childBeanId.getIndex();

    	List<Integer> associations = entries.get(parentId).getLifecycleAssociation();

        if(!associations.contains(childId)) {
            associations.add(childId);
        }
    }

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#addBeanLifecycleObserver(org.milyn.javabean.repository.BeanId, org.milyn.javabean.lifecycle.BeanLifecycle, java.lang.String, boolean, org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver)
	 */
	public void addBeanLifecycleObserver(BeanId beanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanContextLifecycleObserver observer) {
    	AssertArgument.isNotNull(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#removeBeanLifecycleObserver(org.milyn.javabean.repository.BeanId, org.milyn.javabean.lifecycle.BeanLifecycle, java.lang.String)
	 */
	public void removeBeanLifecycleObserver(BeanId beanId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNull(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.removeObserver(lifecycle, observerId);
    	}
    }

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#getBean(java.lang.String)
	 */
	public Object getBean(String beanId) {
		return beanMap.get(beanId);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#getBeanMap()
	 */
	public Map<String, Object> getBeanMap() {
		return repositoryBeanMapAdapter;
	}


    /**
	 * Checks if the repository is still in sync with
	 * then {@link BeanIdRegister}.
	 */
	private void checkUpdatedBeanIdList() {

		//We only check if the size is difference because it
		//is not possible to remove BeanIds from the BeanIdList
		if(entries.size() != beanIdStore.size()) {

			updateBeanMap();

		}
	}

	/**
	 * Sync's the BeanRepositories bean map with
	 * the bean map from the {@link BeanIdRegister}. All
	 * missing keys that are in the BeanIdList's map are added
	 * to the BeanRepositories map.
	 */
	private void updateBeanMap() {

		for(String beanId : beanIdStore.getBeanIdMap().keySet()) {

			if(!beanMap.containsKey(beanId) ) {
				beanMap.put(beanId, null);
			}
		}
		updateRepositoryEntries();
	}

	/**
	 * Sync's the repository entry list by copying all the
	 * {@link Entry} instances from the bean map to the bean list. The
	 * {@link Entry} instances are put at the same index as the index of the
	 * corresponding BeanId. This ensures that direct access to the BeanId his
	 * value is possible.
	 */
	private void updateRepositoryEntries() {
		entries.addAll(Collections.nCopies((beanIdStore.size() - entries.size()), (ContextEntry)null));

		for(Entry<String, Object> beanMapEntry : beanMap.entrySet()) {

			BeanId beanId = beanIdStore.getBeanId(beanMapEntry.getKey());

			int index = beanId.getIndex();
			if(entries.get(index) == null) {

				entries.set(index, new ContextEntry(beanId, beanMapEntry));
			}
		}
	}

	/**
	 * Remove all bean instances of the associating BeanId's of the parent bean id.
	 * The integer index is directly used for performance reasons.
	 *
	 * @param beanId The index of the parent BeanId.
	 */
	private void clean(int beanId) {
        entries.get(beanId).clean();
    }

    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#setBeanInContext(org.milyn.javabean.repository.BeanId, boolean)
	 */
    public void setBeanInContext(BeanId beanId, boolean inContext) {
        ContextEntry repositoryEntry = entries.get(beanId.getIndex());
        if(repositoryEntry != null) {
            repositoryEntry.setBeanInContext(inContext);
        }
    }

    /**
	 * Notify all the observers from the given {@link BeanId} that the given
	 * {@link BeanLifecycle} event happend.
	 *
	 * @param beanId The {@link BeanId} from which the observers are notified.
	 * @param lifecycle The {@link BeanLifecycle} to be notified of
	 * @param bean The bean instance
	 */
    private void notifyObservers(BeanId beanId, BeanLifecycle lifecycle, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }



    /**
     * Returns the {@link BeanLifecycleSubjectGroup} of the given {@link BeanId}.
     *
     * @param beanId The BeanId from which the {@link BeanLifecycleSubjectGroup} needs to be returned
     * @param createIfNotExist If the {@link BeanLifecycleSubjectGroup needs to be created if it not already exists
     * @return The {@link BeanLifecycleSubjectGroup} if found or created else <code>null</code>.
     */
    private BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup(BeanId beanId, boolean createIfNotExist) {
    	checkUpdatedBeanIdList();

    	ContextEntry repositoryEntry = entries.get(beanId.getIndex());

    	BeanLifecycleSubjectGroup subjectGroup = repositoryEntry.getBeanLifecycleSubjectGroup();

    	if(subjectGroup == null && createIfNotExist) {

    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, beanId);

    		repositoryEntry.setBeanLifecycleSubjectGroup(subjectGroup);

    	}

    	return subjectGroup;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    /* (non-Javadoc)
	 * @see org.milyn.javabean.context.BeanContext#toString()
	 */
    @Override
    public String toString() {
        return MultiLineToStringBuilder.toString(getBeanMap());
    }

    /**
     * Repository Entry
     * <p/>
     * Represents an entry of a BeanId and provides an platform of all the objects
     * that needed for that entry
     *
     * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
     *
     */
    private class ContextEntry {

    	private final BeanId beanId;

    	private final Entry<String, Object> entry;

    	private final List<Integer> lifecycleAssociation = new ArrayList<Integer>();

    	private BeanLifecycleSubjectGroup beanLifecycleSubjectGroup;

        private boolean cleaning = false;

        private boolean beanInContext = true;

        /**
		 * @param entry
		 */
		public ContextEntry(BeanId beanId, Entry<String, Object> entry) {
			this.beanId = beanId;
			this.entry = entry;
		}

		/**
		 * @return the beanId
		 */
		public BeanId getBeanId() {
			return beanId;
		}

		/**
		 * @return the entry
		 */
		public Entry<String, Object> getEntry() {
			return entry;
		}

		public Object getValue() {
			return entry.getValue();
		}

		public void setValue(Object value) {
            if(value == null) {
                value = null;
            }
            entry.setValue(value);
		}

		/**
		 * @return the lifecycleAssociation
		 */
		public List<Integer> getLifecycleAssociation() {
			return lifecycleAssociation;
		}

		/**
		 * @return the beanLifecycleSubjectGroup
		 */
		public BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup() {
			return beanLifecycleSubjectGroup;
		}

		/**
		 * @param beanLifecycleSubjectGroup the beanLifecycleSubjectGroup to set
		 */
		public void setBeanLifecycleSubjectGroup(BeanLifecycleSubjectGroup beanLifecycleSubjectGroup) {
			this.beanLifecycleSubjectGroup = beanLifecycleSubjectGroup;
		}

        public void clean() {
            clean(false);
        }

        private void clean(boolean nullifyValue) {
            // Clean the repo entry if it's not already cleaning and the bean is not
            // in context...
            if(cleaning || beanInContext) {
                return;
            }

            setCleaning(true);
            try {
                if(lifecycleAssociation.size() > 0) {
                    for (Integer associationId : lifecycleAssociation) {
                        ContextEntry association = entries.get(associationId);

                        association.clean(true);
                    }
                    lifecycleAssociation.clear();
                }
            } finally {
                if(nullifyValue) {
                    setValue(null);
                }
                setCleaning(false);
            }
        }

        /**
         * Is this repo entry being cleaned.
         * @return True if the entry is being cleaned, otherwise false.
         */
        public boolean isCleaning() {
            return cleaning;
        }

        /**
         * Mark this repo entry as being cleaned.
         * @param cleaning True if the entry is being cleaned, otherwise false.
         */
        public void setCleaning(boolean cleaning) {
            this.cleaning = cleaning;
        }

        public boolean isBeanInContext() {
            return beanInContext;
        }

        public void setBeanInContext(boolean beanInContext) {
            this.beanInContext = beanInContext;
        }

        public String toString() {
            return ContextEntry.class.getSimpleName() + ": Idx (" + beanId.getIndex() + "), Name (" + beanId.getName() + "), Num Associations (" + lifecycleAssociation.size() + ").";
        }
    }

    /**
     * This Map Adapter enables that the bean context can be used as a normal map.
     * There are some important side notes:
     *
     * <ul>
     *   <li> The write performance of the map isn't as good as the write performance of the
     *     	  BeanRepository because it needs to find or register the BeanId every time.
     *        The read performance are as good as any normal Map.</li>
     *   <li> The {@link #entrySet()} method returns an UnmodifiableSet </li>
     *   <li> When a bean gets removed from the BeanRepository then only the value of the
     *        map entry is set to null. This means that null values should be regarded as
     *        deleted beans. That is also why the size() of the bean map isn't accurate. It
     *        also counts the null value entries.
     * </ul>
     *
     * Only use the Map if you absolutely needed it else you should use the BeanContext.
     *
     * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
     *
     */
    private class BeanContextMapAdapter implements Map<String, Object> {

		/* (non-Javadoc)
		 * @see java.util.Map#clear()
		 */
		public void clear() {
			StandaloneBeanContext.this.clear();
		}

		/* (non-Javadoc)
		 * @see java.util.Map#containsKey(java.lang.Object)
		 */
		public boolean containsKey(Object key) {
			return beanMap.containsKey(key);
		}

		/* (non-Javadoc)
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		public boolean containsValue(Object value) {
			return beanMap.containsValue(value);
		}

		/* (non-Javadoc)
		 * @see java.util.Map#entrySet()
		 */
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return Collections.unmodifiableSet(beanMap.entrySet());
		}

		/* (non-Javadoc)
		 * @see java.util.Map#get(java.lang.Object)
		 */
		public Object get(Object key) {
			return beanMap.get(key);
		}

		/* (non-Javadoc)
		 * @see java.util.Map#isEmpty()
		 */
		public boolean isEmpty() {
			return beanMap.isEmpty();
		}

		/* (non-Javadoc)
		 * @see java.util.Map#keySet()
		 */
		public Set<String> keySet() {
			return beanMap.keySet();
		}

		/* (non-Javadoc)
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		public Object put(String key, Object value) {
			AssertArgument.isNotNull(key, "key");

			BeanId beanId = beanIdStore.getBeanId(key);

			Object old = null;
			if(beanId == null) {
				beanId = beanIdStore.register(key);
			} else {
				old = getBean(beanId);
			}

			addBean(beanId, value);

			return old;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		public void putAll(Map<? extends String, ? extends Object> map) {
			AssertArgument.isNotNull(map, "map");

			for(Entry<? extends String, ? extends Object> entry : map.entrySet()) {

				addBean(entry.getKey(), entry.getValue());

			}
		}

		/* (non-Javadoc)
		 * @see java.util.Map#remove(java.lang.Object)
		 */
		public Object remove(Object key) {
			AssertArgument.isNotNull(key, "key");

			if(key instanceof String == false) {
				return null;
			}
			BeanId beanId = beanIdStore.getBeanId((String)key);

			return beanId == null ? null : removeBean(beanId);
		}

		/* (non-Javadoc)
		 * @see java.util.Map#size()
		 */
		public int size() {
			return beanMap.size();
		}

		/* (non-Javadoc)
		 * @see java.util.Map#values()
		 */
		public Collection<Object> values() {
			return beanMap.values();
		}

    }
}
