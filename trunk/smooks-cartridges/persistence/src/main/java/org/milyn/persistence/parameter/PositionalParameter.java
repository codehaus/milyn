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
package org.milyn.persistence.parameter;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class PositionalParameter implements Parameter<PositionalParameterIndex> {

	private final int index;

	private final PositionalParameterIndex containerIndex;

	protected PositionalParameter(PositionalParameterIndex containerIndex, int index) {
		this.containerIndex = containerIndex;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public PositionalParameterIndex getContainerIndex() {
		return containerIndex;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return index;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}

		if(obj instanceof PositionalParameter == false) {
			return false;
		}
		PositionalParameter rhs = (PositionalParameter) obj;
		if(this.index != rhs.index) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Integer.toString(index);
	}
}
