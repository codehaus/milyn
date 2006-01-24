// Copyright 2005 Chibacon
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package org.chiba.adapter.servlet;

import org.apache.log4j.Category;
import org.apache.commons.httpclient.Cookie;
import org.chiba.adapter.ChibaAdapter;
import org.chiba.adapter.flux.FluxAdapter;
import org.chiba.tools.xslt.StylesheetLoader;
import org.chiba.tools.xslt.UIGenerator;
import org.chiba.tools.xslt.XSLTGenerator;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.connector.http.AbstractHTTPConnector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The ChibaServlet handles all interactions between client and
 * form-processor (ChibaBean) for the whole lifetime of a form-filling session.
 * <br>
 * The Processor will be started through a Get-request from the client
 * pointing to the desired form-container. The Processor instance will
 * be stored in a Session-object.<br>
 * <br>
 * All further interaction will be handled through Post-requests.
 * Incoming request params will be mapped to data and action-handlers.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @author William Boyd
 * @version $Id$
 */
public class ChibaServlet extends HttpServlet {
    //init-params
    private static Category cat = Category.getInstance(ChibaServlet.class);

    private static final String FORM_PARAM_NAME = "form";
    private static final String XSL_PARAM_NAME = "xslt";
    private static final String CSS_PARAM_NAME = "css";
    private static final String ACTIONURL_PARAM_NAME = "action_url";
    private static final String JAVASCRIPT_PARAM_NAME = "JavaScript";

    public static final String CHIBA_ADAPTER = "chiba.adapter";
    public static final String CHIBA_UI_GENERATOR = "chiba.ui.generator";
    public static final String CHIBA_SUBMISSION_RESPONSE = "chiba.submission.response";

    /*
     * It is not thread safe to modify these variables once the
     * init(ServletConfig) method has been called
     */
    // the absolute path to the Chiba config-file
    protected String configPath = null;

    // the rootdir of this app; forms + documents fill be searched under this root
    protected String contextRoot = null;

    // where uploaded files are stored
    protected String uploadDir = null;

    protected String stylesPath = null;

    protected String plain_html_agent;
    protected String ajax_agent;


