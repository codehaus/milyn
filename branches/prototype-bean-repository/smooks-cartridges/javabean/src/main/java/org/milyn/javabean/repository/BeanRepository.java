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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.milyn.assertion.AssertArgument;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.lifecycle.BeanLifecycle;
import org.milyn.javabean.lifecycle.BeanLifecycleSubjectGroup;
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanRepository {

	private final ExecutionContext executionContext;

	private final Map<String, Object> beanMap;

	private final ArrayList<RepositoryEntry> repositoryEntries;

	private final BeanIdList beanIdList;

	public BeanRepository(ExecutionContext executionContext, BeanIdList beanIdList, Map<String, Object> beanMap) {
		this.executionContext = executionContext;
		this.beanIdList = beanIdList;
		this.beanMap = beanMap;

		repositoryEntries = new ArrayList<RepositoryEntry>(beanIdList.size());

		updateBeanMap();
	}


	public void addBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		checkUpdatedBeanIdList();

		int index = beanId.getIndex();

		cleanAssociatedLifecycleBeans(index);

		repositoryEntries.get(index).setValue(bean);

		notifyObservers(beanId, BeanLifecycle.BEGIN, bean);
	}

	public boolean containsBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		return repositoryEntries.size() > index && repositoryEntries.get(index).getValue() != null;
	}

	public Object getBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		if(repositoryEntries.size() <= index) {
			return null;
		}

		return repositoryEntries.get(index).getValue();
	}


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

	public void addBeanLifecycleObserver(BeanId beanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, BeanRepositoryLifecycleObserver observer) {
    	AssertArgument.isNotNull(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

	public void removeBeanLifecycleObserver(BeanId beanId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNull(beanId, "beanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.removeObserver(lifecycle, observerId);
    	}
    }

	/**
	 * Returns the bean by it's beanId
	 *
	 * @return the beanMap
	 */
	public Object getBean(String beanId) {
		return beanMap.get(beanId);
	}

	/**
	 *  Returns the an unmodifiable bean Map
	 *
	 * @return the beanMap
	 */
	public Map<String, Object> getBeanMap() {
		return Collections.unmodifiableMap(beanMap);
	}


    /**
	 * @param size
	 */
	private void checkUpdatedBeanIdList() {

		if(repositoryEntries.size() != beanIdList.size()) {

			updateBeanMap();

		}
	}

	/**
	 * @param beanIdList
	 * @param beanmapEmpty
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
	 * @param beanIdList
	 * @param beanMap
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

	private void removeBean(int index) {
    	cleanAssociatedLifecycleBeans(index);

    	repositoryEntries.get(index).setValue(null);
    }

    private void notifyObservers(BeanId beanId, BeanLifecycle lifecycle, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }

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
