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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.expression.MVELExpressionEvaluator;
import org.milyn.javabean.BeanAccessor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
@VisitBeforeIf(	condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value == 'true'")
@VisitAfterIf(	condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value != 'true'")
public class ResultsetRowSelector implements SAXVisitBefore, SAXVisitAfter {

    private static Log logger = LogFactory.getLog(ResultsetRowSelector.class);

    @ConfigParam
    private String resultSetName;

    @ConfigParam(name = "where")
    private MVELExpressionEvaluator whereEvaluator;

    @ConfigParam
    private String beanId;

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        selectRow(executionContext);
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        selectRow(executionContext);
    }

    private void selectRow(ExecutionContext executionContext) {
        Map beanMap = BeanAccessor.getBeanMap(executionContext);

        // Clear the last current row value for the bean...
        beanMap.remove(beanId);

        // Lookup the new current value for the bean...
        try {
            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) beanMap.get(resultSetName);

            if(resultSet == null) {
                throw new SmooksException("Resultset '" + resultSetName + "' not found in bean context.  Make sure an appropriate SQLExecutor resource config wraps this selector config.");
            }

            try {
                try {
                    for (Map<String, Object> row : resultSet) {
                        beanMap.put("row", row);

                        if(whereEvaluator.eval(beanMap)) {
                            beanMap.put(beanId, row);
                        }
                    }
                } finally {
                    beanMap.remove("row");
                }

                if(logger.isDebugEnabled()) {
                    logger.debug("Selected resultset where '" + whereEvaluator.getExpression() + "': [" + beanMap.get(beanId) + "].");
                }
            } catch(ClassCastException e) {
                throw new SmooksException("Bean '" + resultSetName + "' cannot be used as a Reference Data resultset.  The resultset List must contain entries of type Map<String, Object>.");
            }
        } catch(ClassCastException e) {
            throw new SmooksException("Bean '" + resultSetName + "' cannot be used as a Reference Data resultset.  A resultset must be of type List<Map<String, Object>>. '" + resultSetName + "' is of type '" + beanMap.get(resultSetName).getClass().getName() + "'.");
        }
    }
}
