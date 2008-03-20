<#macro outputMessageNodes messageNodes>
    <#list messageNodes as messageNode>
        <#assign nodeDepth = messageNode.depth * 20>
        <div style="padding-left: ${nodeDepth}px;">
            <#if messageNode.visitBefore>
                &lt;${messageNode.elementName}&gt;
            <#else>
                &lt;/${messageNode.elementName}&gt;
            </#if>
            <#if (messageNode.execInfoNodes?size > 0)>
                <a href='#' onclick="return selectElement('block-${messageNode.nodeId}');">*</a>
            </#if>
        </div>
    </#list>
</#macro>
<#macro outputMessageSummaries messageNodes>
    <#list messageNodes as messageNode>
        <#if (messageNode.execInfoNodes?size > 0)>
            <div id="block-${messageNode.nodeId}" style="display:none;" class="report-container">
                <#list messageNode.execInfoNodes as execInfoNode>
                <div>
                    <a href='#' onclick="return selectVisitor('block-details-${execInfoNode.nodeId}');">${execInfoNode.summary}</a>
                </div>
                </#list>
            </div>
        </#if>
    </#list>
</#macro>
<#macro outputMessageDetails messageNodes>
    <#list messageNodes as messageNode>
        <#if (messageNode.execInfoNodes?size > 0)>
            <#list messageNode.execInfoNodes as execInfoNode>
                <div id="block-details-${execInfoNode.nodeId}" style="display:none;" class="report-container"><pre><@htmlEscape>${execInfoNode.detail}</@htmlEscape></pre></div>
            </#list>
        </#if>
    </#list>
</#macro>
