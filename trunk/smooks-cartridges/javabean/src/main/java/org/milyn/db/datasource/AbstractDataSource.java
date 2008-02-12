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
package org.milyn.db.datasource;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.w3c.dom.Element;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource management resource.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractDataSource implements SAXElementVisitor, DOMElementVisitor {

    private static final String CONTEXT_KEY_PREFIX = AbstractDataSource.class.getName() + "#connection:";

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        bindConnection(executionContext);
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        unbindConnection(executionContext);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        bindConnection(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        unbindConnection(executionContext);
    }

    private void bindConnection(ExecutionContext executionContext) {
        try {
            Connection connection = getConnection();
            connection.setAutoCommit(isAutoCommit());
            executionContext.setAttribute(CONTEXT_KEY_PREFIX + getName(), connection);
        } catch (SQLException e) {
            throw new SmooksException("Unable to bind DataSource '" + getName() + "'.", e);
        }
    }

    private void unbindConnection(ExecutionContext executionContext) {
        try {
            Connection connection = getConnection(getName(), executionContext);
            try {
                if(!isAutoCommit()) {
                    connection.commit();
                }
            } finally {
                executionContext.removeAttribute(CONTEXT_KEY_PREFIX + getName());
                connection.close();
            }
        } catch (SQLException e) {
            throw new SmooksException("Unable to unbind DataSource '" + getName() + "'.");
        }
    }

    public static Connection getConnection(String dataSourceName, ExecutionContext executionContext) {
        Connection connection = (Connection) executionContext.getAttribute(CONTEXT_KEY_PREFIX + dataSourceName);

        if(connection == null) {
            throw new SmooksException("Connection to DataSource '" + dataSourceName + "' not bound to context.  Configure an '" + AbstractDataSource.class.getName() +  "' implementation and target it at '$document'.");
        }

        return connection;
    }

    public abstract String getName();

    public abstract Connection getConnection() throws SQLException;

    public abstract boolean isAutoCommit();

}
