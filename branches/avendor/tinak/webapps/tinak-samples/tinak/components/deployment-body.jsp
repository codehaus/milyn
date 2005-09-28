<%@ taglib uri="www.milyn.org/tld/tinak.tld" prefix="tinak" %>
						Deploying Tinak in a J2EE Servlet Container is very simple:
						<ol>
							<li>Deploy the Tinak binaries in the container i.e. in <code>/WEB-INF/lib</code>.
							Included in this are the Jakarta Commons Digester and Jakarta Regexp packages.</li>
							<li>Deploy the Tinak device recognition XML module in the container.  It's default
							deployment location is <code>/WEB-INF/device-ident.xml</code> but this can be configured 
							in the deployment descriptor.</li>
						</ol>
						<p/>
						Deploying the Tinak JSP Tag Library in a J2EE Servlet Container is also very simple:
						<ol>
							<li>Deploy the Tinak JSP Tag Library binary in the container i.e. in <code>/WEB-INF/lib</code>.							
							</li>
							<li>Add a declaration for the Tag Library in the deployment descriptor as follows:</li>
						</ol>
<pre>
&lt;taglib&gt;
	&lt;taglib-uri&gt;www.milyn.org/tld/tinak.tld&lt;/taglib-uri&gt;
	&lt;taglib-location&gt;/WEB-INF/lib/milyn-tinak-taglib.jar&lt;/taglib-location&gt;
&lt;/taglib&gt;
</pre>
						<p/>
						As stated at the start of this section, the Tinak device recognition XML module's
						default deployment location can be overriden.  This is done through the web 
						application's deployment descriptor as follows:
<pre>
&lt;servlet&gt;
	&lt;servlet-name&gt;aservlet&lt;/servlet-name&gt;
	&lt;servlet-class&gt;org.milyn.some.AServlet&lt;/servlet-class&gt;
	&lt;init-param&gt;
		&lt;param-name&gt;<b>DeviceIdentUrl</b>&lt;/param-name&gt;
		&lt;param-value&gt;<i>URL</i>&lt;/param-value&gt;
	&lt;/init-param&gt;
&lt;/servlet&gt;
</pre>
						or,
<pre>
&lt;context-param&gt;
	&lt;param-name&gt;<b>DeviceIdentUrl</b>&lt;/param-name&gt;
	&lt;param-value&gt;<i>URL</i>&lt;/param-value&gt;
&lt;/context-param&gt;
</pre>
						checked by Tinak in that order.  <i>URL</i> can be a context relative URL or a URL to an external resource
						i.e. and absolute URL.  The ability to define an external URL means that two or more web
						applications can share the same configuration.  It also means that the device recognition
						data can be stored in a database and accessed as an XML stream via a HTTP request.
