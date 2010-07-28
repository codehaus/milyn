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

import org.milyn.assertion.AssertArgument;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.smooks.edi.EDIWritable;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
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

    /**
     * Write the interchange to the specified writer.
     * <p/>
     * Uses the default UN/EDIFACT delimiter set.
     * 
     * @param writer The target writer.
     * @throws IOException Error writing interchange.
     */
    public void write(Writer writer) throws IOException {
        write(writer, null);
    }

    /**
     * Write the interchange to the specified writer.
     * @param writer The target writer.
     * @param delimiters The delimiters.
     * @throws IOException Error writing interchange.
     */
    public void write(Writer writer, Delimiters delimiters) throws IOException {
        AssertArgument.isNotNull(writer, "writer");

        if(delimiters != null) {
            // Write a UNA segment definition...
            writer.append("UNA");
            writer.append(delimiters.getComponent());
            writer.append(delimiters.getField());
            writer.append(".");
            writer.append(delimiters.getEscape());
            writer.append(" ");
            writer.append(delimiters.getSegment());
        } else {
            delimiters = UNEdifactInterchangeParser.defaultUNEdifactDelimiters;
        }

        if(interchangeHeader != null) {
            interchangeHeader.write(writer, delimiters);
        }

        UNEdifactMessage previousMessage = null;
        for(UNEdifactMessage message : messages) {

            Object messageObject = message.getMessage();
            if(messageObject == null) {
                throw new IOException("Cannot write null EDI message object.");
            } else if(!(messageObject instanceof EDIWritable)) {
                throw new IOException("Cannot write EDI message object type '" + messageObject.getClass().getName() + "'.  Type must implement '" + EDIWritable.class.getName() + "'.");
            }

            // Write group info...
            if(message.getGroupHeader() != null) {
                if(previousMessage == null) {
                    // Start new group..
                    message.getGroupHeader().write(writer, delimiters);
                } else if(message.getGroupHeader() != previousMessage.getGroupHeader()) {
                    if(previousMessage.getGroupHeader() != null) {
                        // Close out previous group...
                        previousMessage.getGroupTrailer().write(writer, delimiters);
                    }
                    // Start new group..
                    message.getGroupHeader().write(writer, delimiters);
                } else {
                    // The message is part of the same group as the previous message...
                }
            } else if(previousMessage != null && previousMessage.getGroupHeader() != null) {
                // Close out previous group...
                previousMessage.getGroupTrailer().write(writer, delimiters);
            }

            // Write the message...
            if(message.getMessageHeader() != null) {
                message.getMessageHeader().write(writer, delimiters);
            }
            ((EDIWritable)messageObject).write(writer, delimiters);
            if(message.getMessageTrailer() != null) {
                message.getMessageTrailer().write(writer, delimiters);
            }

            // Capture a ref to the message so its group info can be checked
            // against the next message, or closed if it's the last message...
            previousMessage = message;
        }

        // Close out the group of the last message in the interchange (if it's in a group)...
        if(previousMessage != null && previousMessage.getGroupTrailer() != null) {
            // Close out previous group...
            previousMessage.getGroupTrailer().write(writer, delimiters);
        }

        if(interchangeTrailer != null) {
            interchangeTrailer.write(writer, delimiters);
        }
    }
}
