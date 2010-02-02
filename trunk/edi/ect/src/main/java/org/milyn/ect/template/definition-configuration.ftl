<#setting locale="en_US"><#setting number_format="0"/><?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<medi:edimap xmlns:medi="http://www.milyn.org/schema/edi-message-mapping-${configuration.version}.xsd">
    <#list edimap.import as import>
    <medi:import <#if import.resource?exists>resource="${import.resource}" </#if><#if import.namespace?exists>namespace="${import.namespace}" </#if><#if import.truncatableSegments?exists>truncatableSegments="${import.truncatableSegments}" </#if><#if import.truncatableFields?exists>truncatableFields="${import.truncatableFields}" </#if><#if import.truncatableComponents?exists>truncatableComponents="${import.truncatableComponents}" </#if>/>
    </#list>
	<medi:description version="${edimap.description.version}" name="${edimap.description.name}"/>
	<medi:delimiters sub-component="${edimap.delimiters.subComponent!}" segment="${edimap.delimiters.segment!}" field="${edimap.delimiters.field!}" component="${edimap.delimiters.component!}"/>
	<medi:segments xmltag="${edimap.segments.xmltag}">
        <@writeSegmentGroup segmentGroup=edimap.segments indent="\t"/>
    </medi:segments>
</medi:edimap>

<#macro writeSegmentGroup segmentGroup indent>
<#list segmentGroup.segments as segment>
<#if segment.class.simpleName == "Segment">
${indent}<@writeSegment segment=segment indent=indent+"\t"/>
<#else>
${indent}<medi:segmentGroup <#if segment.xmltag?exists>xmltag="<@formatXmlTag value=segment.xmltag/>" </#if><#if
segment.minOccurs?exists>minOccurs="${segment.minOccurs}" </#if><#if
segment.maxOccurs?exists>maxOccurs="${segment.maxOccurs}" </#if>>
<#if segment.documentation?exists>${indent+"\t"}<medi:documentation>${segment.documentation}</medi:documentation></#if>
<@writeSegmentGroup segmentGroup=segment indent=indent+"\t"/>
${indent}</medi:segmentGroup>
</#if>
</#list>
</#macro>

<#macro writeSegment segment indent>
${indent}<medi:segment <#if segment.xmltag?exists>xmltag="<@formatXmlTag value=segment.xmltag/>" </#if><#if
segment.minOccurs?exists>minOccurs="${segment.minOccurs}" </#if><#if
segment.maxOccurs?exists>maxOccurs="${segment.maxOccurs}" </#if><#if
segment.segcode?exists>segcode="${segment.segcode}" </#if><#if
segment.segref?exists>segref="${segment.segref}" </#if><#if
segment.truncatable?exists>truncatable="${segment.truncatable?string}" </#if><#if
segment.description?exists>description="${segment.description}"</#if><#if
segment.fields?size &gt; 0 || segment.documentation?exists>></#if><#if segment.documentation?exists>
${indent+"\t"}<medi:documentation><@handleHtmlEntities value=segment.documentation/></medi:documentation></#if>
<#list segment.fields as field>
<@writeField field=field indent=indent+"\t"/>
</#list><#if segment.fields?size&gt;0 || segment.documentation?exists>${indent}</medi:segment><#else>/></#if>
</#macro>

<#macro writeField field indent>
${indent}<medi:field <@writeValueNodeAttributes value=field/><#if
field.required?exists>required="${field.required?string}" </#if><#if
field.truncatable?exists>truncatable="${field.truncatable?string}" </#if><#if
field.component?size &gt; 0 || field.documentation?exists>></#if><#if field.documentation?exists>
${indent+"\t"}<medi:documentation>${field.documentation}</medi:documentation></#if><#list field.component as component>
<@writeComponent component=component indent=indent+"\t"/></#list><#if field.component?size &gt; 0 || field.documentation?exists>
${indent}</medi:field><#else>/></#if>
</#macro>


<#macro writeComponent component indent>
${indent}<medi:component <@writeValueNodeAttributes value=component/><#if component.required?exists>required="${component.required?string}" </#if><#if component.truncatable?exists>truncatable="${component.truncatable?string}" </#if><#if component.subComponent?size &gt; 0 || component.documentation?exists>></#if><#if component.documentation?exists>
${indent+"\t"}<medi:documentation>${component.documentation}</medi:documentation></#if><#list component.subComponent as subcomponent>
<@writeSubComponent subComponent=subcomponent indent=indent+"\t"/>
</#list><#if component.subComponent?size&gt;0 || component.documentation?exists>
${indent}</medi:component><#else>/></#if>
</#macro>

<#macro writeSubComponent subComponent indent>
${indent}<medi:subComponent <@writeValueNodeAttributes value=subComponent/><#if subComponent.required?exists>required="${subComponent.required?string}" </#if><#if subComponent.documentation?exists>></#if>
<#if subComponent.documentation?exists>${indent+"\t"}<medi:documentation>${subComponent.documentation}</medi:documentation></#if><#if subComponent.documentation?exists>${indent}</medi:subComponent><#else>/></#if>
</#macro>

<#macro writeValueNodeAttributes value><#if value.xmltag?exists>xmltag="<@formatXmlTag value=value.xmltag/>" </#if><#if value.type?exists>type="${value.type}" </#if><#if value.typeParameters?exists>typeParameters="${value.typeParameters}" </#if><#if value.minLength?exists>minLength="${value.minLength}" </#if><#if value.maxLength?exists>maxLength="${value.maxLength}" </#if></#macro>

<#macro formatXmlTag value><#assign result = value/>
    <#assign result = result?replace("&",  "")/>
    <#assign result = result?replace("&#10;",  "")/>
    <#assign result = result?replace("&#13;",  "")/>
    <#assign result = result?replace(" ",  "_")/>
    <#assign result = result?replace("__",  "_")/>${result}</#macro>

<#macro handleHtmlEntities value><#assign result = value/>
    <#assign result = result?replace("&",  "&amp;")/>
    <#assign result = result?replace("<",  "&lt;")/>
    <#assign result = result?replace(">",  "&gt;")/>
    <#assign result = result?replace("\"", "&quot;")/>
    <#assign result = result?replace("'",  "&apos;")/>${result}</#macro>



