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
package org.milyn.edisax.unedifact;

import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser.MappingRegistry;
import org.milyn.edisax.util.EDIUtils;
import org.xml.sax.SAXException;

/**
 * UN/EDIFACT utility methods.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class UNEdifactUtil {

	public static EdifactModel getMappingModel(String messageName, Delimiters delimiters, MappingRegistry reg) throws SAXException {
		String[] nameComponents = EDIUtils.split(messageName, delimiters.getComponent(), delimiters.getEscape());
		StringBuilder lookupNameBuilder = new StringBuilder();
		
		// First 4 components are mandatory...we use those as the lookup...
		for(int i = 0; i < 4; i++) {
			if(i > 0) {
				lookupNameBuilder.append(':');
			}
			lookupNameBuilder.append(nameComponents[i]);
		}
		String lookupName = lookupNameBuilder.toString().trim();
		return reg.getModel(lookupName);
	}
}
