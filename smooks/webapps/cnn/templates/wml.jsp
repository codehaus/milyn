<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>
<wml>
	<template>
		<do label="Back"><prev/></do>
		<snav req-slinks="true">
			<slinks>
				<jsp:include page="/wml.nav.template.slinks" />
			</slinks>
			<snavsrc>
				<sput idref="cnn.home:html/body/table[4]/tr[1]/td/table[1]//a" />
			</snavsrc>
			<snavblock>
				<do label="${text}"><go cache-control="${cache-control}" href="${href}" /></do>
			</snavblock>
		</snav>			
	</template>
	<template:get name="cards"/>
</wml>