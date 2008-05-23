/**
 *
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
import org.milyn.javabean.lifecycle.RepositoryBeanLifecycleObserver;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanRepository {

	private final ExecutionContext executionContext;

	private final Map<String, Object> beanMap;

	private final ArrayList<Entry<String, Object>> beanMapEntries;

	private final ArrayList<List<Integer>> lifecycleAssociations;

	private final ArrayList<BeanLifecycleSubjectGroup> beanLifecycleSubjectGroups;

	private final BeanRepositoryIdList beanRepositoryIdList;

	public BeanRepository(ExecutionContext executionContext, BeanRepositoryIdList beanRepositoryIdList, Map<String, Object> beanMap) {
		this.executionContext = executionContext;
		this.beanRepositoryIdList = beanRepositoryIdList;
		this.beanMap = beanMap;

		int size = beanRepositoryIdList.size();

		beanMapEntries = new ArrayList<Entry<String, Object>>(size);
		lifecycleAssociations = new ArrayList<List<Integer>>(size);
		beanLifecycleSubjectGroups = new ArrayList<BeanLifecycleSubjectGroup>(size);

		initList(beanMapEntries, size);
		initList(lifecycleAssociations, size);
		initList(beanLifecycleSubjectGroups, size);

		initBeanMap();

		initBeanMapEntries();
	}

	public void addBean(BeanRepositoryId beanRepositoryId, Object bean) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		AssertArgument.isNotNull(bean, "bean");

		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			throw new IllegalArgumentException("The BeanRepositoryId object does not belong to this BeanRepository");
		}

		int id = beanRepositoryId.getId();

		cleanAssociatedLifecycleBeans(id);

		beanMapEntries.get(beanRepositoryId.getId()).setValue(bean);

		notifyObservers(beanRepositoryId, BeanLifecycle.BEGIN, bean);
	}

	public boolean containsBean(BeanRepositoryId beanRepositoryId) {
		int id = beanRepositoryId.getId();

		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			return false;
		}

		return beanMapEntries.size() >= id && beanMapEntries.get(beanRepositoryId.getId()).getValue() != null;
	}

	public Object getBean(BeanRepositoryId beanRepositoryId) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");

		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			throw new IllegalArgumentException("The BeanRepositoryId object does not belong to this BeanRepository");
		}

		return beanMapEntries.get(beanRepositoryId.getId()).getValue();
	}

	public void changeBean(BeanRepositoryId beanRepositoryId, Object bean) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		AssertArgument.isNotNull(bean, "bean");

		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			throw new IllegalArgumentException("The BeanRepositoryId object does not belong to this BeanRepository");
		}

		int id = beanRepositoryId.getId();

		if(beanMapEntries.get(id).getValue() != null) {
			beanMapEntries.get(beanRepositoryId.getId()).setValue(bean);

			notifyObservers(beanRepositoryId, BeanLifecycle.CHANGE, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + beanRepositoryId + "' can't be changed because it isn't in the repository.");
    	}
	}

	public void associateLifecycles(BeanRepositoryId parentRepositoryBeanId, BeanRepositoryId childRepositoryBeanId) {
    	AssertArgument.isNotNull(parentRepositoryBeanId, "parentRepositoryBeanId");
    	AssertArgument.isNotNull(childRepositoryBeanId, "childRepositoryBeanId");

    	int parentId = parentRepositoryBeanId.getId();
    	int childId = childRepositoryBeanId.getId();

    	List<Integer> associations = lifecycleAssociations.get(parentId);

        if(associations != null) {
            if(!associations.contains(childId)) {
                associations.add(childId);
            }
        } else {
            associations = new ArrayList<Integer>(1);
            associations.add(childId);

            lifecycleAssociations.set(parentId, associations);
        }
    }

	public void addBeanLifecycleObserver(BeanRepositoryId beanRepositoryId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, RepositoryBeanLifecycleObserver observer) {
    	AssertArgument.isNotNull(beanRepositoryId, "repositoryBeanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanRepositoryId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

	public void removeBeanLifecycleObserver(BeanRepositoryId beanRepositoryId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNull(beanRepositoryId, "repositoryBeanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanRepositoryId, false);

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
	 * @param beanRepositoryIdList
	 * @param beanmapEmpty
	 */
	private void initBeanMap() {
        boolean beanmapEmpty = beanMap.isEmpty();

		for(String beanId : beanRepositoryIdList.getRepositoryBeanIdMap().keySet()) {

			if(beanmapEmpty || !beanMap.containsKey(beanId) ) {
				beanMap.put(beanId, null);
			}
		}
	}

	/**
	 * @param beanRepositoryIdList
	 * @param beanMap
	 */
	private void initBeanMapEntries() {

		for(Entry<String, Object> beanMapEntry : beanMap.entrySet()) {

			BeanRepositoryId beanRepositoryId = beanRepositoryIdList.getRepositoryBeanId(beanMapEntry.getKey());

			beanMapEntries.set(beanRepositoryId.getId(), beanMapEntry);

		}
	}

	private void cleanAssociatedLifecycleBeans(int parentId) {

    	List<Integer> associations = lifecycleAssociations.get(parentId);

        if(associations != null) {
            for (Integer associationId : associations) {
            	removeBean(associationId);
            }
            lifecycleAssociations.set(parentId, null);
        }

    }

	private void removeBean(int id) {
    	cleanAssociatedLifecycleBeans(id);

    	beanMapEntries.get(id).setValue(null);
    }

    private void notifyObservers(BeanRepositoryId beanRepositoryId, BeanLifecycle lifecycle, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanRepositoryId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }

    private BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup(BeanRepositoryId beanRepositoryId, boolean createIfNotExist) {
    	BeanLifecycleSubjectGroup subjectGroup = beanLifecycleSubjectGroups.get(beanRepositoryId.getId());

    	if(subjectGroup == null && createIfNotExist) {
    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, beanRepositoryId);

    		beanLifecycleSubjectGroups.set(beanRepositoryId.getId(), subjectGroup);

    	}

    	return subjectGroup;
    }


	private void initList(List<?> list, int size) {
		for(int i = 0; i < size; i++) {

			list.add(null);

		}
	}

}
