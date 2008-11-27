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
package org.milyn.persistence;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.sax.SAXVisitChildren;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.javabean.BeanRuntimeInfo;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.persistence.dao.DaoRegister;
import org.milyn.persistence.dao.invoker.DaoInvoker;
import org.milyn.persistence.dao.invoker.DaoInvokerFactory;
import org.w3c.dom.Element;


/**
 * @author maurice
 *
 */
@VisitBeforeIf(	condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value != 'true'")
@VisitBeforeReport(summary = "Persisting bean under beanId '${resource.parameters.beanIdName}' with DAO '${resource.parameters.daoName}' using persist mode '${resource.parameters.persistMode}'.")
@VisitAfterReport(summary = "Persisting bean under beanId '${resource.parameters.beanIdName}' with DAO '${resource.parameters.daoName}' using persist mode '${resource.parameters.persistMode}'.")
public class EntityPersister implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter {

    private static Log logger = LogFactory.getLog(EntityPersister.class);

    @ConfigParam(name="beanId")
    private String beanIdName;

    @ConfigParam(defaultVal = "false")
    private boolean flush;

    @ConfigParam(use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(defaultVal = PersistMode.PERSIST_STR, choice = {PersistMode.PERSIST_STR, PersistMode.MERGE_STR}, decoder = PersistMode.DataDecoder.class)
    private PersistMode persistMode;

    @AppContext
    private ApplicationContext appContext;

    private BeanId beanId;

    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	beanId = BeanRepositoryManager.getInstance(appContext).getBeanIdRegister().register(beanIdName);
    }

    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	persist(executionContext);
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	persist(executionContext);
    }

    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	persist(executionContext);
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	persist(executionContext);
    }

	/**
	 * @param executionContext
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void persist(final ExecutionContext executionContext) {

		if(logger.isDebugEnabled()) {
			logger.debug("Persisting bean under BeanId '" + beanIdName + "' with DAO '" + daoName + "' using persist mode '" + persistMode + "'");
		}

		BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);

		Object beanResult = beanRepository.getBean(beanId);

		final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

		Object daoObj = null;
		try {
			daoObj = emr.getDao(daoName);

			if(daoObj == null) {
				throw new IllegalStateException("The DAO registery returned null while getting the DAO [" + daoName + "]");
			}

			final DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(daoObj, appContext);

			Object result;
			switch (persistMode) {

			case PERSIST:
				result = daoInvoker.persist(beanResult);
				break;

			case MERGE:
				result = daoInvoker.merge(beanResult);
				break;

			default:
				throw new IllegalStateException("persistMode '"	+ persistMode + "' not supported");
			}

			if(result != null) {
				beanRepository.changeBean(beanId, beanResult);
			}

			if (flush) {
				daoInvoker.flush();
			}


		} finally {
			if(daoObj != null) {
				emr.returnDao(daoObj);
			}
		}
	}
}