    /**
     * Returns a short description of the servlet.
     *
     * @return - Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Servlet Controller for Chiba XForms Processor";
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {
    }

    /**
     * Initializes the servlet.
     *
     * @param config - the ServletConfig object
     * @throws javax.servlet.ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        cat.info("--------------- initing ChibaServlet... ---------------");
        //read some params from web-inf
        contextRoot = getServletConfig().getServletContext().getRealPath("");
        if (contextRoot == null)
            contextRoot = getServletConfig().getServletContext().getRealPath(".");

        //get the relative path to the chiba config-file
        String path = getServletConfig().getInitParameter("chiba.config");

        //get the real path for the config-file
        if (path != null) {
            configPath = getServletConfig().getServletContext().getRealPath(path);
        }

        //get the path for the stylesheets
        path = getServletConfig().getServletContext().getInitParameter("chiba.xforms.stylesPath");

        //get the real path for the stylesheets and configure a new StylesheetLoader with it
        if (path != null) {
            stylesPath = getServletConfig().getServletContext().getRealPath(path);
            cat.info("stylesPath: " + stylesPath);
        }

        //uploadDir = contextRoot	+ "/" + getServletConfig().getServletContext().getInitParameter("chiba.upload");
        uploadDir = getServletConfig().getServletContext().getInitParameter("chiba.upload");

        //Security constraint
        if (uploadDir != null) {
            if (uploadDir.toUpperCase().indexOf("WEB-INF") >= 0) {
                throw new ServletException("Chiba security constraint: uploadDir '" + uploadDir + "' not allowed");
            }
        }

        //user-agent mappings
        plain_html_agent = getServletConfig().getServletContext().getInitParameter("chiba.useragent.plainhtml.path");
        ajax_agent = getServletConfig().getServletContext().getInitParameter("chiba.useragent.ajax.path");
    }

    /**
     * Starts a new form-editing session.<br>
     * <p/>
     * The default value of a number of settings can be overridden as follows:
     * <p/>
     * 1. The uru of the xform to be displayed can be specified by using a param name of 'form' and a param value
     * of the location of the xform file as follows, which will attempt to load the current xforms file.
     * <p/>
     * http://localhost:8080/chiba-0.9.3/XFormsServlet?form=/forms/hello.xhtml
     * <p/>
     * 2. The uru of the CSS file used to style the form can be specified using a param name of 'css' as follows:
     * <p/>
     * http://localhost:8080/chiba-0.9.3/XFormsServlet?form=/forms/hello.xhtml&css=/chiba/my.css
     * <p/>
     * 3. The uri of the XSLT file used to generate the form can be specified using a param name of 'xslt' as follows:
     * <p/>
     * http://localhost:8080/chiba-0.9.3/XFormsServlet?form=/forms/hello.xhtml&xslt=/chiba/my.xslt
     * <p/>
     * 4. Besides these special params arbitrary other params can be passed via the GET-string and will be available
     * in the context map of ChibaBean. This means they can be used as instance data (with the help of ContextResolver)
     * or to set params for URI resolution.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see org.chiba.xml.xforms.connector.context.ContextResolver
     * @see org.chiba.xml.xforms.connector.ConnectorFactory
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        ChibaAdapter adapter = null;
        HttpSession session = request.getSession(true);

        cat.info("--------------- new XForms session ---------------");
        try {
            //kill everything that may have lived before
            session.removeAttribute(CHIBA_ADAPTER);
            session.removeAttribute(CHIBA_UI_GENERATOR);

            // determine Form to load
            String formURI = getRequestURI(request) + request.getParameter(FORM_PARAM_NAME);
            if (formURI == null) {
                throw new IOException("Resource not found: " + formURI + " not found");
            }

            String xslFile = request.getParameter(XSL_PARAM_NAME);
            String css = request.getParameter(CSS_PARAM_NAME);
            String javascriptPresent = request.getParameter(JAVASCRIPT_PARAM_NAME);

            String actionURL = null;
            if (javascriptPresent != null){
                //do AJAX
                FluxAdapter fluxAdapter = new FluxAdapter();
                fluxAdapter.setSession(session);
                adapter = fluxAdapter;
                actionURL = getActionURL(request, response,true);
            }else{
                //do standard browser support without scripting
                adapter = new ServletAdapter();
                actionURL = getActionURL(request, response,false);
            }
            //setup Adapter
            adapter = setupAdapter(adapter,session, formURI);
            setContextParams(request, adapter);
            storeCookies(request,adapter);
            adapter.init();

            if(load(adapter, response)) return;
            if(replaceAll(adapter, response)) return;

            response.setContentType("text/html");
            java.io.PrintWriter out = response.getWriter();

            UIGenerator uiGenerator = createUIGenerator(request, actionURL, xslFile, css, javascriptPresent);
            uiGenerator.setInputNode(adapter.getXForms());
            uiGenerator.setOutput(out);
            uiGenerator.generate();

            //store adapter in session
            session.setAttribute(CHIBA_ADAPTER, adapter);
            session.setAttribute(CHIBA_UI_GENERATOR,uiGenerator);

            out.close();
        } catch (Exception e) {
            shutdown(adapter, session, e, response, request);

        }
    }





    /**
     * configures the an Adapter for interacting with the XForms processor (ChibaBean). The Adapter itself
     * will create the XFormsProcessor (ChibaBean) and configure it for processing.
     *
     * If you'd like to use a different source of XForms documents e.g. DOM you should extend this class and
     * overwrite this method. Please take care to also set the baseURI of the processor to a reasonable value
     * cause this will be the fundament for all URI resolutions taking place.
     *
     * @param adapter  the ChibaAdapter implementation to setup
     * @param session   - the Servlet session
     * @param formPath  - the relative location where forms are stored
     * @return ServletAdapter
     */
    protected ChibaAdapter setupAdapter(ChibaAdapter adapter,
                                        HttpSession session,
                                        String formPath
    ) throws XFormsException, URISyntaxException {

        adapter.createXFormsProcessor();

        if ((configPath != null) && !(configPath.equals(""))) {
            adapter.setConfigPath(configPath);
        }
        adapter.setXForms(new URI(formPath));
        adapter.setBaseURI(formPath);
        adapter.setUploadDestination(uploadDir);

        Map servletMap = new HashMap();
        servletMap.put(ChibaAdapter.SESSION_ID, session.getId());
        adapter.setContextParam(ChibaAdapter.SUBMISSION_RESPONSE, servletMap);

        return adapter;
    }

