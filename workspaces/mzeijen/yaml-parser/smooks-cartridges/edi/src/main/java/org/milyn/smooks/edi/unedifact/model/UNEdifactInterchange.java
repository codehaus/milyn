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
import java.util.List;

/**
 * UN/EDIFACT message interchange.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNEdifactInterchange implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private UNB interchangeHeader;
	private UNZ interchangeTrailer;
	private List<UNEdifactMessage> messages;
	
	/**
	 * Get the interchange header object.
	 * @return The interchange header instance.
	 */
	public UNB getInterchangeHeader() {
		return interchangeHeader;
	}
	
	/**
	 * Set the interchange header object.
	 * @param interchangeHeader The interchange header instance.
	 */
	public void setInterchangeHeader(UNB interchangeHeader) {
		this.interchangeHeader = interchangeHeader;
	}
	
	/**
	 * Get the interchange trailer object.
	 * @return The interchange trailer instance.
	 */
	public UNZ getInterchangeTrailer() {
		return interchangeTrailer;
	}

	/**
	 * Set the interchange trailer object.
	 * @param interchangeTrailer The interchange trailer instance.
	 */
	public void setInterchangeTrailer(UNZ interchangeTrailer) {
		this.interchangeTrailer = interchangeTrailer;
	}
	
	/**
	 * Get the List of interchange messages.
	 * <p/>
	 * The list is ungrouped.  {@link UNG Interchange group} information is on each
	 * {@link UNEdifactMessage} message instance, if the message is part
	 * of a group of messages.
	 * 
	 * @return The List of interchange messages.
	 */
	public List<UNEdifactMessage> getMessages() {
		return messages;
	}
	
	/**
	 * Set the List of interchange messages.
	 * 
	 * @param messages The List of interchange messages.
	 */
	public void setMessages(List<UNEdifactMessage> messages) {
		this.messages = messages;
	}	
}
