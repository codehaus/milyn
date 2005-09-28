<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>

<template:insert template='/templates/section-block.jsp'>
	<template:put name='section-id'		content='servlet-deployment' direct='true'/>
	<template:put name='section-title'	content='J2EE Deployment' direct='true'/>
	<template:put name='section-body'	content='/tinak/components/deployment-body.jsp'/>
</template:insert>