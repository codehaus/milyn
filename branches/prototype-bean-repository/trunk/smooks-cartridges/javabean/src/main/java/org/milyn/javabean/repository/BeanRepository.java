/**
 * 
 */
package org.milyn.javabean.repository;

import java.util.ArrayList;
import java.util.List;

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
	
	private final List<Object> beans;
	
	private final List<List<Integer>> lifecycleAssociations;

	private final List<BeanLifecycleSubjectGroup> beanLifecycleSubjectGroups;

	
	public BeanRepository(ExecutionContext executionContext, int size) {
		
		this.executionContext = executionContext;
		
		beans = new ArrayList<Object>(size);
		lifecycleAssociations = new ArrayList<List<Integer>>(size);
		beanLifecycleSubjectGroups = new ArrayList<BeanLifecycleSubjectGroup>(size);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#addBean(org.milyn.javabean.repository.RepositoryMember, java.lang.Object)
	 */
	public void addBean(RepositoryBeanId repositoryBeanId, Object bean) {
		
		int id = repositoryBeanId.getId();
		
		cleanAssociatedLifecycleBeans(id);
		
		beans.set(repositoryBeanId.getId(), bean);
		
		notifyObservers(repositoryBeanId, BeanLifecycle.BEGIN, bean);
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#containsBean(org.milyn.javabean.repository.RepositoryMember)
	 */
	public boolean containsBean(RepositoryBeanId repositoryBeanId) {
		int id = repositoryBeanId.getId();
		
		return beans.size() >= id && beans.get(repositoryBeanId.getId()) != null;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#getBean(org.milyn.javabean.repository.RepositoryMember)
	 */
	public Object getBean(RepositoryBeanId repositoryBeanId) {
		return beans.get(repositoryBeanId.getId());
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.repository.Repository#replaceBean(org.milyn.javabean.repository.RepositoryMember, java.lang.Object)
	 */
	public void changeBean(RepositoryBeanId repositoryBeanId, Object bean) {
		
		int id = repositoryBeanId.getId();
		
		if(beans.get(id) == null) {
			beans.set(repositoryBeanId.getId(), bean);

			notifyObservers(repositoryBeanId, BeanLifecycle.CHANGE, bean);
    	} else {
    		throw new IllegalStateException("The bean '" + repositoryBeanId + "' can't be changed because it isn't in the repository.");
    	}
	}
	
	public void associateLifecycles(RepositoryBeanId parentRepositoryBeanId, RepositoryBeanId childRepositoryBeanId) {
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
	
	public void addBeanLifecycleObserver(RepositoryBeanId repositoryBeanId, BeanLifecycle lifecycle, String observerId, boolean notifyOnce, RepositoryBeanLifecycleObserver observer) {
    	AssertArgument.isNotNull(repositoryBeanId, "repositoryBeanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(repositoryBeanId, true);
    	subjectGroup.addObserver(lifecycle, observerId, notifyOnce, observer);
    }

	public void removeBeanLifecycleObserver(RepositoryBeanId repositoryBeanId, BeanLifecycle lifecycle,String observerId) {
    	AssertArgument.isNotNull(repositoryBeanId, "repositoryBeanId");

    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(repositoryBeanId, false);

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

    	beans.set(id, null);
    }

    private void notifyObservers(RepositoryBeanId repositoryBeanId, BeanLifecycle lifecycle, Object bean) {
    	BeanLifecycleSubjectGroup subjectGroup = getBeanLifecycleSubjectGroup(repositoryBeanId, false);

    	if(subjectGroup != null) {
    		subjectGroup.notifyObservers(lifecycle, bean);
    	}
    }

    private BeanLifecycleSubjectGroup getBeanLifecycleSubjectGroup(RepositoryBeanId repositoryBeanId, boolean createIfNotExist) {
    	BeanLifecycleSubjectGroup subjectGroup = beanLifecycleSubjectGroups.get(repositoryBeanId.getId());

    	if(subjectGroup == null && createIfNotExist) {
    		subjectGroup = new BeanLifecycleSubjectGroup(executionContext, repositoryBeanId);

    		beanLifecycleSubjectGroups.set(repositoryBeanId.getId(), subjectGroup);
    		    		
    	}

    	return subjectGroup;
    }

}
