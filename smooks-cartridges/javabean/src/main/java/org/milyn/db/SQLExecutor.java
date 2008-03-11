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

import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.container.ExecutionContext;
import org.milyn.db.datasource.AbstractDataSource;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.javabean.BeanAccessor;
import org.w3c.dom.Element;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SQLExecutor  implements SAXElementVisitor, DOMElementVisitor {

    @ConfigParam
    private String datasource;

    @ConfigParam
    private String statement;
    private StatementExec statementExec;

    @ConfigParam(use = ConfigParam.Use.OPTIONAL)
    private String resultSetName;

    @ConfigParam(defaultVal = "false")
    private boolean executeBefore;

    @Initialize
    public void intitialize() throws SmooksConfigurationException {
        statementExec = new StatementExec(statement);
        if(statementExec.getStatementType() == StatementType.QUERY && resultSetName == null) {
            throw new SmooksConfigurationException("Sorry, query statements must be accompanied by a 'resultSetName' property, under whose value the query results are bound.");
        }
    }

    public void visitBefore(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
        if(executeBefore) {
            executeSQL(executionContext);
        }
    }

    public void onChildText(SAXElement saxElement, SAXText saxText, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void onChildElement(SAXElement saxElement, SAXElement saxElement1, ExecutionContext executionContext) throws SmooksException, IOException {
    }

    public void visitAfter(SAXElement saxElement, ExecutionContext executionContext) throws SmooksException, IOException {
        if(!executeBefore) {
            executeSQL(executionContext);
        }
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        if(executeBefore) {
            executeSQL(executionContext);
        }
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        if(!executeBefore) {
            executeSQL(executionContext);
        }
    }

    private void executeSQL(ExecutionContext executionContext) throws SmooksException {
        Connection connection = AbstractDataSource.getConnection(datasource, executionContext);
        Map beans = BeanAccessor.getBeanMap(executionContext);

        try {
            if(!statementExec.isJoin()) {
                if(statementExec.getStatementType() == StatementType.QUERY) {
                    beans.put(resultSetName, statementExec.executeUnjoinedQuery(connection));
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
}
