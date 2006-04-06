<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>
<%@ taglib uri="www.milyn.org/tld/tinak.tld" prefix="tinak" %>

<html>
	<sput idref="cnn.home:HTML/HEAD" />
	<body bgcolor="grey">
		<!-- Pull in the CNN Logo from the source page. -->
		<sput idref="cnn.home:html/body/table[1]/tr[1]/td[1]/a" />
		<br/>
		<!-- Build the navigation for the top of the page for html medium. -->
		<!-- Pull in the Nav links from the source page into snavsrc. -->
		<!-- Repeat snavblock for each of the links in snavsrc. -->
		<!-- Repeat srepeat based on frequency. -->
		<table width="100%">
		<tr bgcolor="#CCCCCC"><td>
		<snav req-slinks="true">
			<slinks>
				<jsp:include page="/html32-small-medium.nav.top.slinks" />
			</slinks>
			<snavsrc>
				<sput idref='cnn.home:html/body//div[@class="cnnNavText"]/a' />
			</snavsrc>
			<snavblock>
				<a>${text}</a>
			</snavblock>
			<srepeat freq="1"> | </srepeat>
			<tinak:ua match="small">
				<!-- On "small": add a linebreak after every second link. -->
				<srepeat freq="2"> <br/> </srepeat>
			</tinak:ua>
		</snav>	
		</td></tr>
		</table>
		<table width="100%">
		<tr bgcolor="#CCCCCC"><td>
		<snav req-slinks="false">
			<snavsrc>
				<spagelinks/>
			</snavsrc>
			<snavblock>
				<a/>
			</snavblock>
			<srepeat freq="1"> | </srepeat>
			<tinak:ua match="small">
				<!-- On "small": add a linebreak after every second link. -->
				<srepeat freq="2"> <br/> </srepeat>
			</tinak:ua>
		</snav>	
		</td></tr>
		</table>
		<template:get name="body"/>
		
		<tinak:ua match="medium">
		<!-- Only display the bottom nav on medium - not on small -->
		<table width="100%">
		<tr bgcolor="#CCCCCC"><td>
		<snav req-slinks="true">
			<slinks>
				<jsp:include page="/html32-small-medium.nav.bottom.slinks" />
			</slinks>
			<snavsrc>
				<sput idref='cnn.home:html/body//div[@class="cnnNavText"]/a' />
			</snavsrc>
			<snavblock>
				<a>${text}</a>
			</snavblock>
			<srepeat freq="1"> | </srepeat>
		</snav>	
		</td></tr>
		</table>
		</tinak:ua>
	</body>
</html>
