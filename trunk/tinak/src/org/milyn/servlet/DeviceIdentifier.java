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

package org.milyn.servlet;

import org.milyn.resource.ServletResourceLocator;
import org.milyn.resource.URIResourceLocator;
import org.milyn.device.ident.DeviceIdent;
import org.milyn.device.ident.IdentConfigDigester;
import org.milyn.device.ident.UnknownDeviceException;
import org.milyn.device.request.HttpRequest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;

/**
 * HttpServletRequest Device Identifier class.
 * <p/>
 * Masks a HttpServletRequest to the {@link HttpRequest} interface for a HttpServletRequest
 * instance and performs request source device/useragent matching.
 * <p/>
 * The match criteria (list of devices and how to match them) must be supplied to the
 * DeviceIdentifier class in a well defined XML format.  This XML format is defined in
 * <a href="../device/ident/doc-files/device-ident-1.0.txt" type="text/plain">device-ident-1.0.dtd</a> and a sample
 * XML is defined in <a href="../device/ident/doc-files/device-ident-sample.txt" type="text/plain">device-ident-sample.xml</a>.
 * <p/>
 * As you can see from the sample XML, regular expressions are supported in the
 * {@link org.milyn.device.ident.IdentUnit Identification Unit} values.
 * <p/>
 * <b>NOTE:</b> In terms of the XML, the <code>http-req-header</code> and <code>http-req-header</code>
 * tags are examples of Identification Units.
 * <p/>
 * The device identification XML is loaded and stored staticly.  The default
 * load location is "/WEB-INF/device-ident.xml" but this can be overridden by defining
 * an alternative location in the application descriptor (web.xml) either through the servlet
 * config as an &lt;init-param&gt; or as a &lt;context-param&gt;, checked in that order.
 * In either case the parameter name is "DeviceIdentUrl" an can contain a context relative URL
 * or an external URL i.e. an absolute URL.  The ability to load the XML from an external
 * URL allows multiple web applications to share a single XML definition.  It also provides the
 * opportunity to store the device identification data in a format other than XML
 * (e.g. a database) and, for example, use a JSP to provide the data in the required XML
 * format.
 * <p/>
 * @author Tom Fennelly
 */

abstract class DeviceIdentifier {

	// TO-DO: This class needs some unit tests
    /**
     * Device Ident URL application property name.
     */
    private static final String DEVICE_IDENT_CONFIG_PARAM = "DeviceIdentUrl";
    /**
     * Default device ident config file.
     */
    private static final String DEFAULT_CONFIG = "/device-ident.xml";

    /**
     * Match the requesting useragent/device from the ServletRequest.
     * @param config The ServletConfig instance.
     * @param request The HttpServletRequest instance on which to perform
     * the device matching.
     * @return The device name (AKA useragent common name).
     * @throws UnknownDeviceException Device match failure.
     */
    public static String matchDevice(ServletConfig config, HttpServletRequest request) throws UnknownDeviceException {
        DeviceIdent deviceIdent;
        ServletContext context;

        if(config == null) {
        	throw new IllegalArgumentException("null 'config' param in mehtid call.");
        }
        if(request == null) {
        	throw new IllegalArgumentException("null 'request' param in mehtid call.");
        }
        
        context = config.getServletContext();        
        // Do we need to read the device identification config for the context.
        deviceIdent = DeviceIdent.getInstance();
        if(deviceIdent == null) {
            deviceIdent = getDeviceIdent(config);
            // Might be a case for storing the DeviceIdent in the Servlet context!!
			DeviceIdent.setInstance(deviceIdent);
        }

        return deviceIdent.matchDevice(new RequestAdapter((HttpServletRequest)request));
    }

    /**
     * Get the DeviceIdent instance for this Servlet Container Context.
     * @param config Servlet config used to load the device config.
     * @return DeviceIdent instance
     */
    private static DeviceIdent getDeviceIdent(ServletConfig config) {
        InputStream configStream = null;
        ServletResourceLocator resLocator = new ServletResourceLocator(config, new URIResourceLocator()); 

        try {
			configStream = resLocator.getResource(DEVICE_IDENT_CONFIG_PARAM, DEFAULT_CONFIG);
            return (new IdentConfigDigester()).parse(configStream);
        } catch(Exception excep) {
            IllegalStateException state = new IllegalStateException("Error loading device ident config.");
            state.initCause(excep);
            throw state;
        }
    }

    /**
     * Servlet request adapter.
     */
    static class RequestAdapter implements HttpRequest {

        /**
         * HttpServletRequest being masked.
         */
        private HttpServletRequest request = null;

        /**
         * Private constructor.
         * @param request The Servlet request
         */
        private RequestAdapter(HttpServletRequest request) {
            if(request == null) {
                throw new IllegalArgumentException("null request arg in call to RequestAdapter(ServletRequest).");
            }

            this.request = request;
        }

        /**
         * Get the named HTTP request header.
         * @param name The request header name.
         * @return The value of the header with the specified name, or null if the
         * header isn't present in the request.
         */
        public String getHeader(String name) {
            return request.getHeader(name);
        }

        /**
         * Get the named HTTP request parameter from the request query string.
         * @param name The name of the required request parameter.
         * @return The value of the request parameter, or null if the parameter
         * isn't present in the request.
         */
        public String getParameter(String name) {
            return request.getParameter(name);
        }
    }
}
