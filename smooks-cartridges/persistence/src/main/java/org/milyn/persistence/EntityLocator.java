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
import java.util.Collection;
import java.util.Set;

import javax.persistence.NonUniqueResultException;

import org.apache.commons.lang.StringUtils;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.ConfigParam.Use;
import org.milyn.container.ApplicationContext;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.ordering.Consumer;
import org.milyn.delivery.ordering.Producer;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.event.report.annotation.VisitAfterReport;
import org.milyn.event.report.annotation.VisitBeforeReport;
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.repository.BeanId;
import org.milyn.persistence.parameter.NamedParameterContainer;
import org.milyn.persistence.parameter.ParameterContainer;
import org.milyn.persistence.parameter.ParameterIndex;
import org.milyn.persistence.parameter.ParameterManager;
import org.milyn.persistence.parameter.PositionalParameterContainer;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.invoker.DaoInvoker;
import org.milyn.scribe.invoker.DaoInvokerFactory;
import org.milyn.scribe.register.DaoRegister;
import org.milyn.util.CollectionsUtil;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
@VisitBeforeReport(summary = "Initializing parameter container to hold the parameters needed for the lookup.", detailTemplate="reporting/EntityLocator_before.html")
@VisitAfterReport(summary = "Looking up entity to put under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityLocator_after.html")
public class EntityLocator implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter, Producer, Consumer {

	@ConfigParam()
	private int id;

	@ConfigParam(name="beanId")
    private String beanIdName;

    @ConfigParam(name = "dao", use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(name="lookup", use = Use.OPTIONAL)
    private String lookupName;

    @ConfigParam(use = Use.OPTIONAL)
    private String query;

    @ConfigParam(defaultVal = OnNoResult.NULLIFY_STR, decoder = OnNoResult.DataDecoder.class)
    private OnNoResult onNoResult;

    @ConfigParam(defaultVal = "false")
    private boolean uniqueResult;

    @ConfigParam(defaultVal = ParameterListType.NAMED_STR, decoder = ParameterListType.DataDecoder.class)
    private ParameterListType parameterListType;

    @AppContext
    private ApplicationContext appContext;

    private ApplicationContextObjectStore objectStore;

    private ParameterIndex<?, ?> parameterIndex;

    private BeanId beanId;
    @Initialize
    public void initialize() throws SmooksConfigurationException {

    	if(StringUtils.isEmpty(lookupName) && StringUtils.isEmpty(query)) {
    		throw new SmooksConfigurationException("A lookup name or  a query  needs to be set to be able to lookup anything");
    	}

    	if(StringUtils.isNotEmpty(lookupName) && StringUtils.isNotEmpty(query)) {
    		throw new SmooksConfigurationException("Both the lookup name and the query can't be set at the same time");
    	}

    	beanId = appContext.getBeanIdIndex().register(beanIdName);

    	parameterIndex = ParameterManager.initializeParameterIndex(id, parameterListType, appContext);

    	objectStore = new ApplicationContextObjectStore(appContext);
    }

    /* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Producer#getProducts()
	 */
	public Set<? extends Object> getProducts() {
		return CollectionsUtil.toSet(beanIdName);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Consumer#consumes(java.lang.String)
	 */
	public boolean consumes(Object object) {
		return parameterIndex.containsParameter(object);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.dom.DOMVisitBefore#visitBefore(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visitBefore(Element element, ExecutionContext executionContext)	throws SmooksException {

		initParameterContainer(executionContext);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitBefore#visitBefore(org.milyn.delivery.sax.SAXElement, org.milyn.container.ExecutionContext)
	 */
	public void visitBefore(SAXElement element,	ExecutionContext executionContext) throws SmooksException, IOException {

		initParameterContainer(executionContext);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.dom.DOMVisitAfter#visitAfter(org.w3c.dom.Element, org.milyn.container.ExecutionContext)
	 */
	public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
		lookup(executionContext);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.sax.SAXVisitAfter#visitAfter(org.milyn.delivery.sax.SAXElement, org.milyn.container.ExecutionContext)
	 */
	public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
		lookup(executionContext);
	}

	public void initParameterContainer(ExecutionContext executionContext) {
		ParameterManager.initializeParameterContainer(id, parameterListType, executionContext);
	}

	@SuppressWarnings("unchecked")
	public void lookup(ExecutionContext executionContext) {
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

			Object result = lookup(dao, executionContext);

			if(uniqueResult == true) {
				if(result instanceof Collection){
					Collection<Object> resultCollection = (Collection<Object>) result;

					if(resultCollection.size() == 1) {
						for(Object value : resultCollection) {
							result = value;
						}
					} else if(resultCollection.size() == 1) {
						result = null;
					} else {
						throw new NonUniqueResultException("The DAO '" + daoName + "' returned multiple results for the lookup '" + lookupName + "'");
					}

				} else {
					throw new SmooksConfigurationException("The returned result doesn't implement the '" + Collection.class.getName() + "' interface " +
							"and there for the unique result check can't be done.");
				}
			}

			if(result == null && onNoResult == OnNoResult.EXCEPTION) {
				if(lookupName != null) {
					throw new NoLookupResultException("The DAO '" + daoName + "' returned no results for lookup '" + lookupName + "'");
				} else {
					throw new NoLookupResultException("The DAO '" + daoName + "' returned no results found for query '" + query + "'");
				}
			}

			BeanContext beanContext = executionContext.getBeanContext();

			if(result == null) {
				beanContext.removeBean(beanId);
			} else {
				beanContext.addBean(beanId, result);
			}
		} finally {
			if(dao != null) {
				emr.returnDao(dao);
			}
		}
	}

	public Object lookup(Object dao, ExecutionContext executionContext) {
		ParameterContainer<?> container = ParameterManager.getParameterContainer(id, executionContext);
		DaoInvoker daoInvoker = DaoInvokerFactory.getInstance().create(dao, objectStore);

		if(query == null) {
			if(parameterListType == ParameterListType.NAMED) {
				return daoInvoker.lookup(lookupName, ((NamedParameterContainer) container).getParameterMap());
			} else {
				return daoInvoker.lookup(lookupName, ((PositionalParameterContainer) container).getValues());
			}
		} else {
			if(parameterListType == ParameterListType.NAMED) {
				return daoInvoker.lookupByQuery(query, ((NamedParameterContainer) container).getParameterMap());
			} else {
				return daoInvoker.lookupByQuery(query, ((PositionalParameterContainer) container).getValues());
			}
		}
	}

}
