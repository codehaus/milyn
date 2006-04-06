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

package org.milyn.magger;

import org.w3c.css.sac.LexicalUnit;

public class CSSProperty {
	private String name; 
	private LexicalUnit value; 
	private boolean important;
	
	/**
	 * Constructor.
	 * @param name CSSProperty name.
	 * @param value CSSProperty value.
	 * @param important Important property flag. 
	 */
	protected CSSProperty(String name, LexicalUnit value, boolean important) {
		this.name = name;
		this.value = value;
		this.important = important;
	}

	public String getName() {
		return name;
	}
	public LexicalUnit getValue() {
		return value;
	}
	public boolean isImportant() {
		return important;
	}
}
