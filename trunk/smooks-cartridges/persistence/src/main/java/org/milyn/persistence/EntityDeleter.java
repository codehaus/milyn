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
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.context.BeanIdStore;
import org.milyn.javabean.repository.BeanId;
import org.milyn.javabean.repository.BeanIdRegister;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.javabean.repository.BeanRepositoryManager;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.invoker.DaoInvoker;
import org.milyn.scribe.invoker.DaoInvokerFactory;
import org.milyn.scribe.register.DaoRegister;
import org.milyn.util.CollectionsUtil;
import org.w3c.dom.Element;


/**
 * DAO Deleter
 * <p />
 * This DAO deleter calls the delete method of a DAO, using a entity bean from
 * the bean context as parameter.
 *
 *
 *
 * <h3>Configuration</h3>
 * <b>Namespace:</b> http://www.milyn.org/xsd/smooks/persistence-1.2.xsd<br>
 * <b>Element:</b> deleter<br>
 * <b>Attributes:</b>
 * <ul>
 *  <li><b>beanId</b> : The id under which the entity bean is bound in the bean context. (<i>required</i>)
 *  <li><b>deleteOnElement</b> : The element selector to select the element when the delete should execute. (<i>required</i>)
 * 	<li><b>dao</b> : The name of the DAO that will be used. If it is not set then the default DAO is used. (<i>optional</i>)
 *  <li><b>name*</b> : The name of the insert method. Depending of the adapter this can mean different things.
 *                     For instance when using annotated DAO's you can name the methods and target them with this property, but
 *                     when using the Ibatis adapter you set the id of the Ibatis statement in this attribute. (<i>optional</i>)
 *  <li><b>deletedBeanId</b> : The bean id under which the deleted bean will be stored. If not set then the deleted
 *                             bean will not be stored in bean context. (<i>optional</i>)
 *  <li><b>deleteBefore</b> : If the deleter should execute on the 'before' event. (<i>default: false</i>)
 * </ul>
 *
 * <i>* This attribute is not supported by all scribe adapters.</i>
 *
 * <h3>Configuration Example</h3>
 * <pre>
 * &lt;?xml version=&quot;1.0&quot;?&gt;
 * &lt;smooks-resource-list xmlns=&quot;http://www.milyn.org/xsd/smooks-1.1.xsd&quot;
 *   xmlns:dao=&quot;http://www.milyn.org/xsd/smooks/persistence-1.2.xsd&quot;&gt;
 *
 *      &lt;dao:deleter dao=&quot;dao&quot; name=&quot;deleteIt&quot; beanId=&quot;toDelete1&quot; deleteOnElement=&quot;root&quot; deletedBeanId=&quot;deleted&quot; deleteBefore=&quot;false&quot; /&gt;
 *
 * &lt;/smooks-resource-list&gt;
 * </pre>
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
@VisitBeforeIf(	condition = "parameters.containsKey('deleteBefore') && parameters.deleteBefore.value == 'true'")
@VisitAfterIf( condition = "!parameters.containsKey('deleteBefore') || parameters.deleteBefore.value != 'true'")
@VisitBeforeReport(summary = "Deleting bean under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityDeleter.html")
@VisitAfterReport(summary = "Deleting bean under beanId '${resource.parameters.beanId}'.", detailTemplate="reporting/EntityDeleter.html")
public class EntityDeleter implements DOMElementVisitor, SAXVisitBefore, SAXVisitAfter, Consumer, Producer {

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

    	BeanIdStore beanIdStore = appContext.getBeanIdStore();

    	beanId = beanIdStore.register(beanIdName);

    	if(deletedBeanIdName != null) {
    		deletedBeanId = beanIdStore.register(deletedBeanIdName);
    	}

    	objectStore = new ApplicationContextObjectStore(appContext);
    }

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Producer#getProducts()
	 */
	public Set<String> getProducts() {
		if(deletedBeanIdName == null) {
			return Collections.emptySet();
		} else {
			return CollectionsUtil.toSet(deletedBeanIdName);
		}
	}


	/* (non-Javadoc)
	 * @see org.milyn.delivery.ordering.Consumer#consumes(java.lang.String)
	 */
	public boolean consumes(Object object) {
		return object.equals(beanIdName);
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

		BeanContext beanContext = executionContext.getBeanContext();

		Object bean = beanContext.getBean(beanId);

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
				beanContext.addBean(deletedBeanId, result);
			} else if(result != null && bean != result) {
				beanContext.changeBean(beanId, bean);
			}


		} finally {
			if(dao != null) {
				emr.returnDao(dao);
			}
		}
	}





}
