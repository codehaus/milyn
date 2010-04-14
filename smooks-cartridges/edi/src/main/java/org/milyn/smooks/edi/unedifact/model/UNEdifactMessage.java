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

/**
 * UN/EDIFACT message.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNEdifactMessage {

	private UNB interchangeHeader;
	private UNG groupHeader;
	private UNH messageHeader;
	private UNT messageTrailer;
	private Object message;
	
	public UNB getInterchangeHeader() {
		return interchangeHeader;
	}
	public void setInterchangeHeader(UNB interchangeHeader) {
		this.interchangeHeader = interchangeHeader;
	}
	public UNG getGroupHeader() {
		return groupHeader;
	}
	public void setGroupHeader(UNG groupHeader) {
		this.groupHeader = groupHeader;
	}
	public UNH getMessageHeader() {
		return messageHeader;
	}
	public void setMessageHeader(UNH messageHeader) {
		this.messageHeader = messageHeader;
	}
	public UNT getMessageTrailer() {
		return messageTrailer;
	}
	public void setMessageTrailer(UNT messageTrailer) {
		this.messageTrailer = messageTrailer;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}	
}