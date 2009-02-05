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
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.DaoRegister;
import org.milyn.scribe.invoker.DaoInvoker;
import org.milyn.scribe.invoker.DaoInvokerFactory;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@VisitBeforeIf(	condition = "parameters.containsKey('flushBefore') && parameters.flushBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('flushBefore') || parameters.flushBefore.value != 'true'")
//@VisitBeforeReport(summary = "Persisted bean under beanId '${resource.parameters.beanId}' using persist mode '${resource.parameters.persistMode}'.", detailTemplate="reporting/EntityPersister_Before.html")
//@VisitAfterReport(summary = "Persisted bean under beanId '${resource.parameters.beanId}' using persist mode '${resource.parameters.persistMode}'.", detailTemplate="reporting/EntityPersister_After.html")
public class DaoFlusher implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter {

    private static Log logger = LogFactory.getLog(DaoFlusher.class);

    @ConfigParam(name = "dao", use = Use.OPTIONAL)
    private String daoName;

    @AppContext
    private ApplicationContext appContext;

    private ApplicationContextObjectStore objectStore;

    @Initialize
    public void initialize() {
    	objectStore = new ApplicationContextObjectStore(appContext);
    }

    public void visitBefore(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	flush(executionContext);
    }

    public void visitAfter(final Element element, final ExecutionContext executionContext) throws SmooksException {
    	flush(executionContext);
    }

    public void visitBefore(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	flush(executionContext);
    }

    public void visitAfter(final SAXElement element, final ExecutionContext executionContext) throws SmooksException, IOException {
    	flush(executionContext);
    }

	/**
	 * @param executionContext
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void flush(final ExecutionContext executionContext) {

		if(logger.isDebugEnabled()) {
			String msg = "Flushing org.milyn.persistence.test.dao";
			if(daoName != null) {
				msg += " with name '" + daoName + "'";
			}
			msg += ".";
			logger.debug(msg);
		}

		final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

		Object dao = null;
		try {
			if(daoName == null) {
				dao = emr.getDefaultDao();
			} else {
				dao = emr.getDao(daoName);
			}

			if(dao == null) {
				throw new IllegalStateException("The DAO register returned null while getting the DAO [" + daoName + "]");
			}

			flush(dao);

		} finally {
			if(dao != null) {
				emr.returnDao(dao);
			}
		}
	}

	/**
	 * @param org.milyn.persistence.test.dao
	 */
	private void flush(Object dao) {
		final DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(dao, objectStore);

		daoInvoker.flush();
	}

}
