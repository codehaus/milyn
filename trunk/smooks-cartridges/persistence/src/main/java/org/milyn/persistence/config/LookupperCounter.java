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
package org.milyn.persistence.config;

import org.milyn.container.ApplicationContext;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class LookupperCounter {

	private static final String CONTEXT = LookupperCounter.class.getName() + "#Context";

	private int lookupperCount = 0;

	public int incrementLookupperCount() {
		return lookupperCount++;
	}

	public int getCurrentLookupperCount() {
		return lookupperCount;
	}

	public static LookupperCounter getLookupperCounter(ApplicationContext applicationContext) {

		LookupperCounter counter = (LookupperCounter) applicationContext.getAttribute(CONTEXT);

		if(counter == null) {

			counter = new LookupperCounter();

			applicationContext.setAttribute(CONTEXT, counter);

		}

		return counter;

	}

}
