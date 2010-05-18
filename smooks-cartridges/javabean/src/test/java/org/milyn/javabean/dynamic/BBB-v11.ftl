<${nsp}:root <@writeNamespaces/>>

	<${nsp}:bbb>
	   <${nsp}:value property="${bean.floatProperty?string("0.##")}" />
	</${nsp}:bbb>
	<#list bean.aaas as aaa>
    <@writeBean name="aaa" indent="4"/>
    </#list>
	
</${nsp}:root>