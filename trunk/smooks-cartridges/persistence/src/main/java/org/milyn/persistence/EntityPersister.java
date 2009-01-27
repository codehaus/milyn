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
import org.milyn.scribe.dao.DaoRegister;
import org.milyn.scribe.dao.invoker.DaoInvoker;
import org.milyn.scribe.dao.invoker.DaoInvokerFactory;
import org.milyn.scribe.dao.invoker.MappedDaoInvoker;
import org.milyn.scribe.dao.invoker.MappedDaoInvokerFactory;
import org.w3c.dom.Element;


/**
 * @author maurice
 *
 */
@VisitBeforeIf(	condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value != 'true'")
//@VisitBeforeReport(summary = "Persisted bean under beanId '${resource.parameters.beanId}' using persist mode '${resource.parameters.persistMode}'.", detailTemplate="reporting/EntityPersister_Before.html")
//@VisitAfterReport(summary = "Persisted bean under beanId '${resource.parameters.beanId}' using persist mode '${resource.parameters.persistMode}'.", detailTemplate="reporting/EntityPersister_After.html")
public class EntityPersister implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter {

    private static Log logger = LogFactory.getLog(EntityPersister.class);

    @ConfigParam(name = "beanId")
    private String beanIdName;

    @ConfigParam(name = "persistedBeanId", use = Use.OPTIONAL)
    private String persistedBeanIdName;

    @ConfigParam(name = "dao", use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(use = Use.OPTIONAL)
    private String statementId;

    @ConfigParam(defaultVal = PersistMode.PERSIST_STR, choice = {PersistMode.PERSIST_STR, PersistMode.MERGE_STR}, decoder = PersistMode.DataDecoder.class)
    private PersistMode persistMode;

    @AppContext
    private ApplicationContext appContext;

    private ApplicationContextObjectStore objectStore;

    private BeanId beanId;

    private BeanId persistedBeanId;

    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	BeanIdRegister beanIdRegister = BeanRepositoryManager.getInstance(appContext).getBeanIdRegister();

    	beanId = beanIdRegister.register(beanIdName);

    	if(persistedBeanIdName != null) {
    		persistedBeanId = beanIdRegister.register(persistedBeanIdName);
    	}

    	objectStore = new ApplicationContextObjectStore(appContext);
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

			Object result;

			if(statementId != null) {
				result = persistMapped(bean, dao);
			} else {
				result = persist(bean, dao);
			}

			if(persistedBeanId != null) {
				if(result == null) {
					result = bean;
				}
				beanRepository.addBean(persistedBeanId, result);
			} else if(result != null && bean != result) {
				beanRepository.changeBean(beanId, bean);
			}


		} finally {
			if(dao != null) {
				emr.returnDao(dao);
			}
		}
	}

	/**
	 * @param beanResult
	 * @param daoObj
	 * @param result
	 * @return
	 */
	private Object persist(Object beanResult, Object dao) {
		final DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(dao, objectStore);


		switch (persistMode) {

		case PERSIST:
			return daoInvoker.insert(beanResult);

		case MERGE:
			return daoInvoker.update(beanResult);

		default:
			throw new IllegalStateException("The persistMode '"	+ persistMode + "' is not supported");
		}

	}


	/**
	 * @param beanResult
	 * @param daoObj
	 * @param result
	 * @return
	 */
	private Object persistMapped(Object beanResult, Object dao) {
		final MappedDaoInvoker daoInvoker = MappedDaoInvokerFactory.getInstance().create(dao, objectStore);


		switch (persistMode) {

		case PERSIST:
			return daoInvoker.insert(statementId, beanResult);


		case MERGE:
			return daoInvoker.update(statementId, beanResult);

		default:
			throw new IllegalStateException("The persistMode '"	+ persistMode + "' is not supported");
		}

	}
}
