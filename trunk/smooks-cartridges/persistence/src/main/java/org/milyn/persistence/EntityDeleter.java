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
@VisitBeforeIf(	condition = "parameters.containsKey('deleteBefore') && parameters.deleteBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('deleteBefore') || parameters.deleteBefore.value != 'true'")
@VisitBeforeReport(summary = "Deleting bean under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityDeleter.html")
@VisitAfterReport(summary = "Deleting bean under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityDeleter.html")
public class EntityDeleter implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter {

    private static Log logger = LogFactory.getLog(EntityDeleter.class);

    @ConfigParam(name = "beanId")
    private String beanIdName;

    @ConfigParam(name = "deletedBeanId", use = Use.OPTIONAL)
    private String deletedBeanIdName;

    @ConfigParam(name = "dao", use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(use = Use.OPTIONAL)
    private String name;

    @AppContext
    private ApplicationContext appContext;

    private ApplicationContextObjectStore objectStore;

    private BeanId beanId;

    private BeanId deletedBeanId;

    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	BeanIdRegister beanIdRegister = BeanRepositoryManager.getInstance(appContext).getBeanIdRegister();

    	beanId = beanIdRegister.register(beanIdName);

    	if(deletedBeanIdName != null) {
    		deletedBeanId = beanIdRegister.register(deletedBeanIdName);
    	}

    	objectStore = new ApplicationContextObjectStore(appContext);
    }

    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	delete(executionContext);
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	delete(executionContext);
    }

    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	delete(executionContext);
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	delete(executionContext);
    }

	/**
	 * @param executionContext
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void delete(final ExecutionContext executionContext) {

		if(logger.isDebugEnabled()) {
			logger.debug("Deleting bean under BeanId '" + beanIdName + "' with DAO '" + daoName + "'");
		}

		BeanRepository beanRepository = BeanRepositoryManager.getBeanRepository(executionContext);

		Object bean = beanRepository.getBean(beanId);

		final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

		Object dao = null;
		try {
			if(daoName == null) {
				dao = emr.getDefaultDao();
			} else {
				dao = emr.getDao(daoName);
			}

			if(dao == null) {
				String msg = "The DAO register returned null while getting the ";

				if(daoName == null) {
					msg += "default DAO";
				} else {
					msg += "DAO '" + daoName + "'";
				}

				throw new NullPointerException(msg);
			}

			DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(dao, objectStore);

			Object result = (name != null) ? daoInvoker.delete(name, bean) : daoInvoker.delete(bean);

			if(deletedBeanId != null) {
				if(result == null) {
					result = bean;
				}
				beanRepository.addBean(deletedBeanId, result);
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
