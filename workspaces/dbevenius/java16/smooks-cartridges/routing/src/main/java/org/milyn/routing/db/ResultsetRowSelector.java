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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.expression.MVELExpressionEvaluator;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdRegister;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.util.FreeMarkerTemplate;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
@VisitBeforeIf(	condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value == 'true'")
@VisitAfterIf(	condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value != 'true'")
public class ResultsetRowSelector implements SAXVisitBefore, SAXVisitAfter, DOMElementVisitor {

    private static Log logger = LogFactory.getLog(ResultsetRowSelector.class);

    @ConfigParam
    private String resultSetName;

    @ConfigParam(name = "where")
    private MVELExpressionEvaluator whereEvaluator;

    @ConfigParam(use = ConfigParam.Use.OPTIONAL)
    private FreeMarkerTemplate failedSelectError;

    @ConfigParam(name="beanId")
    private String beanIdName;

    private BeanId resultSetBeanId;

    private BeanId beanId;

    @AppContext
    private ApplicationContext appContext;

    @Initialize
    public void intitialize() throws SmooksConfigurationException {
    	BeanIdRegister beanIdRegister = BeanRepositoryManager.getInstance(appContext).getBeanIdRegister();

    	beanId = beanIdRegister.register(beanIdName);
    	resultSetBeanId = beanIdRegister.register(resultSetName);
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        selectRow(executionContext);
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        selectRow(executionContext);
    }

	/* (non-Javadoc)
	 * @see org.milyn.delivery.dom.DOMVisitBefore#visitBefore(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visitBefore(Element element, ExecutionContext executionContext)
			throws SmooksException {
		selectRow(executionContext);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.dom.DOMVisitAfter#visitAfter(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visitAfter(Element element, ExecutionContext executionContext)
			throws SmooksException {
		selectRow(executionContext);
	}

	private void selectRow(ExecutionContext executionContext) throws SmooksException {
    	BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);

    	Map<String, Object> beanMapClone = new HashMap<String, Object>(beanRepository.getBeanMap());

        // Lookup the new current value for the bean...
        try {
        	@SuppressWarnings("unchecked")
            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) beanRepository.getBean(resultSetBeanId);

            if(resultSet == null) {
                throw new SmooksException("Resultset '" + resultSetName + "' not found in bean context.  Make sure an appropriate SQLExecutor resource config wraps this selector config.");
            }

            try {
            	Object selectedRow = null;

            	Iterator<Map<String, Object>> resultIter = resultSet.iterator();
                while (selectedRow == null && resultIter.hasNext()) {
                	Map<String, Object> row = resultIter.next();

                	beanMapClone.put("row", row);

                    if(whereEvaluator.eval(beanMapClone)) {
                    	selectedRow = row;
                    	beanRepository.addBean(beanId, selectedRow);
                    }
                }

                if(selectedRow == null && failedSelectError != null) {
                    throw new DataSelectionException(failedSelectError.apply(beanRepository.getBeanMap()));
                }

                if(logger.isDebugEnabled()) {
                    logger.debug("Selected resultset where '" + whereEvaluator.getExpression() + "': [" + selectedRow + "].");
                }
            } catch(ClassCastException e) {
                throw new SmooksException("Bean '" + resultSetName + "' cannot be used as a Reference Data resultset.  The resultset List must contain entries of type Map<String, Object>.");
            }
        } catch(ClassCastException e) {
            throw new SmooksException("Bean '" + resultSetName + "' cannot be used as a Reference Data resultset.  A resultset must be of type List<Map<String, Object>>. '" + resultSetName + "' is of type '" + beanRepository.getBean(resultSetBeanId).getClass().getName() + "'.");
        }
    }

}
