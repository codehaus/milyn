/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/

package org.milyn.javabean.repository;

import org.milyn.assertion.*;
import org.milyn.container.*;
import org.milyn.javabean.lifecycle.*;

import java.util.*;
import java.util.Map.*;

/**
 * Bean Repository
 * <p/>
 * This class represents a repository of bean's and the means to get and
 * set there instances. 
 * <p/>
 * This class uses a {@link BeanIdList} to optimize the access performance. If
 * all the {@link BeanId} objects are registered with the BeanIdList before this object
 * is created then you get direct access performance. If you regularly register new
 * {@link BeanId} objects with the {@link BeanIdList}, after this object is created
 * then the BeanRepository needs to sync up with the {@link BeanIdList}. That 
 * sync process takes some time, so it is adviced to register all the BeanId's up front.
 * <p/>
 * Only {@link BeanId} objects from the {@link BeanIdList}, which is set on 
 * this BeanRepository, can be used with almost all of the methods.
 * <p/>
 * For ease of use it is also possible to get the bean by it's beanId name. This has however
 * not the direct access performance because a Map lookup is done. It is advised to use
 * the {@link BeanId} to get the bean from the repository.
 * 
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanRepository {

	private final ExecutionContext executionContext;

	private final Map<String, Object> beanMap;

	private final ArrayList<RepositoryEntry> repositoryEntries;

	private final BeanIdList beanIdList;

	/**
	 * Create the BeanRepository
	 * 
	 * @param executionContext The {@link ExecutionContext} to which this object is bound to.
	 * @param beanIdList The {@link BeanIdList} to which this object is bound to.
	 * @param beanMap The {@link Map} in which the bean's will be set. It is important not to modify this map outside of
	 * the BeanRepository! It is only provided as constructor parameter because in some situations we need to controll
	 * which {@link Map} is used.
	 */
	public BeanRepository(ExecutionContext executionContext, BeanIdList beanIdList, Map<String, Object> beanMap) {
		this.executionContext = executionContext;
		this.beanIdList = beanIdList;
		this.beanMap = beanMap;

		repositoryEntries = new ArrayList<RepositoryEntry>(beanIdList.size());

		updateBeanMap();
	}

	/**
     * Add a bean instance under the specified {@link BeanId}.
     *
     * @param beanId The {@link BeanId} under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     */
	public void addBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		// Check if the BeanIdList has new BeanIds and if so then
		// add those new entries to the Map. This ensures we always
		// have an up to date Map.
		checkUpdatedBeanIdList();

		int index = beanId.getIndex();

		cleanAssociatedLifecycleBeans(index);

		repositoryEntries.get(index).setValue(bean);

		notifyObservers(beanId, BeanLifecycle.BEGIN, bean);
	}

    /**
     * Add a bean instance under the specified beanId.
     * <p/>
     * If performance is important, you should get (and cache) a {@link BeanId} instance
     * for the beanId String and then use the {@link #addBean(BeanId, Object)} method.
     *
     * @param beanId The beanId under which the bean is to be stored.
     * @param bean The bean instance to be stored.
     */
    public void addBean(String beanId, Object bean) {
        AssertArgument.isNotNull(beanId, "beanId");
        AssertArgument.isNotNull(bean, "bean");

        addBean(getBeanId(beanId), bean);
    }

    /**
     * Get the {@link BeanId} instance for the specified beanId String.
     * <p/>
     * Regsiters the beanId if it's not already registered.
     *
     * @param beanId The beanId String.
     * @return The associated {@link BeanId} instance.
     */
    public BeanId getBeanId(String beanId) {
        AssertArgument.isNotNull(beanId, "beanId");
        BeanId beanIdObj = beanIdList.getBeanId(beanId);

        if(beanIdObj == null) {
            beanIdObj = beanIdList.register(beanId);
        }
        
        return beanIdObj;
    }

    /**
     * Looks if a bean instance is set under the {@link BeanId}
     *
     * @param beanId The {@link BeanId} under which is looked.
     */
	public boolean containsBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		return repositoryEntries.size() > index && repositoryEntries.get(index).getValue() != null;
	}

	/**
     * Get the current bean, specified by the supplied {@link BeanId}.
     * <p/>
     * @param beanId The {@link BeanId} to get the bean instance from.
     * @return The bean instance, or null if no such bean instance exists
     */
	public Object getBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		if(repositoryEntries.size() <= index) {
			return null;
		}

		return repositoryEntries.get(index).getValue();
	}

	/**
     * Changes a bean instance of the given {@link BeanId}. The difference to {@link #addBean(BeanId, Object)} 
     * is that the bean must exist, the associated beans aren't removed and the observers of the
     * {@link BeanLifecycle#CHANGE} event are notified.
     *
     * @param beanId The {@link BeanId} under which the bean instance is to be stored.
     * @param bean The bean instance to be stored.
     */
	public void changeBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		int index = beanId.getIndex();

		if(repositoryEntries.size() > index && repositoryEntries.get(index).getValue() != null) {
			repositoryEntries.get(index).setValue(bean);

			notifyObservers(beanId, BeanLifecycle.CHANGE, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + beanId + "' can't be changed because it isn't in the repository.");
    	}
	}
	
	/**
     * Associates the lifeCycle of the childBeanId with the parentBeanId. When the parentBean gets overwritten via the
     * addBean method then the associated child beans will get removed from the bean map.
     *
     * @param parentBeanId The {@link BeanId} of the bean that controlles the lifecycle of its childs
     * @param childBeanId The {@link BeanId} of the bean that will be associated to the parent
     */
	public void associateLifecycles(BeanId parentBeanId, BeanId childBeanId) {
    	AssertArgument.isNotNull(parentBeanId, "parentBeanId");
    	AssertArgument.isNotNull(childBeanId, "childBeanId");

    	checkUpdatedBeanIdList();

    	int parentId = parentBeanId.getIndex();
    	int childId = childBeanId.getIndex();

    	List<Integer> associations = repositoryEntries.get(parentId).getLifecycleAssociation();

        if(!associations.contains(childId)) {
            associations.add(childId);
        }
    }
	
	/**
     * Registers an observer which observers when a bean gets added.
     *
     * @param beanId The {@link BeanId} for which the observer is registered
     * @param observerId The id of the observer. This is used to unregister the observer
     * @param observer The actual BeanObserver instance
     */
	public void addBeanLifecycleObserver(BeanId beanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanRepositoryLifecycleObserver observer) {
    	AssertArgument.isNotNull(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

	/**
     * Unregisters a bean observer
     *
     * @param beanId The {@link BeanId} for which the observer is registered
     * @param observerId The id of the observer to unregister
     */
	public void removeBeanLifecycleObserver(BeanId beanId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNull(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.removeObserver(lifecycle, observerId);
    	}
    }

	/**
	 * Returns the bean by it's beanId name.
	 *
	 * @return the bean instance or <code>null</code> if it not exists.
	 */
	public Object getBean(String beanId) {
		return beanMap.get(beanId);
	}

	/**
	 * @return An unmodifiable Map of the bean instances.
	 * Values can be null if they are never set.
	 */
	public Map<String, Object> getBeanMap() {
		return Collections.unmodifiableMap(beanMap);
	}


    /**
	 * Checks if the repository is still in sync with 
	 * then {@link BeanIdList}.
	 */
	private void checkUpdatedBeanIdList() {
		
		//We only check if the size is difference because it
		//is not possible to remove BeanIds from the BeanIdList
		if(repositoryEntries.size() != beanIdList.size()) {

			updateBeanMap();

		}
	}

	/**
	 * Sync's the BeanRepositories bean map with
	 * the bean map from the {@link BeanIdList}. All
	 * missing keys that are in the BeanIdList's map are added
	 * to the BeanRepositories map. 
	 */
	private void updateBeanMap() {

		for(String beanId : beanIdList.getBeanIdMap().keySet()) {

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
		repositoryEntries.addAll(Collections.nCopies((beanIdList.size() - repositoryEntries.size()), (RepositoryEntry)null));

		for(Entry<String, Object> beanMapEntry : beanMap.entrySet()) {

			BeanId beanId = beanIdList.getBeanId(beanMapEntry.getKey());

			int index = beanId.getIndex();
			if(repositoryEntries.get(index) == null) {

				repositoryEntries.set(index, new RepositoryEntry(beanId, beanMapEntry));
			}
		}
	}

	/**
	 * Removes the bean instance from a BeanId. 
	 * The integer index is directly used for performance reasons.
	 * All associating child instances are also removed.
	 * 
	 * @param index The index of the BeanId.
	 */
	private void removeBean(int index) {
    	cleanAssociatedLifecycleBeans(index);

    	repositoryEntries.get(index).setValue(null);
    }

	/**
	 * Remove all bean instances of the associating BeanId's of the parent bean id.
	 * The integer index is directly used for performance reasons.
	 * 
	 * @param parentId The index of the parent BeanId.
	 */
	private void cleanAssociatedLifecycleBeans(int parentId) {

		RepositoryEntry repositoryEntry = repositoryEntries.get(parentId);
    	List<Integer> associations = repositoryEntry.getLifecycleAssociation();

        if(associations.size() > 0) {
            for (Integer associationId : associations) {
            	removeBean(associationId);
            }
            repositoryEntry.getLifecycleAssociation().clear();
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

    	RepositoryEntry repositoryEntry = repositoryEntries.get(beanId.getIndex());

    	BeanLifecycleSubjectGroup subjectGroup = repositoryEntry.getBeanLifecycleSubjectGroup();

    	if(subjectGroup == null && createIfNotExist) {

    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, beanId);

    		repositoryEntry.setBeanLifecycleSubjectGroup(subjectGroup);

    	}

    	return subjectGroup;
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
    private class RepositoryEntry {

    	private final BeanId beanId;

    	private final Entry<String, Object> entry;

    	private final List<Integer> lifecycleAssociation = new ArrayList<Integer>();

    	private BeanLifecycleSubjectGroup beanLifecycleSubjectGroup;

		/**
		 * @param entry
		 */
		public RepositoryEntry(BeanId beanId, Entry<String, Object> entry) {
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

    }

}
