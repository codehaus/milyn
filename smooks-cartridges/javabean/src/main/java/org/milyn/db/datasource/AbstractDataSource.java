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

    private static final String DS_CONTEXT_KEY_PREFIX = AbstractDataSource.class.getName() + "#datasource:";
    private static final String CONNECTION_CONTEXT_KEY_PREFIX = AbstractDataSource.class.getName() + "#connection:";

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        bind(executionContext);
    }

    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        unbind(executionContext);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        bind(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        unbind(executionContext);
    }

    private void bind(ExecutionContext executionContext) {
        executionContext.setAttribute(DS_CONTEXT_KEY_PREFIX + getName(), this);
    }

    private void unbind(ExecutionContext executionContext) {
        try {
            Connection connection = (Connection) executionContext.getAttribute(CONNECTION_CONTEXT_KEY_PREFIX + getName());
            if(connection != null) {
                try {
                    if(!isAutoCommit()) {
                        connection.commit();
                    }
                } finally {
                    executionContext.removeAttribute(CONNECTION_CONTEXT_KEY_PREFIX + getName());
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new SmooksException("Unable to unbind DataSource '" + getName() + "'.");
        } finally {
            executionContext.removeAttribute(DS_CONTEXT_KEY_PREFIX + getName());
        }
    }

    public static Connection getConnection(String dataSourceName, ExecutionContext executionContext) throws SmooksException {
        Connection connection = (Connection) executionContext.getAttribute(CONNECTION_CONTEXT_KEY_PREFIX + dataSourceName);

        if(connection == null) {
            AbstractDataSource datasource = (AbstractDataSource) executionContext.getAttribute(DS_CONTEXT_KEY_PREFIX + dataSourceName);

            if(datasource == null) {
                throw new SmooksException("DataSource '" + dataSourceName + "' not bound to context.  Configure an '" + AbstractDataSource.class.getName() +  "' implementation and target it at '$document'.");
            }

            try {
                connection = datasource.getConnection();
                connection.setAutoCommit(datasource.isAutoCommit());
            } catch (SQLException e) {
                throw new SmooksException("Unable to open connection to dataSource '" + dataSourceName + "'.", e);
            }
            executionContext.setAttribute(CONNECTION_CONTEXT_KEY_PREFIX + dataSourceName, connection);
        }

        return connection;
    }

    public abstract String getName();

    public abstract Connection getConnection() throws SQLException;

    public abstract boolean isAutoCommit();

}
