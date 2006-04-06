<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>

<template:insert template='/templates/section-block.jsp'>
	<template:put name='section-id'		content='requirements' direct='true'/>
	<template:put name='section-title'	content='Requirements' direct='true'/>
	<template:put name='section-body'	content='/tinak/components/requirements-body.jsp'/>
</template:insert>
