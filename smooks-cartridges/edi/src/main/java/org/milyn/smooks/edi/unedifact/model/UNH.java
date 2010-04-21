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
package org.milyn.smooks.edi.unedifact.model;

import java.io.Serializable;

import org.milyn.smooks.edi.unedifact.model.types.MessageIdentifier;
import org.milyn.smooks.edi.unedifact.model.types.SourceIdentifier;
import org.milyn.smooks.edi.unedifact.model.types.TransferStatus;

/**
 * Message Header.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNH implements Serializable {

	private static final long serialVersionUID = 1L;

	private String messageRefNum;
	private MessageIdentifier messageIdentifier;
	private String commonAccessRef;
	private TransferStatus transferStatus;
	private SourceIdentifier subset;
	private SourceIdentifier implementationGuideline;
	private SourceIdentifier scenario;

	public String getMessageRefNum() {
		return messageRefNum;
	}
	public void setMessageRefNum(String messageRefNum) {
		this.messageRefNum = messageRefNum;
	}
	public MessageIdentifier getMessageIdentifier() {
		return messageIdentifier;
	}
	public void setMessageIdentifier(MessageIdentifier messageIdentifier) {
		this.messageIdentifier = messageIdentifier;
	}
	public String getCommonAccessRef() {
		return commonAccessRef;
	}
	public void setCommonAccessRef(String commonAccessRef) {
		this.commonAccessRef = commonAccessRef;
	}
	public TransferStatus getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(TransferStatus transferStatus) {
		this.transferStatus = transferStatus;
	}
	public SourceIdentifier getSubset() {
		return subset;
	}
	public void setSubset(SourceIdentifier subset) {
		this.subset = subset;
	}
	public SourceIdentifier getImplementationGuideline() {
		return implementationGuideline;
	}
	public void setImplementationGuideline(SourceIdentifier implementationGuideline) {
		this.implementationGuideline = implementationGuideline;
	}
	public SourceIdentifier getScenario() {
		return scenario;
	}
	public void setScenario(SourceIdentifier scenario) {
		this.scenario = scenario;
	}
}
