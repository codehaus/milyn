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
 * Application.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Application implements Serializable, EDIWritable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String codeQualifier;

    public void write(Writer writer, Delimiters delimiters) throws IOException {
        if(id != null) {
            writer.write(id);
        }
        writer.write(delimiters.getComponent());
        if(codeQualifier != null) {
            writer.write(codeQualifier);
        }
    }

    public String getId() {
		return id;
	}

    public void setId(String ref) {
		this.id = ref;
	}

    public String getCodeQualifier() {
		return codeQualifier;
	}

    public void setCodeQualifier(String refQualifier) {
		this.codeQualifier = refQualifier;
	}
}
