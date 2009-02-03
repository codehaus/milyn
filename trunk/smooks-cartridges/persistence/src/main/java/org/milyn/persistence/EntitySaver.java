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
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdRegister;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.DaoRegister;
import org.milyn.scribe.invoker.DaoInvoker;
import org.milyn.scribe.invoker.DaoInvokerFactory;
import org.w3c.dom.Element;


/**
 * @author maurice
 *
 */
@VisitBeforeIf(	condition = "parameters.containsKey('saveBefore') && parameters.saveBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('saveBefore') || parameters.saveBefore.value != 'true'")
//@VisitBeforeReport(summary = "Persisted bean under beanId '${resource.parameters.beanId}' using persist mode '${resource.parameters.persistMode}'.", detailTemplate="reporting/EntityPersister_Before.html")
//@VisitAfterReport(summary = "Persisted bean under beanId '${resource.parameters.beanId}' using persist mode '${resource.parameters.persistMode}'.", detailTemplate="reporting/EntityPersister_After.html")
public class EntitySaver implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter {

    private static Log logger = LogFactory.getLog(EntitySaver.class);

    @ConfigParam(name = "beanId")
    private String beanIdName;

    @ConfigParam(name = "savedBeanId", use = Use.OPTIONAL)
    private String savedBeanIdName;

    @ConfigParam(name = "dao", use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(use = Use.OPTIONAL)
    private String statementId;

    @ConfigParam(defaultVal = Action.INSERT_STR, choice = {Action.INSERT_STR, Action.UPDATE_STR}, decoder = Action.DataDecoder.class)
    private Action action;

    @AppContext
    private ApplicationContext appContext;

    private ApplicationContextObjectStore objectStore;

    private BeanId beanId;

    private BeanId savedBeanId;

    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	BeanIdRegister beanIdRegister = BeanRepositoryManager.getInstance(appContext).getBeanIdRegister();

    	beanId = beanIdRegister.register(beanIdName);

    	if(savedBeanIdName != null) {
    		savedBeanId = beanIdRegister.register(savedBeanIdName);
    	}

    	objectStore = new ApplicationContextObjectStore(appContext);
    }

    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	save(executionContext);
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	save(executionContext);
    }

    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	save(executionContext);
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	save(executionContext);
    }

	/**
	 * @param executionContext
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void save(final ExecutionContext executionContext) {

		if(logger.isDebugEnabled()) {
			logger.debug("Saving bean under BeanId '" + beanIdName + "' with DAO '" + daoName + "' as an '" + action + "' action");
		}

		BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);

		Object bean = beanRepository.getBean(beanId);

		final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

		Object dao = null;
		try {
			if(daoName == null) {
				dao = emr.getDao();
			} else {
				dao = emr.getDao(daoName);
			}

			if(dao == null) {
				throw new IllegalStateException("The DAO register returned null while getting the DAO [" + daoName + "]");
			}
			final DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(dao, objectStore);

			Object result;

			switch (action) {

			case INSERT:
				result = statementId == null ? daoInvoker.insert(bean) : daoInvoker.insert(statementId, bean) ;
				break;

			case UPDATE:
				result = statementId == null ? daoInvoker.update(bean) : daoInvoker.update(statementId, bean) ;
				break;

			default:
				throw new IllegalStateException("The action '"	+ action + "' is not supported");
			}

			if(savedBeanId != null) {
				if(result == null) {
					result = bean;
				}
				beanRepository.addBean(savedBeanId, result);
			} else if(result != null && bean != result) {
				beanRepository.changeBean(beanId, bean);
			}


		} finally {
			if(dao != null) {
				emr.returnDao(dao);
			}
		}
	}


}
