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

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.smooks.edi.EDIWritable;
import org.milyn.smooks.edi.unedifact.model.types.Application;
import org.milyn.smooks.edi.unedifact.model.types.DateTime;
import org.milyn.smooks.edi.unedifact.model.types.MessageVersion;

/**
 * Group Header.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNG implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;

	private String groupId;
	private Application senderApp;
	private Application recipientApp;
	private DateTime date;
	private String groupRef;
	private String controllingAgencyCode;
	private MessageVersion messageVersion;
	private String applicationPassword;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        writer.write("UNG");
        writer.write(delimiters.getField());
        if(groupId != null) {
            writer.write(groupId);
        }
        writer.write(delimiters.getField());
        if(senderApp != null) {
            senderApp.write(writer, delimiters);
        }
        writer.write(delimiters.getField());
        if(recipientApp != null) {
            recipientApp.write(writer, delimiters);
        }
        writer.write(delimiters.getField());
        if(date != null) {
            date.write(writer, delimiters);
        }
        writer.write(delimiters.getField());
        if(groupRef != null) {
            writer.write(groupRef);
        }
        writer.write(delimiters.getField());
        if(controllingAgencyCode != null) {
            writer.write(controllingAgencyCode);
        }
        writer.write(delimiters.getField());
        if(messageVersion != null) {
            messageVersion.write(writer, delimiters);
        }
        writer.write(delimiters.getField());
        if(applicationPassword != null) {
            writer.write(applicationPassword);
        }
        writer.write(delimiters.getSegment());
    }

	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Application getSenderApp() {
		return senderApp;
	}
	public void setSenderApp(Application senderApp) {
		this.senderApp = senderApp;
	}
	public Application getRecipientApp() {
		return recipientApp;
	}
	public void setRecipientApp(Application recipientApp) {
		this.recipientApp = recipientApp;
	}
	public DateTime getDate() {
		return date;
	}
	public void setDate(DateTime date) {
		this.date = date;
	}
	public String getGroupRef() {
		return groupRef;
	}
	public void setGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}
	public String getControllingAgencyCode() {
		return controllingAgencyCode;
	}
	public void setControllingAgencyCode(String controllingAgencyCode) {
		this.controllingAgencyCode = controllingAgencyCode;
	}
	public MessageVersion getMessageVersion() {
		return messageVersion;
	}
	public void setMessageVersion(MessageVersion messageVersion) {
		this.messageVersion = messageVersion;
	}
	public String getApplicationPassword() {
		return applicationPassword;
	}
	public void setApplicationPassword(String applicationPassword) {
		this.applicationPassword = applicationPassword;
	}
}
