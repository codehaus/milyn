<%@ taglib uri="www.milyn.org/tld/tinak.tld" prefix="tinak" %>
	        <p>
	        Milyn Tinak is a Java API for performing device/useragent recognition in a Java
	        environment.  
	        </p>
	        <p>
	        Tinak comes with a JSP Tag Library to help in the writing of device aware JSP pages, 
	        as well as an adapter for adding useragent (requesting device) recognition services 
	        to a Servlet.
	        </p>
		<tinak:ua match="medium|large">
	        <p>
	        The advantages of being able to write device aware content are pretty obvious - targeted 
	        advertising, optimised content etc.  There is also the practical problem of the various
	        types of markup used by different devices - versions of HTML, versions of WML etc. and therefore 
	        the need to be able to recognise which form of markup needs to be delivered to the requesting 
	        device.
	        </p>
		</tinak:ua>
		<tinak:ua match="large">
	        <p>
	        Even if you're not interested in delivering different types of markup, wouldn't it be nice to
	        be able to deliver specific images depending on the requesting device type and its capabilities?
	        Wouldn't it be nice to be able to send one advertisement if, for example, the content is 
	        being viewed through a TV (WebTV etc) and another advertisement if the content is being viewed
	        through a PDA?  Wouldn't it be nice to lay the content out a little differently when being
	        viewed though a PDA versus a Desktop Browser? The Tinak JSP Tag Library helps you do these 
	        types of things with a little less hassle and uses Tinak to perform the device recogition.
	        </p>
	        <p>
	        Tinak is actually just the device recognition module for the <a href="#">Milyn Project</a>.  
	        The Milyn Project intends to develop tools for writing and delivering device targeted content.  
	        What we mean by "device target content" is the adaptation of the authored content in a way that 
	        best suits the requesting device, whether that be transforing the content from the source 
	        markup (most likely HTML) to a completely different markup, changing the layout of the content 
	        or just making the content more "well-formed" (for a strict device).
	        </p>
		</tinak:ua>
	        