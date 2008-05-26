/**
 *
 */
package org.milyn.javabean.repository;



/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class BeanId {

	private final int index;

	private final String name;

	private final BeanIdList beanIdList;

	/**
	 * @param index
	 * @param name
	 */
	public BeanId(BeanIdList beanIdList, int index, String beanId) {
		this.beanIdList = beanIdList;
		this.index = index;
		this.name = beanId;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the beanIdList
	 */
	public BeanIdList getBeanIdList() {
		return beanIdList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 54 + index;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}

		if(obj instanceof BeanId == false) {
			return false;
		}
		BeanId rhs = (BeanId) obj;
		if(this.beanIdList != rhs.beanIdList) {
			return false;
		}
		if(this.name != rhs.name) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return index + ": " + name;
	}

}