    /**
     * stores cookies that may exist in request and passes them on to processor for usage in
     * HTTPConnectors. Instance loading and submission then uses these cookies. Important for
     * applications using auth.
     *
     * @param request the servlet request
     * @param adapter the Chiba adapter instance
     */
    protected void storeCookies(HttpServletRequest request,ChibaAdapter adapter){
          javax.servlet.http.Cookie[] cookiesIn = request.getCookies();
          if (cookiesIn != null) {
              Cookie[] commonsCookies = new org.apache.commons.httpclient.Cookie[cookiesIn.length];
              for (int i = 0; i < cookiesIn.length; i += 1) {
                  javax.servlet.http.Cookie c = cookiesIn[i];
                  Cookie newCookie = new Cookie(c.getDomain(),
                                                c.getName(),
                                                c.getValue(),
                                                c.getPath(),
                                                c.getMaxAge(),
                                                c.getSecure());
                  commonsCookies[i] = newCookie;
              }
              adapter.setContextParam(AbstractHTTPConnector.REQUEST_COOKIE,commonsCookies);
          }
    }

    /**
     *
     * creates and configures the UI generating component.
     * @param request
     * @param actionURL
     * @param xslFile
     * @param css
     * @param javascriptPresent
     * @return
     * @throws XFormsException
     */
    protected UIGenerator createUIGenerator(HttpServletRequest request,
                                            String actionURL,
                                            String xslFile,
                                            String css,
                                            String javascriptPresent) throws XFormsException {
    	
        UIGenerator uiGenerator = new SmooksUIGenerator(request);
    	/*
        StylesheetLoader stylesheetLoader = new StylesheetLoader(stylesPath);
        if (xslFile != null){
            stylesheetLoader.setStylesheetFile(xslFile);
        }
        UIGenerator uiGenerator = new XSLTGenerator(stylesheetLoader);

        //set parameters
        uiGenerator.setParameter("contextroot",request.getContextPath());
        uiGenerator.setParameter("action-url",actionURL);
        uiGenerator.setParameter("debug-enabled",String.valueOf(cat.isDebugEnabled()));
        String selectorPrefix = Config.getInstance().getProperty(HttpRequestHandler.SELECTOR_PREFIX_PROPERTY,
                                                                 HttpRequestHandler.SELECTOR_PREFIX_DEFAULT);
        uiGenerator.setParameter("selector-prefix", selectorPrefix);
        String removeUploadPrefix = Config.getInstance().getProperty(HttpRequestHandler.REMOVE_UPLOAD_PREFIX_PROPERTY,
                                                                     HttpRequestHandler.REMOVE_UPLOAD_PREFIX_DEFAULT);
        uiGenerator.setParameter("remove-upload-prefix", removeUploadPrefix);
        if (css != null) {
            uiGenerator.setParameter("css-file", css);
        }
        String dataPrefix = Config.getInstance().getProperty("chiba.web.dataPrefix");
        uiGenerator.setParameter("data-prefix", dataPrefix);

        String triggerPrefix = Config.getInstance().getProperty("chiba.web.triggerPrefix");
        uiGenerator.setParameter("trigger-prefix", triggerPrefix);

        uiGenerator.setParameter("user-agent", request.getHeader("User-Agent"));

        if(javascriptPresent != null){
            uiGenerator.setParameter("scripted","true");
        }
        */

        return uiGenerator;
    }

    /**
     * this method is responsible for passing all context information needed by the Adapter and Processor from
     * ServletRequest to ChibaContext. Will be called only once when the form-session is inited (GET).
     *
     * @param request           the ServletRequest
     * @param chibaAdapter    the ChibaAdapter to use
     */
    protected void setContextParams(HttpServletRequest request, ChibaAdapter chibaAdapter) {

        //[1] pass user-agent to Adapter for UI-building
        chibaAdapter.setContextParam(ServletAdapter.USERAGENT, request.getHeader("User-Agent"));

        //[2] read any request params that are *not* Chiba params and pass them into the context map
        Enumeration params = request.getParameterNames();
        String s;
        while (params.hasMoreElements()) {
            s = (String) params.nextElement();
            //store all request-params we don't use in the context map of ChibaBean
            if (!(s.equals(FORM_PARAM_NAME) ||
                    s.equals(XSL_PARAM_NAME) ||
                    s.equals(CSS_PARAM_NAME) ||
                    s.equals(ACTIONURL_PARAM_NAME) ||
                    s.equals(JAVASCRIPT_PARAM_NAME))) {
                String value = request.getParameter(s);
                //servletAdapter.setContextProperty(s, value);
                chibaAdapter.setContextParam(s, value);
                if (cat.isDebugEnabled()) {
                    cat.debug("added request param '" + s + "' added to context");
                }
            }
        }
    }

