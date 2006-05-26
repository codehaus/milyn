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
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.container.servlet.HttpServletContainerRequest;
import org.milyn.container.servlet.ServletContainerContext;
import org.milyn.delivery.response.ServletResponseWrapper;
import org.milyn.delivery.response.XMLServletResponseWrapper;
import org.milyn.delivery.response.ServletResponseWrapperFactory;
import org.milyn.device.ident.UnknownDeviceException;
import org.milyn.logging.SmooksLogger;
import org.milyn.resource.ContainerResourceLocator;

/**
 * Smooks Servlet Filter.
 * <p/>
 * This Servlet Filter plugs {@link org.milyn.delivery.SmooksXML} into a Servlet Container via
 * the {@link org.milyn.delivery.response.XMLServletResponseWrapper}.
 * <p/>
 * This Filter can also be configured to filter other response types in a useragent optimizable
 * fashion e.g. filter using a differnt image filter depending on the requesting browser.
 * See {@link org.milyn.delivery.response.ServletResponseWrapperFactory}.
 * <p/>
 * So, this class simply pipes the Servlet response into a {@link org.milyn.delivery.response.ServletResponseWrapper}
 * implementation.  The default response wrapper is the {@link org.milyn.delivery.response.XMLServletResponseWrapper}. 
 * See {@link org.milyn.delivery.response.PassThruServletResponseWrapper}.
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
 * 	<li>Download the <b>milyn-smooks-sf-<i>X</i>.zip</b> distribution from 
 * 		<a href="http://www.milyn.org/downloads">Milyn Downloads</a>.</li>
 * 	<li>This distrubution can be unzipped into your web application.  Note it also contains a web.xml, so be 
 * 		sure not to loose your existing configurations.
 *  </li>
 * </ol>
 * As can be seen from the web.xml in this distro, to enable this Filter in your Servlet container simply 
 * add the following to the application web.xml file.
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
 * How does Smooks load Content Delivery Units in a Servlet container?  This section tries
 * to explain how this works at present.  
 * <div class="indent">
 * <h4>Webapp WEB-INF Structure</h4>
 * The following illustration shows a sample Smooks Content Delivery Unit/Resource 
 * configuration in a Servlet container.  Smooks uses the "smooks-cdr.lst" file to
 * load the {@link org.milyn.cdr.SmooksResourceConfiguration .cdrl} files from the cdr 
 * folder.  Make sure to read the docs on the {@link org.milyn.cdr.SmooksResourceConfiguration .cdrl configuration files}.
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
		smooksContainerContext.getStore().load(listBufferedReader);
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
				// Default to the XMLServletResponseWrapper.
				responseWrapper = new XMLServletResponseWrapper(containerRequest, (HttpServletResponse)response);
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
	 * @param selector The smooks-resource selector id for the required ServletResponseWrapper
	 * configuration.
	 * @param response The original servlet response (to be wrapped).
	 * @param containerRequest The Smooks ContainerRequest instance.
	 * @return The ServletResponseWrapper instance, or null if no such response wrapper is
	 * configured for the requesting device.
	 */
	private ServletResponseWrapper getResponseWrapper(String selector, ServletResponse response, ContainerRequest containerRequest) {
		ServletResponseWrapper responseWrapper = null; 
		
		if(selector != null) {
			List resourceConfigList = containerRequest.getDeliveryConfig().getSmooksResourceConfigurations(selector);
			if(resourceConfigList != null && !resourceConfigList.isEmpty()) {
				responseWrapper = ServletResponseWrapperFactory.createServletResponseWrapper((SmooksResourceConfiguration)resourceConfigList.get(0), containerRequest, (HttpServletResponse)response);
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
