<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>

<template:insert template='/templates/section-block.jsp'>
	<template:put name='section-id'		content='whatstinak' direct='true'/>
	<template:put name='section-title'	content='What is Tinak?' direct='true'/>
	<template:put name='section-body'	content='/tinak/components/whatistinak-body.jsp'/>
</template:insert>
