/**
 * 
 */
package org.milyn.javabean.repository;



/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanRepositoryId {
	
	private final int id;
	
	private final String beanId;
	
	private final BeanRepositoryIdList beanRepositoryIdList;
	
	/**
	 * @param id
	 * @param beanId
	 */
	public BeanRepositoryId(BeanRepositoryIdList beanRepositoryIdList, int id, String beanId) {
		this.beanRepositoryIdList = beanRepositoryIdList;
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
	 * @return the beanRepositoryIdList
	 */
	public BeanRepositoryIdList getBeanRepositoryIdList() {
		return beanRepositoryIdList;
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
		
		if(obj instanceof BeanRepositoryId == false) {
			return false;
		}
		BeanRepositoryId rhs = (BeanRepositoryId) obj;
		if(this.beanRepositoryIdList != rhs.beanRepositoryIdList) {
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
