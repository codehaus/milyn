<?xml version="1.0"?>
<smooks-resource-list <@writeNamespaces/>>

	<#list bean.modelComponents as modelComponent>
    <@writeBean bean=modelComponent />
    </#list>

</smooks-resource-list>