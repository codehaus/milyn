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
 * Syntax Identifier.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SyntaxIdentifier extends Identifier implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;

	private String serviceCodeListDirVersion;
	private String codedCharacterEncoding;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        if(getId() != null) {
            writer.write(getId());
        }
        writer.write(delimiters.getComponent());
        if(getVersionNum() != null) {
            writer.write(getVersionNum());
        }
        writer.write(delimiters.getComponent());
        if(getReleaseNum() != null) {
            writer.write(getReleaseNum());
        }
        writer.write(delimiters.getComponent());
        if(serviceCodeListDirVersion != null) {
            writer.write(serviceCodeListDirVersion);
        }
        writer.write(delimiters.getComponent());
        if(codedCharacterEncoding != null) {
            writer.write(codedCharacterEncoding);
        }
    }

    public String getServiceCodeListDirVersion() {
		return serviceCodeListDirVersion;
	}
	public void setServiceCodeListDirVersion(String serviceCodeListDirVersion) {
		this.serviceCodeListDirVersion = serviceCodeListDirVersion;
	}
	public String getCodedCharacterEncoding() {
		return codedCharacterEncoding;
	}
	public void setCodedCharacterEncoding(String codedCharacterEncoding) {
		this.codedCharacterEncoding = codedCharacterEncoding;
	}
}
