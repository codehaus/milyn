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

import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Initialize;
import org.milyn.util.ClassUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Direct DataSource.
 * <p/>
 * Configured with a specific JDBC driver plus username etc.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DirectDataSource extends AbstractDataSource {

    @ConfigParam(name = "datasource")
    private String datasourceName;

    @ConfigParam
    private boolean autoCommit;

    @ConfigParam
    private String driver;

    @ConfigParam
    private String url;

    @ConfigParam
    private String username;

    @ConfigParam
    private String password;

    @Initialize
    public void intitialize() throws ClassNotFoundException {
        // Register the driver...
        ClassUtil.forName(driver, getClass());
    }

    public String getName() {
        return datasourceName;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }
}
