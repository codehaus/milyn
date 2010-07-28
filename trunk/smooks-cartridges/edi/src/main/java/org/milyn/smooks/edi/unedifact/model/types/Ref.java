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
 * Reference.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Ref implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;

	private String ref;
    private String refQualifier;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        if(ref != null) {
            writer.write(ref);
        }
        writer.write(delimiters.getComponent());
        if(refQualifier != null) {
            writer.write(refQualifier);
        }
    }

    public String getRef() {
		return ref;
	}

    public void setRef(String ref) {
		this.ref = ref;
	}

    public String getRefQualifier() {
		return refQualifier;
	}

    public void setRefQualifier(String refQualifier) {
		this.refQualifier = refQualifier;
	}
}