    /**
     * @deprecated should be re-implemented using chiba events on adapter
     */
    protected boolean load(ChibaAdapter adapter, HttpServletResponse response) throws XFormsException, IOException {
        if (adapter.getContextParam(ChibaAdapter.LOAD_URI) != null) {
            String redirectTo = (String) adapter.removeContextParam(ChibaAdapter.LOAD_URI);
            adapter.shutdown();
            response.sendRedirect(response.encodeRedirectURL(redirectTo));
            return true;
        }
        return false;
    }

    /**
     * @deprecated should be re-implemented using chiba events on adapter
     */
    protected boolean replaceAll(ChibaAdapter chibaAdapter, HttpServletResponse response) throws XFormsException, IOException {
            if (chibaAdapter.getContextParam(ChibaAdapter.SUBMISSION_RESPONSE) != null) {
                Map forwardMap = (Map) chibaAdapter.removeContextParam(ChibaAdapter.SUBMISSION_RESPONSE);
                if (forwardMap.containsKey(ChibaAdapter.SUBMISSION_RESPONSE_STREAM)) {
                    forwardResponse(forwardMap, response);
                    chibaAdapter.shutdown();
                    return true;
                }
            }
            return false;
    }

    private String getActionURL(HttpServletRequest request, HttpServletResponse response, boolean scripted) {

        String defaultActionURL=null;
        if(scripted){

            defaultActionURL = getRequestURI(request) + ajax_agent;
        }else{
            defaultActionURL = getRequestURI(request) + plain_html_agent;
        }
        String encodedDefaultActionURL = response.encodeURL(defaultActionURL);
        int sessIdx = encodedDefaultActionURL.indexOf(";jsession");
        String sessionId = null;
        if (sessIdx > -1) {
            sessionId = encodedDefaultActionURL.substring(sessIdx);
        }
        String actionURL = request.getParameter(ACTIONURL_PARAM_NAME);
        if (null == actionURL) {
            actionURL = encodedDefaultActionURL;
        } else if (null != sessionId) {
            actionURL += sessionId;
        }

        cat.info("actionURL: " + actionURL);
        // encode the URL to allow for session id rewriting
        actionURL = response.encodeURL(actionURL);
        return actionURL;
    }

    private String getRequestURI(HttpServletRequest request){
        StringBuffer buffer = new StringBuffer(request.getScheme());
        buffer.append("://");
        buffer.append(request.getServerName());
        buffer.append(":");
        buffer.append(request.getServerPort()) ;
        buffer.append(request.getContextPath());
        return buffer.toString();
    }

    private void forwardResponse(Map forwardMap, HttpServletResponse response) throws IOException {
        // fetch response stream
        InputStream responseStream = (InputStream) forwardMap.remove(ChibaAdapter.SUBMISSION_RESPONSE_STREAM);

        // copy header information
        Iterator iterator = forwardMap.keySet().iterator();
        while (iterator.hasNext()) {
        	
            String name = (String) iterator.next();
            
            if ("Transfer-Encoding".equalsIgnoreCase(name)) {
            	// Some servers (e.g. WebSphere) may set a "Transfer-Encoding"
            	// with the value "chunked". This may confuse the client since
            	// ChibaServlet output is not encoded as "chunked", so this
            	// header is ignored.
            	continue;
            }
            String value = (String) forwardMap.get(name);
            response.setHeader(name, value);
        }

        // copy stream content
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        for (int b = responseStream.read();
             b > -1;
             b = responseStream.read()) {
            outputStream.write(b);
        }

        // close streams
        responseStream.close();
        outputStream.close();
    }

    protected void shutdown(ChibaAdapter chibaAdapter,
                          HttpSession session,
                          Exception e,
                          HttpServletResponse response,
                          HttpServletRequest request) throws IOException {
        // attempt to shutdown processor
        if (chibaAdapter != null) {
            try {
                chibaAdapter.shutdown();
            } catch (XFormsException xfe) {
                xfe.printStackTrace();
            }
        }

        // store exception
        session.setAttribute("chiba.exception", e);

        // redirect to error page (after encoding session id if required)
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/" +
                request.getSession().getServletContext().getInitParameter("error.page")));
    }
}

// end of class
