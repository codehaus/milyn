<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>
<html>
    <head>
		<title>Milyn-Tinak</title>
		<link rel="stylesheet" type="text/css" href="style.css" title="Style">		
	</head>
    <body>
		<template:get name="banner"/>
		<hr/>
		<table>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td class="nav">
					<template:get name="nav"/>
				</td>
				<td> 
					<!-- Details -->
					<template:get name="section1"/>
					<template:get name="section2"/>
					<template:get name="section3"/>
					<template:get name="section4"/>
					<template:get name="section5"/>
					<template:get name="section6"/>
					<template:get name="section7"/>
					<template:get name="section8"/>
				</td>
			</tr>				
		</table>
    </body>
</html>