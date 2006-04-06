<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>
<%@ include file="/WEB-INF/meta/tinak.jsp"%>

<tinak:select>
	<tinak:ua match="html">
		<html>
			<head>
				<title>Tinak Samples</title>
			</head>
			<body>
				<jsp:include page="index-content.html" />
			</body>
		</html>
	</tinak:ua>
	
	<tinak:ua match="wml">
		<wml>
			<card title="Tinak Samples">
				<p>
				<jsp:include page="index-content.html" />
				</p>
			</card>
		</wml>
	</tinak:ua>
</tinak:select>
