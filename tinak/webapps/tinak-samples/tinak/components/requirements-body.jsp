<%@ taglib uri="www.milyn.org/tld/tinak.tld" prefix="tinak" %>
			<p>
			Tinak can be used in any JDK 1.4+ Java environment as long as it has a request adapter to 
			handle the request servicing environment.  Tinak comes with a Servlet request adapter - the 
			HTTP request interface is actually bassed on the HttpServletRequest.  At the moment Tinak
			device recognition is based solely on HTTP request - HTTP Headers and/or Request Parameters.
			</p>
		<tinak:ua match="medium|large">
			<p>
			Therefore, the requirements for Tinak and the HTTP Servlet Request adaptor which comes with 
			it are:
			<ul>
				<li>JDK 1.4+</li>
				<li>Servlet Specification 2.3+ compliant container</li>
			</ul>
			</p>
		</tinak:ua>