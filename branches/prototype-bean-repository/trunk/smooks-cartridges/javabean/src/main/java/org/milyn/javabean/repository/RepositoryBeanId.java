/**
 * 
 */
package org.milyn.javabean.repository;



/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class RepositoryBeanId {
	
	private final int id;
	
	private final String beanId;
	
	private final RepositoryBeanIdList repositoryBeanIdList;
	
	/**
	 * @param id
	 * @param beanId
	 */
	public RepositoryBeanId(RepositoryBeanIdList repositoryBeanIdList, int id, String beanId) {
		this.repositoryBeanIdList = repositoryBeanIdList;
		this.id = id;
		this.beanId = beanId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the beanId
	 */
	public String getBeanId() {
		return beanId;
	}
	
	/**
	 * @return the repositoryBeanIdList
	 */
	public RepositoryBeanIdList getRepositoryBeanIdList() {
		return repositoryBeanIdList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 54 + id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		
		if(obj instanceof RepositoryBeanId == false) {
			return false;
		}
		RepositoryBeanId rhs = (RepositoryBeanId) obj;
		if(this.repositoryBeanIdList != rhs.repositoryBeanIdList) {
			return false;
		}
		if(this.beanId != rhs.beanId) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + ": " + beanId;
	}

}
