<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>
<%@ include file="/WEB-INF/meta/tinak.jsp"%>

<tinak:select>
	<tinak:ua match="html">
		<tinak:select>
			<tinak:ua match="large">
				<template:insert template='templates/html-large.jsp'>
					<template:put name='banner' 	content='/tinak/components/banner.jsp'/>
					<template:put name='nav' 		content='/tinak/components/nav.jsp'/>
					<template:put name='section1' 	content='/tinak/whatistinak.jsp'/>
					<template:put name='section2' 	content='/tinak/requirements.jsp'/>
					<template:put name='section3' 	content='/tinak/deployment.jsp'/>
				</template:insert>
			</tinak:ua>
			
			<tinak:ua match="medium">
				<template:insert template='templates/html-medium.jsp'>
					<template:put name='banner' 	content='/tinak/components/banner.jsp'/>
					<template:put name='nav' 		content='/tinak/components/nav.jsp'/>
					<template:put name='section1' 	content='/tinak/whatistinak.jsp'/>
					<template:put name='section2' 	content='/tinak/requirements.jsp'/>
				</template:insert>
			</tinak:ua>
		</tinak:select>
	</tinak:ua>
	
	<tinak:ua match="wml">
		<template:insert template='templates/wml.jsp'>
			<template:put name='nav' 		content='/tinak/components/nav.jsp'/>
			<template:put name='section1' 	content='/tinak/whatistinak.jsp'/>
			<template:put name='section2' 	content='/tinak/requirements.jsp'/>
		</template:insert>
	</tinak:ua>
</tinak:select>
