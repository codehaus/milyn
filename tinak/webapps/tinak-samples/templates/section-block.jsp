<%@ taglib uri="www.milyn.org/tld/tinak.tld" prefix="tinak" %>
<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>

			<tinak:select>
				<tinak:ua match="html4">
					<div id="<template:get name='section-id'/>" class="sectiontitle"><template:get name="section-title"/></div>
					<div class="sectiontext">
				</tinak:ua>
				<tinak:ua match="html32">
					<h1 id="<template:get name='section-id'/>"><template:get name="section-title"/></h1>
				</tinak:ua>
				<tinak:ua match="wml">
					<card id="<template:get name='section-id'/>" <template:get name="section-title"/>>
				</tinak:ua>
			</tinak:select>
					<template:get name="section-body"/>
			<tinak:select>
				<tinak:ua match="html4">
					</div>
				</tinak:ua>
				<tinak:ua match="wml">
					</card>
				</tinak:ua>
			</tinak:select>
			