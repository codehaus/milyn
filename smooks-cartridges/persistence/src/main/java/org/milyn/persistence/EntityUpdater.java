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
import java.util.Collections;
import java.util.Set;

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
import org.milyn.delivery.ordering.Consumer;
import org.milyn.delivery.ordering.Producer;
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
import org.milyn.scribe.ObjectStore;
import org.milyn.scribe.invoker.DaoInvoker;
import org.milyn.scribe.invoker.DaoInvokerFactory;
import org.milyn.util.CollectionsUtil;
import org.w3c.dom.Element;


/**
 * @author maurice
 *
 */
@VisitBeforeIf(	condition = "parameters.containsKey('updateBefore') && parameters.updateBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('updateBefore') || parameters.updateBefore.value != 'true'")
@VisitBeforeReport(summary = "Updating bean under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityUpdater.html")
@VisitAfterReport(summary = "Updating bean under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityUpdater.html")
public class EntityUpdater implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter, Producer, Consumer {

    private static Log logger = LogFactory.getLog(EntityUpdater.class);

    @ConfigParam(name = "beanId")
    private String beanIdName;

    @ConfigParam(name = "updatedBeanId", use = Use.OPTIONAL)
    private String updatedBeanIdName;

    @ConfigParam(name = "dao", use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(use = Use.OPTIONAL)
    private String name;

    @AppContext
    private ApplicationContext appContext;

    private ObjectStore objectStore;

    private BeanId beanId;

    private BeanId updatedBeanId;

    @Initialize
    public void initialize() throws SmooksConfigurationException {
    	BeanIdRegister beanIdRegister = BeanRepositoryManager.getInstance(appContext).getBeanIdRegister();

    	beanId = beanIdRegister.register(beanIdName);

    	if(updatedBeanIdName != null) {
    		updatedBeanId = beanIdRegister.register(updatedBeanIdName);
    	}

    	objectStore = new ApplicationContextObjectStore(appContext);
    }

    /* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Producer#getProducts()
	 */
	public Set<? extends Object> getProducts() {
		if(updatedBeanIdName == null) {
			return Collections.emptySet();
		} else {
			return CollectionsUtil.toSet(updatedBeanIdName);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Consumer#consumes(java.lang.String)
	 */
	public boolean consumes(Object object) {
		return object.equals(beanIdName);
	}

    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	update(executionContext);
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	update(executionContext);
    }

    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	update(executionContext);
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	update(executionContext);
    }

	/**
	 * @param executionContext
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void update(final ExecutionContext executionContext) {

		if(logger.isDebugEnabled()) {
			logger.debug("Updating bean under BeanId '" + beanIdName + "' with DAO '" + daoName + "'.");
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
				throw new IllegalStateException("The DAO register returned null while getting the DAO '" + daoName + "'");
			}

			final DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(dao, objectStore);

			Object result = name == null ? daoInvoker.update(bean) : daoInvoker.update(name, bean) ;

			if(updatedBeanId != null) {
				if(result == null) {
					result = bean;
				}
				beanRepository.addBean(updatedBeanId, result);
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
