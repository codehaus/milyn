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
package org.milyn.xml;

import org.milyn.container.ExecutionContext;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Cloneable Reader interface.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface CloneableReader {
	
	/**
	 * Clone The reader instance.
	 * @param execContext Execution context for which the reader is being cloned.
	 * @param handler The content handler for the instance.
	 * @return A clone of the reader instance.
	 */
	XMLReader cloneReader(ExecutionContext execContext, DefaultHandler2 handler);
}
