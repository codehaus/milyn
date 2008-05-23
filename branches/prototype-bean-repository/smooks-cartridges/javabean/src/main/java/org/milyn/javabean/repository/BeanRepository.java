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

		growLists(size);
	}


	public void addBean(BeanRepositoryId beanRepositoryId, Object bean) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		AssertArgument.isNotNull(bean, "bean");

		int id = beanRepositoryId.getId();

		cleanAssociatedLifecycleBeans(id);

		if(beanMapEntries.size() <= id) {
			growLists(id);
		}
		Entry<String, Object> entry = beanMapEntries.get(id);

		entry.setValue(bean);

		notifyObservers(beanRepositoryId, BeanLifecycle.BEGIN, bean);
	}

	public boolean containsBean(BeanRepositoryId beanRepositoryId) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");

		int id = beanRepositoryId.getId();

		return beanMapEntries.size() > id && beanMapEntries.get(id).getValue() != null;
	}

	public Object getBean(BeanRepositoryId beanRepositoryId) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");

		int id = beanRepositoryId.getId();

		if(beanMapEntries.size() <= id) {
			return null;
		}

		Entry<String, Object> entry = beanMapEntries.get(id);

		return entry.getValue();
	}


	public void changeBean(BeanRepositoryId beanRepositoryId, Object bean) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		AssertArgument.isNotNull(bean, "bean");

		int id = beanRepositoryId.getId();

		if(beanMapEntries.size() > id && beanMapEntries.get(id).getValue() != null) {
			beanMapEntries.get(id).setValue(bean);

			notifyObservers(beanRepositoryId, BeanLifecycle.CHANGE, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + beanRepositoryId + "' can't be changed because it isn't in the repository.");
    	}
	}

	public void associateLifecycles(BeanRepositoryId parentRepositoryBeanId, BeanRepositoryId childRepositoryBeanId) {
    	AssertArgument.isNotNull(parentRepositoryBeanId, "parentBeanRepositoryId");
    	AssertArgument.isNotNull(childRepositoryBeanId, "childBeanRepositoryId");

    	int parentId = parentRepositoryBeanId.getId();
    	int childId = childRepositoryBeanId.getId();

    	int checkId = parentId < childId ? childId : parentId;
    	if(lifecycleAssociations.size() <= checkId) {
    		growLists(checkId);
		}

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
    	AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanRepositoryId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

	public void removeBeanLifecycleObserver(BeanRepositoryId beanRepositoryId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");

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
		initBeanMap();

		for(Entry<String, Object> beanMapEntry : beanMap.entrySet()) {

			BeanRepositoryId beanRepositoryId = beanRepositoryIdList.getRepositoryBeanId(beanMapEntry.getKey());

			int id = beanRepositoryId.getId();
			if(beanMapEntries.get(id) == null) {
				beanMapEntries.set(id, beanMapEntry);
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

	private void removeBean(int id) {
    	cleanAssociatedLifecycleBeans(id);

    	Entry<String, Object> entry = beanMapEntries.get(id);
    	if(entry != null) {
    		entry.setValue(null);
    	}
    }

    private void notifyObservers(BeanRepositoryId beanRepositoryId, BeanLifecycle lifecycle, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(beanRepositoryId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }

    private BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup(BeanRepositoryId beanRepositoryId, boolean createIfNotExist) {

    	int id = beanRepositoryId.getId();
    	if(beanLifecycleSubjectGroups.size() <= id) {
    		growLists(id);
		}

    	BeanLifecycleSubjectGroup subjectGroup = beanLifecycleSubjectGroups.get(id);

    	if(subjectGroup == null && createIfNotExist) {

    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, beanRepositoryId);

    		beanLifecycleSubjectGroups.set(id, subjectGroup);

    	}

    	return subjectGroup;
    }

	/**
	 * @param size
	 */
	private void growLists(int index) {
		beanMapEntries.addAll(Collections.nCopies((index - beanMapEntries.size())+1, (Entry<String, Object>)null));
		lifecycleAssociations.addAll(Collections.nCopies((index - lifecycleAssociations.size())+1, (List<Integer>)null));
		beanLifecycleSubjectGroups.addAll(Collections.nCopies((index - beanLifecycleSubjectGroups.size())+1, (BeanLifecycleSubjectGroup)null));

		initBeanMapEntries();
	}

}
