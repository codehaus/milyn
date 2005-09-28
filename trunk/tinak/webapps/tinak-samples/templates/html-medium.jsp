<%@ taglib uri='/WEB-INF/struts-template.tld' prefix='template' %>
<html>
    <head>
		<title>Milyn-Tinak</title>
	</head>
    <body>
		<table width="350">
			<tr>
				<td>
					<template:get name="banner"/>
					<template:get name="nav"/>
				</td>
			</tr>
		</table>
		<hr/>
		<table width="350">
			<tr>
				<td> 
					<template:get name="section1"/>
					<template:get name="section2"/>
					<template:get name="section3"/>
				</td>
			</tr>				
		</table>
    </body>
</html>