<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
				xmlns:smooks="http://milyn.codehaus.org/smooks"
				xmlns:smooks-bean="org.milyn.templating.xslt.XalanJavabeanExtension"
				extension-element-prefixes="smooks-bean" 
				version="1.0">

	<xsl:template match="*" name="templatelet">
		<!-- 
			Don't remove the enclosing "root-do-not-remove" element.  Smooks will remove this for you.
			This "root-do-not-remove" element helps avoid the "can't add content before the root element"
			reported by Xalan if the template doesn't produce a single rot element node.
		-->
		<smooks:root-do-not-remove>@@@templatelet@@@</smooks:root-do-not-remove>
	</xsl:template>

</xsl:stylesheet>