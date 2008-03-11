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

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.delivery.annotation.Initialize;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Jndi based DataSource.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JndiDataSource extends AbstractDataSource {

    @ConfigParam(name = "datasource")
    private String datasourceName;

    @ConfigParam
    private boolean autoCommit;

    private DataSource datasource;

    @Initialize
    public void intitialize() {
        InitialContext context = null;

        try {
            context = new InitialContext();
            datasource = (DataSource) context.lookup(datasourceName);
        } catch (NamingException e) {
            throw new SmooksConfigurationException("DataSource lookup failed for DataSource '" + datasourceName + "'.  Make sure you have the DataSource descriptor deployed and that the JNDI names match.", e);
        } finally {
            if(context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    throw new SmooksConfigurationException("Error closing Naming Context after looking up DataSource '" + datasourceName + "'.", e);
                }
            }
        }
    }

    public String getName() {
        return datasourceName;
    }

    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }
}
