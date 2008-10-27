/*
	Milyn - Copyright (C) 2006

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

package org.milyn.edisax;

import org.xml.sax.SAXException;
import org.milyn.edisax.model.internal.Edimap;

/**
 * EDI message parsing exception.
 * @author tfennelly
 */
public class EDIParseException extends SAXException {

	private static final long serialVersionUID = 1L;

    /**
	 * Public constructor.	 
	 * @param message Exception message.
     * @param cause Exception cause
	 */
	public EDIParseException(String message, Exception cause) {
		super(message, cause);
	}

    /**
	 * Public constructor.
	 * @param mappingModel The mapping model for the message on which the exception was encoutered.
	 * @param message Exception message.
	 */
	public EDIParseException(Edimap mappingModel, String message) {
		super(getMessagePrefix(mappingModel) + "  " + message);
	}

	/**
	 * Public constructor.
	 * @param mappingModel The mapping model for the message on which the exception was encoutered.
	 * @param message Exception message.
	 * @param cause Exception cause.
	 */
	public EDIParseException(Edimap mappingModel, String message, Exception cause) {
		super(getMessagePrefix(mappingModel) + "  " + message, cause);
	}

	private static String getMessagePrefix(Edimap mappingModel) {
		return "EDI message processing failed [" + mappingModel.getDescription().getName() + "][" + mappingModel.getDescription().getVersion() + "].";
	}
}
