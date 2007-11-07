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

import org.milyn.delivery.Filter;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Smooks SAX Filter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksSAXFilter extends Filter {

    public SmooksSAXFilter(ExecutionContext executionContext) {
    }

    public void doFilter(Source source, Result result) throws SmooksException {
        if (!(source instanceof StreamSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported by the SAX Filter. Only supports StreamSource at present.");
        }
        if (!(result instanceof StreamResult) && result != null) {
            throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported by the SAX Filter. Only supports StreamResult at present.");
        }

        
    }
}
