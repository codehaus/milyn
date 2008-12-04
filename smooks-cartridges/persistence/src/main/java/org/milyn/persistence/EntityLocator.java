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
import java.util.HashMap;

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
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.persistence.container.ParameterContainer;
import org.milyn.persistence.container.ParameterIndex;
import org.milyn.persistence.container.ParameterManager;
import org.milyn.persistence.dao.DaoRegister;
import org.milyn.persistence.dao.DaoUtil;
import org.milyn.persistence.util.PersistenceUtil;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class EntityLocator implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter {

	public static final String PARAMETER_CONTEXT = EntityLocator.class + "#PARAMETERS";

	public static String getParameterRepositoryId(int id) {
		return PARAMETER_CONTEXT + "#" + id;
	}

	@ConfigParam()
	private int id;

	@ConfigParam
    private String beanId;

    @ConfigParam(use = Use.OPTIONAL)
    private String daoName;

    @ConfigParam(name="lookup", use = Use.OPTIONAL)
    private String lookupName;

    @ConfigParam(use = Use.OPTIONAL)
    private String query;

    @ConfigParam(defaultVal = OnNoResult.NULLIFY_STR, decoder = OnNoResult.DataDecoder.class)
    private OnNoResult onNoResult;

    @ConfigParam(defaultVal = "false")
    private boolean uniqueResult;

    @AppContext
    private ApplicationContext appContext;

    private ParameterIndex parameterIndex;

    @Initialize
    public void initialize() throws SmooksConfigurationException {

    	if(StringUtils.isEmpty(lookupName) && StringUtils.isEmpty(query)) {
    		throw new SmooksConfigurationException("A lookup or query value needs to be set to be able to lookup anything");
    	}

    	parameterIndex = ParameterManager.getParameterIndex(id, appContext);
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
		ParameterContainer container = ParameterManager.getParameterContainer(id, executionContext);
    	container.clear();
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getParameterRepository(ExecutionContext executionContext) {
		return (HashMap<String, Object>) executionContext.getAttribute(getParameterRepositoryId(id));
	}

	@SuppressWarnings("unchecked")
	public void lookup(ExecutionContext executionContext) {

		ParameterContainer container = ParameterManager.getParameterContainer(id, executionContext);

		final DaoRegister emr = PersistenceUtil.getDAORegister(executionContext);

		Object dao = null;
		try {
			if(daoName == null) {
				dao = emr.getDao();
			} else {
				dao = emr.getDao(daoName);
			}

			if(dao == null) {
				throw new IllegalStateException("The DAO register returned null while getting the DAO '" + daoName + "'");
			}

			if(DaoUtil.isDao(dao)) {
				lookup(dao, container);
			} else if(DaoUtil.isMappedDao(dao)) {
				lookupMapped(dao, container);
			} else {
				throw new IllegalStateException("The DAO '" + daoName + "' of type '" + dao.getClass().getName() + "' isn't recognized as a Dao or a MappedDao.");
			}

		} finally {
			if(dao != null) {
				emr.returnDao(dao);
			}
		}
	}

	public void lookup(Object dao, ParameterContainer container) {

	}

	public void lookupMapped(Object dao, ParameterContainer container) {

	}
}
