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
package org.milyn.routing.db;

import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ExecutionContext;
import org.milyn.container.ApplicationContext;
import org.milyn.db.AbstractDataSource;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.*;
import org.milyn.javabean.BeanAccessor;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.w3c.dom.Element;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SQLExecutor Visitor.
 * <p/>
 * Supports extraction and persistence to a Database.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@VisitBeforeIf(	condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value == 'true'")
@VisitAfterIf(	condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value != 'true'")
public class SQLExecutor implements SAXVisitBefore, SAXVisitAfter, DOMElementVisitor {

    @ConfigParam
    private String datasource;

    @ConfigParam
    private String statement;
    private StatementExec statementExec;
    private String rsAppContextKey;

    @ConfigParam(use = ConfigParam.Use.OPTIONAL)
    private String resultSetName;

    @ConfigParam(defaultVal = "EXECUTION", choice = {"EXECUTION", "APPLICATION"}, decoder = ResultSetScopeDecoder.class)
    private ResultSetScope resultSetScope;

    @ConfigParam(defaultVal = "900000")
    private long resultSetTTL;

    private enum ResultSetScope {
        EXECUTION,
        APPLICATION
    }

    @Initialize
    public void intitialize() throws SmooksConfigurationException {
        statementExec = new StatementExec(statement);
        if(statementExec.getStatementType() == StatementType.QUERY && resultSetName == null) {
            throw new SmooksConfigurationException("Sorry, query statements must be accompanied by a 'resultSetName' property, under whose value the query results are bound.");
        }

        rsAppContextKey = datasource + ":" + statement;
    }

    public void visitBefore(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
        executeSQL(executionContext);
    }

    public void visitAfter(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
        executeSQL(executionContext);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        executeSQL(executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        executeSQL(executionContext);
    }

    private void executeSQL(ExecutionContext executionContext) throws SmooksException {
        Connection connection = AbstractDataSource.getConnection(datasource, executionContext);
        Map beans = BeanAccessor.getBeanMap(executionContext);

        try {
            if(!statementExec.isJoin()) {
                if(statementExec.getStatementType() == StatementType.QUERY) {
                    if(resultSetScope == ResultSetScope.EXECUTION) {
                        beans.put(resultSetName, statementExec.executeUnjoinedQuery(connection));
                    } else {
                        List<Map<String, Object>> resultMap;
                        // Cached in the application context...
                        ApplicationContext appContext = executionContext.getContext();
                        ResultSetContextObject rsContextObj = ResultSetContextObject.getInstance(rsAppContextKey, appContext);

                        if(rsContextObj.hasExpired()) {
                            synchronized (rsContextObj) {
                                if(rsContextObj.hasExpired()) {
                                    rsContextObj.resultSet = statementExec.executeUnjoinedQuery(connection);
                                    rsContextObj.expiresAt = System.currentTimeMillis() + resultSetTTL;
                                }
                            }
                        }
                        resultMap = rsContextObj.resultSet;
                        beans.put(resultSetName, resultMap);
                    }
                } else {
                    statementExec.executeUnjoinedUpdate(connection);
                }
            } else {
                if(statementExec.getStatementType() == StatementType.QUERY) {
                    List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
                    statementExec.executeJoinedQuery(connection, beans, resultMap);
                    beans.put(resultSetName, resultMap);
                } else {
                    if(resultSetName == null) {
                        statementExec.executeJoinedUpdate(connection, beans);
                    } else {
                        Object resultSetObj = beans.get(resultSetName);

                        if(resultSetObj != null) {
                            try {
                                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) resultSetObj;
                                statementExec.executeJoinedStatement(connection, resultSet);
                            } catch(ClassCastException e) {
                                throw new SmooksException("Cannot execute joined statement '" + statementExec.getStatement() + "' on ResultSet '" + resultSetName + "'.  Must be of type 'List<Map<String, Object>>'.  Is of type '" + resultSetObj.getClass().getName() + "'.");
                            }
                        } else {
                            throw new SmooksException("Cannot execute joined statement '" + statementExec.getStatement() + "' on ResultSet '" + resultSetName + "'.  ResultSet not found in ExecutionContext.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class ResultSetScopeDecoder implements DataDecoder {

        public Object decode(String data) throws DataDecodeException {
            ResultSetScope scope;

            data = data.trim();
            try {
                scope = ResultSetScope.valueOf(data);
            } catch (IllegalArgumentException e) {
                throw new DataDecodeException("Failed to decode ResultSetScope value '" + data + "'.  Allowed values are " + Arrays.asList(ResultSetScope.values()) + ".");
            }

            return scope;
        }
    }

    private static class ResultSetContextObject {
        private List<Map<String, Object>> resultSet;
        private long expiresAt = 0L;

        private boolean hasExpired() {
            if(expiresAt <= System.currentTimeMillis()) {
                return true;
            }            
            return false;
        }

        private static ResultSetContextObject getInstance(String rsAppContextKey, ApplicationContext appContext) {
            ResultSetContextObject rsContextObj = (ResultSetContextObject) appContext.getAttribute(rsAppContextKey);

            if(rsContextObj == null) {
                synchronized (appContext) {
                    rsContextObj = (ResultSetContextObject) appContext.getAttribute(rsAppContextKey);
                    if(rsContextObj == null) {
                        rsContextObj = new ResultSetContextObject();
                        appContext.setAttribute(rsAppContextKey, rsContextObj);
                    }
                }
            }

            return rsContextObj;
        }
    }
}
