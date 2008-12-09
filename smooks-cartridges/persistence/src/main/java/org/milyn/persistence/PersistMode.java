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
package org.milyn.persistence;

import org.milyn.javabean.DataDecodeException;


/**
 * @author maurice_zeijen
 *
 */
public enum PersistMode {
	PERSIST,
	MERGE;

	public static final String PERSIST_STR = "persist";
	public static final String MERGE_STR = "merge";

	/**
	 * A Data decoder for this Enum
	 *
	 * @author maurice_zeijen
	 *
	 */
	public static class DataDecoder implements org.milyn.javabean.DataDecoder {

		/* (non-Javadoc)
		 * @see org.milyn.javabean.DataDecoder#decode(java.lang.String)
		 */
		public Object decode(final String data) throws DataDecodeException {
			final String value = data.toUpperCase();

			return valueOf(value);
		}

	}



}
