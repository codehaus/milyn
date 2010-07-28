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

import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.smooks.edi.EDIWritable;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

/**
 * Interchange Party (sender or recipient).
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Party implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String codeQualifier;
	private String internalId;
	private String internalSubId;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        if(id != null) {
            writer.write(id);
        }
        writer.write(delimiters.getComponent());
        if(codeQualifier != null) {
            writer.write(codeQualifier);
        }
        writer.write(delimiters.getComponent());
        if(internalId != null) {
            writer.write(internalId);
        }
        writer.write(delimiters.getComponent());
        if(internalSubId != null) {
            writer.write(internalSubId);
        }
    }

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
