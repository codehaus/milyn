/*
	Milyn - Copyright (C) 2006 - 2010

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

import java.util.List;
import java.util.Vector;

/**
 * CSSStylesheet implementation.
 * <p/>
 * Maintains a list of CSS {@link CSSRule Rules}.
 * @author tfennelly
 */
public class CSSStylesheet {
	private List rules = new Vector();

	/**
	 * Get the {@link CSSRule} list associated with this stylesheet.
	 * @return {@link CSSRule} list.
	 */
	public List getRules() {
		return rules;
	}
	
	protected void addRule(CSSRule rule) {
		rules.add(rule);
	}
}
