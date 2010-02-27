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
package org.milyn.routing.db.JIRAs.MILYN_345;

import junit.framework.TestCase;
import org.milyn.routing.db.ResultsetRowSelector;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class MILYN_345_Test extends TestCase {

    public void test_where() {
        ResultsetRowSelector selector = new ResultsetRowSelector();

        selector.setWhereClause("a == b");
        assertFalse(selector.consumes("x"));
        assertTrue(selector.consumes("b"));
    }

    public void test_failError() {
        ResultsetRowSelector selector = new ResultsetRowSelector();

        selector.setFailedSelectError("this is an error on ${productId}");
        assertFalse(selector.consumes("zzzzzzzzzz"));
        assertTrue(selector.consumes("productId"));
    }
}
