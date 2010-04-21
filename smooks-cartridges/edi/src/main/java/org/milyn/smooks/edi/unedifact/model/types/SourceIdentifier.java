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

import java.io.Serializable;

/**
 * Sender Identifier.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SourceIdentifier extends Identifier implements Serializable {

	private static final long serialVersionUID = 1L;

	private String controllingAgencyCode;

	public String getControllingAgencyCode() {
		return controllingAgencyCode;
	}
	public void setControllingAgencyCode(String controllingAgencyCode) {
		this.controllingAgencyCode = controllingAgencyCode;
	}
}
