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
package org.milyn.ect;

import org.milyn.edisax.model.internal.Edimap;

import java.io.IOException;
import java.util.Set;

/**
 * EDI Specification Reader.
 *
 * @author bardl
 */
public interface EdiSpecificationReader {

    /**
     * Get a list of the names of the messages defined in the EDI Specification (e.g. UN/EDIFACT
     * specification) instance.
     *
     * @return The namels of the messages.
     */
    Set<String> getMessageNames();

    /**
     * Get the EDI Mapping Model for the named message.
     * <p/>
     * The Mapping Model is constructed after converting/translating the
     * message definition in the specification.  This is the "normalized"
     * definition of any EDI message in Smooks.  From the EDI Mapping Model,
     * EJC can be used to construct Java Bindings etc.
     *
     * @param messageName The name of the message.
     * @return The messages EDI Mapping Model.
     * @throws IOException Error reading/converting the message definition to
     *                     an EDI Mapping Model.
     */
    Edimap getMappingModel(String messageName) throws IOException;
}
