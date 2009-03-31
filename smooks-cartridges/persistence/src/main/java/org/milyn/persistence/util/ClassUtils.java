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
package org.milyn.persistence.util;



/**
 * @author maurice_zeijen
 *
 */
public final class ClassUtils {

	/**
	 * Checks if the class in the first parameter is assignable
	 * to one of the classes in the second or any later parameter.
	 *
	 * @param toFind
	 * @param classes
	 * @return
	 */
	public static boolean containsAssignableClass(final Class<?> toFind, final Class<?> ... classes) {
		return indexOffFirstAssignableClass(toFind, classes) != -1;
	}

	/**
	 *
	 * @param toFind
	 * @param classes
	 * @return
	 */
	public static int indexOffFirstAssignableClass(final Class<?> toFind, final Class<?> ... classes) {

		for(int i = 0; i < classes.length; i++) {
			final Class<?> cls = classes[i];

			if(cls.isAssignableFrom(toFind)) {
				return i;
			}

		}
		return -1;
	}


	/**
	 *
	 */
	private ClassUtils() {
	}

}
