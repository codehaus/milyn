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

package org.milyn.servlet;

import org.milyn.device.UAContext;
import org.milyn.device.ident.UnknownDeviceException;
import org.milyn.device.profile.Profile;
import org.milyn.device.profile.ProfileSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Servlet useragent context.
 * 
 * <h3>Requirements</h3>
 * <ul>
 * 	<li>JDK 1.4+</li>
 * 	<li>Servlet Specification 2.3+ compliant container</li>
 * </ul>
 * 
 * <h3 id="deployment">Deployment</h3>
 * Deploying Tinak in a J2EE Servlet Container is very simple:
 * <ol>
 * 	<li>Download and explode the Tinak distribution.</li>
 * 	<li>Deploy the Tinak binaries into the Servlet container i.e. into <code>/WEB-INF/lib</code>.
 * 	The binaries are located in the "build" folder in the distribution.</li>
 * 	<li>Deploy the Tinak dependencies into the Servlet container i.e. into <code>/WEB-INF/lib</code>.
 * 	The dependencies are located in the "lib" folder in the distribution.<br/>
 *  <i>Note: <u>Don't copy the servlet.jar file</u> to the target container</i>.
 *  </li>
 * 	<li>Deploy the Tinak device recognition XML module in the container.  It's default
 * 	deployment location is <code>/WEB-INF/device-ident.xml</code> but this can be configured 
 * 	in the deployment descriptor.  A sample device-ident.xml can be found in the root 
 *  of the distribution</li>
 * 	<li>Deploy the Tinak device profiling XML module in the container.  It's default
 * 	deployment location is <code>/WEB-INF/device-profile.xml</code> but this can be configured 
 * 	in the deployment descriptor.  A sample device-profile.xml can be found in the root 
 *  of the distribution</li>
 * </ol>
 * <p/>
 * As stated at the start of this section, the Tinak device recognition XML module
 * default deployment location can be overridden.  This is done through the web 
 * application's deployment descriptor as follows:
 * <pre>
 * &lt;servlet&gt;
 * 	&lt;servlet-name&gt;aservlet&lt;/servlet-name&gt;
 * 	&lt;servlet-class&gt;org.milyn.some.AServlet&lt;/servlet-class&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;<b>DeviceIdentUrl</b>&lt;/param-name&gt;
 * 		&lt;param-value&gt;<i>URL</i>&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * &lt;/servlet&gt;
 * </pre>
 * or,
 * <pre>
 * &lt;context-param&gt;
 * 	&lt;param-name&gt;<b>DeviceIdentUrl</b>&lt;/param-name&gt;
 * 	&lt;param-value&gt;<i>URL</i>&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * checked by Tinak in that order.  <i>URL</i> can be a context relative URL or a URL to an external resource
 * i.e. and absolute URL.  The ability to define an external URL means that two or more web
 * applications can share the same configuration.  It also means that the device recognition
 * data can be stored in a database (or some other format) and accessed as an XML stream via a HTTP request.
 * See "<a href="http://www.milyn.org/tinak/device-recognition.shtml">Device Recognition</a>" for more.
 * <p/>
 * The default deployment location of the profiling XML module can also be overridden in exactly the same fashion.
 * The name of the parameter in this case is "<b>DeviceProfileUrl</b>". 
 * 					
 * <h3 id="addingtoservlet">Adding Browser Recognition &amp; Profiling to a J2EE Servlet</h3>
 * Adding browser recognition and profiling support to a Servlet implementation 
 * using Tinak is very simple - literally a one-liner.  
 * <p/>
 * It's done using this class as follows:
 * <pre>
 * try {
 *     {@link org.milyn.device.UAContext} uaContext = ServletUAContext.{@link ServletUAContext#getInstance(HttpServletRequest, ServletConfig)};
 * } catch({@link org.milyn.device.ident.UnknownDeviceException} unknownDevice) {
 *     // Handle Exception...
 * }</pre>
 * The {@link org.milyn.device.UAContext} instance can then be used to:
 * <ul>
 * 	<li>get the browser (device) common name, and</li>
 * 	<li>check the browser for membership of a given profile.</li>
 * </ul>
 * See the <a href="http://www.milyn.org/tinak/" target="new">online user docs</a> for details on how to configure
 * device recognition and profiling in a Servlet container.
 * @author Tom Fennelly
 */

public final class ServletUAContext implements UAContext {

    /**
     * The useragent common name.
     */
    private String commonName = null;
    /**
     * The ProfileSet.  Set of profiles of which the device has membership.
     */
    private ProfileSet profileSet = null;
	/**
     * ServletUAContext session key.
     */
    protected static final String CONTEXT_KEY = "xxx." + ServletUAContext.class.getName() + ".Context_KEY";
    /**
     * Table of preconstructed ServletUAContext instances keyed by their common name.
     */
    private static Hashtable contexts = new Hashtable();
	/**
	 * Profiles Servlet Context key.
	 */
	public static final String PROFILES_KEY = "xxx." + ServletUAContext.class.getName() + ".ctxProfiles";

    /**
     * Private constructor.
     * <p/>
     * Hiding the constructor.
     * @param commonName The device common name.
     */
    private ServletUAContext(String commonName) {
        if(commonName == null) {
            throw new IllegalArgumentException("null commonName arg in constructor call.");
        } 

        this.commonName = commonName;
    }

    /**
     * Get the Useragent Context for the Servlet request.
	 * <p/>
	 * Factory construction method.
     * @param request The HttpServletRequest instance associated with the request.
     * @param config The Servet Configuration.
     * @return Useragent context.
     * @throws UnknownDeviceException Device match failure.
     */
    public static UAContext getInstance(HttpServletRequest request, ServletConfig config) throws UnknownDeviceException {
        ServletUAContext context;
        String deviceMatchCName;
        HttpSession session;
		
        if(request == null) {
            throw new IllegalArgumentException("null 'request' arg in call to getInstance()");
        } else if(config == null) {
			throw new IllegalArgumentException("null 'config' arg in call to getInstance()");
		}

        // Is the context already set in the request
        context = (ServletUAContext)request.getAttribute(CONTEXT_KEY);
        if(context != null) {
            return context;
        }

        // Is the context set in the session
        session = request.getSession();
        context = (ServletUAContext)session.getAttribute(CONTEXT_KEY);
        if(context != null) {
            request.setAttribute(CONTEXT_KEY, context);

            return context;
        }

        // OK, need to match the device and store the context.
		deviceMatchCName = DeviceIdentifier.matchDevice(config, request);
        context = (ServletUAContext)contexts.get(deviceMatchCName);
        if(context == null) {
            context = new ServletUAContext(deviceMatchCName);
            context.profileSet = DeviceProfiler.getDeviceProfile(deviceMatchCName, request, config);
            if(context.profileSet == null) {
            	context.profileSet = new ProfileSet() {
					public boolean isMember(String profile) {
						return false;
					}
					public void addProfile(Profile profile) {
					}
					public Profile getProfile(String profile) {
						return null;
					}
					public Iterator iterator() {
						// return an empty iterator
						return (new Vector()).iterator();
					}};
            }
			contexts.put(deviceMatchCName, context);
        }
        request.setAttribute(CONTEXT_KEY, context);
        session.setAttribute(CONTEXT_KEY, context);

        return context;
    }

    /**
     * Get the useragent common name.
     * @return The useragent common name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Set the useragent common name.
     * @param name The useragent common name.
     */
    private void setDeviceName(String name) {
        commonName = name;
    }

	/**
	 * Get the ProfileSet for the device.
	 * @return The ProfileSet
	 */
	public ProfileSet getProfileSet() {
		if(profileSet == null) {
			throw new IllegalStateException("Call to 'isMember' before device has been matched.");
		}
		return profileSet;
	}
	
	/**
	 * Unit Test class. 
	 */
	static class UnitTest {
		static UAContext getUAContext(String commonName) {
			return new ServletUAContext(commonName);
		}
	}
}
