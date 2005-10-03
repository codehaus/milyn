 /*
	Milyn - Copyright (C) 2003

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

package org.milyn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.container.servlet.HttpServletContainerRequest;
import org.milyn.container.servlet.ServletContainerContext;
import org.milyn.delivery.response.ServletResponseWrapper;
import org.milyn.delivery.response.HtmlServletResponseWrapper;
import org.milyn.delivery.response.ServletResponseWrapperFactory;
import org.milyn.device.ident.UnknownDeviceException;
import org.milyn.logging.SmooksLogger;
import org.milyn.resource.ContainerResourceLocator;

/**
 * Smooks Servlet Filter.
 * <p/>
 * Smooks controller class for the J2EE Servlet environment.  This 
 * class pipes the Servlet response into a {@link javax.servlet.ServletResponseWrapper}
 * instance for transformation and serialisation.  The default response wrapper is the
 * {@link org.milyn.delivery.response.HtmlServletResponseWrapper} which transforms a
 * HTML stream based on the content delivery resource configuration for the requesting
 * device. See {@link org.milyn.delivery.response.PassThruServletResponseWrapper}.
 * 
 * <h3>Requirements</h3>
 * <ul>
 * 	<li>JDK 1.4+</li>
 * 	<li>Servlet Specification 2.3+ compliant container</li>
 * </ul>
 * 
 * <h3 id="deployment">Deployment</h3>
 * To deploy Smooks:
 * <ol>
 * 	<li>Download and explode the Smooks distribution.</li>
 * 	<li>The distribution "build" folder contains the file "WEB-INF.zip".  This is 
 *  	the WEB-INF folder from the CNN.com sample (minus binaries i.e. empty lib folder).
 * 		This file contains the only Smooks configuration-set that's available at the moment.
 * 		Unzip this file into the root of your target application.  This is a temporary
 * 		measure - the next release should manage the configurations more cleanly.</li>
 * 	<li>Deploy the Smooks binaries into the Servlet container i.e. into <code>/WEB-INF/lib</code>.
 * 	The binaries are located in the "build" folder in the distribution.<br/>
 *  <i>Note: <u>Don't copy the WEB-INF.zip file</u> to the target container</i>.
 *  </li>
 * 	<li>Deploy the Smooks dependencies into the Servlet container i.e. into <code>/WEB-INF/lib</code>.
 * 	The dependencies are located in the "lib" folder in the distribution.<br/>
 *  <i>Note: <u>Don't copy the servlet.jar file</u> to the target container</i>.<br/>
 *  <i>Note: The Smooks distribution contains the <a href="http://milyn.codehaus.org/downloads">Tinak</a> binaries so there's
 * 	no need to pre-install Tinak.</i>
 *  </li>
 * </ol>
 * To enable Smooks Content delivery in your Servlet container, add the following
 * to the application web.xml file.
 * <pre>
 * &lt;filter&gt;
 *	&lt;filter-name&gt;SmooksFilter&lt;/filter-name&gt;
 *	&lt;filter-class&gt;org.milyn.SmooksServletFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *	&lt;filter-name&gt;SmooksFilter&lt;/filter-name&gt;
 *	&lt;url-pattern&gt;*.jsp&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;</pre>
 * 
 * <h3 id="cdu-config">Content Delivery Unit Configuration</h3>
 * Read the <a href="delivery/doc-files/unit-config.html">Content Delivery Units</a> overview 
 * before reading this section.
 * <p/>
 * How does Smooks load Content Delivery Units in a Servlet container?  This section tries
 * to explain how this works at present.  
 * <div class="indent">
 * <h4>Webapp WEB-INF Structure</h4>
 * The following illustration shows a sample Smooks Content Delivery Unit/Resource 
 * configuration in a Servlet container.  Smooks uses the "smooks-cdr.lst" file to
 * load the .cdrl and <a href="delivery/doc-files/unit-config.html#cdrar">.cdrar</a> files from the cdr folder.
 * Read the <a href="delivery/doc-files/unit-config.html">Content Delivery Units</a> overview for more
 * on how Smooks uses the .cdrl and .cdrar files to load the Content Delivery Resources.
 * <p/>
 * <div align="center"><img src="doc-files/cdu-servlet-structure.png" border="1" /></div>
 * </div>
 * @author tfennelly
 */
public class SmooksServletFilter implements Filter {

