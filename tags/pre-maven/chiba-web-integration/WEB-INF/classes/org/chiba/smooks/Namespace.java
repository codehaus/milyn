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

package org.chiba.smooks;

/**
 * Namespace URI definitions. 
 * @author tfennelly
 */
public abstract class Namespace {

	/**
	 * XHTML namespace URI.
	 */
	public static final String XHTML = "http://www.w3.org/1999/xhtml".intern();
	/**
	 * XMLSchema namespace URI.
	 */
	public static final String XMLSCHEMA = "http://www.w3.org/2001/XMLSchema".intern();
	/**
	 * XMLSchema-instance namespace URI.
	 */
	public static final String XMLSCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance".intern();
	/**
	 * XForms namespace URI.
	 */
	public static final String XFORMS = "http://www.w3.org/2002/xforms".intern();
	/**
	 * Chiba namespace URI.
	 */
	public static final String CHIBA = "http://chiba.sourceforge.net/xforms".intern();
}
