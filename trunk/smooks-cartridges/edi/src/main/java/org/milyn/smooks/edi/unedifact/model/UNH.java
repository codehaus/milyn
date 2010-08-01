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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.milyn.edisax.model.internal.DelimiterType;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.util.EDIUtils;
import org.milyn.smooks.edi.EDIWritable;
import org.milyn.smooks.edi.unedifact.model.types.MessageIdentifier;
import org.milyn.smooks.edi.unedifact.model.types.SourceIdentifier;
import org.milyn.smooks.edi.unedifact.model.types.TransferStatus;

/**
 * Message Header.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNH implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;

	private String messageRefNum;
	private MessageIdentifier messageIdentifier;
	private String commonAccessRef;
	private TransferStatus transferStatus;
	private SourceIdentifier subset;
	private SourceIdentifier implementationGuideline;
	private SourceIdentifier scenario;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        Writer nodeWriter = new StringWriter();
        List<String> nodeTokens = new ArrayList<String>();

        nodeWriter.write("UNH");
        nodeWriter.write(delimiters.getField());
        if(messageRefNum != null) {
            nodeWriter.write(messageRefNum);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        nodeWriter.write(delimiters.getField());
        if(messageIdentifier != null) {
            messageIdentifier.write(nodeWriter, delimiters);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        nodeWriter.write(delimiters.getField());
        if(commonAccessRef != null) {
            nodeWriter.write(commonAccessRef);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        nodeWriter.write(delimiters.getField());
        if(transferStatus != null) {
            transferStatus.write(nodeWriter, delimiters);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        nodeWriter.write(delimiters.getField());
        if(subset != null) {
            subset.write(nodeWriter, delimiters);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        nodeWriter.write(delimiters.getField());
        if(implementationGuideline != null) {
            implementationGuideline.write(nodeWriter, delimiters);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        nodeWriter.write(delimiters.getField());
        if(scenario != null) {
            scenario.write(nodeWriter, delimiters);
            nodeTokens.add(nodeWriter.toString());
            ((StringWriter)nodeWriter).getBuffer().setLength(0);
        }
        
        nodeTokens.add(nodeWriter.toString());

        writer.write(EDIUtils.concatAndTruncate(nodeTokens, DelimiterType.FIELD, delimiters));
        writer.write(delimiters.getSegment());
    }

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
