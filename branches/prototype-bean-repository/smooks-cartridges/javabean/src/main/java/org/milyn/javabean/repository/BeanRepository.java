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
import org.milyn.javabean.lifecycle.BeanRepositoryLifecycleObserver;

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

	private final BeanIdList beanIdList;

	private int listsSize = 0;

	public BeanRepository(ExecutionContext executionContext, BeanIdList beanIdList, Map<String, Object> beanMap) {
		this.executionContext = executionContext;
		this.beanIdList = beanIdList;
		this.beanMap = beanMap;
		this.listsSize = beanIdList.size();

		beanMapEntries = new ArrayList<Entry<String, Object>>(listsSize);
		lifecycleAssociations = new ArrayList<List<Integer>>(listsSize);
		beanLifecycleSubjectGroups = new ArrayList<BeanLifecycleSubjectGroup>(listsSize);

		updateLists(false);
	}


	public void addBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		updateLists(true);

		int index = beanId.getIndex();

		cleanAssociatedLifecycleBeans(index);

		Entry<String, Object> entry = beanMapEntries.get(index);

		entry.setValue(bean);

		notifyObservers(beanId, BeanLifecycle.BEGIN, bean);
	}

	public boolean containsBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		return listsSize > index && beanMapEntries.get(index).getValue() != null;
	}

	public Object getBean(BeanId beanId) {
		AssertArgument.isNotNull(beanId, "beanId");

		int index = beanId.getIndex();

		if(listsSize <= index) {
			return null;
		}

		Entry<String, Object> entry = beanMapEntries.get(index);

		return entry.getValue();
	}


	public void changeBean(BeanId beanId, Object bean) {
		AssertArgument.isNotNull(beanId, "beanId");
		AssertArgument.isNotNull(bean, "bean");

		int index = beanId.getIndex();

		if(listsSize > index && beanMapEntries.get(index).getValue() != null) {
			beanMapEntries.get(index).setValue(bean);

			notifyObservers(beanId, BeanLifecycle.CHANGE, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + beanId + "' can't be changed because it isn't in the repository.");
    	}
	}

	public void associateLifecycles(BeanId parentBeanId, BeanId childBeanId) {
    	AssertArgument.isNotNull(parentBeanId, "parentBeanId");
    	AssertArgument.isNotNull(childBeanId, "childBeanId");

    	updateLists(true);

    	int parentId = parentBeanId.getIndex();
    	int childId = childBeanId.getIndex();

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
	private void updateLists(boolean check) {

		if(!check || listsSize != beanIdList.size()) {
			listsSize = beanIdList.size();

			beanMapEntries.addAll(Collections.nCopies((listsSize - beanMapEntries.size())+1, (Entry<String, Object>)null));
			lifecycleAssociations.addAll(Collections.nCopies((listsSize - lifecycleAssociations.size())+1, (List<Integer>)null));
			beanLifecycleSubjectGroups.addAll(Collections.nCopies((listsSize - beanLifecycleSubjectGroups.size())+1, (BeanLifecycleSubjectGroup)null));

			updateBeanMap();

		}
	}

	/**
	 * @param beanIdList
	 * @param beanmapEmpty
	 */
	private void updateBeanMap() {
        boolean foundChanges = false;

		for(String beanId : beanIdList.getBeanIdMap().keySet()) {

			if(!beanMap.containsKey(beanId) ) {
				beanMap.put(beanId, null);

				foundChanges = true;
			}
		}
		if(foundChanges) {
			updateBeanMapEntries();
		}
	}

	/**
	 * @param beanIdList
	 * @param beanMap
	 */
	private void updateBeanMapEntries() {
		for(Entry<String, Object> beanMapEntry : beanMap.entrySet()) {

			BeanId beanId = beanIdList.getBeanId(beanMapEntry.getKey());

			int index = beanId.getIndex();
			if(beanMapEntries.get(index) == null) {
				beanMapEntries.set(index, beanMapEntry);
			}
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

	private void removeBean(int index) {
    	cleanAssociatedLifecycleBeans(index);

    	Entry<String, Object> entry = beanMapEntries.get(index);
    	if(entry != null) {
    		entry.setValue(null);
    	}
    }

    private void notifyObservers(BeanId beanId, BeanLifecycle lifecycle, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }

    private BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup(BeanId beanId, boolean createIfNotExist) {
    	updateLists(true);

    	int index = beanId.getIndex();
    	BeanLifecycleSubjectGroup subjectGroup = beanLifecycleSubjectGroups.get(index);

    	if(subjectGroup == null && createIfNotExist) {

    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, beanId);

    		beanLifecycleSubjectGroups.set(index, subjectGroup);

    	}

    	return subjectGroup;
    }


}
