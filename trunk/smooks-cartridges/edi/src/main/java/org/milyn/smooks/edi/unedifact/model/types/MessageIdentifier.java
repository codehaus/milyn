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
 * Message Identifier.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MessageIdentifier extends SourceIdentifier {

	private String associationAssignedCode;
	private String codeListDirVersionNum;
	private String typeSubFunctionId;
	
	public String getAssociationAssignedCode() {
		return associationAssignedCode;
	}
	public void setAssociationAssignedCode(String associationAssignedCode) {
		this.associationAssignedCode = associationAssignedCode;
	}
	public String getCodeListDirVersionNum() {
		return codeListDirVersionNum;
	}
	public void setCodeListDirVersionNum(String codeListDirVersionNum) {
		this.codeListDirVersionNum = codeListDirVersionNum;
	}
	public String getTypeSubFunctionId() {
		return typeSubFunctionId;
	}
	public void setTypeSubFunctionId(String typeSubFunctionId) {
		this.typeSubFunctionId = typeSubFunctionId;
	}
}
