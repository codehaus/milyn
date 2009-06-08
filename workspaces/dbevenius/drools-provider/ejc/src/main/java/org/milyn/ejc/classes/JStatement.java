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
package org.milyn.ejc.classes;

/**
 * JStatement are the statements found in {@link org.milyn.ejc.classes.JMethod}.
 * @see org.milyn.ejc.classes.JMethod
 * @author bardl
 */
public class JStatement {
    public static String STATEMENT_SUFFIX = ";";

    private String statement;

    public JStatement(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public String toString() {
        return statement;
    }
}
