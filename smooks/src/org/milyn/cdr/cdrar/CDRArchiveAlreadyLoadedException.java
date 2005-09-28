/*
	Milyn - Copyright (C) 2003

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

package org.milyn.cdr.cdrar;

/**
 * CDRArchive archive already loaded exception.
 * @author tfennelly
 */
public class CDRArchiveAlreadyLoadedException extends Exception {

	/**
	 * Public constructor.
	 * @param message Exception message.
	 */
	public CDRArchiveAlreadyLoadedException(String message) {
		super(message);
	}
}