    /**
     * Smooks cdrar list URL application property name.
     */
	private static final String SMOOKS_CDRAR_LIST_CONFIG_PARAM = "SmooksCdrarListUrl";
    /**
     * Default smooks cdrar list config file.
     */
    private static final String DEFAULT_CONFIG = "/smooks-cdr.lst";
	/**
	 * Smooks view on the servlet context.
	 */
	private ServletContainerContext smooksContainerContext;
	/**
	 * FilterConfig adapter.
	 */
	private FilterToServletConfigAdapter servletConfig;
	/**
	 * Logger.
	 */
	private Log logger = SmooksLogger.getLog();
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		try {
			servletConfig = new FilterToServletConfigAdapter(config);
			smooksContainerContext = new ServletContainerContext(config.getServletContext(), servletConfig);
			loadCdrarStore();
			logger.info("Smooks Servlet Filter initalised.");
		} catch(Exception e) {
			throw new ServletException("CDRArchive list load failure.", e);
		}		
	}

	/**
	 * Load the CDRStore for this filter instance.
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private void loadCdrarStore() throws IllegalArgumentException, IOException {
		ContainerResourceLocator containerResLocator;
		BufferedReader listBufferedReader;
		InputStream cdrarListStream;
		
		containerResLocator = smooksContainerContext.getResourceLocator();
		cdrarListStream = containerResLocator.getResource(SMOOKS_CDRAR_LIST_CONFIG_PARAM, DEFAULT_CONFIG);
		listBufferedReader = new BufferedReader(new InputStreamReader(cdrarListStream));
		smooksContainerContext.getCdrarStore().load(listBufferedReader);
		logger.info("CDRStore load complete.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		ServletResponseWrapper responseWrapper = null;

		try {
			long startTime = 0L;
			ContainerRequest containerRequest = new HttpServletContainerRequest((HttpServletRequest)request, servletConfig, smooksContainerContext);
			List responseWrappers;

			if(logger.isDebugEnabled()) {
				startTime = System.currentTimeMillis();
			}

			// Check for a response wrapper configuration on the request.
			responseWrapper = getResponseWrapper(request.getParameter("smooksrw"), response, containerRequest);
			if(responseWrapper == null) {
				// Check for a response wrapper configuration for HTML.  This allows
				// overridding of the default (below).
				responseWrapper = getResponseWrapper("html-smooksrw", response, containerRequest);
			}
			if(responseWrapper == null) {
				// Default to the HtmlServletResponseWrapper.
				responseWrapper = new HtmlServletResponseWrapper(containerRequest, (HttpServletResponse)response);
			}

			if(logger.isDebugEnabled()) {
				logger.debug("Applying response wrapper ["+ responseWrapper.getClass() + "] to request [" + ((HttpServletRequest)request).getRequestURI() + "].");
			}
			filterChain.doFilter(request, responseWrapper);
			responseWrapper.deliverResponse();
			if(logger.isDebugEnabled()) {
				logger.debug("[doFilter] " + (System.currentTimeMillis() - startTime) + "ms");
			}
		} catch (UnknownDeviceException e) {
			logger.error("Unknown Device.  Smooks not being used to deliver content.", e);
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if(responseWrapper != null) {
				responseWrapper.close();
			}
		}
	}

	/**
	 * Get the response wrapper for the requesting device based on the specified by
	 * response wrapper.
	 * @param selector The cdres selector id for the required ServletResponseWrapper
	 * configuration.
	 * @param response The original servlet response (to be wrapped).
	 * @param containerRequest The Smooks ContainerRequest instance.
	 * @return The ServletResponseWrapper instance, or null if no such response wrapper is
	 * configured for the requesting device.
	 */
	private ServletResponseWrapper getResponseWrapper(String selector, ServletResponse response, ContainerRequest containerRequest) {
		ServletResponseWrapper responseWrapper = null; 
		
		if(selector != null) {
			List cdrDefList = containerRequest.getDeliveryConfig().getCDRDefs(selector);
			if(cdrDefList != null && !cdrDefList.isEmpty()) {
				responseWrapper = ServletResponseWrapperFactory.createServletResponseWrapper((CDRDef)cdrDefList.get(0), containerRequest, (HttpServletResponse)response);
			}
		}
		
		return responseWrapper;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * Adaptorfor Filter to Servlet config.
	 * @author tfennelly
	 */
	private class FilterToServletConfigAdapter implements ServletConfig {		
		/**
		 * Filter configuration.
		 */
		private FilterConfig config;
		/**
		 * Constructor.
		 * @param config FilterConfig instance.
		 */
		private FilterToServletConfigAdapter(FilterConfig config) {
			this.config = config;
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletConfig#getServletName()
		 */
		public String getServletName() {
			return config.getFilterName();
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletConfig#getServletContext()
		 */
		public ServletContext getServletContext() {
			return config.getServletContext();
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
		 */
		public String getInitParameter(String paramName) {
			return config.getInitParameter(paramName);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletConfig#getInitParameterNames()
		 */
		public Enumeration getInitParameterNames() {
			return config.getInitParameterNames();
		}		
	}
}
