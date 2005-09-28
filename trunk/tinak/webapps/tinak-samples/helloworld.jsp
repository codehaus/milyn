<%@ include file="/WEB-INF/meta/tinak.jsp"%>
<tinak:select>

<tinak:ua match="html">
<html>
	<body>
		<tinak:ua match="MSIE6"><img src="images/tinak.png"/><br/></tinak:ua>
		Hello World
	</body>
</html>	
</tinak:ua>

<tinak:ua match="wml">
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<wml>
	<card>
		<p>Hello World</p>
	</card>
</wml>	
</tinak:ua>

</tinak:select>