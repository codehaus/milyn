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

import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.SACMediaList;

public class CSSRule {

	private ExtendedSelector selector;
	private CSSProperty property;
	private SACMediaList mediaList;

	/**
	 * CSS CSSRule Constructor.
	 * @param selector Style selector.
	 * @param property Style property.
	 * @param mediaList Media list to which this rule applies, or null if 
	 * it applies to all media.
	 */
	protected CSSRule(ExtendedSelector selector, CSSProperty property, SACMediaList mediaList) {
		this.selector = selector;
		this.property = property;
		this.mediaList = mediaList;
	}
	
	public ExtendedSelector getSelector() {
		return selector;
	}
	
	/**
	 * Get the {@link CSSProperty} for this rule. 
	 * @return CSSRule {@link CSSProperty}.
	 */
	public CSSProperty getProperty() {
		return property;
	}
	
	public SACMediaList getMediaList() {
		return mediaList;
	}
}
