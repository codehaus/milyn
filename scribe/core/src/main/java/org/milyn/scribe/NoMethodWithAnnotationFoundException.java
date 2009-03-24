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
package org.milyn.scribe;

/**
 * Indicates that a no method was found with a certain Annotation when one was expected.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class NoMethodWithAnnotationFoundException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -8613863508327591649L;

	/**
	 *
	 */
	public NoMethodWithAnnotationFoundException() {
	}

	/**
	 * @param message
	 */
	public NoMethodWithAnnotationFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoMethodWithAnnotationFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoMethodWithAnnotationFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
