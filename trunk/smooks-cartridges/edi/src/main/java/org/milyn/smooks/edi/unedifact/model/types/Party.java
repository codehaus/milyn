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
package org.milyn.smooks.edi.unedifact.model.types;

/**
 * Interchange Party (sender or recipient).
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Party {
	
	private String id;
	private String codeQualifier;
	private String internalId;
	private String internalSubId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodeQualifier() {
		return codeQualifier;
	}
	public void setCodeQualifier(String codeQualifier) {
		this.codeQualifier = codeQualifier;
	}
	public String getInternalId() {
		return internalId;
	}
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}
	public String getInternalSubId() {
		return internalSubId;
	}
	public void setInternalSubId(String internalSubId) {
		this.internalSubId = internalSubId;
	}
}
