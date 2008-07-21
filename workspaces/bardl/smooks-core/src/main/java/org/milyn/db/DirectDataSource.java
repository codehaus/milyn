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
package org.milyn.db;

import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.util.ClassUtil;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.event.report.annotation.VisitAfterReport;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Direct DataSource.
 * <p/>
 * Configured with a specific JDBC driver plus username etc.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@VisitBeforeReport(summary = "Bind DirectDataSource <b>${resource.parameters.datasource}</b> to ExecutionContext.", detailTemplate = "reporting/DirectDataSource_before.html")
@VisitAfterReport(summary = "Cleaning up DirectDataSource <b>${resource.parameters.datasource}</b>. Includes performing commit/rollback etc.", detailTemplate = "reporting/DirectDataSource_after.html")
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

    public String getName() {
        return datasourceName;
    }

    @Initialize
    public void registerDriver() throws SQLException {
        Driver driverInstance;

        try {
            driverInstance = (Driver) ClassUtil.forName(driver, getClass()).newInstance();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to register JDBC driver '" + driver + "'.  Driver class not available on classpath.");
        } catch (Exception e) {
            throw new SQLException("Failed to register JDBC driver '" + driver + "'.  Unable to create instance of driver class.");
        }
        
        DriverManager.registerDriver(driverInstance);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }
}
