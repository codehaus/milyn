/**
 * 
 */
package org.milyn.javabean.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	
	private final Map<String, Object> beanMap = new HashMap<String, Object>();
	
	private final List<Entry<String, Object>> beansEntrySet;
	
	private final List<List<Integer>> lifecycleAssociations;

	private final List<BeanLifecycleSubjectGroup> beanLifecycleSubjectGroups;

	private final BeanRepositoryIdList beanRepositoryIdList;
	
	public BeanRepository(ExecutionContext executionContext, BeanRepositoryIdList beanRepositoryIdList) {
		this.executionContext = executionContext;
		this.beanRepositoryIdList = beanRepositoryIdList;
		
		int size = beanRepositoryIdList.size();
		
		beansEntrySet = new ArrayList<Entry<String, Object>>(size);
		lifecycleAssociations = new ArrayList<List<Integer>>(size);
		beanLifecycleSubjectGroups = new ArrayList<BeanLifecycleSubjectGroup>(size);
		
		for(String beanId : beanRepositoryIdList.getRepositoryBeanIdMap().keySet()) {
			
			beanMap.put(beanId, null);
			
		}
		
		for(Entry<String, Object> beanMapEntry : beanMap.entrySet()) {
		
			BeanRepositoryId beanRepositoryId = beanRepositoryIdList.getRepositoryBeanId(beanMapEntry.getKey());
			
			beansEntrySet.set(beanRepositoryId.getId(), beanMapEntry);
		
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#addBean(org.milyn.javabean.repository.RepositoryMember, java.lang.Object)
	 */
	public void addBean(BeanRepositoryId beanRepositoryId, Object bean) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		AssertArgument.isNotNull(bean, "bean");
		
		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			throw new IllegalArgumentException("The BeanRepositoryId object does not belong to this BeanRepository");
		}
		
		int id = beanRepositoryId.getId();
		
		cleanAssociatedLifecycleBeans(id);
		
		beansEntrySet.get(beanRepositoryId.getId()).setValue(bean);
		
		notifyObservers(beanRepositoryId, BeanLifecycle.BEGIN, bean);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#containsBean(org.milyn.javabean.repository.RepositoryMember)
	 */
	public boolean containsBean(BeanRepositoryId beanRepositoryId) {
		int id = beanRepositoryId.getId();
		
		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			return false;
		}
		
		return beansEntrySet.size() >= id && beansEntrySet.get(beanRepositoryId.getId()) != null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#getBean(org.milyn.javabean.repository.RepositoryMember)
	 */
	public Object getBean(BeanRepositoryId beanRepositoryId) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		
		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			throw new IllegalArgumentException("The BeanRepositoryId object does not belong to this BeanRepository");
		}
		
		return beansEntrySet.get(beanRepositoryId.getId()).getValue();
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#replaceBean(org.milyn.javabean.repository.RepositoryMember, java.lang.Object)
	 */
	public void changeBean(BeanRepositoryId beanRepositoryId, Object bean) {
		AssertArgument.isNotNull(beanRepositoryId, "beanRepositoryId");
		AssertArgument.isNotNull(bean, "bean");
		
		if(beanRepositoryIdList != beanRepositoryId.getBeanRepositoryIdList()) {
			throw new IllegalArgumentException("The BeanRepositoryId object does not belong to this BeanRepository");
		}
		
		int id = beanRepositoryId.getId();
		
		if(beansEntrySet.get(id) == null) {
			beansEntrySet.get(beanRepositoryId.getId()).setValue(bean);

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

    	beansEntrySet.set(id, null);
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

	/**
	 * @return the beanMap
	 */
	public Map<String, Object> getBeanMap() {
		return Collections.unmodifiableMap(beanMap);
	}

}
