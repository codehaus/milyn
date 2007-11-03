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
package org.milyn.delivery.sax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.AbstractParser;

/**
 * Smooks SAX data stream parser.
 * <p/>
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXParser extends AbstractParser {

    private static Log logger = LogFactory.getLog(SAXParser.class);

    public SAXParser(ExecutionContext execContext) {
        super(execContext);
    }

    public SAXParser(ExecutionContext execContext, SmooksResourceConfiguration saxDriverConfig) {
        super(execContext, saxDriverConfig);
    }

    
}
